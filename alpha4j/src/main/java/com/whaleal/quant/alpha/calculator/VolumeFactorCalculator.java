package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 成交量因子计算器
 *
 * ⚠️ 重要变更：改用double类型，提升性能10-100倍
 *
 * 计算Alpha158中的成交量相关因子：
 * - VOLUME: 成交量
 *
 * 归一化：所有成交量因子除以当前成交量
 *
 * @author arkmsg
 */
@Slf4j
public class VolumeFactorCalculator {

    private static final double EPSILON = 1e-12;

    /**
     * 计算所有成交量因子
     */
    public static Map<String, Double> calculateAll(
            List<Candlestick> data,
            List<Integer> windows) {

        Map<String, Double> factors = new LinkedHashMap<>();

        if (data == null || data.isEmpty()) {
            log.warn("数据为空，无法计算成交量因子");
            return factors;
        }

        // 获取当前成交量（用于归一化）
        double currentVolume = data.get(data.size() - 1).getVolume();

        // 按窗口期计算
        for (Integer window : windows) {
            String factorName = "VOLUME" + window;
            double value = calculateVolumeFactor(data, window, currentVolume);
            factors.put(factorName, value);
        }

        return factors;
    }

    /**
     * 计算单个成交量因子
     */
    private static double calculateVolumeFactor(
            List<Candlestick> data,
            int window,
            double currentVolume) {

        try {
            // 检查数据是否足够
            if (data.size() < window + 1) {
                return 0.0;
            }

            // 获取目标K线的成交量
            long targetVolume = data.get(data.size() - 1 - window).getVolume();

            // 归一化：除以当前成交量（加EPSILON防止除零）
            return targetVolume / (currentVolume + EPSILON);

        } catch (Exception e) {
            log.error("计算成交量因子失败: window={}, error={}", window, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 计算VOLUME因子
     */
    public static double calculateVOLUME(List<Candlestick> data, int window) {
        double currentVolume = data.get(data.size() - 1).getVolume();
        return calculateVolumeFactor(data, window, currentVolume);
    }
}
