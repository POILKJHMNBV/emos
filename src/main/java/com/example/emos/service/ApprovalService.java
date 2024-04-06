package com.example.emos.service;

import com.example.emos.common.util.Page;

import java.util.HashMap;

public interface ApprovalService {

    /**
     * 查询会议申请分页数据
     * @param param 查询条件
     * @return 会议申请分页数据
     */
    Page searchTaskByPage(HashMap<String, Object> param);

    /**
     * 查询审批任务详情信息
     * @param param 查询条件
     * @return 审批任务详情信息
     */
    HashMap<String, Object> searchApprovalContent(HashMap<String, Object> param);

    /**
     * 审批任务
     * @param param 工作流实例id 审批结果(同意|不同意)
     */
    void approvalTask(HashMap<String, Object> param);

    /**
     * 归档任务
     * @param param 请求参数
     */
    void archiveTask(HashMap<String, Object> param);

    /**
     * 查询用户的审批任务数目
     * @param userId 用户id
     * @return 用户的审批任务数目
     */
    long searchApprovalTaskCount(int userId);
}
