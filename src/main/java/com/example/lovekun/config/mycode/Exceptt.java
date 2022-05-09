package com.example.lovekun.config.mycode;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
@ControllerAdvice
public class Exceptt {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception ex) {
        return new Result(CodeEnum.unkon,ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = AbExcept.class)
    public Result AbExceptHandler(AbExcept abExcept) {

        return new Result(abExcept.getCode(),abExcept.getMessage());
    }
}
