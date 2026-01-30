package com.whaleal.quant.metrics.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 复合度量计算器
 * 用于整合所有度量计算器的结果
 */
public class CompositeMetricCalculator implements MetricCalculator {
    
    private final List<MetricCalculator> calculators;
    
    /**
     * 默认构造方法，初始化所有计算器
     */
    public CompositeMetricCalculator() {
        this.calculators = new ArrayList<>();
        this.calculators.add(new ReturnMetricCalculator());
        this.calculators.add(new RiskMetricCalculator());
        this.calculators.add(new TradeMetricCalculator());
    }
    
    /**
     * 自定义构造方法，允许指定计算器
     */
    public CompositeMetricCalculator(List<MetricCalculator> calculators) {
        this.calculators = calculators;
    }
    
    @Override
    public Map<String, Double> calculate(Map<String, Object> data) {
        Map<String, Double> allMetrics = new HashMap<>();
        
        // 调用所有计算器计算指标
        for (MetricCalculator calculator : calculators) {
            Map<String, Double> metrics = calculator.calculate(data);
            allMetrics.putAll(metrics);
        }
        
        return allMetrics;
    }
    
    @Override
    public String getName() {
        return "CompositeMetricCalculator";
    }
    
    /**
     * 添加计算器
     */
    public void addCalculator(MetricCalculator calculator) {
        this.calculators.add(calculator);
    }
    
    /**
     * 移除计算器
     */
    public void removeCalculator(MetricCalculator calculator) {
        this.calculators.remove(calculator);
    }
    
    /**
     * 获取所有计算器
     */
    public List<MetricCalculator> getCalculators() {
        return new ArrayList<>(calculators);
    }
}
