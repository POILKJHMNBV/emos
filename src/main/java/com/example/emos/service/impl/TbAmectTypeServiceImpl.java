package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbAmectType;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbAmectTypeService;
import com.example.emos.db.dao.TbAmectTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_amect_type(罚金类型表)】的数据库操作Service实现
* @createDate 2023-06-27 21:42:22
*/
@Service
public class TbAmectTypeServiceImpl extends ServiceImpl<TbAmectTypeMapper, TbAmectType>
    implements TbAmectTypeService {

    @Resource
    private TbAmectTypeMapper tbAmectTypeMapper;

    @Override
    public Page searchAmectTypeByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        long count = tbAmectTypeMapper.searchAmectTypeCount(param);
        ArrayList<HashMap<String, Object>> list = tbAmectTypeMapper.searchAmectTypeByPage(param);
        return new Page(page, length, count, list);
    }

    @Override
    public int insert(HashMap<String, Object> param) {
        String type = (String) param.get("type");
        synchronized (type.intern()) {
            long count = query().eq("type", type).count();
            if (count > 0) {
                throw new EmosException("罚款类型已经存在");
            }
        }
        return tbAmectTypeMapper.insert(param);
    }
}




