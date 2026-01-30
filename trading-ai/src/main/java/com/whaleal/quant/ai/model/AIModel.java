package com.whaleal.quant.ai.model;

import com.whaleal.quant.core.model.Bar;
import com.whaleal.quant.core.model.Ticker;

import java.util.List;
import java.util.Map;

/**
 * AI模型接口
 * 定义AI模型的基本行为
 * 
 * @author whaleal
 * @version 1.0.0
 */
public interface AIModel {
    
    /**
     * 初始化模型
     * 
     * @param config 模型配置
     */
    void initialize(Map<String, Object> config);
    
    /**
     * 预测股票趋势
     * 
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 预测结果
     */
    PredictionResult predictTrend(String ticker, List<Bar> bars);
    
    /**
     * 预测多个股票的趋势
     * 
     * @param tickers 股票代码列表
     * @param barsMap K线数据映射
     * @return 多个股票的预测结果
     */
    Map<String, PredictionResult> predictBatchTrend(List<String> tickers, Map<String, List<Bar>> barsMap);
    
    /**
     * 分析市场情绪
     * 
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 市场情绪分析结果
     */
    MarketSentiment analyzeMarketSentiment(String ticker, List<Bar> bars);
    
    /**
     * 生成交易信号
     * 
     * @param ticker 股票代码
     * @param bars K线数据
     * @return 交易信号
     */
    TradingSignal generateTradingSignal(String ticker, List<Bar> bars);
    
    /**
     * 获取模型信息
     * 
     * @return 模型信息
     */
    ModelInfo getModelInfo();
    
    /**
     * 关闭模型资源
     */
    void close();
}