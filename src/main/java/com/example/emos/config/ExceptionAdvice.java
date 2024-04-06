package com.example.emos.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONObject;
import com.example.emos.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        JSONObject json = new JSONObject();

        if (e instanceof MethodArgumentNotValidException) {
            // 处理后端验证失败产生的异常
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            json.set("error", Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
        } else if (e instanceof EmosException) {
            // 处理业务异常
            log.error("执行异常", e);
            EmosException exception = (EmosException) e;
            json.set("error", exception.getMsg());
        } else {
            // 处理其余的异常
            log.error("执行异常", e);
            json.set("error", "执行异常");
        }

        return json.toString();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public String unLoginHandler(Exception e) {
        JSONObject json = new JSONObject();
        json.set("error", e.getMessage());
        return json.toString();
    }
}
