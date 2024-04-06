package com.example.emos.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.common.util.Page;
import com.example.emos.exception.EmosException;
import com.example.emos.service.ApprovalService;
import com.example.emos.service.TbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Slf4j
@PropertySource({"classpath:workflow.properties"})
public class ApprovalServiceImpl implements ApprovalService {

    @Resource
    private TbUserService tbUserService;

    @Value("${workflow.url}")
    private String workflowUrl;

    @Value("${workflow.searchTaskByPage}")
    private String searchTaskByPagePath;

    @Value("${workflow.searchApprovalContent}")
    private String searchApprovalContentPath;

    @Value("${workflow.approvalTaskPath}")
    private String approvalTaskPath;

    @Value("${workflow.archiveTaskPath}")
    private String archiveTaskPath;

    @Value("${workflow.searchApprovalTaskCountPath}")
    private String searchApprovalTaskCountPath;

    @Override
    public Page searchTaskByPage(HashMap<String, Object> param) {
        String url = workflowUrl + searchTaskByPagePath;
        HttpResponse httpResponse = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(param)).execute();
        if (httpResponse.getStatus() == 200) {
            JSONObject jsonObject = JSONUtil.parseObj(httpResponse.body());
            JSONObject page = jsonObject.getJSONObject("page");
            ArrayList<JSONObject> list = page.get("list", ArrayList.class);
            ArrayList<HashMap<String, Object>> dataList = new ArrayList<>(list.size());
            for (JSONObject json : list) {
                HashMap<String, Object> map = JSONUtil.parse(json).toBean(HashMap.class);
                dataList.add(map);
            }
            Long totalCount = page.getLong("totalCount");
            Integer pageIndex = page.getInt("pageIndex");
            Integer pageSize = page.getInt("pageSize");
            return new Page(pageIndex, pageSize, totalCount, dataList);
        } else {
            log.error(httpResponse.body());
            throw new EmosException("获取工作流数据异常");
        }
    }

    @Override
    public HashMap<String, Object> searchApprovalContent(HashMap<String, Object> param) {
        String url = workflowUrl + searchApprovalContentPath;
        HttpResponse response = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(param)).execute();
        if (response.getStatus() == 200) {
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            HashMap<String, Object> content = jsonObject.get("content", HashMap.class);
            return content;
        } else {
            log.error(response.body());
            throw new EmosException("获取工作流数据异常");
        }
    }

    @Override
    public void approvalTask(HashMap<String, Object> param) {
        String url = workflowUrl + approvalTaskPath;
        HttpResponse response = HttpRequest
                .post(url)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(param)).execute();
        if (response.getStatus() != 200) {
            log.error(response.body());
            throw new EmosException("获取工作流数据异常");
        }
    }

    @Override
    public void archiveTask(HashMap<String, Object> param) {
        String url = workflowUrl + archiveTaskPath;
        HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(param)).execute();
        if (resp.getStatus() != 200) {
            log.error(resp.body());
            throw new EmosException("调用工作流审批异常");
        }
    }

    @Override
    public long searchApprovalTaskCount(int userId) {
        long count = tbUserService.query()
                .eq("id", userId).eq("`status`", 1).count();
        if (count == 0) {
            return 0;
        }
        String url = workflowUrl + searchApprovalTaskCountPath + "?userId=" + userId;
        HttpResponse resp = HttpRequest.get(url).execute();
        if (resp.isOk()) {
            JSONObject jsonObject = JSONUtil.parseObj(resp.body());
            Long approvalTaskCount = jsonObject.get("count", Long.class);
            if (approvalTaskCount == null) {
                return 0;
            }
            return approvalTaskCount;
        }
        return 0;
    }
}