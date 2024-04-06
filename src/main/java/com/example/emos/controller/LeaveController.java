package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.Page;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.leave.InsertLeaveForm;
import com.example.emos.controller.form.leave.SearchLeaveByPageForm;
import com.example.emos.db.pojo.TbLeave;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
@Tag(name = "LeaveController", description = "员工请假Web接口")
@Slf4j
public class LeaveController {

    @Resource
    private TbLeaveService leaveService;

    @PostMapping("/searchLeaveByPage")
    @Operation(summary = "查询请假分页数据")
    @SaCheckPermission(value = {"ROOT", "LEAVE:SELECT"}, mode = SaMode.OR)
    public R searchLeaveByPage(@Valid @RequestBody SearchLeaveByPageForm searchLeaveByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchLeaveByPageForm).toBean(HashMap.class);
        param.put("currentUserId", StpUtil.getLoginIdAsInt());
        int loginId = StpUtil.getLoginIdAsInt();
        List<String> roleList = StpUtil.getRoleList(loginId);
        if (!roleList.contains("总经理") && !roleList.contains("超级管理员")) {
            param.put("userId", loginId);
        }
        Page page = leaveService.searchLeaveByPage(param);
        return R.ok().put("page", page);
    }

    @GetMapping("/searchLeaveById")
    @Operation(summary = "根据Id查询请假数据")
    @SaCheckPermission(value = {"ROOT", "LEAVE:SELECT"}, mode = SaMode.OR)
    public R searchLeaveById(@RequestParam("leaveId") Integer leaveId) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("leaveId", leaveId);
        if (!(StpUtil.hasPermission("LEAVE:SELECT")||StpUtil.hasPermission("ROOT"))) {
            param.put("userId", StpUtil.getLoginIdAsInt());
        }
        return R.ok(leaveService.searchLeaveById(param));
    }

    @PostMapping("/insert")
    @Operation(summary = "添加请假记录")
    @SaCheckPermission(value = {"ROOT", "LEAVE:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertLeaveForm insertLeaveForm) {
        String startDateTime = insertLeaveForm.getStart();
        String endDateTime = insertLeaveForm.getEnd();
        DateTime parsesStartDateTime = DateUtil.parse(startDateTime);
        DateTime parseEndDateTime = DateUtil.parse(endDateTime);
        if (parsesStartDateTime.isAfterOrEquals(parseEndDateTime)) {
            return R.error("请假开始时间不能晚于或者等于截止时间");
        }
        int userId = StpUtil.getLoginIdAsInt();
        Map<String, Object> param = Map.of("userId", userId,
                "start", startDateTime,
                "end", endDateTime);
        if (leaveService.searchContradiction(param)) {
            return R.error("当前请假申请与已有请假申请冲突");
        }

        // 计算请假天数
        long hours = parsesStartDateTime.between(parseEndDateTime, DateUnit.HOUR);
        String days = new BigDecimal(hours).divide(new BigDecimal(24), 1, RoundingMode.CEILING).toString();
        if (days.contains(".0")) {
            days = days.replace(".0", "");
        }
        if (days.equals("0")) {
            days = "0.1";
        }
        TbLeave leave = new TbLeave();
        leave.setStart(startDateTime);
        leave.setEnd(endDateTime);
        leave.setType(insertLeaveForm.getType());
        leave.setReason(insertLeaveForm.getReason());
        leave.setUserId(StpUtil.getLoginIdAsInt());
        leave.setDays(days);
        return R.ok().put("rows", leaveService.insert(leave));
    }

    @GetMapping("/deleteLeaveById")
    @Operation(summary = "删除请假记录")
    @SaCheckPermission(value = {"ROOT", "LEAVE:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteLeaveById(@RequestParam("leaveId") Integer leaveId) {
        if (leaveId == null || leaveId < 1) {
            throw new EmosException("id格式不正确");
        }
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("leaveId", leaveId);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("rows", leaveService.deleteLeaveById(param));
    }

    @GetMapping("/receiveNotify")
    @Operation(summary = "接收请假申请的审批结果")
    public R receiveNotify(@RequestParam("title") String title,
                           @RequestParam("result") String result,
                           @RequestParam("days") String days) {
        if ("同意".equals(result)) {
            log.info("{}的审批结果为：{}，请假天数为{}", title, result, days);
        } else {
            log.error("{}的审批结果为：{}，请假天数为{}", title, result, days);
        }
        return R.ok();
    }
}