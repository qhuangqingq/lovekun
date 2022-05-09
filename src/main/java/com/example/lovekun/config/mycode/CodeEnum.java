package com.example.lovekun.config.mycode;


public enum CodeEnum {

    success(200,"success"),
    nosuccess(6000,"未认证"),
    unkon(8000,"服务端异常");
    private int code;
    private String message;

    CodeEnum(int code,String message){

        this.code=code;
        this.message=message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    CodeEnum(){

    }

    public int getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }
}
