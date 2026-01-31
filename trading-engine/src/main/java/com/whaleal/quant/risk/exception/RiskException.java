package com.whaleal.quant.risk.exception;

public class RiskException extends RuntimeException {
    public RiskException(String message) {
        super(message);
    }

    public RiskException(String message, Throwable cause) {
        super(message, cause);
    }
}
