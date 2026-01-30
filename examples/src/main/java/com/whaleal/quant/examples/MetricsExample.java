package com.whaleal.quant.examples;

import com.whaleal.quant.metrics.calculator.CompositeMetricCalculator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 度量计算器示例
 * 展示如何使用度量计算器来计算回测结果的性能指标
 */
public class MetricsExample {
    
    public static void main(String[] args) {
        System.out.println("=== 度量计算器示例 ===");
        
        // 1. 准备输入数据
        Map<String, Object> inputData = prepareInputData();
        
        // 2. 创建复合度量计算器
        CompositeMetricCalculator calculator = new CompositeMetricCalculator();
        
        // 3. 计算指标
        System.out.println("\n计算性能指标...");
        Map<String, Double> metrics = calculator.calculate(inputData);
        
        // 4. 输出结果
        System.out.println("\n=== 计算结果 ===");
        metrics.forEach((key, value) -> {
            System.out.printf("%s: %.4f\n", key, value);
        });
        
        // 5. 分析结果
        System.out.println("\n=== 结果分析 ===");
        analyzeMetrics(metrics);
        
        System.out.println("\n=== 示例完成 ===");
    }
    
    /**
     * 准备输入数据
     */
    private static Map<String, Object> prepareInputData() {
        Map<String, Object> data = new HashMap<>();
        
        // 基本回测信息
        data.put("startDate", LocalDateTime.now().minusMonths(3));
        data.put("endDate", LocalDateTime.now());
        data.put("initialCapital", 100000.0);
        data.put("totalPnl", 15000.0);
        
        // 交易统计信息
        data.put("tradeCount", 50);
        data.put("winCount", 30);
        data.put("lossCount", 20);
        data.put("totalWin", 20000.0);
        data.put("totalLoss", -5000.0);
        
        // 风险相关信息
        data.put("maxDrawdown", -0.15); // 15% 最大回撤
        data.put("volatility", 0.2); // 20% 波动率
        
        return data;
    }
    
    /**
     * 分析指标结果
     */
    private static void analyzeMetrics(Map<String, Double> metrics) {
        double totalReturn = metrics.getOrDefault("totalReturn", 0.0);
        double annualizedReturn = metrics.getOrDefault("annualizedReturn", 0.0);
        double sharpeRatio = metrics.getOrDefault("sharpeRatio", 0.0);
        double winRate = metrics.getOrDefault("winRate", 0.0);
        double maxDrawdown = metrics.getOrDefault("maxDrawdown", 0.0);
        
        System.out.println("总收益率: " + (totalReturn * 100) + "%");
        System.out.println("年化收益率: " + (annualizedReturn * 100) + "%");
        System.out.println("夏普比率: " + sharpeRatio);
        System.out.println("胜率: " + (winRate * 100) + "%");
        System.out.println("最大回撤: " + (maxDrawdown * 100) + "%");
        
        // 简单的策略评估
        if (sharpeRatio > 1.0) {
            System.out.println("策略评估: 优秀 - 夏普比率大于1.0");
        } else if (sharpeRatio > 0.5) {
            System.out.println("策略评估: 良好 - 夏普比率大于0.5");
        } else {
            System.out.println("策略评估: 需要改进 - 夏普比率较低");
        }
        
        if (winRate > 0.6) {
            System.out.println("交易评估: 优秀 - 胜率大于60%");
        } else if (winRate > 0.5) {
            System.out.println("交易评估: 良好 - 胜率大于50%");
        } else {
            System.out.println("交易评估: 需要改进 - 胜率较低");
        }
    }
}
