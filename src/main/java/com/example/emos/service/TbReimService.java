package com.example.emos.service;

import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbReim;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_reim(报销单表)】的数据库操作Service
* @createDate 2023-07-02 17:13:10
*/
public interface TbReimService extends IService<TbReim> {

    /**
     * 查询报销记录分页数据
     * @param param 查询条件
     * @return 报销分页数据
     */
    Page searchReimByPage(HashMap<String, Object> param);

    /**
     * 查询报销记录
     * @param param 查询条件
     * @return 报销记录信息
     */
    HashMap<String, Object> searchReimById(HashMap<String, Object> param);

    /**
     * 插入新的报销记录
     * @param tbReim 报销记录信息
     * @return  插入成功的报销记录条数
     */
    int insert(TbReim tbReim);

    /**
     * 根据报销记录id删除报销记录
     * @param param 删除条件
     * @return 删除是否成功
     */
    int deleteReimById(HashMap<String, Object> param);
}
