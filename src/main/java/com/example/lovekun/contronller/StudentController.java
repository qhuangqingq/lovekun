package com.example.lovekun.contronller;


import com.example.lovekun.entity.Student;
import com.example.lovekun.service.studentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "学生查询接口")
@RestController
@RequestMapping("/studentController")
public class StudentController extends BaseController<studentService, Student>{


    @ApiOperation(value = "获取int", notes = "获取int")
    @PostMapping(value = "/getint")
    private Integer getList(){
        int a=10;
        int b=11;
        return null;
    }
}
