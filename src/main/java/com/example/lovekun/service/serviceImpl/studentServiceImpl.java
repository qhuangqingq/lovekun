package com.example.lovekun.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovekun.dao.studentMapper;
import com.example.lovekun.entity.Student;
import com.example.lovekun.service.studentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class studentServiceImpl extends ServiceImpl<studentMapper, Student>  implements studentService {
    @Autowired
    private studentMapper smapper;
    @Override
    public List<Student> getStudent() {
        return smapper.getStudent();
    }
}
