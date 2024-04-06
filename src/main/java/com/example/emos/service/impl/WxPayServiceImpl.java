package com.example.emos.service.impl;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.constants.WxPayConstants;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.OrderQueryRequest;
import com.lly835.bestpay.model.OrderQueryResponse;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.wxpay.WxPayApi;
import com.lly835.bestpay.model.wxpay.request.WxOrderQueryRequest;
import com.lly835.bestpay.model.wxpay.response.WxOrderQueryResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import com.lly835.bestpay.service.impl.WxPaySignature;
import com.lly835.bestpay.utils.MapUtil;
import com.lly835.bestpay.utils.RandomUtil;
import com.lly835.bestpay.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import javax.annotation.Resource;
import java.io.IOException;

@Service("wxPayService")
@Slf4j
public class WxPayServiceImpl extends BestPayServiceImpl {

    @Resource
    private WxPayConfig wxPayConfig;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(WxPayConstants.WXPAY_GATEWAY)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .addInterceptor((new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)))
                    .build()
            )
            .build();

    @Override
    public PayResponse pay(PayRequest request) {
        super.setWxPayConfig(wxPayConfig);
        return super.pay(request);
    }

    @Override
    public OrderQueryResponse query(OrderQueryRequest request) {
        if (request.getPlatformEnum() == BestPayPlatformEnum.WX) {
            return this.queryOrderResult(request);
        } else {
            return super.query(request);
        }
    }

    private OrderQueryResponse queryOrderResult(OrderQueryRequest request) {
        WxOrderQueryRequest wxRequest = new WxOrderQueryRequest();
        wxRequest.setOutTradeNo(request.getOrderId());
        wxRequest.setTransactionId(request.getOutOrderId());

        wxRequest.setAppid(wxPayConfig.getAppId());
        wxRequest.setMchId(wxPayConfig.getMchId());
        wxRequest.setNonceStr(RandomUtil.getRandomStr());
        wxRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxRequest), wxPayConfig.getMchKey()));
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), XmlUtil.toString(wxRequest));

        Call<WxOrderQueryResponse> call = retrofit.create(WxPayApi.class).orderquery(body);
        Response<WxOrderQueryResponse> retrofitResponse  = null;
        try{
            retrofitResponse = call.execute();
        }catch (IOException e) {
            e.printStackTrace();
        }
        assert retrofitResponse != null;
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信订单查询】网络异常");
        }
        WxOrderQueryResponse response = retrofitResponse.body();
        assert response != null;
        if(!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信订单查询】returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信订单查询】resultCode != SUCCESS, err_code = " + response.getErrCode() + ", err_code_des=" + response.getErrCodeDes());
        }
        return OrderQueryResponse.builder()
                .orderStatusEnum(OrderStatusEnum.findByName(response.getTradeState()))
                .resultMsg(response.getTradeStateDesc())
                .outTradeNo(response.getTransactionId())
                .orderId(response.getOutTradeNo())
                .attach(response.getAttach())
                .finishTime(response.getTimeEnd() == null ?
                        null :
                        response.getTimeEnd().replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6"))
                .build();
    }
}
