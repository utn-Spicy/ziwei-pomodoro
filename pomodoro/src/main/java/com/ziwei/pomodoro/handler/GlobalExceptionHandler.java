package com.ziwei.pomodoro.handler;

import com.ziwei.pomodoro.dto.ResultDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultDTO<Void> handleException(Exception e){
        return ResultDTO.error(500,"服务器内部错误：" + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultDTO<Void> handleIllegalArgument(IllegalArgumentException e){
        return ResultDTO.error(400,"参数错误：" + e.getMessage());
    }

}
