package com.whaleal.quant.ai.model;

import com.whaleal.quant.core.indicator.MACDIndicator;
import com.whaleal.quant.core.indicator.RSIIndicator;
import com.whaleal.quant.core.indicator.BOLLIndicator;
import com.whaleal.quant.core.model.Bar;
import java.util.List;

public class ConfidenceScoreSystem {
    
    private static final int MAX_SCORE = 100;
    private static final int MIN_CONFIDENCE = 70;
    
    private final MACDIndicator macdIndicator;
    private final RSIIndicator rsiIndicator;
    private final BOLLIndicator bollIndicator;
    
    public ConfidenceScoreSystem() {
        this.macdIndicator = new MACDIndicator();
        this.rsiIndicator = new RSIIndicator();
        this.bollIndicator = new BOLLIndicator();
    }
    
    public int calculateConfidenceScore(String ticker, List<Bar> bars, TradingSignal.SignalType signalType) {
        if (bars.size() < 30) {
            return 0;
        }
        
        int score = 0;
        
        // 技术指标分析 (40%)
        score += calculateTechnicalIndicatorScore(bars, signalType) * 0.4;
        
        // 市场情绪分析 (25%)
        score += calculateMarketSentimentScore(bars) * 0.25;
        
        // 量价分析 (20%)
        score += calculateVolumePriceScore(bars, signalType) * 0.2;
        
        // 趋势强度分析 (15%)
        score += calculateTrendStrengthScore(bars, signalType) * 0.15;
        
        return Math.min(MAX_SCORE, Math.max(0, score));
    }
    
    private int calculateTechnicalIndicatorScore(List<Bar> bars, TradingSignal.SignalType signalType) {
        int score = 0;
        
        // MACD分析
        double macdValue = macdIndicator.calculate(bars);
        if (signalType == TradingSignal.SignalType.BUY && macdValue > 0) {
            score += 25;
        } else if (signalType == TradingSignal.SignalType.SELL && macdValue < 0) {
            score += 25;
        }
        
        // RSI分析
        double rsiValue = rsiIndicator.calculate(bars);
        if (signalType == TradingSignal.SignalType.BUY && rsiValue > 30 && rsiValue < 70) {
            score += 15;
        } else if (signalType == TradingSignal.SignalType.SELL && rsiValue > 70) {
            score += 15;
        }
        
        // 布林带分析
        double bollValue = bollIndicator.calculate(bars);
        if (signalType == TradingSignal.SignalType.BUY && bollValue < 0.3) {
            score += 10;
        } else if (signalType == TradingSignal.SignalType.SELL && bollValue > 0.7) {
            score += 10;
        }
        
        return score;
    }
    
    private int calculateMarketSentimentScore(List<Bar> bars) {
        int score = 0;
        
        // 最近价格变化趋势
        double recentChange = (bars.get(bars.size() - 1).getClose().doubleValue() - 
                              bars.get(bars.size() - 10).getClose().doubleValue()) / 
                             bars.get(bars.size() - 10).getClose().doubleValue();
        
        if (Math.abs(recentChange) > 0.05) {
            score += 15;
        } else if (Math.abs(recentChange) > 0.02) {
            score += 10;
        }
        
        // 波动率分析
        double volatility = calculateVolatility(bars);
        if (volatility > 0.02) {
            score += 10;
        }
        
        return score;
    }
    
    private int calculateVolumePriceScore(List<Bar> bars, TradingSignal.SignalType signalType) {
        int score = 0;
        
        // 成交量分析
        double recentVolume = 0;
        double averageVolume = 0;
        
        for (int i = bars.size() - 5; i < bars.size(); i++) {
            recentVolume += bars.get(i).getVolume().doubleValue();
        }
        
        for (Bar bar : bars) {
            averageVolume += bar.getVolume().doubleValue();
        }
        averageVolume /= bars.size();
        
        double volumeRatio = recentVolume / (averageVolume * 5);
        
        if (volumeRatio > 1.5) {
            score += 15;
        } else if (volumeRatio > 1.2) {
            score += 10;
        }
        
        // 量价配合分析
        double priceChange = (bars.get(bars.size() - 1).getClose().doubleValue() - 
                             bars.get(bars.size() - 2).getClose().doubleValue()) / 
                            bars.get(bars.size() - 2).getClose().doubleValue();
        
        double volumeChange = (bars.get(bars.size() - 1).getVolume().doubleValue() - 
                              bars.get(bars.size() - 2).getVolume().doubleValue()) / 
                             bars.get(bars.size() - 2).getVolume().doubleValue();
        
        if ((signalType == TradingSignal.SignalType.BUY && priceChange > 0 && volumeChange > 0) ||
            (signalType == TradingSignal.SignalType.SELL && priceChange < 0 && volumeChange > 0)) {
            score += 5;
        }
        
        return score;
    }
    
    private int calculateTrendStrengthScore(List<Bar> bars, TradingSignal.SignalType signalType) {
        int score = 0;
        
        // 趋势线斜率分析
        double slope = calculateTrendSlope(bars);
        
        if ((signalType == TradingSignal.SignalType.BUY && slope > 0.001) ||
            (signalType == TradingSignal.SignalType.SELL && slope < -0.001)) {
            score += 10;
        }
        
        // 移动平均线排列
        if (checkMovingAverageAlignment(bars, signalType)) {
            score += 5;
        }
        
        return score;
    }
    
    private double calculateVolatility(List<Bar> bars) {
        double sum = 0;
        double average = 0;
        
        for (Bar bar : bars) {
            average += bar.getClose().doubleValue();
        }
        average /= bars.size();
        
        for (Bar bar : bars) {
            double diff = bar.getClose().doubleValue() - average;
            sum += diff * diff;
        }
        
        return Math.sqrt(sum / bars.size()) / average;
    }
    
    private double calculateTrendSlope(List<Bar> bars) {
        int n = bars.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += bars.get(i).getClose().doubleValue();
            sumXY += i * bars.get(i).getClose().doubleValue();
            sumX2 += i * i;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        return slope / bars.get(0).getClose().doubleValue();
    }
    
    private boolean checkMovingAverageAlignment(List<Bar> bars, TradingSignal.SignalType signalType) {
        if (bars.size() < 50) {
            return false;
        }
        
        double shortMA = calculateMA(bars, 20);
        double mediumMA = calculateMA(bars, 50);
        double longMA = calculateMA(bars, 100);
        
        if (signalType == TradingSignal.SignalType.BUY) {
            return shortMA > mediumMA && mediumMA > longMA;
        } else {
            return shortMA < mediumMA && mediumMA < longMA;
        }
    }
    
    private double calculateMA(List<Bar> bars, int period) {
        if (bars.size() < period) {
            return 0;
        }
        
        double sum = 0;
        for (int i = bars.size() - period; i < bars.size(); i++) {
            sum += bars.get(i).getClose().doubleValue();
        }
        
        return sum / period;
    }
    
    public boolean isHighQualitySignal(int confidenceScore) {
        return confidenceScore >= MIN_CONFIDENCE;
    }
    
    public String getSignalQualityDescription(int confidenceScore) {
        if (confidenceScore >= 90) {
            return "强烈信号";
        } else if (confidenceScore >= 80) {
            return "高质量信号";
        } else if (confidenceScore >= 70) {
            return "中等质量信号";
        } else {
            return "低质量信号";
        }
    }
}
