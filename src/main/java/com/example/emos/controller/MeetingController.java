package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.common.util.tencent.TrtcUtil;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.meeting.*;
import com.example.emos.db.pojo.TbMeeting;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会议web接口")
@Slf4j
@PropertySource("classpath:tencent.properties")
public class MeetingController {

    @Resource
    private TbMeetingService meetingService;

    @Resource
    private TrtcUtil trtcUtil;

    @Value("${tencent.trtc.appId}")
    private int appId;

    @PostMapping("/searchOfflineMeetingByPage")
    @Operation(summary = "分页查询线下会议信息")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm searchOfflineMeetingByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchOfflineMeetingByPageForm).toBean(HashMap.class);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("page", meetingService.searchOfflineMeetingByPage(param));
    }

    @PostMapping("/searchOnlineMeetingByPage")
    @Operation(summary = "分页查询线上会议信息")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchOnlineMeetingByPage(@Valid @RequestBody SearchOnlineMeetingByPageForm searchOnlineMeetingByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchOnlineMeetingByPageForm).toBean(HashMap.class);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("page", meetingService.searchOnlineMeetingByPage(param));
    }

    @PostMapping("/searchOfflineMeetingInWeek")
    @Operation(summary = "查询从当天开始一周内的会议信息")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchOfflineMeetingInWeek(@Valid @RequestBody SearchOfflineMeetingInWeekForm searchOfflineMeetingInWeekForm) {
        String date = searchOfflineMeetingInWeekForm.getDate();
        DateTime startDate, endDate;
        if (date != null && !date.isEmpty()) {
            // 从用户输入的日期开始，生成七天日期
            startDate = DateUtil.parseDate(date);
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK, 6);
        } else {
            // 从当前日期开始，生成七天日期
            startDate = DateUtil.beginOfWeek(new Date());
            endDate = DateUtil.endOfWeek(new Date());
        }
        Map<String, Object> param = Map.of("place", searchOfflineMeetingInWeekForm.getName(),
                "startDate", startDate.toDateStr(),
                "endDate", endDate.toDateStr(),
                "mold", searchOfflineMeetingInWeekForm.getMold(),
                "userId", StpUtil.getLoginIdAsInt());
        ArrayList<HashMap<String, Object>> list = meetingService.searchOfflineMeetingInWeek(param);

        // 生成周日历水平表头的文字标题
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_WEEK);
        ArrayList<JSONObject> days = new ArrayList<>();
        for (DateTime dateTime : range) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("date", dateTime.toString("MM/dd"));
            jsonObject.set("day", dateTime.dayOfWeekEnum().toChinese("周"));
            days.add(jsonObject);
        }

        return Objects.requireNonNull(R.ok().put("list", list)).put("days", days);
    }

    @PostMapping("/insert")
    @Operation(summary = "新增会议信息")
    @SaCheckPermission(value = {"ROOT", "MEETING:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertMeetingForm insertMeetingForm) {
        String date = insertMeetingForm.getDate();
        DateTime start = DateUtil.parse(date + " " + insertMeetingForm.getStart());
        DateTime end = DateUtil.parse(date + " " + insertMeetingForm.getEnd());
        if (start.isAfterOrEquals(end)) {
            return R.error("会议结束时间必须大于开始时间");
        } else if (new DateTime().isAfterOrEquals(start)) {
            return R.error("会议开始时间不能早于当前时间");
        }
        TbMeeting meeting = JSONUtil.parse(insertMeetingForm).toBean(TbMeeting.class);
        meeting.setUuid(UUID.randomUUID().toString(true));
        meeting.setCreatorId(StpUtil.getLoginIdAsInt());
        meeting.setStatus(1);
        int rows = meetingService.insert(meeting);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchMeetingInfo")
    @Operation(summary = "查询会议详情信息")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchMeetingInfo(@Valid @RequestBody SearchMeetingInfoForm searchMeetingInfoForm) {
        return R.ok(meetingService.searchMeetingInfo(searchMeetingInfoForm.getStatus(), searchMeetingInfoForm.getId()));
    }

    @PostMapping("/deleteMeetingApplication")
    @Operation(summary = "删除会议申请")
    @SaCheckPermission(value = {"ROOT", "MEETING:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteMeetingApplication(@Valid @RequestBody DeleteMeetingApplicationForm deleteMeetingApplicationForm) {
        HashMap<String, Object> param = JSONUtil.parse(deleteMeetingApplicationForm).toBean(HashMap.class);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("rows", meetingService.deleteMeetingApplication(param));
    }

    @GetMapping("/searchRoomIdByUUID")
    @Operation(summary = "查询线上会议室ID")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchRoomIdByUUID(@RequestParam("uuid") String uuid) {
        if (StrUtil.isEmpty(uuid)) {
            throw new EmosException("uuid不能为空");
        }
        return R.ok().put("roomId", meetingService.searchRoomIdByUUID(uuid));
    }

    @GetMapping("/searchOnlineMeetingMembers")
    @Operation(summary = "查询线上会议的参会人员")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchOnlineMeetingMembers(@RequestParam("meetingId") Integer meetingId) {
        if (meetingId == null) {
            throw new EmosException("meetingId不能为空");
        }
        if (meetingId < 1) {
            throw new EmosException("meetingId不能小于1");
        }
        HashMap<String, Integer> param = new HashMap<>(2);
        param.put("userId", StpUtil.getLoginIdAsInt());
        param.put("meetingId", meetingId);
        return R.ok().put("list", meetingService.searchOnlineMeetingMembers(param));
    }

    @GetMapping("/searchMyUserSig")
    @Operation(summary = "获取用户签名")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R searchMyUserSig() {
        int userId = StpUtil.getLoginIdAsInt();
        String userSig = trtcUtil.genUserSig(userId + "");
        Map<String, Object> map = Map.of("userSig", userSig, "userId", userId, "appId", appId);
        return R.ok(map);
    }

    @GetMapping("/receiveNotify")
    @Operation(summary = "接收会议申请的审批结果")
    public R receiveNotify(@RequestParam("uuid") String uuid, @RequestParam("result") String result) {
        if ("同意".equals(result)) {
            log.info("会议{}审批结果为:{}", uuid, result);
        } else {
            log.error("会议{}审批结果为:{}", uuid, result);
        }
        return R.ok();
    }

    @GetMapping("/updateMeetingPresent")
    @Operation(summary = "执行会议签到")
    @SaCheckPermission(value = {"ROOT", "MEETING:SELECT"}, mode = SaMode.OR)
    public R updateMeetingPresent(@RequestParam("meetingId") Integer meetingId) {
        if (meetingId == null || meetingId < 1) {
            throw new EmosException("meetingId格式不正确");
        }
        return R.ok().put("rows", meetingService.updateMeetingPresent(meetingId, StpUtil.getLoginIdAsInt()));
    }
}