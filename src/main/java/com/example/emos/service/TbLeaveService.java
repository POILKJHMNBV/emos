package com.example.emos.service;

import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbLeave;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_leave】的数据库操作Service
* @createDate 2023-07-01 09:43:24
*/
public interface TbLeaveService extends IService<TbLeave> {

    /**
     * 查询请假分页数据
     * @param param 查询条件
     * @return 请假分页数据
     */
    Page searchLeaveByPage(HashMap<String, Object> param);

    /**
     * 根据id查询请假信息
     * @param param 查询条件
     * @return 请假信息
     */
    HashMap<String, Object> searchLeaveById(HashMap<String, Object> param);

    /**
     * 查询添加的请假记录与已有的请假记录是否冲突
     * @param param 查询条件
     * @return 冲突:true  不冲突:false
     */
    boolean searchContradiction(Map<String, Object> param);

    /**
     * 添加新的请假记录
     * @param leave 请求记录
     * @return 添加成功的请假条数
     */
    int insert(TbLeave leave);

    /**
     * 删除请假申请
     * @param param 删除条件
     * @return 删除是否成功
     */
    int deleteLeaveById(HashMap<String, Object> param);
}
