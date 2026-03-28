package com.lilac.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.lilac.domain.result.Result;
import com.lilac.enums.HttpsCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理自定义的业务异常
     *
     * @param e 业务异常
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleSystemException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理未登录异常
     *
     * @param e 未登录异常
     * @return Result
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> notLoginException(NotLoginException e) {
        log.error("NotLoginException", e);
        return Result.error(HttpsCodeEnum.NEED_LOGIN);
    }

    /**
     * 处理未授权异常
     *
     * @param e 未授权异常
     * @return Result
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return Result.error(HttpsCodeEnum.UNAUTHORIZED, e.getMessage());
    }

    /**
     * 处理所有未被捕获的未知异常
     *
     * @param e 未知异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未捕获的系统异常！ ", e);
        // 对外屏蔽内部细节，返回统一的系统错误提示
        return Result.error(HttpsCodeEnum.SYSTEM_ERROR);
    }
}