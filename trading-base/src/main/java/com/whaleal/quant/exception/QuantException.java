package com.whaleal.quant.exception;

/**
 * 量化交易系统基础异常类
 * 所有量化交易相关的异常都应该继承自这个类
 *
 * @author whaleal
 * @version 1.0.0
 */
public class QuantException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 构造方法
     *
     * @param message 错误信息
     */
    public QuantException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }

    /**
     * 构造方法
     *
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public QuantException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造方法
     *
     * @param message 错误信息
     * @param cause 异常原因
     */
    public QuantException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
    }

    /**
     * 构造方法
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 异常原因
     */
    public QuantException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
}
