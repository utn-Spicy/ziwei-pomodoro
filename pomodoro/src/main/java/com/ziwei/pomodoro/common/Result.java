package com.ziwei.pomodoro.common;

import lombok.Data;

/**
 * 统一返回结果类
 * @param <T>
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result success(T data){
        Result r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> Result error(int code, String message){
        Result r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }
}
