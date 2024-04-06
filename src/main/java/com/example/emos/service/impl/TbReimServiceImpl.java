package com.example.emos.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.common.util.Page;
import com.example.emos.db.pojo.TbReim;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbReimService;
import com.example.emos.db.dao.TbReimMapper;
import com.example.emos.task.ReimWorkflowTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author zhenwu
* @description 针对表【tb_reim(报销单表)】的数据库操作Service实现
* @createDate 2023-07-02 17:13:10
*/
@Service
public class TbReimServiceImpl extends ServiceImpl<TbReimMapper, TbReim>
    implements TbReimService {

    @Resource
    private TbReimMapper tbReimMapper;

    @Resource
    private ReimWorkflowTask reimWorkflowTask;

    @Override
    public Page searchReimByPage(HashMap<String, Object> param) {
        int page = (int) param.get("page");
        int length = (int) param.get("length");
        int start = (page - 1) * length;
        param.put("start", start);

        long roleCount = tbReimMapper.searchReimCount(param);
        ArrayList<HashMap<String, Object>> list = tbReimMapper.searchReimByPage(param);
        return new Page(page, length, roleCount, list);
    }

    @Override
    public HashMap<String, Object> searchReimById(HashMap<String, Object> param) {
        HashMap<String, Object> result = tbReimMapper.searchReimById(param);
        String instanceId = MapUtil.getStr(result, "instanceId");

        // 生成报销单二维码
        QrConfig qrConfig = new QrConfig(70, 70);
        qrConfig.setMargin(2);
        String qrCodeBase64 = QrCodeUtil.generateAsBase64(instanceId, qrConfig, "jpg");
        result.put("qrCodeBase64", qrCodeBase64);
        return result;
    }

    @Override
    public int insert(TbReim tbReim) {
        int rows = tbReimMapper.insert(tbReim);
        //开启工作流
        if (rows == 1) {
            reimWorkflowTask.startReimWorkflow(tbReim.getId(), tbReim.getUserId());
        } else {
            throw new EmosException("报销申请保存失败");
        }
        return rows;
    }

    @Override
    public int deleteReimById(HashMap<String, Object> param) {
        TbReim tbReim = tbReimMapper.selectById((Integer) param.get("reimId"));
        String instanceId = tbReim.getInstanceId();
        int rows = tbReimMapper.deleteReimById(param);
        if (rows == 1) {
            reimWorkflowTask.deleteLeaveWorkflow(instanceId, "员工报销", "删除报销申请");
        } else {
            throw new EmosException("删除报销记录失败");
        }
        return rows;
    }
}




