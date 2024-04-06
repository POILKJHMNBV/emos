package com.example.emos.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@PropertySource("classpath:workflow.properties")
public class UserWorkflowTask {

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.turnTaskPath}")
    private String turnTaskPath;

    @Async("AsyncTaskExecutor")
    public void turnTask(int userId, int assignId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("userId", userId);
        jsonObject.set("assignId", assignId);

        String url = workflowUrl + turnTaskPath;
        HttpResponse httpResponse = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(jsonObject.toString()).execute();
        if (httpResponse.getStatus() != 200) {
            log.error(httpResponse.body());
        }
    }
}
