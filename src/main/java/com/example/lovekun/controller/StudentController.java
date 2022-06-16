package com.example.lovekun.controller;


import com.example.lovekun.entity.Student;
import com.example.lovekun.service.studentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;

@Api(value = "学生查询接口")
@RestController
@RequestMapping("/studentController")
public class StudentController extends BaseController<studentService, Student> {


    @ApiOperation(value = "获取int", notes = "获取int")
    @PostMapping(value = "/getint")
    private Integer getList(){
        Student student=new Student();
        Field[] fields =student.getClass().getFields();
        return null;
    }

    public static void main(String[] args) throws IllegalAccessException {

        Student student=new Student();
        student.setName("testName");
        Field[] fields =student.getClass().getDeclaredFields();
        fields[0].setAccessible(true);
        System.out.println(fields[0].get(student));
    }
}
