package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbRole;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbRoleService;
import com.example.emos.db.dao.TbRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_role(角色表)】的数据库操作Service实现
* @createDate 2023-05-16 22:09:53
*/
@Service
public class TbRoleServiceImpl extends ServiceImpl<TbRoleMapper, TbRole>
    implements TbRoleService {

    @Resource
    private TbRoleMapper tbRoleMapper;

    @Override
    public Page searchRoleByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);

        long roleCount = tbRoleMapper.searchRoleCount(param);
        ArrayList<HashMap<String, Object>> list = tbRoleMapper.searchRoleByPage(param);
        return new Page(page, length, roleCount, list);
    }

    @Override
    public int insert(TbRole tbRole) {
        String roleName = tbRole.getRoleName();
        synchronized (roleName.intern()) {
            long count = query().eq("role_name", roleName).count();
            if (count > 0) {
                throw new EmosException("角色名已存在");
            }
        }
        return tbRoleMapper.insert(tbRole);
    }

    @Override
    public HashMap<String, Object> searchById(Integer roleId) {
        return tbRoleMapper.searchById(roleId);
    }

    @Override
    public ArrayList<Integer> searchUserIdByRoleId(int roleId) {
        return tbRoleMapper.searchUserIdByRoleId(roleId);
    }

    @Override
    public int update(TbRole tbRole) {
        return tbRoleMapper.update(tbRole);
    }

    @Override
    public int deleteRoleByIds(Integer[] roleIds) {
        // 1.查询指定角色是否有关联的未离职用户
        boolean result = tbRoleMapper.searchRoleIfExistRelativeUser(roleIds);
        if (result) {
            throw new EmosException("无法删除关联用户的角色");
        }

        // 2.删除角色信息
        return tbRoleMapper.deleteRoleByIds(roleIds);
    }
}




