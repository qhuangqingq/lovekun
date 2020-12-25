package com.example.lovekun.config.mycode;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object data;

    Result(CodeEnum codeEnum,Object data){
        this.code=codeEnum.getCode();
        this.message=codeEnum.getMessage();
        this.data=data;
    }
    Result(int code,String message){
        this.code=code;
        this.message=message;
        this.data=message;
    }
    Result(){

    }
}
