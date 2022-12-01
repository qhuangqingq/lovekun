package com.example.lovekun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(value = "table接口")
@RestController
@RequestMapping("/tableController")
public class TableController {

    @GetMapping(value = "getDatabases")
    @ApiOperation(value = "测试debug", notes = "测试debug")
    public HashMap<String,Object> getDatabases() {
        List<Integer> list=new ArrayList<>();
        Integer sum=0;
        for (int i = 0; i < 10; i++) {
            list.add(i);
            sum=i+sum;
            int a=0;
        }
        System.out.println(sum);
        return null;
    }

}
