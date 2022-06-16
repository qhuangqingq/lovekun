package com.example.lovekun.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public class BaseController<M extends IService<T>, T>  {

    @Autowired
    private M service;

    @ApiOperation(value = "获取集合", notes = "获取集合")
    @PostMapping(value = "/getList")
    private List<T> getList(){
        return service.list();
    }

}
