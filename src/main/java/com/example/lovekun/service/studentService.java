package com.example.lovekun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lovekun.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface studentService extends IService<Student> {
    List<Student> getStudent();
}
