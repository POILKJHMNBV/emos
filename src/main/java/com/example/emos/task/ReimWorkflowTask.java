package com.example.emos.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.db.dao.TbReimMapper;
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
public class ReimWorkflowTask {

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.reimWorkflowPath}")
    private String startReimPath;

    @Value("${workflow.deleteProcessPath}")
    private String deleteReimPath;

    @Value("${workflow.reimApprovalReceiveNotifyUrl}")
    private String receiveNotifyUrl;

    @Resource
    private TbUserMapper tbUserMapper;

    @Resource
    private TbReimMapper tbReimMapper;

    @Async("AsyncTaskExecutor")
    public void startReimWorkflow(int reimId, int creatorId) {
        HashMap<String, Object> userInfo = tbUserMapper.searchUserInfo(creatorId);
        String creatorName = userInfo.get("name").toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("creatorId", creatorId);
        jsonObject.set("creatorName", creatorName);
        jsonObject.set("title", userInfo.get("deptName").toString() + creatorName + "的报销申请");
        jsonObject.set("gmId", tbUserMapper.searchGmId());
        jsonObject.set("managerId", tbUserMapper.searchDeptManagerId(creatorId));
        jsonObject.set("notifyUrl", receiveNotifyUrl);

        String url = workflowUrl + startReimPath;
        HttpResponse httpResponse = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() == 200) {
            JSONObject responseJson = JSONUtil.parseObj(httpResponse.body());
            String instanceId = responseJson.getStr("instanceId");
            Map<String, Object> map = Map.of("reimId", reimId, "instanceId", instanceId);
            int rows = tbReimMapper.updateReimInstanceId(map);
            if (rows != 1) {
                throw new EmosException("保存报销工作流实例ID失败");
            }
        } else {
            log.error(httpResponse.body());
        }
    }

    @Async("AsyncTaskExecutor")
    public void deleteLeaveWorkflow(String instanceId, String type, String reason) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("reason", reason);
        jsonObject.set("instanceId", instanceId);
        jsonObject.set("type", type);
        String url = workflowUrl + deleteReimPath;
        HttpResponse httpResponse = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() == 200) {
            log.info("报销申请删除成功");
        } else {
            log.error(httpResponse.body());
        }
    }
}
