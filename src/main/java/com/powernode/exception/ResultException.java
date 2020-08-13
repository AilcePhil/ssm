package com.powernode.exception;

import com.powernode.utils.ExceptionEnum;

public class ResultException extends RuntimeException{
    private ExceptionEnum exceptionEnum;

    public ResultException(ExceptionEnum exceptionEnum){

    }

    public ResultException(String message){
        super(message);
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public void setExceptionEnum(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }
}
