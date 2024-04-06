package com.example.emos.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.emos.common.util.R;
import com.example.emos.config.EmosLog;
import com.example.emos.controller.form.amect.*;
import com.example.emos.db.pojo.TbAmect;
import com.example.emos.service.TbAmectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/amect")
@Tag(name = "AmectController", description = "缴纳罚款Web接口")
@Slf4j
public class AmectController {

    @Resource
    private TbAmectService amectService;


    @GetMapping("/searchById")
    @Operation(summary = "根据ID查找罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestParam("amectId") Integer amectId) {
        TbAmect tbAmect = amectService.getById(amectId);
        Map<String, Object> map = Map.of("typeId", tbAmect.getTypeId(),
                                        "amount", tbAmect.getAmount(),
                                        "reason", tbAmect.getReason());
        return R.ok(map);
    }

    @PostMapping("/searchAmectByPage")
    @Operation(summary = "分页查询罚款信息")
    @SaCheckPermission(value = {"ROOT", "AMECT:SELECT"}, mode = SaMode.OR)
    public R searchAmectByPage(@RequestBody @Valid SearchAmectByPageForm searchAmectByPageForm) {
        String startDate = searchAmectByPageForm.getStartDate();
        String endDate = searchAmectByPageForm.getEndDate();
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            return R.error("startDate和endDate只能同时为空，或者不为空");
        }
        HashMap<String, Object> param = JSONUtil.parse(searchAmectByPageForm).toBean(HashMap.class);
        param.put("currentUserId", StpUtil.getLoginIdAsInt());
        int loginId = StpUtil.getLoginIdAsInt();
        List<String> roleList = StpUtil.getRoleList(loginId);
        if (!roleList.contains("总经理") && !roleList.contains("超级管理员")) {
            param.put("userId", loginId);
        }
        return R.ok().put("page", amectService.searchAmectByPage(param));
    }

    @PostMapping("/searchChart")
    @Operation(summary = "查询Chart图表")
    @SaCheckPermission(value = {"ROOT", "AMECT:SELECT"}, mode = SaMode.OR)
    public R searchChart(@Valid @RequestBody SearchChartForm searchChartForm) {
        HashMap<String, Object> param = JSONUtil.parse(searchChartForm).toBean(HashMap.class);
        return R.ok(amectService.searchChart(param));
    }

    @PostMapping("/update")
    @Operation(summary = "更新罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:UPDATE"}, mode = SaMode.OR)
    @EmosLog
    public R update(@Valid @RequestBody UpdateAmectForm updateAmectForm) {
        UpdateWrapper<TbAmect> tbAmectUpdateWrapper = new UpdateWrapper<>();
        tbAmectUpdateWrapper.eq("id", updateAmectForm.getId());
        tbAmectUpdateWrapper.set("type_id", updateAmectForm.getTypeId());
        tbAmectUpdateWrapper.set("amount", new BigDecimal(updateAmectForm.getAmount()));
        tbAmectUpdateWrapper.set("reason", updateAmectForm.getReason());
        amectService.update(tbAmectUpdateWrapper);
        return R.ok().put("rows", 1);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:INSERT"}, mode = SaMode.OR)
    @EmosLog
    public R insert(@RequestBody @Valid InsertAmectForm insertAmectForm) {
        Integer[] userIds = insertAmectForm.getUserId();
        ArrayList<TbAmect> list = new ArrayList<>(userIds.length);
        String amount = insertAmectForm.getAmount();
        Integer typeId = insertAmectForm.getTypeId();
        String reason = insertAmectForm.getReason();
        for (Integer userId : userIds) {
            TbAmect tbAmect = new TbAmect();
            tbAmect.setUserId(userId);
            tbAmect.setAmount(new BigDecimal(amount));
            tbAmect.setTypeId(typeId);
            tbAmect.setReason(reason);
            tbAmect.setUuid(IdUtil.simpleUUID());
            tbAmect.setStatus(1);
            list.add(tbAmect);
        }
        return R.ok().put("rows", amectService.insert(list));
    }

    @PostMapping("/deleteAmectByIds")
    @Operation(summary = "删除罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:DELETE"}, mode = SaMode.OR)
    @EmosLog
    public R deleteAmectByIds(@Valid @RequestBody DeleteAmectByIdsForm deleteAmectByIdsForm) {
        Integer[] ids = deleteAmectByIdsForm.getIds();
        List<Integer> list = Arrays.asList(ids);
        return R.ok().put("rows", amectService.removeBatchByIds(list));
    }

    @GetMapping("/createNativeAmectPayOrder")
    @Operation(summary = "创建Native支付罚款订单")
    @SaCheckLogin
    public R createNativeAmectPayOrder(@RequestParam("amectId") Integer amectId) {
        if (amectId == null || amectId < 1) {
            return R.error("amectId格式不正确");
        }
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("amectId", amectId);
        param.put("userId", StpUtil.getLoginIdAsInt());
        return R.ok().put("qrCodeBase64", amectService.createNativeAmectPayOrder(param));
    }

    @GetMapping("/searchNativeAmectPayResult")
    @Operation(summary = "查询Native支付罚款订单结果")
    @SaCheckLogin
    public R searchNativeAmectPayResult(@RequestParam("amectId") Integer amectId) {
        if (amectId == null || amectId < 1) {
            return R.error("amectId格式不正确");
        }
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("userId", StpUtil.getLoginIdAsInt());
        param.put("amectId", amectId);
        amectService.searchNativeAmectPayResult(param);
        return R.ok();
    }

    @Operation(summary = "接收微信支付平台的支付消息通知")
    @PostMapping("/receiveMessage")
    public void receiveMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1.解析数据
        StringBuffer notifyData = new StringBuffer();
        request.setCharacterEncoding("UTF-8");
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            notifyData.append(line);
        }
        reader.close();

        // 2.校验微信支付平台的支付消息通知，更新罚款单状态
        amectService.receiveMessage(notifyData.toString());

        // 3.向微信支付平台响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xml");
        Writer writer = response.getWriter();
        writer.write("<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>");
        writer.close();
    }
}
