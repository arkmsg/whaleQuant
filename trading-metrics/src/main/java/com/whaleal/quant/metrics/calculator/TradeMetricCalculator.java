package com.whaleal.quant.metrics.calculator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易度量计算器
 * 用于计算回测结果的交易相关指标
 */
public class TradeMetricCalculator implements MetricCalculator {
    
    @Override
    public Map<String, Double> calculate(Map<String, Object> data) {
        Map<String, Double> metrics = new HashMap<>();
        
        // 计算胜率
        double winRate = calculateWinRate(data);
        metrics.put("winRate", winRate);
        
        // 计算盈亏比
        double profitLossRatio = calculateProfitLossRatio(data);
        metrics.put("profitLossRatio", profitLossRatio);
        
        // 计算平均盈利
        double averageWin = calculateAverageWin(data);
        metrics.put("averageWin", averageWin);
        
        // 计算平均亏损
        double averageLoss = calculateAverageLoss(data);
        metrics.put("averageLoss", averageLoss);
        
        // 计算交易频率
        double tradeFrequency = calculateTradeFrequency(data);
        metrics.put("tradeFrequency", tradeFrequency);
        
        // 计算最大连续盈利次数
        int maxConsecutiveWins = calculateMaxConsecutiveWins(data);
        metrics.put("maxConsecutiveWins", (double) maxConsecutiveWins);
        
        // 计算最大连续亏损次数
        int maxConsecutiveLosses = calculateMaxConsecutiveLosses(data);
        metrics.put("maxConsecutiveLosses", (double) maxConsecutiveLosses);
        
        return metrics;
    }
    
    @Override
    public String getName() {
        return "TradeMetricCalculator";
    }
    
    /**
     * 计算胜率
     */
    private double calculateWinRate(Map<String, Object> data) {
        if (data.containsKey("tradeCount") && data.containsKey("winCount")) {
            int tradeCount = (int) data.get("tradeCount");
            if (tradeCount == 0) {
                return 0;
            }
            
            int winCount = (int) data.get("winCount");
            return (double) winCount / tradeCount;
        }
        
        return 0;
    }
    
    /**
     * 计算盈亏比
     */
    private double calculateProfitLossRatio(Map<String, Object> data) {
        // 从数据中获取盈亏比
        if (data.containsKey("profitLossRatio")) {
            return (double) data.get("profitLossRatio");
        }
        
        double averageWin = calculateAverageWin(data);
        double averageLoss = calculateAverageLoss(data);
        
        if (averageLoss == 0) {
            return 0;
        }
        
        return averageWin / Math.abs(averageLoss);
    }
    
    /**
     * 计算平均盈利
     */
    private double calculateAverageWin(Map<String, Object> data) {
        // 从数据中获取平均盈利
        if (data.containsKey("averageWin")) {
            return (double) data.get("averageWin");
        }
        
        if (data.containsKey("winCount") && data.containsKey("totalWin")) {
            int winCount = (int) data.get("winCount");
            if (winCount == 0) {
                return 0;
            }
            
            double totalWin = (double) data.get("totalWin");
            return totalWin / winCount;
        }
        
        return 0;
    }
    
    /**
     * 计算平均亏损
     */
    private double calculateAverageLoss(Map<String, Object> data) {
        // 从数据中获取平均亏损
        if (data.containsKey("averageLoss")) {
            return (double) data.get("averageLoss");
        }
        
        if (data.containsKey("lossCount") && data.containsKey("totalLoss")) {
            int lossCount = (int) data.get("lossCount");
            if (lossCount == 0) {
                return 0;
            }
            
            double totalLoss = (double) data.get("totalLoss");
            return totalLoss / lossCount; // 负值
        }
        
        return 0;
    }
    
    /**
     * 计算交易频率（每日平均交易次数）
     */
    private double calculateTradeFrequency(Map<String, Object> data) {
        if (data.containsKey("tradeCount") && data.containsKey("startDate") && data.containsKey("endDate")) {
            int tradeCount = (int) data.get("tradeCount");
            if (tradeCount == 0) {
                return 0;
            }
            
            LocalDateTime startDate = (LocalDateTime) data.get("startDate");
            LocalDateTime endDate = (LocalDateTime) data.get("endDate");
            
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) {
                return 0;
            }
            
            return (double) tradeCount / days;
        }
        
        return 0;
    }
    
    /**
     * 计算最大连续盈利次数
     */
    private int calculateMaxConsecutiveWins(Map<String, Object> data) {
        // 从数据中获取最大连续盈利次数
        if (data.containsKey("maxConsecutiveWins")) {
            return (int) data.get("maxConsecutiveWins");
        }
        
        // 默认最大连续盈利次数为0
        return 0;
    }
    
    /**
     * 计算最大连续亏损次数
     */
    private int calculateMaxConsecutiveLosses(Map<String, Object> data) {
        // 从数据中获取最大连续亏损次数
        if (data.containsKey("maxConsecutiveLosses")) {
            return (int) data.get("maxConsecutiveLosses");
        }
        
        // 默认最大连续亏损次数为0
        return 0;
    }
}
