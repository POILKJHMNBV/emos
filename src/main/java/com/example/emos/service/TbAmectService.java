package com.example.emos.service;

import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbAmect;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author zhenwu
* @description 针对表【tb_amect(罚金表)】的数据库操作Service
* @createDate 2023-06-26 22:21:44
*/
public interface TbAmectService extends IService<TbAmect> {

    /**
     * 分页查询罚款信息
     * @param param 查询条件
     * @return 该页罚款信息
     */
    Page searchAmectByPage(HashMap<String, Object> param);

    /**
     * 添加罚款记录
     * @param list 罚款记录
     * @return 添加成功记录条数
     */
    int insert(ArrayList<TbAmect> list);

    /**
     * 查询图表所需数据
     * @param param 查询条件
     * @return 图表所需数据
     */
    Map<String, Object> searchChart(HashMap<String, Object> param);

    /**
     * 创建罚款订单
     * @param param 创建订单所需要的参数
     * @return 罚款订单
     */
    String createNativeAmectPayOrder(HashMap<String, Object> param);


    /**
     * 查询罚款单的支付结果
     * @param param 查询条件
     */
    void searchNativeAmectPayResult(HashMap<String, Object> param);

    /**
     * 处理微信支付平台的支付消息通知
     * @param notifyData 微信支付平台的支付消息通知
     */
    void receiveMessage(String notifyData);
}
