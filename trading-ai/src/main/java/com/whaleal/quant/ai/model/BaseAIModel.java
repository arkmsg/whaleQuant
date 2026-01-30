package com.whaleal.quant.ai.model;

import com.whaleal.quant.model.Bar;

import java.util.List;
import java.util.Map;

/**
 * 基础AI模型抽象类
 * 提供通用的模型实现和工具方法
 *
 * @author whaleal
 * @version 1.0.0
 */
public abstract class BaseAIModel implements AIModel {

    protected Map<String, Object> config;
    protected ModelInfo modelInfo;
    protected ConfidenceScoreSystem confidenceScoreSystem;

    @Override
    public void initialize(Map<String, Object> config) {
        this.config = config;
        this.modelInfo = createModelInfo();
        this.confidenceScoreSystem = new ConfidenceScoreSystem();
        initializeInternal(config);
    }

    /**
     * 内部初始化方法，由子类实现
     *
     * @param config 模型配置
     */
    protected abstract void initializeInternal(Map<String, Object> config);

    /**
     * 创建模型信息
     *
     * @return 模型信息
     */
    protected abstract ModelInfo createModelInfo();

    @Override
    public PredictionResult predictTrend(String ticker, List<Bar> bars) {
        validateInput(ticker, bars);
        return doPredictTrend(ticker, bars);
    }

    /**
     * 执行趋势预测，由子类实现
     *
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 预测结果
     */
    protected abstract PredictionResult doPredictTrend(String ticker, List<Bar> bars);

    @Override
    public Map<String, PredictionResult> predictBatchTrend(List<String> tickers, Map<String, List<Bar>> barsMap) {
        validateBatchInput(tickers, barsMap);
        return doPredictBatchTrend(tickers, barsMap);
    }

    /**
     * 执行批量趋势预测，由子类实现
     *
     * @param tickers 股票代码列表
     * @param barsMap K线数据映射
     * @return 多个股票的预测结果
     */
    protected abstract Map<String, PredictionResult> doPredictBatchTrend(List<String> tickers, Map<String, List<Bar>> barsMap);

    @Override
    public MarketSentiment analyzeMarketSentiment(String ticker, List<Bar> bars) {
        validateInput(ticker, bars);
        return doAnalyzeMarketSentiment(ticker, bars);
    }

    /**
     * 执行市场情绪分析，由子类实现
     *
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 市场情绪分析结果
     */
    protected abstract MarketSentiment doAnalyzeMarketSentiment(String ticker, List<Bar> bars);

    @Override
    public TradingSignal generateTradingSignal(String ticker, List<Bar> bars) {
        validateInput(ticker, bars);
        return doGenerateTradingSignal(ticker, bars);
    }

    /**
     * 执行交易信号生成，由子类实现
     *
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 交易信号
     */
    protected abstract TradingSignal doGenerateTradingSignal(String ticker, List<Bar> bars);

    @Override
    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Override
    public void close() {
        cleanupResources();
    }

    /**
     * 清理资源，由子类实现
     */
    protected abstract void cleanupResources();

    /**
     * 验证输入参数
     *
     * @param ticker 股票代码
     * @param bars K线数据
     */
    protected void validateInput(String ticker, List<Bar> bars) {
        if (ticker == null || ticker.isEmpty()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }
        if (bars == null || bars.isEmpty()) {
            throw new IllegalArgumentException("Bars cannot be null or empty");
        }
    }

    /**
     * 验证批量输入参数
     *
     * @param tickers 股票代码列表
     * @param barsMap K线数据映射
     */
    protected void validateBatchInput(List<String> tickers, Map<String, List<Bar>> barsMap) {
        if (tickers == null || tickers.isEmpty()) {
            throw new IllegalArgumentException("Tickers cannot be null or empty");
        }
        if (barsMap == null || barsMap.isEmpty()) {
            throw new IllegalArgumentException("BarsMap cannot be null or empty");
        }
        for (String ticker : tickers) {
            if (!barsMap.containsKey(ticker)) {
                throw new IllegalArgumentException("Missing bars for ticker: " + ticker);
            }
        }
    }
}
