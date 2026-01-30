package com.whaleal.quant.core.model.indicators;

/**
 * MACD指标
 * 移动平均线收敛发散指标
 *
 * @author whaleal
 * @version 1.0.0
 */
public class MACD {
    
    private String symbol;
    private Long timestamp;
    private double macd;
    private double signal;
    private double histogram;
    private int fastPeriod = 12;
    private int slowPeriod = 26;
    private int signalPeriod = 9;
    private int trendSignal = 0;
    private int histogramSignal = 0;
    
    /**
     * 构造函数
     */
    public MACD() {
    }
    
    /**
     * 全参构造函数
     */
    public MACD(String symbol, Long timestamp, double macd, double signal, double histogram, 
                int fastPeriod, int slowPeriod, int signalPeriod, 
                int trendSignal, int histogramSignal) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.macd = macd;
        this.signal = signal;
        this.histogram = histogram;
        this.fastPeriod = fastPeriod;
        this.slowPeriod = slowPeriod;
        this.signalPeriod = signalPeriod;
        this.trendSignal = trendSignal;
        this.histogramSignal = histogramSignal;
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private String symbol;
        private Long timestamp;
        private double macd;
        private double signal;
        private double histogram;
        private int fastPeriod = 12;
        private int slowPeriod = 26;
        private int signalPeriod = 9;
        private int trendSignal = 0;
        private int histogramSignal = 0;
        
        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }
        
        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder macd(double macd) {
            this.macd = macd;
            return this;
        }
        
        public Builder signal(double signal) {
            this.signal = signal;
            return this;
        }
        
        public Builder histogram(double histogram) {
            this.histogram = histogram;
            return this;
        }
        
        public Builder fastPeriod(int fastPeriod) {
            this.fastPeriod = fastPeriod;
            return this;
        }
        
        public Builder slowPeriod(int slowPeriod) {
            this.slowPeriod = slowPeriod;
            return this;
        }
        
        public Builder signalPeriod(int signalPeriod) {
            this.signalPeriod = signalPeriod;
            return this;
        }
        
        public Builder trendSignal(int trendSignal) {
            this.trendSignal = trendSignal;
            return this;
        }
        
        public Builder histogramSignal(int histogramSignal) {
            this.histogramSignal = histogramSignal;
            return this;
        }
        
        public MACD build() {
            return new MACD(symbol, timestamp, macd, signal, histogram, 
                          fastPeriod, slowPeriod, signalPeriod, 
                          trendSignal, histogramSignal);
        }
    }
    
    /**
     * 获取Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getMacd() {
        return macd;
    }
    
    public void setMacd(double macd) {
        this.macd = macd;
    }
    
    public double getSignal() {
        return signal;
    }
    
    public void setSignal(double signal) {
        this.signal = signal;
    }
    
    public double getHistogram() {
        return histogram;
    }
    
    public void setHistogram(double histogram) {
        this.histogram = histogram;
    }
    
    public int getFastPeriod() {
        return fastPeriod;
    }
    
    public void setFastPeriod(int fastPeriod) {
        this.fastPeriod = fastPeriod;
    }
    
    public int getSlowPeriod() {
        return slowPeriod;
    }
    
    public void setSlowPeriod(int slowPeriod) {
        this.slowPeriod = slowPeriod;
    }
    
    public int getSignalPeriod() {
        return signalPeriod;
    }
    
    public void setSignalPeriod(int signalPeriod) {
        this.signalPeriod = signalPeriod;
    }
    
    public int getTrendSignal() {
        return trendSignal;
    }
    
    public void setTrendSignal(int trendSignal) {
        this.trendSignal = trendSignal;
    }
    
    public int getHistogramSignal() {
        return histogramSignal;
    }
    
    public void setHistogramSignal(int histogramSignal) {
        this.histogramSignal = histogramSignal;
    }
}
