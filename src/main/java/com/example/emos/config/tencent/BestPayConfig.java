package com.example.emos.config.tencent;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.lly835.bestpay.config.WxPayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class BestPayConfig {

    private static final String key = "18bcnz3l6u5gm9qy";

    @Resource
    private WxAccountConfig wxAccountConfig;

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key.getBytes()).getEncoded();
        AES aes = SecureUtil.aes(byteKey);
        wxPayConfig.setAppId(aes.decryptStr(wxAccountConfig.getAppId()));
        wxPayConfig.setMchId(aes.decryptStr(wxAccountConfig.getMchId()));
        wxPayConfig.setMchKey(aes.decryptStr(wxAccountConfig.getMchKey()));
        wxPayConfig.setNotifyUrl(wxAccountConfig.getNotifyUrl());
        wxPayConfig.setReturnUrl(wxAccountConfig.getReturnUrl());
        return wxPayConfig;
    }
}
