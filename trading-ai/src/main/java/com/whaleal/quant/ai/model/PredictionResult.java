package com.whaleal.quant.ai.model;

import lombok.Data;

/**
 * 预测结果类
 * 表示AI模型的预测输出
 * 
 * @author whaleal
 * @version 1.0.0
 */
@Data
public class PredictionResult {
    
    private String ticker;
    private double predictedPrice;
    private double confidence;
    private TrendDirection trendDirection;
    private long timestamp;
    
    public PredictionResult(String ticker, double predictedPrice, double confidence, TrendDirection trendDirection) {
        this.ticker = ticker;
        this.predictedPrice = predictedPrice;
        this.confidence = confidence;
        this.trendDirection = trendDirection;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 趋势方向枚举
     */
    public enum TrendDirection {
        BULLISH("看涨"),
        BEARISH("看跌"),
        NEUTRAL("中性");
        
        private final String description;
        
        TrendDirection(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 判断是否为高置信度预测
     * 
     * @return 是否为高置信度
     */
    public boolean isHighConfidence() {
        return confidence > 0.7;
    }
    
    /**
     * 判断是否为低置信度预测
     * 
     * @return 是否为低置信度
     */
    public boolean isLowConfidence() {
        return confidence < 0.3;
    }
}