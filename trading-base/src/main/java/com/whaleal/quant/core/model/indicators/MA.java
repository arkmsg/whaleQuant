package com.whaleal.quant.core.model.indicators;

/**
 * MA指标
 * 移动平均线指标
 *
 * @author whaleal
 * @version 1.0.0
 */
public class MA {
    
    private String symbol;
    private Long timestamp;
    private double ma;
    private int period = 20;
    private String type = "SMA"; // SMA, EMA, WMA
    private int signal = 0;
    private double price;
    private double prevMa;
    
    /**
     * 构造函数
     */
    public MA() {
    }
    
    /**
     * 全参构造函数
     */
    public MA(String symbol, Long timestamp, double ma, int period, String type, 
              int signal, double price, double prevMa) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.ma = ma;
        this.period = period;
        this.type = type;
        this.signal = signal;
        this.price = price;
        this.prevMa = prevMa;
    }
    
    /**
     * Builder类
     */
    public static class Builder {
        private String symbol;
        private Long timestamp;
        private double ma;
        private int period = 20;
        private String type = "SMA";
        private int signal = 0;
        private double price;
        private double prevMa;
        
        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }
        
        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder ma(double ma) {
            this.ma = ma;
            return this;
        }
        
        public Builder period(int period) {
            this.period = period;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder signal(int signal) {
            this.signal = signal;
            return this;
        }
        
        public Builder price(double price) {
            this.price = price;
            return this;
        }
        
        public Builder prevMa(double prevMa) {
            this.prevMa = prevMa;
            return this;
        }
        
        public MA build() {
            return new MA(symbol, timestamp, ma, period, type, signal, price, prevMa);
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
    
    public double getMa() {
        return ma;
    }
    
    public void setMa(double ma) {
        this.ma = ma;
    }
    
    public int getPeriod() {
        return period;
    }
    
    public void setPeriod(int period) {
        this.period = period;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getSignal() {
        return signal;
    }
    
    public void setSignal(int signal) {
        this.signal = signal;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getPrevMa() {
        return prevMa;
    }
    
    public void setPrevMa(double prevMa) {
        this.prevMa = prevMa;
    }
}
