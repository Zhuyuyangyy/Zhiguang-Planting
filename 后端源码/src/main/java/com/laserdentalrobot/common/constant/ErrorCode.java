package com.laserdentalrobot.common.constant;

/**
 * 错误码常量 就放这了
 */
public class ErrorCode {
    // 通用http错误
    
    public static final String SUCCESS = "200";
    public static final String PARAM_ERROR = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String SERVER_ERROR = "500";
    public static final String RATE_LIMIT = "429";

    // 业务错误 自己定义的
    
    public static final String MODEL_NOT_FOUND = "1004";
    public static final String AVATAR_ANIMATION_NOT_FOUND = "3004";
    public static final String DEVICE_PARAM_INVALID = "4003";
    public static final String USER_NOT_FOUND = "5004";
    public static final String USER_NAME_EXIST = "5005";
    public static final String PASSWORD_ERROR = "5006";
    public static final String USER_BANNED = "5007";
    public static final String OPERATE_SELF_FORBID = "5008";
    public static final String TOKEN_INVALID = "5009";

    // 错误提示文字
    public static final String MSG_PARAM_ERROR = "参数格式错误";
    public static final String MSG_UNAUTHORIZED = "请先登录";
    public static final String MSG_FORBIDDEN = "无权限访问";
    public static final String MSG_NOT_FOUND = "资源不存在";
    public static final String MSG_SERVER_ERROR = "服务器内部错误";
    public static final String MSG_MODEL_NOT_FOUND = "3D模型文件不存在";
    public static final String MSG_USER_NOT_FOUND = "用户不存在";
    public static final String MSG_USER_NAME_EXIST = "用户名已存在";
    public static final String MSG_PASSWORD_ERROR = "密码错误";
    public static final String MSG_USER_BANNED = "账号已被封禁，请联系管理员";
    public static final String MSG_OPERATE_SELF_FORBID = "禁止封禁/解封自身账号";
    public static final String MSG_TOKEN_INVALID = "Token无效或已过期，请重新登录";
}
