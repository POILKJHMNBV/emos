package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.db.pojo.TbPermission;
import com.example.emos.service.TbPermissionService;
import com.example.emos.db.dao.TbPermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_permission】的数据库操作Service实现
* @createDate 2023-05-21 10:35:47
*/
@Service
public class TbPermissionServiceImpl extends ServiceImpl<TbPermissionMapper, TbPermission>
    implements TbPermissionService{

    @Resource
    private TbPermissionMapper tbPermissionMapper;

    @Override
    public ArrayList<HashMap<String, Object>> searchAllPermission() {
        return tbPermissionMapper.searchAllPermission();
    }
}




