package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.meetingroom.*;
import com.example.emos.db.pojo.TbMeetingRoom;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbMeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@Tag(name = "MeetingRoomController", description = "会议室web接口")
@RequestMapping("/meeting_room")
public class MeetingRoomController {

    @Resource
    private TbMeetingRoomService meetingRoomService;

    @GetMapping("/searchAllMeetingRoom")
    @Operation(summary = "查询所有可用的会议室")
    @SaCheckLogin
    public R searchAllMeetingRoom() {
        List<TbMeetingRoom> tbMeetingRoomNames = meetingRoomService.query()
                .eq("`status`", 1)
                .select("name").list();
        return R.ok().put("list", tbMeetingRoomNames);
    }

    @PostMapping("/searchMeetingRoomByPage")
    @Operation(summary = "分页查询会议室信息")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:SELECT"}, mode = SaMode.OR)
    public R searchMeetingRoomByPage(@Valid @RequestBody SearchMeetingRoomByPageForm searchMeetingRoomByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchMeetingRoomByPageForm).toBean(HashMap.class);
        return R.ok().put("page", meetingRoomService.searchMeetingRoomByPage(param));
    }

    @GetMapping("/searchById")
    @Operation(summary = "根据会议室id查询会议室信息")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestParam("meetingRoomId") Integer meetingRoomId) {
        if (meetingRoomId == null) {
            throw new EmosException("roleId不能为空");
        }
        if (meetingRoomId < 1) {
            throw new EmosException("roleId不能小于1");
        }
        return R.ok(meetingRoomService.searchById(meetingRoomId));
    }

    @PostMapping("/searchFreeMeetingRoom")
    @Operation(summary = "查询空闲会议室")
    @SaCheckLogin
    public R searchFreeMeetingRoom(@Valid @RequestBody SearchFreeMeetingRoomForm searchFreeMeetingRoomForm) {
        HashMap<String, String> param = JSONUtil.parse(searchFreeMeetingRoomForm).toBean(HashMap.class);
        return R.ok().put("list", meetingRoomService.searchFreeMeetingRoom(param));
    }

    @PostMapping("/update")
    @Operation(summary = "更新会议室信息")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R update(@Valid @RequestBody UpdateMeetingRoomForm updateMeetingRoomForm) {
        TbMeetingRoom tbMeetingRoom = JSONUtil.parse(updateMeetingRoomForm).toBean(TbMeetingRoom.class);
        return R.ok().put("rows", meetingRoomService.update(tbMeetingRoom));
    }

    @PostMapping("/insert")
    @Operation(summary = "新增会议室信息")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertMeetingRoomForm insertMeetingRoomForm) {
        TbMeetingRoom tbMeetingRoom = JSONUtil.parse(insertMeetingRoomForm).toBean(TbMeetingRoom.class);
        return R.ok().put("rows", meetingRoomService.insert(tbMeetingRoom));
    }

    @PostMapping("/deleteMeetingRoomByIds")
    @Operation(summary = "删除会议室信息")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteMeetingRoomByIds(@Valid @RequestBody DeleteMeetingRoomByIdsForm deleteMeetingRoomByIdsForm) {
        return R.ok().put("rows", meetingRoomService.deleteMeetingRoomByIds(deleteMeetingRoomByIdsForm.getIds()));
    }
}
