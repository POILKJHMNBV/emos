package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbMeeting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
* @createDate 2023-06-04 10:06:34
* @Entity com.example.emos.db.pojo.TbMeeting
*/
public interface TbMeetingMapper extends BaseMapper<TbMeeting> {

    /**
     * 分页查询线下会议信息
     * @param param 查询条件
     * @return 该页线下会议信息
     */
    ArrayList<HashMap<String, Object>> searchOfflineMeetingByPage(HashMap<String, Object> param);

    /**
     * 查询线下会议数量
     * @param param 查询条件
     * @return 线下会议数量
     */
    long searchOfflineMeetingByPageCount(HashMap<String, Object> param);


    /**
     * 分页查询线上会议信息
     * @param param 查询条件
     * @return 该页线上会议信息
     */
    ArrayList<HashMap<String, Object>> searchOnlineMeetingByPage(HashMap<String, Object> param);

    /**
     * 查询线上会议数量
     * @param param 查询条件
     * @return 线上会议数量
     */
    long searchOnlineMeetingByPageCount(HashMap<String, Object> param);

    /**
     * 查询会议参会者是否在同一部门
     * @param uuid 会议uuid
     * @return 参会者在同一部门-true，参会者不在同一部门-false
     */
    boolean searchMeetingMembersInSameDept(String uuid);

    /**
     * 更新会议记录的instance_id
     * @param param 更新参数
     * @return 更新的记录条数
     */
    int updateMeetingInstanceId(HashMap<String, String> param);

    /**
     * 新增会议信息
     * @param meeting 会议信息
     * @return 新增的记录条数
     */
    int insert(TbMeeting meeting);

    /**
     * 查询从某个日期开始某个会议室一周内的会议信息
     * @param param 查询条件
     * @return 从当天开始一周内的会议信息
     */
    ArrayList<HashMap<String, Object>> searchOfflineMeetingInWeek(Map<String, Object> param);

    /**
     * 查询会议详情信息
     * @param id 会议id
     * @return 会议详情信息
     */
    HashMap<String, Object> searchMeetingInfo(long id);

    /**
     * 查询当前正在进行会议的详情信息
     * @param id 会议id
     * @return 当前正在进行会议的详情信息
     */
    HashMap<String, Object> searchCurrentMeetingInfo(long id);

    /**
     * 查询线上会议的参会人员
     * @param param 查询条件
     * @return 线上会议的参会人员
     */
    ArrayList<HashMap<String, Object>> searchOnlineMeetingMembers(HashMap<String, Integer> param);

    /**
     * 判断会议是否可以签到
     * @param param 查询条件
     * @return 是否可以签到
     */
    long searchCanCheckinMeeting(Map<String, Integer> param);

    /**
     * 进行会议签到
     * @param param 请求参数
     * @return 签到是否成功
     */
    int updateMeetingPresent(Map<String, Integer> param);
}




