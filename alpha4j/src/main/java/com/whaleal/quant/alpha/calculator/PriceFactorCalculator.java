package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 价格因子计算器
 *
 * ⚠️ 重要变更：改用double类型，提升性能10-100倍
 *
 * 计算Alpha158中的价格相关因子：
 * - OPEN: 开盘价
 * - HIGH: 最高价
 * - LOW: 最低价
 * - VWAP: 成交量加权平均价
 *
 * 归一化：所有价格因子除以当前收盘价
 *
 * @author arkmsg
 */
@Slf4j
public class PriceFactorCalculator {

    /**
     * 计算所有价格因子
     */
    public static Map<String, Double> calculateAll(
            List<Candlestick> data,
            List<Integer> windows,
            List<String> features) {

        Map<String, Double> factors = new LinkedHashMap<>();

        if (data == null || data.isEmpty()) {
            log.warn("数据为空，无法计算价格因子");
            return factors;
        }

        // 获取当前收盘价（用于归一化）
        double currentClose = data.get(data.size() - 1).getClose();

        // 按特征和窗口期计算
        for (String feature : features) {
            for (Integer window : windows) {
                String factorName = feature + window;
                double value = calculatePriceFactor(data, feature, window, currentClose);
                factors.put(factorName, value);
            }
        }

        return factors;
    }

    /**
     * 计算单个价格因子
     */
    private static double calculatePriceFactor(
            List<Candlestick> data,
            String feature,
            int window,
            double currentClose) {

        try {
            // 检查数据是否足够
            if (data.size() < window + 1) {
                return 0.0;
            }

            // 获取目标K线
            Candlestick targetCandle = data.get(data.size() - 1 - window);

            // 提取特征值
            double value = switch (feature.toUpperCase()) {
                case "OPEN" -> targetCandle.getOpen();
                case "HIGH" -> targetCandle.getHigh();
                case "LOW" -> targetCandle.getLow();
                case "VWAP" -> targetCandle.getVwap();
                default -> {
                    log.warn("未知的价格特征: {}", feature);
                    yield 0.0;
                }
            };

            // 归一化：除以当前收盘价
            return value / currentClose;

        } catch (Exception e) {
            log.error("计算价格因子失败: feature={}, window={}, error={}", feature, window, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 计算OPEN因子
     */
    public static double calculateOPEN(List<Candlestick> data, int window) {
        double currentClose = data.get(data.size() - 1).getClose();
        return calculatePriceFactor(data, "OPEN", window, currentClose);
    }

    /**
     * 计算HIGH因子
     */
    public static double calculateHIGH(List<Candlestick> data, int window) {
        double currentClose = data.get(data.size() - 1).getClose();
        return calculatePriceFactor(data, "HIGH", window, currentClose);
    }

    /**
     * 计算LOW因子
     */
    public static double calculateLOW(List<Candlestick> data, int window) {
        double currentClose = data.get(data.size() - 1).getClose();
        return calculatePriceFactor(data, "LOW", window, currentClose);
    }

    /**
     * 计算VWAP因子
     */
    public static double calculateVWAP(List<Candlestick> data, int window) {
        double currentClose = data.get(data.size() - 1).getClose();
        return calculatePriceFactor(data, "VWAP", window, currentClose);
    }
}
