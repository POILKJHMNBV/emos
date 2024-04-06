package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_permission】的数据库操作Mapper
* @createDate 2023-05-21 10:35:47
* @Entity com.example.emos.db.pojo.TbPermission
*/
public interface TbPermissionMapper extends BaseMapper<TbPermission> {

    /**
     * 查询所有的权限信息
     * @return 所有的权限信息
     */
    ArrayList<HashMap<String, Object>> searchAllPermission();
}




