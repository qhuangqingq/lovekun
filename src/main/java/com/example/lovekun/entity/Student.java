package com.example.lovekun.entity;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Student extends BasePlus<Student> {
    @Excel(name="姓名")
    private String name;
    @Excel(name="性别")
    private String sex;
    //上传的图片会直接存到 savePath指定的目录下
    @Excel(name = "头像", width = 20 , height = 40,type = 2,savePath = "D:\\student")
    private String photo;

}
