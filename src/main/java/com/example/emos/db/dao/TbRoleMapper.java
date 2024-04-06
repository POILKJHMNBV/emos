package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_role(角色表)】的数据库操作Mapper
* @createDate 2023-05-16 22:09:53
* @Entity com.example.emos.db.pojo.TbRole
*/
public interface TbRoleMapper extends BaseMapper<TbRole> {

    /**
     * 分页查询角色信息
     * @param param 查询条件
     * @return 该页的角色信息
     */
    ArrayList<HashMap<String, Object>> searchRoleByPage(HashMap<String, Object> param);

    /**
     * 根据条件查询角色总数量
     * @param param 查询条件
     * @return 角色总数量
     */
    long searchRoleCount(HashMap<String, Object> param);

    /**
     * 根据角色id查询角色信息
     * @param roleId 角色id
     * @return 角色信息
     */
    HashMap<String, Object> searchById(int roleId);


    /**
     * 新增角色
     * @param tbRole 角色信息
     * @return 新增的数据条数
     */
    int insert(TbRole tbRole);


    /**
     * 更改角色信息
     * @param tbRole 角色信息
     * @return 更改的记录条数
     */
    int update(TbRole tbRole);

    /**
     * 查询某一角色的所有用户信息
     * @param roleId 角色id
     * @return 具备该角色的所有用户信息
     */
    ArrayList<Integer> searchUserIdByRoleId(int roleId);

    /**
     * 根据角色id批量删除角色信息
     * @param roleIds 角色id数组
     * @return 删除的记录条数
     */
    int deleteRoleByIds(Integer[] roleIds);

    /**
     * 查询指定角色是否有关联的未离职用户
     * @param roleIds 角色id数组
     * @return 指定角色是否有关联的用户
     */
    boolean searchRoleIfExistRelativeUser(Integer[] roleIds);
}




