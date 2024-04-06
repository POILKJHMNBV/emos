package com.example.emos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbAmectType;

import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_amect_type(罚金类型表)】的数据库操作Service
* @createDate 2023-06-27 21:42:22
*/
public interface TbAmectTypeService extends IService<TbAmectType> {

    /**
     * 分页查询罚款类型信息
     * @param param 查询条件
     * @return 该页的罚款类型信息
     */
    Page searchAmectTypeByPage(HashMap<String, Object> param);

    /**
     * 添加罚款类型
     * @param param 罚款类型信息
     * @return 成功添加的记录条数
     */
    int insert(HashMap<String, Object> param);
}
