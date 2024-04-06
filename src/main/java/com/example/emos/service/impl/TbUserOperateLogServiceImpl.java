package com.example.emos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.emos.db.pojo.TbUserOperateLog;
import com.example.emos.service.TbUserOperateLogService;
import com.example.emos.db.dao.TbUserOperateLogMapper;
import org.springframework.stereotype.Service;

/**
* @author zhenwu
* @description 针对表【tb_user_operate_log(用户操作日志表)】的数据库操作Service实现
* @createDate 2023-06-26 22:21:44
*/
@Service
public class TbUserOperateLogServiceImpl extends ServiceImpl<TbUserOperateLogMapper, TbUserOperateLog>
    implements TbUserOperateLogService{

}
