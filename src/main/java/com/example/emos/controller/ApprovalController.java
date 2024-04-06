package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.approval.ApprovalTaskForm;
import com.example.emos.controller.form.approval.ArchiveTaskForm;
import com.example.emos.controller.form.approval.SearchApprovalContentForm;
import com.example.emos.controller.form.approval.SearchTaskByPageForm;
import com.example.emos.exception.EmosException;
import com.example.emos.service.ApprovalService;
import com.example.emos.service.TbUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@RestController
@RequestMapping("/approval")
@PropertySource({"classpath:workflow.properties"})
@Tag(name = "ApprovalController", description = "任务审批Web接口")
@Slf4j
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    @Resource
    private TbUserService userService;

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.searchApprovalBpmn}")
    private String searchApprovalBpmnPath;

    @PostMapping("/searchTaskByPage")
    @Operation(summary = "查询分页任务列表")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchTaskByPage(@Valid @RequestBody SearchTaskByPageForm searchTaskByPageForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchTaskByPageForm).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        param.put("userId", userId);
        param.put("role", userService.searchUserRoles(userId));
        return R.ok().put("page", approvalService.searchTaskByPage(param));
    }

    @PostMapping("/searchApprovalContent")
    @Operation(summary = "查询审批任务详情信息")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm searchApprovalContentForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchApprovalContentForm).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        param.put("userId", userId);
        param.put("role", userService.searchUserRoles(userId));
        return R.ok().put("content", approvalService.searchApprovalContent(param));
    }

    @GetMapping("/searchApprovalBpmn")
    @Operation(summary = "获取BPMN图")
    public void searchApprovalBpmn(@RequestParam("instanceId") String instanceId, HttpServletResponse httpServletResponse) {
        if (StrUtil.isBlankIfStr(instanceId)) {
            throw new EmosException("instanceId不能为空");
        }
        String url = workflowUrl + searchApprovalBpmnPath;
        HttpResponse response = HttpRequest.get(url + "?instanceId=" + instanceId).execute();
        if (response.getStatus() == 200) {
            InputStream inputStream = response.bodyStream();
            try {
                IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
            } catch (IOException e) {
                log.error("Get BPMN image failure!", e);
            }
        } else {
            log.error("Get BPMN image failure!");
            throw new EmosException("获取工作流BPMN失败");
        }
    }

    @PostMapping("/approvalTask")
    @Operation(summary = "审批任务")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL"}, mode = SaMode.OR)
    @EmosLog
    public R approvalTask(@Valid @RequestBody ApprovalTaskForm approvalTaskForm) {
        HashMap<String, Object> param = JSONUtil.parse(approvalTaskForm).toBean(HashMap.class);
        approvalService.approvalTask(param);
        return R.ok();
    }

    @PostMapping("/archiveTask")
    @Operation(summary = "归档任务")
    @SaCheckPermission(value = {"ROOT", "FILE:ARCHIVE"}, mode = SaMode.OR)
    @EmosLog
    public R archiveTask(@Valid @RequestBody ArchiveTaskForm archiveTaskForm) {
        if (!JSONUtil.isJsonArray(archiveTaskForm.getFiles())) {
            return R.error("files不是JSON数组");
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("taskId", archiveTaskForm.getTaskId());
        param.put("files", archiveTaskForm.getFiles());
        param.put("userId", StpUtil.getLoginIdAsInt());
        approvalService.archiveTask(param);
        return R.ok();
    }
}
