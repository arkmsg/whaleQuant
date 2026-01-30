package com.whaleal.quant.binance.exception;

import lombok.Getter;

/**
 * 币安SDK统一异常类
 *
 * <p>提供结构化的异常信息，便于问题定位和处理。
 *
 * @author Binance SDK Team
 * @version 1.0.0
 */
@Getter
public class BinanceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String errorMessage;
    private final transient Object data;

    public BinanceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
        this.data = null;
    }

    public BinanceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.errorMessage = customMessage;
        this.data = null;
    }

    public BinanceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
        this.data = null;
    }

    public BinanceException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = customMessage;
        this.data = null;
    }

    public BinanceException(ErrorCode errorCode, String customMessage, Object data) {
        super(customMessage);
        this.errorCode = errorCode;
        this.errorMessage = customMessage;
        this.data = data;
    }

    /**
     * 是否可重试
     */
    public boolean isRetryable() {
        return errorCode.isRetryable();
    }

    /**
     * 获取完整错误信息
     */
    public String getFullMessage() {
        return String.format("[%s] %s", errorCode.getCode(), errorMessage);
    }

    @Override
    public String toString() {
        return String.format("BinanceException{code=%s, message=%s, retryable=%s}",
                errorCode.getCode(), errorMessage, isRetryable());
    }
}

