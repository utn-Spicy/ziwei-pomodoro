package com.ziwei.pomodoro.dto;

import lombok.Data;

@Data
public class ResultDTO <T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResultDTO success(T data){
        ResultDTO r = new ResultDTO<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> ResultDTO error(int code, String message){
        ResultDTO r = new ResultDTO<>();
        r.code = code;
        r.message = message;
        return r;
    }
}
