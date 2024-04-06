package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbAmect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_amect(罚金表)】的数据库操作Mapper
* @createDate 2023-06-26 22:21:44
* @Entity com.example.emos.db.pojo.TbAmect
*/
public interface TbAmectMapper extends BaseMapper<TbAmect> {

    /**
     * 分页查询罚款信息
     * @param param 查询条件
     * @return 该页罚款信息
     */
    ArrayList<HashMap<String, Object>> searchAmectByPage(HashMap<String, Object> param);

    /**
     * 查询罚款记录的条数
     * @param param 查询条件
     * @return 罚款记录的条数
     */
    long searchAmectCount(HashMap<String, Object> param);

    /**
     * 根据条件查询罚款单信息
     * @param param 查询条件
     * @return 罚款单信息
     */
    TbAmect searchAmectByCondition(HashMap<String, Object> param);

    /**
     * 查询图表1所需数据
     * @param param 查询条件
     * @return 图表1所需数据
     */
    ArrayList<HashMap<String, Object>> searchChart_1(HashMap<String, Object> param);

    /**
     * 查询图表2所需数据
     * @param param 查询条件
     * @return 图表2所需数据
     */
    ArrayList<HashMap<String, Object>> searchChart_2(HashMap<String, Object> param);

    /**
     * 查询图表3所需数据
     * @param param 查询条件
     * @return 图表3所需数据
     */
    ArrayList<HashMap<String, Object>> searchChart_3(HashMap<String, Object> param);

    /**
     * 查询图表4所需数据
     * @param param 查询条件
     * @return 图表4所需数据
     */
    ArrayList<HashMap<String, Object>> searchChart_4(HashMap<String, Object> param);

    /**
     * 更新支付状态
     * @param param 更新条件
     * @return 更新成功的记录条数
     */
    int updateStatus(HashMap<String, Object> param);
}




