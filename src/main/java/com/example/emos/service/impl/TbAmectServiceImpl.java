package com.example.emos.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.config.websocket.WebSocketService;
import com.example.emos.db.pojo.TbAmect;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbAmectService;
import com.example.emos.db.dao.TbAmectMapper;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.OrderQueryRequest;
import com.lly835.bestpay.model.OrderQueryResponse;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
* @author zhenwu
* @description 针对表【tb_amect(罚金表)】的数据库操作Service实现
* @createDate 2023-06-26 22:21:44
*/
@Service
@Slf4j
public class TbAmectServiceImpl extends ServiceImpl<TbAmectMapper, TbAmect>
    implements TbAmectService {

    @Resource(name = "wxPayService")
    private WxPayServiceImpl wxPayService;

    @Resource
    private TbAmectMapper tbAmectMapper;

    @Override
    public Page searchAmectByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);
        ArrayList<HashMap<String, Object>> list = tbAmectMapper.searchAmectByPage(param);
        long count = tbAmectMapper.searchAmectCount(param);
        return new Page(page, length, count, list);
    }

    @Transactional
    @Override
    public int insert(ArrayList<TbAmect> list) {
        for (TbAmect tbAmect : list) {
            tbAmectMapper.insert(tbAmect);
        }
        return list.size();
    }

    @Override
    public Map<String, Object> searchChart(HashMap<String, Object> param) {
        ArrayList<HashMap<String, Object>> chart_1 = tbAmectMapper.searchChart_1(param);
        ArrayList<HashMap<String, Object>> chart_2 = tbAmectMapper.searchChart_2(param);
        ArrayList<HashMap<String, Object>> chart_3 = tbAmectMapper.searchChart_3(param);
        param.clear();
        param.put("year", DateUtil.year(new Date()));
        param.put("status", 1);
        ArrayList<HashMap<String, Object>> paidList = tbAmectMapper.searchChart_4(param);
        param.replace("status", 2);
        ArrayList<HashMap<String, Object>> unpaidList = tbAmectMapper.searchChart_4(param);

        ArrayList<HashMap<String, Object>> chart_4_1 = new ArrayList<>();
        ArrayList<HashMap<String, Object>> chart_4_2 = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("month", i);
            map.put("ct", 0);
            chart_4_1.add(map);
            chart_4_2.add((HashMap<String, Object>) map.clone());
        }
        paidList.forEach(one -> {
            chart_4_1.forEach(temp -> {
                if (Objects.equals(MapUtil.getInt(one, "month"), MapUtil.getInt(temp, "month"))) {
                    temp.replace("ct", MapUtil.getInt(one, "ct"));
                }
            });
        });
        unpaidList.forEach(one -> {
            chart_4_2.forEach(temp -> {
                if (Objects.equals(MapUtil.getInt(one, "month"), MapUtil.getInt(temp, "month"))) {
                    temp.replace("ct", MapUtil.getInt(one, "ct"));
                }
            });
        });
        return Map.of("chart_1", chart_1, "chart_2", chart_2, "chart_3", chart_3,
                    "chart_4_1", chart_4_1, "chart_4_2", chart_4_2);
    }

    @Override
    public String createNativeAmectPayOrder(HashMap<String, Object> param) {
        int userId = MapUtil.getInt(param, "userId");
        int amectId = MapUtil.getInt(param, "amectId");
        // 查询罚款记录是否存在
        QueryWrapper<TbAmect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", amectId);
        queryWrapper.eq("status", 1);
        TbAmect tbAmect = tbAmectMapper.selectOne(queryWrapper);
        if (tbAmect != null) {

            // 1.准备支付请求参数
            String orderId = tbAmect.getUuid();
            PayRequest payRequest = new PayRequest();
            payRequest.setOrderId(orderId);
            payRequest.setOrderName("缴纳罚款");
            payRequest.setOrderAmount(tbAmect.getAmount().doubleValue());
            payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);

            // 2.发起支付
            PayResponse payResponse = wxPayService.pay(payRequest);
            log.info("罚款单{}的创建结果：{}", orderId, payResponse);

            // 3.获取支付结果信息
            String prepayId = payResponse.getPackAge().split("=")[1];
            String codeUrl = payResponse.getCodeUrl();
            if (!StrUtil.isEmpty(prepayId) && !StrUtil.isEmpty(codeUrl)) {

                // 4. 更新罚款单的微信预支付交易会话标识
                UpdateWrapper<TbAmect> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", amectId);
                updateWrapper.set("prepay_id", prepayId);
                TbAmect amect = new TbAmect();
                amect.setId(amectId);
                amect.setPrepayId(prepayId);
                int rows = tbAmectMapper.update(amect, updateWrapper);
                if (rows != 1) {
                    throw new EmosException("更新罚款单的微信预支付交易会话标识失败！");
                }

                // 5.将罚款单url转换为二维码
                QrConfig qrConfig = new QrConfig(255, 255);
                qrConfig.setMargin(2);
                return QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");
            } else {
                throw new EmosException("创建支付罚款单失败");
            }
        } else {
            throw new EmosException("罚款单信息不存在");
        }
    }

    @Override
    public void searchNativeAmectPayResult(HashMap<String, Object> param) {
        TbAmect tbAmect = tbAmectMapper.searchAmectByCondition(param);
        if (tbAmect != null) {
            // 发送微信平台查询罚款单信息
            String orderId = tbAmect.getUuid();
            OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
            orderQueryRequest.setPlatformEnum(BestPayPlatformEnum.WX);
            orderQueryRequest.setOrderId(orderId);
            OrderQueryResponse orderQueryResponse = wxPayService.query(orderQueryRequest);
            log.info("罚款单{}的支付结果：{}", orderId, orderQueryResponse);

            if (orderQueryResponse != null) {
                OrderStatusEnum orderStatusEnum = orderQueryResponse.getOrderStatusEnum();
                if (OrderStatusEnum.SUCCESS.getDesc().equals(orderStatusEnum.getDesc())) {
                    // 支付成功，更新罚款单状态
                    param.clear();
                    param.put("status", 2);
                    param.put("uuid", tbAmect.getUuid());
                    int rows = tbAmectMapper.updateStatus(param);
                    if (rows != 1) {
                        throw new EmosException("更新罚款单支付信息失败");
                    }
                }
            } else {
                throw new EmosException("查询罚款单信息失败");
            }
        } else {
            throw new EmosException("罚款单信息不存在");
        }
    }

    @Override
    public void receiveMessage(String notifyData) {

        // 1.签名认证
        PayResponse payResponse = wxPayService.asyncNotify(notifyData);
        log.info("微信支付平台异步通知结果 payResponse={}", payResponse);

        // 2.金额校验，查询罚款单是否存在
        QueryWrapper<TbAmect> queryWrapper = new QueryWrapper<>();
        String orderId = payResponse.getOrderId();
        queryWrapper.eq("uuid", orderId);
        TbAmect tbAmect = tbAmectMapper.selectOne(queryWrapper);
        if (tbAmect == null) {
            log.error("罚款单{}的状态有误", orderId);
            throw new EmosException("罚款单" + orderId + "的状态有误");
        } else {
            Double returnOrderAmount = payResponse.getOrderAmount();
            double currentAmount = tbAmect.getAmount().doubleValue();
            if (currentAmount != returnOrderAmount) {
                log.error("罚款单{}的金额有误，微信支付平台返回的金额为{}，数据库中金额为{}", orderId, returnOrderAmount, currentAmount);
            }
        }

        // 3.修改支付状态，更新微信订单号
        HashMap<String, Object> param = new HashMap<>(3);
        param.put("uuid", orderId);
        param.put("status", 2);
        param.put("transactionId", payResponse.getOutTradeNo());
        int rows = tbAmectMapper.updateStatus(param);
        if (rows != 1) {
            log.error("罚款单{}的状态更新失败", orderId);
            throw new EmosException("罚款单" + orderId + "的状态更新失败");
        }

        // 4.推送付款结果
        WebSocketService.sendInfo("收款成功", tbAmect.getUserId() + "");
    }
}




