package com.example.lovekun.config.mycode;

import lombok.Data;

@Data

public class AbExcept extends RuntimeException{
    private int code;

    public AbExcept(CodeEnum codeEnum, String msg){
        super(msg);
        this.code = codeEnum.getCode();
    }

    public AbExcept(String msg){
        super(msg);
    }
    AbExcept(){

    }
}
