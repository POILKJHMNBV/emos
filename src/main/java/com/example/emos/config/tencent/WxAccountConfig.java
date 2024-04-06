package com.example.emos.config.tencent;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource("classpath:tencent.properties")
public class WxAccountConfig {

    @Value("${tencent.wx.appId}")
    private String appId;

    @Value("${tencent.wx.mchId}")
    private String mchId;

    @Value("${tencent.wx.mchKey}")
    private String mchKey;

    @Value("${tencent.wx.notifyUrl}")
    private String notifyUrl;

    @Value("${tencent.wx.returnUrl}")
    private String returnUrl;
}
