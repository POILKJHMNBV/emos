package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_dept】的数据库操作Mapper
* @createDate 2023-05-16 22:21:49
* @Entity com.example.emos.db.pojo.TbDept
*/
public interface TbDeptMapper extends BaseMapper<TbDept> {

    /**
     * 分页查询部门信息
     * @param param 查询条件
     * @return 当前页的部门信息
     */
    ArrayList<HashMap<String, Object>> searchDeptByPage(HashMap<String, Object> param);

    /**
     * 查询部门总数
     * @param param 查询条件
     * @return 部门总数
     */
    long searchDeptCount(HashMap<String, Object> param);

    /**
     * 更新部门信息
     * @param tbDept 部门信息
     * @return 更新的记录条数
     */
    int update(TbDept tbDept);

    /**
     * 新增部门信息
     * @param tbDept 部门信息
     * @return 新增的记录条数
     */
    int insert(TbDept tbDept);

    /**
     * 查询部门是否有在职的用户
     * @param deptIds 部门id
     * @return 部门是否有在职的用户
     */
    boolean searchDeptIfExistRelativeUser(Integer[] deptIds);

    /**
     * 删除部门信息
     * @param deptIds 部门id
     * @return 删除的记录条数
     */
    int deleteDeptByIds(Integer[] deptIds);
}




