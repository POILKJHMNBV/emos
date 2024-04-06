package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
* @author zhenwu
* @description 针对表【tb_user(用户表)】的数据库操作Mapper
* @createDate 2023-05-14 21:19:21
* @Entity com.example.emos.db.dao.TbUser
*/
public interface TbUserMapper extends BaseMapper<TbUser> {
    /**
     * 查询用户权限信息
     * @param userId 用户id
     * @return 用户权限信息集合
     */
    Set<String> searchUserPermissions(long userId);

    /**
     * 用户登录
     * @param param 用户名和密码
     * @return 用户id
     */
    Integer login(HashMap<String, String> param);

    /**
     * 修改用户密码
     * @param param 用户新密码和旧密码
     * @return 修改的记录数
     */
    Integer updatePassword(HashMap<String, Object> param);

    /**
     * 分页查询用户信息
     * @param param 查询条件
     * @return 该页的用户信息
     */
    List<HashMap<String, Object>> searchUserByPage(HashMap<String, Object> param);

    /**
     * 查询用户总数
     * @param param 查询条件
     * @return 用户总数
     */
    long searchUserCount(HashMap<String, Object> param);

    /**
     * 新增用户
     * @param user 用户信息
     * @return 新增记录条数
     */
    int insert(TbUser user);

    /**
     * 根据用户id查询用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    HashMap<String, Object> searchById(int userId);

    /**
     * 根据用户id查询用户信息，包括部门、角色信息
     * @param userId 用户id
     * @return 用户信息
     */
    HashMap<String, Object> searchUserInfo(int userId);

    /**
     * 查找员工姓名和部门
     * @param userId 员工id
     * @return 员工姓名和部门
     */
    HashMap<String, Object> searchNameAndDept(int userId);

    /**
     * 修改用户信息
     * @param user 用户信息
     * @return 修改的记录条数
     */
    int update(TbUser user);

    /**
     * 删除用户信息
     * @param ids 用户id
     * @return 删除的记录条数
     */
    int deleteUserByIds(Integer[] ids);

    /**
     * 查询除当前用户外其他用户的信息
     * @param userId 用户id
     * @return 与当前用户在同一部门的其它用户信息
     */
    ArrayList<HashMap<String, Object>> searchUserGroupByDept(int userId);

    /**
     * 查询总经理id
     * @return 总经理id
     */
    int searchGmId();

    /**
     * 查询用户所在部门的部门经理id
     * @param userId 用户id
     * @return 部门经理id
     */
    int searchDeptManagerId(int userId);

    /**
     * 查询用户的角色
     * @param userId 用户id
     * @return 用户的角色
     */
    Set<String> searchUserRoles(int userId);
}




