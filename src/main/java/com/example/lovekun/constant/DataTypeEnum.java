package com.example.lovekun.constant;


public enum DataTypeEnum {
    di70("mysql"),
    esb8("mysql");
    private String type;

    DataTypeEnum(String type){
        this.type=type;
    }

    public String getType(){
        return type;
    }
}
