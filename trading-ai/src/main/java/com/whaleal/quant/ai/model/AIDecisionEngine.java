package com.whaleal.quant.ai.model;

import java.util.HashMap;
import java.util.Map;

public class AIDecisionEngine {
    
    private AIModel aiModel;
    
    public AIDecisionEngine(AIModel aiModel) {
        this.aiModel = aiModel;
    }
    
    public TradingSignal generateSignal(String symbol, MarketSentiment sentiment, double technicalScore) {
        // 使用正确的构造函数
        PredictionResult prediction = new PredictionResult(symbol, 0.0, technicalScore, PredictionResult.TrendDirection.NEUTRAL);
        
        Map<String, Double> factors = new HashMap<>();
        factors.put("technicalScore", technicalScore);
        factors.put("marketSentimentScore", sentiment.getSentimentScore());
        factors.put("volumeScore", calculateVolumeScore(prediction));
        factors.put("riskScore", calculateRiskScore(prediction));
        
        int confidence = ConfidenceScoring.calculateConfidence(factors);
        ConfidenceScoring.SignalStrength strength = ConfidenceScoring.getSignalStrength(confidence);
        
        TradingSignal.SignalType signalType = determineSignalType(prediction, sentiment);
        
        return new TradingSignal(
            symbol,
            signalType,
            confidence / 100.0, // 转换为0-1范围
            0.0 // 推荐价格，暂时设为0.0
        );
    }
    
    private double calculateVolumeScore(PredictionResult prediction) {
        // 这里可以添加成交量分析逻辑
        return 0.7; // 示例值
    }
    
    private double calculateRiskScore(PredictionResult prediction) {
        // 这里可以添加风险评估逻辑
        return 0.8; // 示例值
    }
    
    private TradingSignal.SignalType determineSignalType(PredictionResult prediction, MarketSentiment sentiment) {
        if (prediction.getTrendDirection() == PredictionResult.TrendDirection.BULLISH && sentiment.getSentimentType() == MarketSentiment.SentimentType.BULLISH) {
            return TradingSignal.SignalType.BUY;
        } else if (prediction.getTrendDirection() == PredictionResult.TrendDirection.BEARISH && sentiment.getSentimentType() == MarketSentiment.SentimentType.BEARISH) {
            return TradingSignal.SignalType.SELL;
        } else {
            return TradingSignal.SignalType.HOLD;
        }
    }
}
