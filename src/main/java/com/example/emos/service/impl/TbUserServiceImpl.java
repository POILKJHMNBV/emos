package com.example.emos.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbUser;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbUserService;
import com.example.emos.db.dao.TbUserMapper;
import com.example.emos.task.UserWorkflowTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author zhenwu
* @description 针对表【tb_user(用户表)】的数据库操作Service实现
* @createDate 2023-05-14 21:19:21
*/
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser>
    implements TbUserService{

    @Resource
    private TbUserMapper tbUserMapper;

    @Resource
    private UserWorkflowTask userWorkflowTask;

    @Override
    public Set<String> searchUserPermissions(long userId) {
        return tbUserMapper.searchUserPermissions(userId);
    }

    @Override
    public Integer login(HashMap<String, String> param) {
        return tbUserMapper.login(param);
    }

    @Override
    public Integer updatePassword(HashMap<String, Object> param) {
        return tbUserMapper.updatePassword(param);
    }

    @Override
    public Page searchUserByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);

        long totalCount = tbUserMapper.searchUserCount(param);
        List<HashMap<String, Object>> list = tbUserMapper.searchUserByPage(param);
        return new Page(page, length, totalCount, list);
    }

    @Override
    public int insert(TbUser user) {
        String username = user.getUsername();
        synchronized (username.intern())  {
            long count = this.query().eq("username", username).count();
            if (count > 0) {
                throw new EmosException("用户名已经存在");
            }
        }
        return tbUserMapper.insert(user);
    }

    @Override
    public HashMap<String, Object> searchById(Integer userId) {
        return tbUserMapper.searchById(userId);
    }

    @Override
    public HashMap<String, Object> searchNameAndDept(Integer userId) {
        return tbUserMapper.searchNameAndDept(userId);
    }

    @Override
    public int update(TbUser user) {
        return tbUserMapper.update(user);
    }

    @Override
    public int deleteUserByIds(Integer[] ids) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", Arrays.asList(ids));
        queryWrapper.eq("status", "1");
        Long count = tbUserMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("无法删除在职的用户");
        }
        return tbUserMapper.deleteUserByIds(ids);
    }

    @Override
    public JSONArray searchUserGroupByDept(Integer userId) {
        ArrayList<HashMap<String, Object>> list = tbUserMapper.searchUserGroupByDept(userId);
        JSONArray result = new JSONArray();
        Set<Object> deptIds = list.stream().map(e -> e.get("deptId")).collect(Collectors.toSet());
        for (Object deptId : deptIds) {
            JSONObject dept = new JSONObject();
            dept.set("deptId", deptId);

            List<HashMap<String, Object>> deptUserList = list.stream().filter(e -> deptId.equals(e.get("deptId"))).collect(Collectors.toList());
            dept.set("deptName", deptUserList.get(0).get("deptName"));
            JSONArray users = new JSONArray();
            for (HashMap<String, Object> deptUser : deptUserList) {
                JSONObject user = new JSONObject();
                user.set("userId", deptUser.get("userId"));
                user.set("name", deptUser.get("name"));
                users.put(user);
            }
            dept.set("users", users);
            result.put(dept);
        }
        return result;
    }

    @Override
    public Set<String> searchUserRoles(int userId) {
        return tbUserMapper.searchUserRoles(userId);
    }

    @Override
    public int dismiss(int userId, int assignId) {
        TbUser tbUser = new TbUser();
        tbUser.setId(userId);
        tbUser.setStatus(2);
        int rows = tbUserMapper.update(tbUser);
        if (rows != 1) {
            throw new EmosException("设置员工离职状态失败");
        }
        userWorkflowTask.turnTask(userId, assignId);
        return rows;
    }
}