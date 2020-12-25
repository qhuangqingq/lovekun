package com.example.lovekun.service.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovekun.dao.TeacherMapper;
import com.example.lovekun.entity.Teacher;
import com.example.lovekun.service.ITeacherService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huangqing
 * @since 2020-11-12
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

}
