package com.whaleal.quant.core.time;

public enum TradingSession {
    PRE_MARKET("盘前交易"),
    OPENING("开盘时段"),
    REGULAR("正常交易"),
    CLOSING("收盘时段"),
    AFTER_HOURS("盘后交易"),
    NOT_TRADING("非交易时段");
    
    private final String description;
    
    TradingSession(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isTrading() {
        return this != NOT_TRADING;
    }
    
    public boolean isRegularTrading() {
        return this == REGULAR;
    }
    
    public boolean isExtendedHours() {
        return this == PRE_MARKET || this == AFTER_HOURS;
    }
}
