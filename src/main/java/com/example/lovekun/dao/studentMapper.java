package com.example.lovekun.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovekun.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface studentMapper extends BaseMapper<Student> {
    @Select("select * from student limit 1")
    List<Student> getStudent();
}
