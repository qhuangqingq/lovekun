package com.example.lovekun.config;

import com.example.lovekun.controller.BaseController;
import com.example.lovekun.utils.DataSourceUtil;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Interceptor extends BaseController implements HandlerInterceptor {



    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String database = request.getParameter("database");
            if("esb8".equals(database)){
                DataSourceUtil.setDB(database);
            }
            int a=0;
            //这里设置拦截以后重定向的页面，一般设置为登陆页面地址
//            response.sendRedirect(request.getContextPath() + "/error.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;//如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        DataSourceUtil.clearDB();
    }

}
