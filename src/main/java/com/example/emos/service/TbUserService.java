package com.example.emos.service;

import cn.hutool.json.JSONArray;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.Set;

/**
* @author zhenwu
* @description 针对表【tb_user(用户表)】的数据库操作Service
* @createDate 2023-05-14 21:19:21
*/
public interface TbUserService extends IService<TbUser> {

    /**
     * 查询用户权限信息
     * @param userId 用户id
     * @return 用户权限信息集合
     */
    Set<String> searchUserPermissions(long userId);

    /**
     * 用户登录
     * @param param 用户户和密码
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
     * @return 分页数据
     */
    Page searchUserByPage(HashMap<String, Object> param);

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
    HashMap<String, Object> searchById(Integer userId);

    /**
     * 查找员工姓名和部门
     * @param userId 员工id
     * @return 员工姓名和部门
     */
    HashMap<String, Object> searchNameAndDept(Integer userId);

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
    JSONArray searchUserGroupByDept(Integer userId);

    /**
     * 查询用户的角色
     * @param userId 用户id
     * @return 用户的角色
     */
    Set<String> searchUserRoles(int userId);

    /**
     * 员工离职
     * @param userId 离职人员id
     * @param assignId 交接人员id
     * @return 员工状态是否更新成功
     */
    int dismiss(int userId, int assignId);
}
