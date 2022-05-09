package com.example.lovekun.contronller;


import com.example.lovekun.entity.Student;
import com.example.lovekun.service.studentService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "学生查询接口")
@RestController
@RequestMapping("/studentController")
public class StudentController extends BaseController<studentService, Student>{

}
