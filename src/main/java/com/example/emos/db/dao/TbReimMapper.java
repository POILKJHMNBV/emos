package com.example.emos.db.dao;

import com.example.emos.db.pojo.TbReim;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_reim(报销单表)】的数据库操作Mapper
* @createDate 2023-07-02 17:13:10
* @Entity com.example.emos.db.pojo.TbReim
*/
public interface TbReimMapper extends BaseMapper<TbReim> {
    /**
     * 查询报销记录分页数据
     * @param param 查询条件
     * @return 报销分页数据
     */
    ArrayList<HashMap<String, Object>> searchReimByPage(HashMap<String, Object> param);

    /**
     * 查询报销记录总数目
     * @param param 查询条件
     * @return 报销记录总数目
     */
    long searchReimCount(HashMap<String, Object> param);

    /**
     * 查询报销记录
     * @param param 查询条件
     * @return 报销记录信息
     */
    HashMap<String, Object> searchReimById(HashMap<String, Object> param);

    /**
     * 更新报销记录的实例id
     * @param param 更新条件
     * @return 更新成功的记录条数
     */
    int updateReimInstanceId(Map<String, Object> param);

    /**
     * 根据报销记录id删除报销记录
     * @param param 删除条件
     * @return 删除是否成功
     */
    int deleteReimById(HashMap<String, Object> param);
}