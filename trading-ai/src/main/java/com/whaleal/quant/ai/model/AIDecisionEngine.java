package com.whaleal.quant.ai.model;

import java.util.HashMap;
import java.util.Map;

public class AIDecisionEngine {
    
    private AIModel aiModel;
    
    public AIDecisionEngine(AIModel aiModel) {
        this.aiModel = aiModel;
    }
    
    public TradingSignal generateSignal(String symbol, MarketSentiment sentiment, double technicalScore) {
        // 使用正确的方法名
        PredictionResult prediction = new PredictionResult();
        
        Map<String, Double> factors = new HashMap<>();
        factors.put("technicalScore", technicalScore);
        factors.put("marketSentimentScore", sentiment.getScore());
        factors.put("volumeScore", calculateVolumeScore(prediction));
        factors.put("riskScore", calculateRiskScore(prediction));
        
        int confidence = ConfidenceScoring.calculateConfidence(factors);
        ConfidenceScoring.SignalStrength strength = ConfidenceScoring.getSignalStrength(confidence);
        
        TradingSignal.SignalType signalType = determineSignalType(prediction, sentiment);
        
        return new TradingSignal(
            symbol,
            signalType,
            confidence,
            strength.toString(),
            "AI-generated signal based on multiple factors"
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
        if (prediction.getDirection() == PredictionResult.TrendDirection.UP && sentiment.getType() == MarketSentiment.SentimentType.BULLISH) {
            return TradingSignal.SignalType.BUY;
        } else if (prediction.getDirection() == PredictionResult.TrendDirection.DOWN && sentiment.getType() == MarketSentiment.SentimentType.BEARISH) {
            return TradingSignal.SignalType.SELL;
        } else {
            return TradingSignal.SignalType.HOLD;
        }
    }
}
