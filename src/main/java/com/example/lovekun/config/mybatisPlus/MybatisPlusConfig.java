package com.example.lovekun.config.mybatisPlus;


import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.lovekun.dao")
public class MybatisPlusConfig {
    /**
     * 若mp的版本是3.3.1以下的就需要在此处注入这个bean对象
     * 若mp的版本高于3.3.1的就无需在此处注入bean对象了
     */

    /**
     * mybatis-plus分页插件<br>
     * 文档：http://mp.baomidou.com<br>
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }



    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig config = new GlobalConfig();
        config.setMetaObjectHandler(new MyMetaObjectHandler());
        return config;
    }

}
