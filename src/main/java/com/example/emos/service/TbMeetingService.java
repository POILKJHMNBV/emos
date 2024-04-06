package com.example.emos.service;

import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbMeeting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_meeting(会议表)】的数据库操作Service
* @createDate 2023-06-04 10:06:35
*/
public interface TbMeetingService extends IService<TbMeeting> {

    /**
     * 分页查询线下会议信息
     * @param param 查询条件
     * @return 该页线下会议信息
     */
    Page searchOfflineMeetingByPage(HashMap<String, Object> param);

    /**
     * 分页查询线上会议信息
     * @param param 查询条件
     * @return 该页线上会议信息
     */
    Page searchOnlineMeetingByPage(HashMap<String, Object> param);

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
    ArrayList<HashMap<String, Object>> searchOfflineMeetingInWeek(Map<String,Object> param);

    /**
     * 查询会议详情信息
     * @param id 会议id
     * @return 会议详情信息
     */
    HashMap<String, Object> searchMeetingInfo(short status, long id);

    /**
     * 删除会议申请
     * @param param 删除条件
     * @return 删除记录的条数
     */
    int deleteMeetingApplication(HashMap<String, Object> param);

    /**
     * 根据会议uuid查询线上会议室id
     * @param uuid 会议uuid
     * @return 线上会议室id
     */
    Long searchRoomIdByUUID(String uuid);

    /**
     * 查询线上会议的参会人员
     * @param param 查询条件
     * @return 线上会议的参会人员
     */
    ArrayList<HashMap<String, Object>> searchOnlineMeetingMembers(HashMap<String, Integer> param);

    /**
     * 进行会议签到
     * @param meetingId 会议id
     * @param userId 用户id
     * @return 签到是否成功
     */
    int updateMeetingPresent(Integer meetingId, Integer userId);
}
