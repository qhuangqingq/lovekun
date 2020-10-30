package com.example.lovekun.contronller;


import com.example.lovekun.dao.studentMapper;
import com.example.lovekun.entity.Student;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "学生查询接口")
@RestController
public class UserController {
    @Resource
    private studentMapper mapper;

    @ApiOperation(value = "获取名字", notes = "提示接口使用者注意事项")
    @ApiImplicitParam()
    @PostMapping(value = "/getUserName")
    public List<Student> getUserName() {
        List<Student> student = mapper.getStudent();
        Student student1=new Student();
        student1.setName("hq11");
        student1.setSex("nan");
        mapper.insert(student1);
        List<Student> students = mapper.selectList(null);
        return student;
    }
}
