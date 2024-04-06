package com.example.emos.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.db.dao.TbMeetingMapper;
import com.example.emos.db.dao.TbUserMapper;
import com.example.emos.db.pojo.TbUser;
import com.example.emos.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@Component
@PropertySource("classpath:workflow.properties")
public class MeetingWorkflowTask {

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.meetingWorkflowPath}")
    private String startMeetingPath;

    @Value("${workflow.deleteProcessPath}")
    private String deleteMeetingPath;

    @Value("${workflow.meetingApprovalReceiveNotifyUrl}")
    private String receiveNotifyUrl;

    @Resource
    private TbUserMapper tbUserMapper;

    @Resource
    private TbMeetingMapper tbMeetingMapper;

    /**
     * 使用异步线程向工作流项目发送http请求创建会议工作流实例
     * @param uuid 会议的唯一标识，用于定时器名
     * @param creatorId 会议创建者id
     * @param title 会议主题
     * @param date 会议日期
     * @param start 会议开始时间
     * @param meetingType 会议类型
     */
    @Async("AsyncTaskExecutor")
    public void startMeetingWorkflow(String uuid,
                                     int creatorId,
                                     String title,
                                     String date,
                                     String start,
                                     String meetingType) {
        // 查询会议申请者的基本信息
        TbUser user = tbUserMapper.selectById(creatorId);
        JSONObject jsonObject = new JSONObject();
        String userRole = user.getRole().toString();
        if (!userRole.contains("1")) {
            // 会议申请者不是总经理
            // 查询部门经理id
            int deptManagerId = tbUserMapper.searchDeptManagerId(creatorId);
            jsonObject.set("managerId", deptManagerId);

            // 查询总经理id
            int gmId = tbUserMapper.searchGmId();
            jsonObject.set("gmId", gmId);

            // 查询参会人是否为同一部门
            boolean result = tbMeetingMapper.searchMeetingMembersInSameDept(uuid);
            jsonObject.set("sameDept", result);
        }
        jsonObject.set("uuid", uuid);
        jsonObject.set("creatorId", creatorId);
        jsonObject.set("creatorName", user.getName());
        jsonObject.set("title", title);
        jsonObject.set("notifyUrl", receiveNotifyUrl);
        jsonObject.set("date", date);
        jsonObject.set("start", start);
        jsonObject.set("meetingType", meetingType);

        // 向工作流项目发送http请求，请求创建会议工作流实例
        String url = workflowUrl + startMeetingPath;
        HttpResponse response = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (response.getStatus() == 200) {
            JSONObject responseJson = JSONUtil.parseObj(response.body());
            String instanceId = responseJson.getStr("instanceId");

            // 更新会议记录的instance_id
            HashMap<String, String> param = new HashMap<>(2);
            param.put("uuid", uuid);
            param.put("instanceId", instanceId);
            int rows = tbMeetingMapper.updateMeetingInstanceId(param);
            if (rows != 1) {
                throw new EmosException("保存会议工作流实例ID失败！");
            }
        } else {
            log.error(response.body());
        }
    }

    /**
     * 使用异步前线向工作流系统发送http请求删除会议工作流实例
     * @param uuid 会议的唯一标识
     * @param instanceId 会议工作流实例id
     * @param reason 删除会议原因
     */
    @Async("AsyncTaskExecutor")
    public void deleteMeetingApplication(String uuid, String instanceId, String reason) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("uuid", uuid);
        jsonObject.set("instanceId", instanceId);
        jsonObject.set("reason", reason);
        jsonObject.set("type", "会议申请");
        String url = workflowUrl + deleteMeetingPath;
        HttpResponse httpResponse = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() == 200) {
            log.info("会议申请删除成功");
        } else {
            log.error(httpResponse.body());
        }
    }
}
