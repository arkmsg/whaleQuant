package com.whaleal.quant.metrics.calculator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * 风险度量计算器
 * 用于计算回测结果的风险相关指标
 */
public class RiskMetricCalculator implements MetricCalculator {
    
    private static final double RISK_FREE_RATE = 0.03; // 无风险利率
    
    @Override
    public Map<String, Double> calculate(Map<String, Object> data) {
        Map<String, Double> metrics = new HashMap<>();
        
        // 计算夏普比率
        double sharpeRatio = calculateSharpeRatio(data);
        metrics.put("sharpeRatio", sharpeRatio);
        
        // 计算索提诺比率
        double sortinoRatio = calculateSortinoRatio(data);
        metrics.put("sortinoRatio", sortinoRatio);
        
        // 计算最大回撤
        double maxDrawdown = calculateMaxDrawdown(data);
        metrics.put("maxDrawdown", maxDrawdown);
        
        // 计算卡玛比率
        double calmarRatio = calculateCalmarRatio(data, maxDrawdown);
        metrics.put("calmarRatio", calmarRatio);
        
        // 计算波动率
        double volatility = calculateVolatility(data);
        metrics.put("volatility", volatility);
        
        return metrics;
    }
    
    @Override
    public String getName() {
        return "RiskMetricCalculator";
    }
    
    /**
     * 计算夏普比率
     */
    private double calculateSharpeRatio(Map<String, Object> data) {
        Double annualizedReturn = (Double) data.get("annualizedReturn");
        Double volatility = calculateVolatility(data);
        
        if (annualizedReturn == null || volatility == 0) {
            return 0;
        }
        
        return (annualizedReturn - RISK_FREE_RATE) / volatility;
    }
    
    /**
     * 计算索提诺比率
     */
    private double calculateSortinoRatio(Map<String, Object> data) {
        Double annualizedReturn = (Double) data.get("annualizedReturn");
        
        if (annualizedReturn == null) {
            return 0;
        }
        
        // 计算下行风险
        double downsideRisk = calculateDownsideRisk(data);
        if (downsideRisk == 0) {
            return 0;
        }
        
        return (annualizedReturn - RISK_FREE_RATE) / downsideRisk;
    }
    
    /**
     * 计算最大回撤
     */
    private double calculateMaxDrawdown(Map<String, Object> data) {
        // 从数据中获取最大回撤
        if (data.containsKey("maxDrawdown")) {
            return (double) data.get("maxDrawdown");
        }
        
        // 默认最大回撤为0
        return 0;
    }
    
    /**
     * 计算卡玛比率
     */
    private double calculateCalmarRatio(Map<String, Object> data, double maxDrawdown) {
        Double annualizedReturn = (Double) data.get("annualizedReturn");
        
        if (annualizedReturn == null || maxDrawdown == 0) {
            return 0;
        }
        
        return annualizedReturn / Math.abs(maxDrawdown);
    }
    
    /**
     * 计算波动率
     */
    private double calculateVolatility(Map<String, Object> data) {
        // 从数据中获取波动率
        if (data.containsKey("volatility")) {
            return (double) data.get("volatility");
        }
        
        // 默认波动率为0.1
        return 0.1;
    }
    
    /**
     * 计算下行风险
     */
    private double calculateDownsideRisk(Map<String, Object> data) {
        // 从数据中获取下行风险
        if (data.containsKey("downsideRisk")) {
            return (double) data.get("downsideRisk");
        }
        
        // 默认下行风险为0.08
        return 0.08;
    }
}
