package com.whaleal.quant.risk.exception;

import com.whaleal.quant.base.exception.QuantException;

public class RiskException extends QuantException {
    public RiskException(String message) {
        super("RISK_ERROR", message);
    }

    public RiskException(String message, Throwable cause) {
        super("RISK_ERROR", message, cause);
    }
}
