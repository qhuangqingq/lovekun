package com.example.lovekun.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data
public class Student extends Model<Student> {
    private String name;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private int age;
    private String sex;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private boolean isDelete;
}
