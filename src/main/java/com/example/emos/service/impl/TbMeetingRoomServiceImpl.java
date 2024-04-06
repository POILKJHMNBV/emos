package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbMeetingRoom;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbMeetingRoomService;
import com.example.emos.db.dao.TbMeetingRoomMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_meeting_room(会议室表)】的数据库操作Service实现
* @createDate 2023-05-24 21:22:34
*/
@Service
public class TbMeetingRoomServiceImpl extends ServiceImpl<TbMeetingRoomMapper, TbMeetingRoom>
    implements TbMeetingRoomService {

    @Resource
    private TbMeetingRoomMapper tbMeetingRoomMapper;

    @Override
    public Page searchMeetingRoomByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        long count = tbMeetingRoomMapper.searchMeetingRoomCount(param);
        ArrayList<HashMap<String, Object>> list = tbMeetingRoomMapper.searchMeetingRoomByPage(param);
        return new Page(page, length, count, list);
    }

    @Override
    public HashMap<String, Object> searchById(int meetingRoomId) {
        return tbMeetingRoomMapper.searchById(meetingRoomId);
    }

    @Override
    public ArrayList<String> searchFreeMeetingRoom(HashMap<String, String> param) {
        return tbMeetingRoomMapper.searchFreeMeetingRoom(param);
    }

    @Override
    public int update(TbMeetingRoom tbMeetingRoom) {
        return tbMeetingRoomMapper.update(tbMeetingRoom);
    }

    @Override
    public int insert(TbMeetingRoom tbMeetingRoom) {
        String roomName = tbMeetingRoom.getName();
        synchronized (roomName.intern()) {
            long count = query().eq("name", roomName).count();
            if (count > 0) {
                throw new EmosException("会议室已经存在");
            }
        }
        return tbMeetingRoomMapper.insert(tbMeetingRoom);
    }

    @Override
    public int deleteMeetingRoomByIds(Integer[] meetingRoomIds) {
        if (tbMeetingRoomMapper.searchMeetingRoomIfExistRelativeMeeting(meetingRoomIds)) {
            throw new EmosException("无法删除关联会议的会议室");
        }
        return tbMeetingRoomMapper.deleteMeetingRoomByIds(meetingRoomIds);
    }
}




