package com.example.emos.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.db.dao.TbLeaveMapper;
import com.example.emos.db.dao.TbUserMapper;
import com.example.emos.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@PropertySource("classpath:workflow.properties")
public class LeaveWorkflowTask {
    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.leaveWorkflowPath}")
    private String startLeavePath;

    @Value("${workflow.leaveApprovalReceiveNotifyUrl}")
    private String receiveNotifyUrl;

    @Value("${workflow.deleteProcessPath}")
    private String deleteLeavePath;

    @Resource
    private TbUserMapper tbUserMapper;

    @Resource
    private TbLeaveMapper tbLeaveMapper;

    @Async("AsyncTaskExecutor")
    public void startLeaveWorkflow(int leaveId, int creatorId, String days) {
        HashMap<String, Object> userInfo = tbUserMapper.searchUserInfo(creatorId);
        String creatorName = userInfo.get("name").toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("notifyUrl", receiveNotifyUrl);
        jsonObject.set("creatorId", creatorId);
        jsonObject.set("creatorName", creatorName);
        jsonObject.set("title", userInfo.get("deptName").toString() + creatorName + "的请假申请");
        jsonObject.set("managerId", tbUserMapper.searchDeptManagerId(creatorId));
        jsonObject.set("gmId", tbUserMapper.searchGmId());
        jsonObject.set("days", Double.parseDouble(days));

        String url = workflowUrl + startLeavePath;
        HttpResponse httpResponse = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() == 200) {
            JSONObject responseJson = JSONUtil.parseObj(httpResponse.body());
            String instanceId = responseJson.getStr("instanceId");
            Map<String, Object> map = Map.of("leaveId", leaveId, "instanceId", instanceId);
            int rows = tbLeaveMapper.updateLeaveInstanceId(map);
            if (rows != 1) {
                throw new EmosException("保存请假工作流实例ID失败");
            }
        } else {
            log.error(httpResponse.body());
        }
    }

    @Async("AsyncTaskExecutor")
    public void deleteLeaveWorkflow(String instanceId, String type, String reason) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("instanceId", instanceId);
        jsonObject.set("reason", reason);
        jsonObject.set("type", type);
        String url = workflowUrl + deleteLeavePath;
        HttpResponse httpResponse = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() == 200) {
            log.info("请假申请删除成功");
        } else {
            log.error(httpResponse.body());
        }
    }
}
