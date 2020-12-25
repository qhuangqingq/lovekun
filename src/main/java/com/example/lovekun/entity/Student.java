package com.example.lovekun.entity;


import lombok.Data;

@Data
public class Student extends BasePlus<Student> {
    private String name;

    private String sex;

}
