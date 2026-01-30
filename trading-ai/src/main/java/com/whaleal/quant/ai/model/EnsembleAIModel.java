package com.whaleal.quant.ai.model;

import com.whaleal.quant.model.Bar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 集成学习AI模型实现
 * 基于多个模型的集成预测和交易信号生成
 *
 * @author whaleal
 * @version 1.0.0
 */
public class EnsembleAIModel extends BaseAIModel {

    private List<AIModel> baseModels;

    @Override
    protected void initializeInternal(Map<String, Object> config) {
        // 初始化集成模型，创建多个基础模型
        baseModels = new ArrayList<>();

        // 添加线性模型
        LinearAIModel linearModel = new LinearAIModel();
        linearModel.initialize(config);
        baseModels.add(linearModel);

        // 添加树模型
        TreeAIModel treeModel = new TreeAIModel();
        treeModel.initialize(config);
        baseModels.add(treeModel);
    }

    @Override
    protected ModelInfo createModelInfo() {
        return new ModelInfo(
            "Ensemble Model",
            "ensemble",
            ModelInfo.ModelType.PROPRIETARY,
            Map.of(
                "type", "ensemble",
                "description", "基于多个模型集成的趋势预测模型",
                "base_models", List.of("linear", "tree")
            )
        );
    }

    @Override
    protected PredictionResult doPredictTrend(String ticker, List<Bar> bars) {
        // 基于集成模型的趋势预测实现
        List<PredictionResult> predictions = new ArrayList<>();

        // 获取每个基础模型的预测结果
        for (AIModel model : baseModels) {
            predictions.add(model.predictTrend(ticker, bars));
        }

        // 集成预测结果（简单投票和加权平均）
        int bullishCount = 0;
        int bearishCount = 0;
        double totalConfidence = 0;
        double totalPredictedPrice = 0;

        for (PredictionResult prediction : predictions) {
            if (prediction.getTrendDirection() == PredictionResult.TrendDirection.BULLISH) {
                bullishCount++;
            } else if (prediction.getTrendDirection() == PredictionResult.TrendDirection.BEARISH) {
                bearishCount++;
            }
            totalConfidence += prediction.getConfidence();
            totalPredictedPrice += prediction.getPredictedPrice();
        }

        PredictionResult.TrendDirection trendDirection = bullishCount > bearishCount ? PredictionResult.TrendDirection.BULLISH : PredictionResult.TrendDirection.BEARISH;
        double avgConfidence = totalConfidence / predictions.size();
        double avgPredictedPrice = totalPredictedPrice / predictions.size();
        // 集成模型的置信度可以稍微提高，因为多个模型一致
        double ensembleConfidence = Math.min(avgConfidence * 1.05, 0.95);

        return new PredictionResult(
            ticker,
            avgPredictedPrice,
            ensembleConfidence,
            trendDirection
        );
    }

    @Override
    protected Map<String, PredictionResult> doPredictBatchTrend(List<String> tickers, Map<String, List<Bar>> barsMap) {
        // 批量趋势预测实现
        Map<String, PredictionResult> results = new java.util.HashMap<>();
        for (String ticker : tickers) {
            results.put(ticker, doPredictTrend(ticker, barsMap.get(ticker)));
        }
        return results;
    }

    @Override
    protected MarketSentiment doAnalyzeMarketSentiment(String ticker, List<Bar> bars) {
        // 市场情绪分析实现
        List<MarketSentiment> sentiments = new ArrayList<>();

        // 获取每个基础模型的情绪分析结果
        for (AIModel model : baseModels) {
            sentiments.add(model.analyzeMarketSentiment(ticker, bars));
        }

        // 集成情绪分析结果
        double totalScore = 0;
        int bullishCount = 0;
        int bearishCount = 0;
        int neutralCount = 0;

        for (MarketSentiment sentiment : sentiments) {
            totalScore += sentiment.getSentimentScore();
            switch (sentiment.getSentimentType()) {
                case BULLISH:
                case VERY_BULLISH:
                    bullishCount++;
                    break;
                case BEARISH:
                case VERY_BEARISH:
                    bearishCount++;
                    break;
                case NEUTRAL:
                    neutralCount++;
                    break;
            }
        }

        double avgScore = totalScore / sentiments.size();
        MarketSentiment.SentimentType sentimentType;

        if (bullishCount > bearishCount && bullishCount > neutralCount) {
            sentimentType = MarketSentiment.SentimentType.BULLISH;
        } else if (bearishCount > bullishCount && bearishCount > neutralCount) {
            sentimentType = MarketSentiment.SentimentType.BEARISH;
        } else {
            sentimentType = MarketSentiment.SentimentType.NEUTRAL;
        }

        return new MarketSentiment(
            ticker,
            sentimentType,
            avgScore
        );
    }

    @Override
    protected TradingSignal doGenerateTradingSignal(String ticker, List<Bar> bars) {
        // 交易信号生成实现
        List<TradingSignal> signals = new ArrayList<>();

        // 获取每个基础模型的交易信号
        for (AIModel model : baseModels) {
            signals.add(model.generateTradingSignal(ticker, bars));
        }

        // 集成交易信号
        int buyCount = 0;
        int sellCount = 0;
        int holdCount = 0;
        double totalConfidence = 0;
        double totalRecommendedPrice = 0;

        for (TradingSignal signal : signals) {
            if (signal.isBuySignal()) {
                buyCount++;
            } else if (signal.isSellSignal()) {
                sellCount++;
            } else if (signal.isHoldSignal()) {
                holdCount++;
            }
            totalConfidence += signal.getConfidence();
            totalRecommendedPrice += signal.getRecommendedPrice();
        }

        TradingSignal.SignalType signalType;
        if (buyCount > sellCount && buyCount > holdCount) {
            signalType = TradingSignal.SignalType.BUY;
        } else if (sellCount > buyCount && sellCount > holdCount) {
            signalType = TradingSignal.SignalType.SELL;
        } else {
            signalType = TradingSignal.SignalType.HOLD;
        }

        double avgConfidence = totalConfidence / signals.size();
        double avgRecommendedPrice = totalRecommendedPrice / signals.size();
        // 集成模型的置信度可以稍微提高，因为多个模型一致
        double ensembleConfidence = Math.min(avgConfidence * 1.05, 0.95);

        return new TradingSignal(
            ticker,
            signalType,
            ensembleConfidence,
            avgRecommendedPrice
        );
    }

    @Override
    protected void cleanupResources() {
        // 清理集成模型资源
        for (AIModel model : baseModels) {
            model.close();
        }
        baseModels.clear();
    }
}
