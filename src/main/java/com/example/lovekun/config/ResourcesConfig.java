package com.example.lovekun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
    /**
     * 图片地址映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //获取文件的真实路径
        String testStudent = "D:/images/student/";
        registry.addResourceHandler("/student/**").addResourceLocations("file:"+testStudent);
    }
}
