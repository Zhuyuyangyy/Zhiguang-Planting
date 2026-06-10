package com.laserdentalrobot.pojo.vo;

/**
 * AI聊天响应 前端要这个格式
 */
public class AIChatResponse {
    private int code;
    private String msg;
    private String data;

    public AIChatResponse() {}

    public AIChatResponse(int code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public AIChatResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = "";
    }

    public static AIChatResponse success(String data) {
        return new AIChatResponse(200, "请求成功", data);
    }

    public static AIChatResponse fail(int code, String msg) {
        return new AIChatResponse(code, msg);
    }

    // getter setter 手写的 没用lombok
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
