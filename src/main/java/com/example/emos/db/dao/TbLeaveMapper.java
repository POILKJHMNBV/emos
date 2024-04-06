package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbLeave;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_leave】的数据库操作Mapper
* @createDate 2023-07-01 09:43:24
* @Entity com.example.emos.db.pojo.TbLeave
*/
public interface TbLeaveMapper extends BaseMapper<TbLeave> {

    /**
     * 查询请假分页数据
     * @param param 查询条件
     * @return 请假分页数据
     */
    ArrayList<HashMap<String, Object>> searchLeaveByPage(HashMap<String, Object> param);

    /**
     * 查询请假的数目
     * @param param 查询条件
     * @return 请假的数目
     */
    long searchLeaveCount(HashMap<String, Object> param);

    /**
     * 根据id查询请假信息
     * @param param 查询条件
     * @return 请假信息
     */
    HashMap<String, Object> searchLeaveById(HashMap<String, Object> param);

    /**
     * 查询添加的请假记录与已有的请假记录是否冲突
     * @param param 查询条件
     * @return 冲突的数目
     */
    long searchContradiction(Map<String, Object> param);

    /**
     * 根据请假记录id更新请假流程实例id
     * @param param 请假记录id, 请假流程实例id
     * @return 更新成功条数
     */
    int updateLeaveInstanceId(Map<String, Object> param);

    /**
     * 删除请假申请
     * @param param 删除条件
     * @return 删除是否成功
     */
    int deleteLeaveById(HashMap<String, Object> param);
}