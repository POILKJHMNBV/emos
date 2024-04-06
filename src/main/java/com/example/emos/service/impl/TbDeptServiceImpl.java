package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbDept;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbDeptService;
import com.example.emos.db.dao.TbDeptMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_dept】的数据库操作Service实现
* @createDate 2023-05-16 22:21:50
*/
@Service
public class TbDeptServiceImpl extends ServiceImpl<TbDeptMapper, TbDept>
    implements TbDeptService {

    @Resource
    private TbDeptMapper tbDeptMapper;

    @Override
    public Page searchDeptByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        long count = tbDeptMapper.searchDeptCount(param);
        ArrayList<HashMap<String, Object>> list = tbDeptMapper.searchDeptByPage(param);
        return new Page(page, length, count, list);
    }

    @Override
    public int update(TbDept tbDept) {
        return tbDeptMapper.update(tbDept);
    }

    @Override
    public int insert(TbDept tbDept) {
        String deptName = tbDept.getDeptName();
        synchronized (deptName.intern()) {
            long count = query().eq("dept_name", deptName).count();
            if (count > 0) {
                throw new EmosException("部门已经存在");
            }
        }
        return tbDeptMapper.insert(tbDept);
    }

    @Override
    public int deleteDeptByIds(Integer[] deptIds) {
        if (tbDeptMapper.searchDeptIfExistRelativeUser(deptIds)) {
            throw new EmosException("无法删除关联用户的部门");
        }
        return tbDeptMapper.deleteDeptByIds(deptIds);
    }
}




