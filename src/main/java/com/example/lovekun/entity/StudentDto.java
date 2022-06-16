package com.example.lovekun.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.example.lovekun.utils.ListUrlConverterUtil;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.net.URL;
import java.util.List;

@Data
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
@HeadStyle(horizontalAlignment = HorizontalAlignment.CENTER)
@ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
public class StudentDto {

    @ExcelProperty(value = "姓名",index = 0)
    private String name;

    @ExcelProperty(value = "性别",index = 1)
    private String sex;


    @ExcelProperty(value = "图片", index = 2,converter = ListUrlConverterUtil.class)
    private List<URL> listImage;

    @ExcelProperty(value = "id",index =3)
    private Integer id;

    @ExcelProperty(value = "delFlag",index = 4)
    private boolean delFlag;

    @ExcelProperty(value = "age",index = 5)
    private int age;

}
