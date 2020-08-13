package com.powernode.handler;

import com.powernode.exception.ResultException;
import com.powernode.utils.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//切面类
@ControllerAdvice
public class GlobalException {

    //关注点
    @ExceptionHandler
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest request, ResultException e) {
        return Result.build(1200,e);

    }

}
