package com.example.emos;

import com.example.emos.common.util.tencent.TrtcUtil;
import com.example.emos.config.tencent.WxAccountConfig;
import com.example.emos.config.websocket.WebSocketService;
import com.example.emos.db.dao.TbAmectMapper;
import com.example.emos.db.dao.TbUserMapper;
import com.example.emos.db.pojo.TbAmect;
import com.example.emos.service.TbAmectService;
import com.example.emos.task.MeetingWorkflowTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
class EmosApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MeetingWorkflowTask meetingWorkflowTask;

    @Resource
    private TbUserMapper tbUserMapper;

    @Resource
    private TbAmectMapper tbAmectMapper;

    @Resource
    private TrtcUtil trtcUtil;

    @Resource
    private WxAccountConfig wxAccountConfig;

    @Resource
    private TbAmectService amectService;

    @Test
    void contextLoads() {
        HashMap<String, String> param = new HashMap<>();
        param.put("startDate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.of(2022, 5, 2)));
        param.put("endDate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
        HashMap<String, String> clone = (HashMap<String, String>) param.clone();
        clone.put("startDate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.of(2022, 6, 2)));
        System.out.println(param);
        System.out.println(clone);
    }

    @Test
    void bigDecimalTest() {
        BigDecimal oneDay = new BigDecimal(24);
        BigDecimal days = new BigDecimal("1").divide(oneDay, 1, RoundingMode.CEILING);
        System.out.println(days);
    }

    @Test
    void testWxPay() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("userId", 24);
        param.put("amectId", 84);
        amectService.createNativeAmectPayOrder(param);
    }

    @Test
    void testWxPayQuery() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("userId", 24);
        param.put("amectId", 8);
        amectService.searchNativeAmectPayResult(param);
    }

    @Test
    void testAmectMapper() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("userId", 24);
        param.put("amectId", 8);
        TbAmect tbAmect = tbAmectMapper.searchAmectByCondition(param);
        System.out.println(tbAmect);
        WebSocketService.sendInfo("发送成功", "23");
    }
}
