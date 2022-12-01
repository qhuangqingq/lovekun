package com.example.lovekun.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lovekun.entity.Student;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface studentMapper extends BaseMapper<Student> {
    @Select("select * from student limit 1")
    List<Student> getStudent();
}
