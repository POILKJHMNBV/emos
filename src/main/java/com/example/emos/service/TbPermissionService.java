package com.example.emos.service;

import com.example.emos.db.pojo.TbPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_permission】的数据库操作Service
* @createDate 2023-05-21 10:35:47
*/
public interface TbPermissionService extends IService<TbPermission> {
    /**
     * 查询所有的权限信息
     * @return 所有的权限信息
     */
    ArrayList<HashMap<String, Object>> searchAllPermission();
}
