package com.example.lovekun.config.mycode;

import com.example.lovekun.annotations.JsonKeyLowerCase;
import com.example.lovekun.utils.JSONUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice(basePackages = "com.example.lovekun.controller")
public class Myadvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //获取当前处理请求的controller的方法
        String methodName=methodParameter.getMethod().getName();
        // 不拦截/不需要处理返回值 的方法
        String method= "loginCheck"; //如登录
        //不拦截
        return !method.equals(methodName);
    }


    @Override
    public Result beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        JsonKeyLowerCase jsonKeyLowerCase = Optional
                .ofNullable(methodParameter.getMethodAnnotation(JsonKeyLowerCase.class))
                .orElse(methodParameter.getContainingClass().getAnnotation(JsonKeyLowerCase.class));//优先获取方法上的注解 然后获取类上
        if (jsonKeyLowerCase != null && jsonKeyLowerCase.isEnable()) {
            if (o instanceof List) {
                o = JSONUtils.keyToLowerCase((List) o);
            } else if (o instanceof Map) {
                o = JSONUtils.keyToLowerCase(o);
            }
        }
        return new Result(CodeEnum.success, o);
    }

}
