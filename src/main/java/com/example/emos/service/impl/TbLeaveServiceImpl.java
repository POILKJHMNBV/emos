package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.dao.TbLeaveMapper;
import com.example.emos.db.pojo.TbLeave;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbLeaveService;
import com.example.emos.task.LeaveWorkflowTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_leave】的数据库操作Service实现
* @createDate 2023-07-01 09:43:24
*/
@Service
public class TbLeaveServiceImpl extends ServiceImpl<TbLeaveMapper, TbLeave>
    implements TbLeaveService {

    @Resource
    private TbLeaveMapper tbLeaveMapper;

    @Resource
    private LeaveWorkflowTask leaveWorkflowTask;

    @Override
    public Page searchLeaveByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        ArrayList<HashMap<String, Object>> list = tbLeaveMapper.searchLeaveByPage(param);
        long count = tbLeaveMapper.searchLeaveCount(param);
        return new Page(page, length, count, list);
    }

    @Override
    public HashMap<String, Object> searchLeaveById(HashMap<String, Object> param) {
        return tbLeaveMapper.searchLeaveById(param);
    }

    @Override
    public boolean searchContradiction(Map<String, Object> param) {
        return tbLeaveMapper.searchContradiction(param) > 0;
    }

    @Override
    public int insert(TbLeave leave) {
        int rows = tbLeaveMapper.insert(leave);
        if (rows == 1) {
            leaveWorkflowTask.startLeaveWorkflow(leave.getId(), leave.getUserId(), leave.getDays());
        } else {
            throw new EmosException("请假记录添加失败");
        }
        return rows;
    }

    @Override
    public int deleteLeaveById(HashMap<String, Object> param) {
        TbLeave leave = tbLeaveMapper.selectById((Integer) param.get("leaveId"));
        String instanceId = leave.getInstanceId();
        int rows = tbLeaveMapper.deleteLeaveById(param);
        if (rows == 1) {
            leaveWorkflowTask.deleteLeaveWorkflow(instanceId, "员工请假", "删除请假申请");
        } else {
            throw new EmosException("删除请假记录失败");
        }
        return rows;
    }
}




