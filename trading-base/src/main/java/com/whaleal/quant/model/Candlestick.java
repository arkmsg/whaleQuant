package com.whaleal.quant.model;

import com.whaleal.quant.enums.Interval;

/**
 * K线数据模型 - 专为指标计算优化
 * 使用double类型存储价格数据以获得最佳计算性能
 *
 * @author Trading System
 * @version 1.0.0
 */
public class Candlestick {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * K线周期（使用枚举）
     */
    private Interval interval;

    /**
     * 时间戳（秒级）
     */
    private Long timestamp;

    /**
     * 开盘价 - 使用double类型以获得最佳计算性能
     */
    private double open;

    /**
     * 最高价 - 使用double类型以获得最佳计算性能
     */
    private double high;

    /**
     * 最低价 - 使用double类型以获得最佳计算性能
     */
    private double low;

    /**
     * 收盘价 - 使用double类型以获得最佳计算性能
     */
    private double close;

    /**
     * 成交量
     */
    private long volume;

    /**
     * 成交额 - 使用double类型以获得最佳计算性能
     */
    private double amount;

    /**
     * 换手率
     */
    private double turnoverRate;

    /**
     * 构造函数
     */
    public Candlestick() {
    }

    /**
     * 全参构造函数
     */
    public Candlestick(String symbol, Interval interval, Long timestamp, double open, double high, double low, double close, long volume, double amount, double turnoverRate) {
        this.symbol = symbol;
        this.interval = interval;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.amount = amount;
        this.turnoverRate = turnoverRate;
    }

    /**
     * 获取股票代码
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 设置股票代码
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * 获取K线周期
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * 设置K线周期
     */
    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    /**
     * 获取时间戳
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取开盘价
     */
    public double getOpen() {
        return open;
    }

    /**
     * 设置开盘价
     */
    public void setOpen(double open) {
        this.open = open;
    }

    /**
     * 获取最高价
     */
    public double getHigh() {
        return high;
    }

    /**
     * 设置最高价
     */
    public void setHigh(double high) {
        this.high = high;
    }

    /**
     * 获取最低价
     */
    public double getLow() {
        return low;
    }

    /**
     * 设置最低价
     */
    public void setLow(double low) {
        this.low = low;
    }

    /**
     * 获取收盘价
     */
    public double getClose() {
        return close;
    }

    /**
     * 设置收盘价
     */
    public void setClose(double close) {
        this.close = close;
    }

    /**
     * 获取成交量
     */
    public long getVolume() {
        return volume;
    }

    /**
     * 设置成交量
     */
    public void setVolume(long volume) {
        this.volume = volume;
    }

    /**
     * 获取成交额
     */
    public double getAmount() {
        return amount;
    }

    /**
     * 设置成交额
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * 获取换手率
     */
    public double getTurnoverRate() {
        return turnoverRate;
    }

    /**
     * 设置换手率
     */
    public void setTurnoverRate(double turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String symbol;
        private Interval interval;
        private Long timestamp;
        private double open;
        private double high;
        private double low;
        private double close;
        private long volume;
        private double amount;
        private double turnoverRate;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder interval(Interval interval) {
            this.interval = interval;
            return this;
        }

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder open(double open) {
            this.open = open;
            return this;
        }

        public Builder high(double high) {
            this.high = high;
            return this;
        }

        public Builder low(double low) {
            this.low = low;
            return this;
        }

        public Builder close(double close) {
            this.close = close;
            return this;
        }

        public Builder volume(long volume) {
            this.volume = volume;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder turnoverRate(double turnoverRate) {
            this.turnoverRate = turnoverRate;
            return this;
        }

        public Candlestick build() {
            return new Candlestick(symbol, interval, timestamp, open, high, low, close, volume, amount, turnoverRate);
        }
    }

    /**
     * 获取Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
}
