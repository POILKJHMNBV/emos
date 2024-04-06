package com.example.emos.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbMeeting;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbMeetingService;
import com.example.emos.db.dao.TbMeetingMapper;
import com.example.emos.task.MeetingWorkflowTask;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_meeting(会议表)】的数据库操作Service实现
* @createDate 2023-06-04 10:06:35
*/
@Service
public class TbMeetingServiceImpl extends ServiceImpl<TbMeetingMapper, TbMeeting>
    implements TbMeetingService {

    @Resource
    private TbMeetingMapper tbMeetingMapper;

    @Resource
    private MeetingWorkflowTask meetingWorkflowTask;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Page searchOfflineMeetingByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        long count = tbMeetingMapper.searchOfflineMeetingByPageCount(param);
        ArrayList<HashMap<String, Object>> list = tbMeetingMapper.searchOfflineMeetingByPage(param);
        for (HashMap<String, Object> map : list) {
            String meeting = (String) map.get("meeting");
            if (meeting != null && !meeting.isEmpty()) {
                map.replace("meeting", JSONUtil.parseArray(meeting));
            }
        }
        return new Page(page, length, count, list);
    }

    @Override
    public Page searchOnlineMeetingByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        long count = tbMeetingMapper.searchOnlineMeetingByPageCount(param);
        ArrayList<HashMap<String, Object>> list = tbMeetingMapper.searchOnlineMeetingByPage(param);
        return new Page(page, length, count, list);
    }

    @Override
    public int insert(TbMeeting meeting) {
        // 添加会议信息
        int rows = tbMeetingMapper.insert(meeting);
        if (rows != 1) {
            throw new EmosException("会议添加失败");
        }

        // 向工作流系统发送请求创建会议工作流实例
        meetingWorkflowTask.startMeetingWorkflow(meeting.getUuid(),
                meeting.getCreatorId(),
                meeting.getTitle(),
                meeting.getDate(),
                meeting.getStart() + ":00",
                meeting.getType() == 1 ? "线上会议" : "线下会议");
        return rows;
    }

    @Override
    public ArrayList<HashMap<String, Object>> searchOfflineMeetingInWeek(Map<String, Object> param) {
        return tbMeetingMapper.searchOfflineMeetingInWeek(param);
    }

    @Override
    public HashMap<String, Object> searchMeetingInfo(short status, long id) {
        //正在进行和已经结束的会议都可以查询present和unpresent字段
        if (status == 4||status==5) {
            return tbMeetingMapper.searchCurrentMeetingInfo(id);
        } else {
            return tbMeetingMapper.searchMeetingInfo(id);
        }
    }

    @Override
    public int deleteMeetingApplication(HashMap<String, Object> param) {
        // 1.距离会议开始时间不足20分钟的会议不能删除
        long id = (long) param.get("id");
        QueryWrapper<TbMeeting> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("creator_id", "date", "start", "`status`");
        queryWrapper.eq("id", id);
        TbMeeting meeting = tbMeetingMapper.selectOne(queryWrapper);
        DateTime startTime = DateUtil.parse(meeting.getDate() + " "+ meeting.getStart());
        if (DateUtil.date().isAfterOrEquals(startTime.offset(DateField.MINUTE, -20))) {
            throw new EmosException("距离会议开始时间不足20分钟，不能删除会议");
        }

        // 2.只有会议申请人才可以删除会议
        int userId = (int) param.get("userId");
        if (userId != meeting.getCreatorId()) {
            throw new EmosException("只有该会议申请者才可以删除会议");
        }

        // 3.只有待审批和未开始的会议可以删除
        Integer status = meeting.getStatus();
        if (status == 1 || status == 3) {
            QueryWrapper<TbMeeting> meetingQueryWrapper = new QueryWrapper<>();
            meetingQueryWrapper.eq("id", id).eq("creator_id", userId);

            // 删除数据库中的会议信息
            int rows = tbMeetingMapper.delete(meetingQueryWrapper);

            // 删除会议的工作流实例
            meetingWorkflowTask.deleteMeetingApplication(
                    (String) param.get("uuid"),
                    (String) param.get("instanceId"),
                    (String) param.get("reason"));
            return rows;
        } else {
            throw new EmosException("只有待审批和未开始的会议可以删除");
        }
    }

    @Override
    public Long searchRoomIdByUUID(String uuid) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(uuid))) {
            String roomId = stringRedisTemplate.opsForValue().get(uuid);
            if (!StrUtil.isEmpty(roomId)) {
                return Long.parseLong(roomId);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public ArrayList<HashMap<String, Object>> searchOnlineMeetingMembers(HashMap<String, Integer> param) {
        return tbMeetingMapper.searchOnlineMeetingMembers(param);
    }

    @Override
    public int updateMeetingPresent(Integer meetingId, Integer userId) {
        Map<String, Integer> param = Map.of("meetingId", meetingId, "userId", userId);
        if (tbMeetingMapper.searchCanCheckinMeeting(param) == 1) {
            return tbMeetingMapper.updateMeetingPresent(param);
        }
        return 0;
    }
}