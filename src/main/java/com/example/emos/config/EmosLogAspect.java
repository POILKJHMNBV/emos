package com.example.emos.config;

import cn.dev33.satoken.stp.StpUtil;
import com.example.emos.common.util.RedisIdWorker;
import com.example.emos.common.util.RequestUtil;
import com.example.emos.db.pojo.TbUserOperateLog;
import com.example.emos.exception.EmosException;
import com.example.emos.service.TbUserOperateLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author zhenwu
 */
@Aspect
@Component
@Slf4j
public class EmosLogAspect {

    private static final String USER_ID= "userId";

    private static final String REQUEST_METHOD = "requestMethod";

    @Resource
    private TbUserOperateLogService tbUserOperateLogService;
    
    @Resource
    private RedisIdWorker redisIdWorker;

    @Pointcut("execution(public * com.example.emos.controller.*.*(..))")
    public void controllerCall() {
    }

    @Before("controllerCall()")
    public void before(JoinPoint joinPoint) {
        MDC.put(REQUEST_METHOD, joinPoint.getSignature().getName());
        if (StpUtil.isLogin()) {
            MDC.put(USER_ID, StpUtil.getLoginIdAsString());
        }
    }

    @AfterReturning(returning = "req", pointcut = "controllerCall()")
    public void afterReturning(JoinPoint jp, Object req) throws Exception {
        MDC.remove(USER_ID);
    }

    @Around("controllerCall()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 1.获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (!method.isAnnotationPresent(EmosLog.class)) {
            return joinPoint.proceed();
        }

        TbUserOperateLog tbUserOperateLog = new TbUserOperateLog();
        tbUserOperateLog.setId(this.redisIdWorker.nextId("userOperateLog"));

        if (StpUtil.isLogin()) {
            tbUserOperateLog.setUserId(StpUtil.getLoginIdAsInt());
        }

        // 2.获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        tbUserOperateLog.setRequestMethod(request.getMethod());
        tbUserOperateLog.setRequestUri(request.getRequestURI().substring(request.getContextPath().length()));
        tbUserOperateLog.setRequestIp(RequestUtil.getRequestIp(request));

        // 3.获取API接口信息
        Operation annotation = method.getAnnotation(Operation.class);
        tbUserOperateLog.setOperationDescription(annotation.summary());

        // 4.获取请求参数
        Object[] args = joinPoint.getArgs();
        tbUserOperateLog.setRequestParameter(RequestUtil.extractParameter(method, args));

        // 5.执行目标方法并记录日志
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            if (e instanceof EmosException) {
                EmosException emosException = (EmosException) e;
                tbUserOperateLog.setResponseStatus(emosException.getCode());
                tbUserOperateLog.setErrorReason(emosException.getMessage());
            } else {
                tbUserOperateLog.setResponseStatus(500);
                tbUserOperateLog.setErrorReason(e.getMessage());
            }
            throw e;
        } finally {
            stopWatch.stop();
            tbUserOperateLog.setCostTime((int) stopWatch.getTotalTimeMillis());
            log.info(tbUserOperateLog.toString());
            this.tbUserOperateLogService.save(tbUserOperateLog);
        }
        return result;
    }
}
