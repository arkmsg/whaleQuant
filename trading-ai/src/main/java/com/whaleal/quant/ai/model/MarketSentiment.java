package com.whaleal.quant.ai.model;

import lombok.Data;

/**
 * 市场情绪分析结果
 * 表示AI模型对市场情绪的分析
 * 
 * @author whaleal
 * @version 1.0.0
 */
@Data
public class MarketSentiment {
    
    private String ticker;
    private SentimentType sentimentType;
    private double sentimentScore;
    private long timestamp;
    
    public MarketSentiment(String ticker, SentimentType sentimentType, double sentimentScore) {
        this.ticker = ticker;
        this.sentimentType = sentimentType;
        this.sentimentScore = sentimentScore;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 情绪类型枚举
     */
    public enum SentimentType {
        VERY_BULLISH("极度看涨", 1.0),
        BULLISH("看涨", 0.5),
        NEUTRAL("中性", 0.0),
        BEARISH("看跌", -0.5),
        VERY_BEARISH("极度看跌", -1.0);
        
        private final String description;
        private final double score;
        
        SentimentType(String description, double score) {
            this.description = description;
            this.score = score;
        }
        
        public String getDescription() {
            return description;
        }
        
        public double getScore() {
            return score;
        }
        
        /**
         * 根据情绪得分获取对应的情绪类型
         * 
         * @param score 情绪得分
         * @return 情绪类型
         */
        public static SentimentType fromScore(double score) {
            if (score >= 0.7) {
                return VERY_BULLISH;
            } else if (score >= 0.2) {
                return BULLISH;
            } else if (score > -0.2) {
                return NEUTRAL;
            } else if (score > -0.7) {
                return BEARISH;
            } else {
                return VERY_BEARISH;
            }
        }
    }
    
    /**
     * 判断是否为积极情绪
     * 
     * @return 是否为积极情绪
     */
    public boolean isPositive() {
        return sentimentScore > 0;
    }
    
    /**
     * 判断是否为消极情绪
     * 
     * @return 是否为消极情绪
     */
    public boolean isNegative() {
        return sentimentScore < 0;
    }
    
    /**
     * 判断是否为强烈情绪
     * 
     * @return 是否为强烈情绪
     */
    public boolean isStrong() {
        return Math.abs(sentimentScore) > 0.7;
    }
}