package com.whaleal.quant.metrics.calculator;

import java.util.Map;

/**
 * 度量计算器接口
 * 用于计算回测结果的各种性能指标
 */
public interface MetricCalculator {
    
    /**
     * 计算度量指标
     * @param data 输入数据，包含计算所需的各种值
     * @return 计算的度量指标映射
     */
    Map<String, Double> calculate(Map<String, Object> data);
    
    /**
     * 获取计算器名称
     * @return 计算器名称
     */
    String getName();
}
