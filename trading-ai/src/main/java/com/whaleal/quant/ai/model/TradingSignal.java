package com.whaleal.quant.ai.model;

import lombok.Data;

/**
 * 交易信号类
 * 表示AI模型生成的交易建议
 * 
 * @author whaleal
 * @version 1.0.0
 */
@Data
public class TradingSignal {
    
    private String ticker;
    private SignalType signalType;
    private double confidence;
    private int confidenceScore;
    private double recommendedPrice;
    private long timestamp;
    
    public TradingSignal(String ticker, SignalType signalType, double confidence, int confidenceScore, double recommendedPrice) {
        this.ticker = ticker;
        this.signalType = signalType;
        this.confidence = confidence;
        this.confidenceScore = confidenceScore;
        this.recommendedPrice = recommendedPrice;
        this.timestamp = System.currentTimeMillis();
    }
    
    public TradingSignal(String ticker, SignalType signalType, double confidence, double recommendedPrice) {
        this(ticker, signalType, confidence, (int)(confidence * 100), recommendedPrice);
    }
    
    /**
     * 信号类型枚举
     */
    public enum SignalType {
        BUY("买入"),
        SELL("卖出"),
        HOLD("持有"),
        STRONG_BUY("强烈买入"),
        STRONG_SELL("强烈卖出");
        
        private final String description;
        
        SignalType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 判断是否为买入信号
     * 
     * @return 是否为买入信号
     */
    public boolean isBuySignal() {
        return signalType == SignalType.BUY || signalType == SignalType.STRONG_BUY;
    }
    
    /**
     * 判断是否为卖出信号
     * 
     * @return 是否为卖出信号
     */
    public boolean isSellSignal() {
        return signalType == SignalType.SELL || signalType == SignalType.STRONG_SELL;
    }
    
    /**
     * 判断是否为持有信号
     * 
     * @return 是否为持有信号
     */
    public boolean isHoldSignal() {
        return signalType == SignalType.HOLD;
    }
    
    /**
     * 判断是否为强烈信号
     * 
     * @return 是否为强烈信号
     */
    public boolean isStrongSignal() {
        return signalType == SignalType.STRONG_BUY || signalType == SignalType.STRONG_SELL;
    }
    
    /**
     * 判断是否为高置信度信号
     * 
     * @return 是否为高置信度信号
     */
    public boolean isHighConfidence() {
        return confidence > 0.7;
    }
    
    /**
     * 判断是否为高质量信号（基于0-100评分系统）
     * 
     * @return 是否为高质量信号
     */
    public boolean isHighQualitySignal() {
        return confidenceScore >= 70;
    }
    
    /**
     * 判断是否为强烈信号（基于0-100评分系统）
     * 
     * @return 是否为强烈信号
     */
    public boolean isVeryStrongSignal() {
        return confidenceScore >= 90;
    }
}