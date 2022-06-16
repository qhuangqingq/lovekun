package com.example.lovekun.config.mycode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

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
        ObjectMapper mapper=new ObjectMapper();
        if(o!=null){

            return  new Result(CodeEnum.success,o);

        }else{
            return  new Result(CodeEnum.success,o);
        }
    }

}
