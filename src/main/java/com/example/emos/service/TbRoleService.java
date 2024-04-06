package com.example.emos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbRole;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_role(角色表)】的数据库操作Service
* @createDate 2023-05-16 22:09:53
*/
public interface TbRoleService extends IService<TbRole> {

    /**
     * 分页查询角色信息
     * @param param 查询条件
     * @return 该页的角色信息
     */
    Page searchRoleByPage(HashMap<String, Object> param);

    /**
     * 新增角色
     * @param tbRole 角色信息
     * @return 新增的数据条数
     */
    int insert(TbRole tbRole);

    /**
     * 根据角色id查询角色信息
     * @param roleId 角色id
     * @return 角色信息
     */
    HashMap<String, Object> searchById(Integer roleId);

    /**
     * 查询某一角色的所有用户信息
     * @param roleId 角色id
     * @return 具备该角色的所有用户信息
     */
    ArrayList<Integer> searchUserIdByRoleId(int roleId);

    /**
     * 更改角色信息
     * @param tbRole 角色信息
     * @return 更改的记录条数
     */
    int update(TbRole tbRole);

    /**
     * 根据角色id批量删除角色信息
     * @param roleIds 角色id数组
     * @return 删除的记录条数
     */
    int deleteRoleByIds(Integer[] roleIds);
}
