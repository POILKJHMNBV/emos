package com.example.emos.service;

import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_dept】的数据库操作Service
* @createDate 2023-05-16 22:21:50
*/
public interface TbDeptService extends IService<TbDept> {

    /**
     * 分页查询部门信息
     * @param param 查询条件
     * @return 当前页的部门信息
     */
    Page searchDeptByPage(HashMap<String, Object> param);

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
     * 删除部门信息
     * @param deptIds 部门id
     * @return 删除的记录条数
     */
    int deleteDeptByIds(Integer[] deptIds);
}
