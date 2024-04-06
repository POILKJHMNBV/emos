package com.example.emos.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.emos.db.dao.TbUserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private TbUserMapper tbUserMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        int userId = Integer.parseInt(loginId.toString());
        Set<String> permissions = tbUserMapper.searchUserPermissions(userId);
        return new ArrayList<>(permissions);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        int userId = Integer.parseInt(loginId.toString());
        Set<String> roles = tbUserMapper.searchUserRoles(userId);
        return new ArrayList<>(roles);
    }
}