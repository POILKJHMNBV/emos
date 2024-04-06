package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbAmectType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_amect_type(罚金类型表)】的数据库操作Mapper
* @createDate 2023-06-27 21:42:21
* @Entity com.example.emos.db.pojo.TbAmectType
*/
public interface TbAmectTypeMapper extends BaseMapper<TbAmectType> {

    /**
     * 分页查询罚款类型信息
     * @param param 查询条件
     * @return 该页的罚款类型信息
     */
    ArrayList<HashMap<String, Object>> searchAmectTypeByPage(HashMap<String, Object> param);

    /**
     * 查询罚款类型数量
     * @param param 查询条件
     * @return 罚款类型数量
     */
    long searchAmectTypeCount(HashMap<String, Object> param);

    /**
     * 添加罚款类型
     * @param param 罚款类型信息
     * @return 成功添加的记录条数
     */
    int insert(HashMap<String, Object> param);
}




