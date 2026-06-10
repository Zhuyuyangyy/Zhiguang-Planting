package com.laserdentalrobot.common.exception;

import com.laserdentalrobot.common.constant.ErrorCode;
import lombok.Getter;

/**
 * 业务异常 就两种构造方式
 */
@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final String message;

    // 带错误码和提示
    public BusinessException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    // 只传提示 默认500
    public BusinessException(String message) {
        this.errorCode = ErrorCode.SERVER_ERROR;
        this.message = message;
    }
}
