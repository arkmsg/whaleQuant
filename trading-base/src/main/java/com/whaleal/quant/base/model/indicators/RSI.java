package com.whaleal.quant.base.model.indicators;

/**
 * RSI指标
 *
 * @author Trading System
 * @version 1.0.0
 */
public class RSI {

    private String symbol;

    /**
     * 时间戳（秒级）
     */
    private Long timestamp;

    private double rsi;
    private double avgGain;
    private double avgLoss;

    private int period = 14;

    private int signal = 0;

    private int signalStrength = 0;

    /**
     * 是否超卖
     */
    private Boolean isOversold;

    /**
     * 是否超买
     */
    private Boolean isOverbought;

    /**
     * 构造函数
     */
    public RSI() {
    }

    /**
     * 全参构造函数
     */
    public RSI(String symbol, Long timestamp, double rsi, double avgGain, double avgLoss, int period, int signal, int signalStrength, Boolean isOversold, Boolean isOverbought) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.rsi = rsi;
        this.avgGain = avgGain;
        this.avgLoss = avgLoss;
        this.period = period;
        this.signal = signal;
        this.signalStrength = signalStrength;
        this.isOversold = isOversold;
        this.isOverbought = isOverbought;
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
     * 获取RSI值
     */
    public double getRsi() {
        return rsi;
    }

    /**
     * 设置RSI值
     */
    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    /**
     * 获取平均增益
     */
    public double getAvgGain() {
        return avgGain;
    }

    /**
     * 设置平均增益
     */
    public void setAvgGain(double avgGain) {
        this.avgGain = avgGain;
    }

    /**
     * 获取平均损失
     */
    public double getAvgLoss() {
        return avgLoss;
    }

    /**
     * 设置平均损失
     */
    public void setAvgLoss(double avgLoss) {
        this.avgLoss = avgLoss;
    }

    /**
     * 获取周期
     */
    public int getPeriod() {
        return period;
    }

    /**
     * 设置周期
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * 获取信号
     */
    public int getSignal() {
        return signal;
    }

    /**
     * 设置信号
     */
    public void setSignal(int signal) {
        this.signal = signal;
    }

    /**
     * 获取信号强度
     */
    public int getSignalStrength() {
        return signalStrength;
    }

    /**
     * 设置信号强度
     */
    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    /**
     * 获取是否超卖
     */
    public Boolean getIsOversold() {
        return isOversold;
    }

    /**
     * 设置是否超卖
     */
    public void setIsOversold(Boolean isOversold) {
        this.isOversold = isOversold;
    }

    /**
     * 获取是否超买
     */
    public Boolean getIsOverbought() {
        return isOverbought;
    }

    /**
     * 设置是否超买
     */
    public void setIsOverbought(Boolean isOverbought) {
        this.isOverbought = isOverbought;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String symbol;
        private Long timestamp;
        private double rsi;
        private double avgGain;
        private double avgLoss;
        private int period = 14;
        private int signal = 0;
        private int signalStrength = 0;
        private Boolean isOversold;
        private Boolean isOverbought;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder rsi(double rsi) {
            this.rsi = rsi;
            return this;
        }

        public Builder avgGain(double avgGain) {
            this.avgGain = avgGain;
            return this;
        }

        public Builder avgLoss(double avgLoss) {
            this.avgLoss = avgLoss;
            return this;
        }

        public Builder period(int period) {
            this.period = period;
            return this;
        }

        public Builder signal(int signal) {
            this.signal = signal;
            return this;
        }

        public Builder signalStrength(int signalStrength) {
            this.signalStrength = signalStrength;
            return this;
        }

        public Builder isOversold(Boolean isOversold) {
            this.isOversold = isOversold;
            return this;
        }

        public Builder isOverbought(Boolean isOverbought) {
            this.isOverbought = isOverbought;
            return this;
        }

        public RSI build() {
            return new RSI(symbol, timestamp, rsi, avgGain, avgLoss, period, signal, signalStrength, isOversold, isOverbought);
        }
    }

    /**
     * 获取Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
}
