package com.whaleal.quant.base.enums;

/**
 * K线周期枚举
 *
 * @author Trading System
 * @version 1.0.0
 */
public enum Interval {
    /**
     * 1分钟
     */
    MINUTE_1("1m", 60),
    /**
     * 5分钟
     */
    MINUTE_5("5m", 300),
    /**
     * 15分钟
     */
    MINUTE_15("15m", 900),
    /**
     * 30分钟
     */
    MINUTE_30("30m", 1800),
    /**
     * 1小时
     */
    HOUR_1("1h", 3600),
    /**
     * 4小时
     */
    HOUR_4("4h", 14400),
    /**
     * 1天
     */
    DAY_1("1d", 86400),
    /**
     * 1周
     */
    WEEK_1("1w", 604800),
    /**
     * 1月
     */
    MONTH_1("1M", 2592000);

    private final String code;
    private final int seconds;

    Interval(String code, int seconds) {
        this.code = code;
        this.seconds = seconds;
    }

    public String getCode() {
        return code;
    }

    public int getSeconds() {
        return seconds;
    }

    /**
     * 根据代码获取枚举
     */
    public static Interval fromCode(String code) {
        for (Interval interval : values()) {
            if (interval.code.equals(code)) {
                return interval;
            }
        }
        throw new IllegalArgumentException("Invalid interval code: " + code);
    }
}
