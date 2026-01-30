package com.whaleal.quant.binance.model;

/**
 * K线时间间隔枚举
 *
 * @author Binance SDK Team
 */
public enum KlineIntervalEnum {
    /**
     * 1分钟
     */
    MIN_1("1m"),

    /**
     * 3分钟
     */
    MIN_3("3m"),

    /**
     * 5分钟
     */
    MIN_5("5m"),

    /**
     * 15分钟
     */
    MIN_15("15m"),

    /**
     * 30分钟
     */
    MIN_30("30m"),

    /**
     * 1小时
     */
    HOUR_1("1h"),

    /**
     * 2小时
     */
    HOUR_2("2h"),

    /**
     * 4小时
     */
    HOUR_4("4h"),

    /**
     * 6小时
     */
    HOUR_6("6h"),

    /**
     * 8小时
     */
    HOUR_8("8h"),

    /**
     * 12小时
     */
    HOUR_12("12h"),

    /**
     * 1天
     */
    DAY_1("1d"),

    /**
     * 3天
     */
    DAY_3("3d"),

    /**
     * 1周
     */
    WEEK_1("1w"),

    /**
     * 1月
     */
    MONTH_1("1M");

    private final String value;

    KlineIntervalEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

