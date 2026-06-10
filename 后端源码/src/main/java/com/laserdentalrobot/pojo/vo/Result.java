package com.laserdentalrobot.pojo.vo;

import lombok.Data;

/**
 * 统一返回结果
 * 
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private String errorCode;

    // 成功
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        r.setErrorCode(null);
        return r;
    }

    // 失败
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        r.setData(null);
        r.setErrorCode("500");
        return r;
    }

    // 失败 带code
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(null);
        r.setErrorCode(String.valueOf(code));
        return r;
    }

    // 失败 带code和data
    public static <T> Result<T> error(Integer code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        r.setErrorCode(String.valueOf(code));
        return r;
    }
}
