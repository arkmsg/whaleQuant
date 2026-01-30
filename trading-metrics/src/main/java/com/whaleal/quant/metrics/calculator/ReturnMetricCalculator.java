package com.whaleal.quant.metrics.calculator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 收益率度量计算器
 * 用于计算回测结果的收益率相关指标
 */
public class ReturnMetricCalculator implements MetricCalculator {
    
    @Override
    public Map<String, Double> calculate(Map<String, Object> data) {
        Map<String, Double> metrics = new HashMap<>();
        
        // 计算总收益率
        double totalReturn = calculateTotalReturn(data);
        metrics.put("totalReturn", totalReturn);
        
        // 计算年化收益率
        double annualizedReturn = calculateAnnualizedReturn(data, totalReturn);
        metrics.put("annualizedReturn", annualizedReturn);
        
        // 计算月化收益率
        double monthlyReturn = calculateMonthlyReturn(data, totalReturn);
        metrics.put("monthlyReturn", monthlyReturn);
        
        // 计算日均收益率
        double dailyReturn = calculateDailyReturn(data, totalReturn);
        metrics.put("dailyReturn", dailyReturn);
        
        return metrics;
    }
    
    @Override
    public String getName() {
        return "ReturnMetricCalculator";
    }
    
    /**
     * 计算总收益率
     */
    private double calculateTotalReturn(Map<String, Object> data) {
        // 从数据中获取总收益率
        if (data.containsKey("totalReturn")) {
            return (double) data.get("totalReturn");
        }
        
        // 基于盈亏和初始资金计算总收益率
        if (data.containsKey("totalPnl") && data.containsKey("initialCapital")) {
            double totalPnl = (double) data.get("totalPnl");
            double initialCapital = (double) data.get("initialCapital");
            return totalPnl / initialCapital;
        }
        
        return 0;
    }
    
    /**
     * 计算年化收益率
     */
    private double calculateAnnualizedReturn(Map<String, Object> data, double totalReturn) {
        if (data.containsKey("startDate") && data.containsKey("endDate")) {
            LocalDateTime startDate = (LocalDateTime) data.get("startDate");
            LocalDateTime endDate = (LocalDateTime) data.get("endDate");
            
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) {
                return 0;
            }
            
            double years = days / 365.0;
            return Math.pow(1 + totalReturn, 1 / years) - 1;
        }
        
        return 0;
    }
    
    /**
     * 计算月化收益率
     */
    private double calculateMonthlyReturn(Map<String, Object> data, double totalReturn) {
        if (data.containsKey("startDate") && data.containsKey("endDate")) {
            LocalDateTime startDate = (LocalDateTime) data.get("startDate");
            LocalDateTime endDate = (LocalDateTime) data.get("endDate");
            
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) {
                return 0;
            }
            
            double months = days / 30.0;
            return Math.pow(1 + totalReturn, 1 / months) - 1;
        }
        
        return 0;
    }
    
    /**
     * 计算日均收益率
     */
    private double calculateDailyReturn(Map<String, Object> data, double totalReturn) {
        if (data.containsKey("startDate") && data.containsKey("endDate")) {
            LocalDateTime startDate = (LocalDateTime) data.get("startDate");
            LocalDateTime endDate = (LocalDateTime) data.get("endDate");
            
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) {
                return 0;
            }
            
            return Math.pow(1 + totalReturn, 1 / days) - 1;
        }
        
        return 0;
    }
}
