package com.whaleal.quant.ai.model;

import java.util.Map;

public class ConfidenceScoring {
    
    private static final int HIGH_CONFIDENCE_THRESHOLD = 80;
    private static final int MEDIUM_CONFIDENCE_THRESHOLD = 60;
    
    public static int calculateConfidence(Map<String, Double> factors) {
        double totalScore = 0;
        double totalWeight = 0;
        
        // 技术指标权重
        if (factors.containsKey("technicalScore")) {
            totalScore += factors.get("technicalScore") * 0.4;
            totalWeight += 0.4;
        }
        
        // 市场情绪权重
        if (factors.containsKey("marketSentimentScore")) {
            totalScore += factors.get("marketSentimentScore") * 0.25;
            totalWeight += 0.25;
        }
        
        // 成交量权重
        if (factors.containsKey("volumeScore")) {
            totalScore += factors.get("volumeScore") * 0.15;
            totalWeight += 0.15;
        }
        
        // 风险评估权重
        if (factors.containsKey("riskScore")) {
            totalScore += factors.get("riskScore") * 0.2;
            totalWeight += 0.2;
        }
        
        if (totalWeight == 0) {
            return 0;
        }
        
        return (int) Math.round((totalScore / totalWeight) * 100);
    }
    
    public static SignalStrength getSignalStrength(int confidence) {
        if (confidence >= HIGH_CONFIDENCE_THRESHOLD) {
            return SignalStrength.STRONG;
        } else if (confidence >= MEDIUM_CONFIDENCE_THRESHOLD) {
            return SignalStrength.MODERATE;
        } else {
            return SignalStrength.WEAK;
        }
    }
    
    public enum SignalStrength {
        STRONG,
        MODERATE,
        WEAK
    }
}
