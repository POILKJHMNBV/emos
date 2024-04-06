package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbMeetingRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_meeting_room(会议室表)】的数据库操作Mapper
* @createDate 2023-05-24 21:22:34
* @Entity com.example.emos.db.pojo.TbMeetingRoom
*/
public interface TbMeetingRoomMapper extends BaseMapper<TbMeetingRoom> {

    /**
     * 分页查询会议室信息
     * @param param 查询条件
     * @return 该页的会议室信息
     */
    ArrayList<HashMap<String, Object>> searchMeetingRoomByPage(HashMap<String, Object> param);

    /**
     * 查询总的会议室数
     * @param param 查询条件
     * @return 总的会议室数
     */
    long searchMeetingRoomCount(HashMap<String, Object> param);

    /**
     * 新增会议室信息
     * @param tbMeetingRoom 会议室信息
     * @return 新增数据的条数
     */
    int insert(TbMeetingRoom tbMeetingRoom);

    /**
     * 根据id查询会议室信息
     * @param meetingRoomId 会议室id
     * @return 会议室信息
     */
    HashMap<String, Object> searchById(int meetingRoomId);

    /**
     * 更新会议室信息
     * @param tbMeetingRoom 会议室信息
     * @return 更新的记录条数
     */
    int update(TbMeetingRoom tbMeetingRoom);

    /**
     * 查询会议室是否有关联线下会议
     * @param meetingRoomIds 会议室id
     * @return 会议室是否有关联线下会议
     */
    boolean searchMeetingRoomIfExistRelativeMeeting(Integer[] meetingRoomIds);

    /**
     * 删除会议室信息
     * @param meetingRoomIds 会议室id
     * @return 删除的记录条数
     */
    int deleteMeetingRoomByIds(Integer[] meetingRoomIds);

    /**
     * 查询空闲会议室
     * @param param 查询条件
     * @return 空闲会议室
     */
    ArrayList<String> searchFreeMeetingRoom(HashMap<String, String> param);
}




