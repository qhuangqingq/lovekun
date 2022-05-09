package com.example.lovekun.contronller;


import com.example.lovekun.config.mycode.AbExcept;
import com.example.lovekun.config.mycode.CodeEnum;
import com.example.lovekun.dao.studentMapper;
import com.example.lovekun.entity.Student;
import com.example.lovekun.entity.Teacher;
import com.example.lovekun.service.ITeacherService;
import com.example.lovekun.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Api(value = "学生查询接口")
@RestController
@RequestMapping("/UserController")
public class TeacherController extends BaseController<ITeacherService,Teacher>{
    @Autowired
    private studentMapper smapper;
    @Autowired
    private ITeacherService smapper1;

    @ApiOperation(value = "获取名字", notes = "提示接口使用者注意事项")
    @ApiImplicitParam()
    @PostMapping(value = "/getUserName")
    public List<Student> getUserName() {
        List<Student> student = smapper.selectList(null);
        return student;
    }

    @ApiOperation(value = "添加", notes = "提示接口使用者注意事项")
    @ApiImplicitParam()
    @PostMapping(value = "/insertTeacher")
    public List<Teacher> insertTeacher(String name) {
        Teacher teacher=new Teacher();
        teacher.setName(name);
        teacher.insert();
        List<Teacher> teacher1 = smapper1.list();
        return teacher1;
    }
    @ApiOperation(value = "上传", notes = "提示接口使用者注意事项")
    @ApiImplicitParam()
    @PostMapping(value = "/upload")
    public void upload(MultipartFile file) throws IOException {
        Utils.dowloadFile(file);
    }
    @ApiOperation(value = "异常", notes = "提示接口使用者注意事项")
    @ApiImplicitParam()
    @PostMapping(value = "/excep")
    public Object excep() {
        if(1==1){
            throw new AbExcept(CodeEnum.success,"搞错了");
        }else{
            return "hao1";
        }

    }

}
