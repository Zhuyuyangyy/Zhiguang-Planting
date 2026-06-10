package com.laserdentalrobot.common.exception;

import com.laserdentalrobot.pojo.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理 报错了都走这
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBusinessException(BusinessException ex) {
        int code = 500;
        try {
            code = Integer.parseInt(ex.getErrorCode());
        } catch (NumberFormatException e) {
            // 有时候errorCode不是数字 就给个404
            code = 404;
        }
        return Result.error(code, ex.getMessage());
    }

    // 参数校验没过
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return Result.error(400, "参数校验失败", errors);
    }

    // 请求体格式不对
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return Result.error(400, "请求体格式错误");
    }

    // 兜底 其他所有异常
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleGeneralException(Exception ex) {
        return Result.error(500, "服务器异常：" + ex.getMessage());
    }
}
