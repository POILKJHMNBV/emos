package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.Page;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.reim.InsertReimForm;
import com.example.emos.controller.form.reim.SearchReimByPageForm;
import com.example.emos.db.pojo.TbReim;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbReimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/reim")
@Tag(name = "ReimController", description = "报销模块Web接口")
@Slf4j
public class ReimController {

    @Resource
    private TbReimService reimService;

    @PostMapping("/searchReimByPage")
    @Operation(summary = "查询报销分页记录")
    @SaCheckPermission(value = {"ROOT", "REIM:SELECT"}, mode = SaMode.OR)
    public R searchReimByPage(@Valid @RequestBody SearchReimByPageForm searchReimByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchReimByPageForm).toBean(HashMap.class);
        param.put("currentUserId", StpUtil.getLoginIdAsInt());
        int loginId = StpUtil.getLoginIdAsInt();
        List<String> roleList = StpUtil.getRoleList(loginId);
        if (!roleList.contains("总经理") &&
            !roleList.contains("超级管理员") &&
                !roleList.contains("财务")) {
            param.put("userId", loginId);
        }
        Page page = reimService.searchReimByPage(param);
        return R.ok().put("page", page);
    }

    @GetMapping("/searchReimById")
    @Operation(summary = "根据ID查询报销单")
    @SaCheckPermission(value = {"ROOT", "REIM:SELECT"}, mode = SaMode.OR)
    public R searchReimById(@RequestParam("reimId") Integer reimId) {
        if (reimId == null || reimId < 1) {
            throw new EmosException("reimId格式不正确");
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("reimId", reimId);
        if (!(StpUtil.hasPermission("REIM:SELECT") || StpUtil.hasPermission("ROOT"))) {
            param.put("userId", StpUtil.getLoginIdAsInt());
        }
        HashMap<String, Object> map = reimService.searchReimById(param);
        return R.ok(map);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加报销申请")
    @SaCheckPermission(value = {"ROOT", "REIM:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@Valid @RequestBody InsertReimForm insertReimForm) {
        if (!JSONUtil.isJsonArray(insertReimForm.getContent())) {
            return R.error("content不是JSON数组");
        }
        TbReim tbReim = new TbReim();
        tbReim.setContent(insertReimForm.getContent());
        tbReim.setAmount(insertReimForm.getAmount());
        tbReim.setAnleihen(insertReimForm.getAnleihen());
        tbReim.setBalance(insertReimForm.getBalance());
        tbReim.setTypeId(insertReimForm.getTypeId());
        tbReim.setUserId(StpUtil.getLoginIdAsInt());
        tbReim.setStatus(1);
        return R.ok().put("rows", reimService.insert(tbReim));
    }

    @GetMapping("/deleteReimById")
    @Operation(summary = "删除报销申请")
    @SaCheckPermission(value = {"ROOT", "REIM:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteReimById(@RequestParam("reimId") Integer reimId) {
        if (reimId == null || reimId < 1) {
            throw new EmosException("reimId格式不正确");
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("reimId", reimId);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("rows", reimService.deleteReimById(param));
    }

    @GetMapping("/receiveNotify")
    @Operation(summary = "接收报销申请的审批结果")
    public R receiveNotify(@RequestParam("title") String title,
                           @RequestParam("result") String result) {
        if ("同意".equals(result)) {
            log.info("{}的审批结果为：{}", title, result);
        } else {
            log.error("{}的审批结果为：{}", title, result);
        }
        return R.ok();
    }
}