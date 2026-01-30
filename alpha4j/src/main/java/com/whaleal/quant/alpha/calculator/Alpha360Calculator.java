package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.*;
import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Alpha360因子计算器
 *
 * ⚠️ 重要变更：改用double类型，提升性能10-100倍
 *
 * Alpha360因子库包含360个原始时间序列特征：60天 × 6个特征 = 360个因子
 *
 * ⚠️ 配置参数未实现说明：
 * - 当前实现：固定返回 360 个因子（60天 × 6特征）
 * - Alpha360Config 的参数（lookbackDays、priceFields、includeVolume）当前未使用
 * - 所有因子系列和窗口长度均为硬编码
 *
 * 📌 未来扩展支持：
 * - TODO: 支持自定义窗口长度（config.getLookbackDays()）
 * - TODO: 支持自定义价格字段（config.getPriceFields()）
 * - TODO: 支持可选的成交量因子（config.isIncludeVolume()）
 * - TODO: 需要同步更新 Alpha360FactorOrder 的因子顺序生成逻辑
 *
 * @author arkmsg
 * @see Alpha360Config 配置类（当前未生效）
 */
@Slf4j
public class Alpha360Calculator {

    /**
     * 时间窗口长度（硬编码）
     *
     * ⚠️ 未实现配置支持
     * TODO: 改为使用 config.getLookbackDays()
     */
    private static final int TIME_WINDOW = 60;

    /**
     * 浮点数比较精度
     */
    private static final double EPSILON = 1e-12;

    /**
     * 计算Alpha360因子（使用默认配置）
     */
    public static AlphaFactorResult calculate(List<Candlestick> data) {
        return calculate(data, Alpha360Config.createDefault());
    }

    /**
     * 计算Alpha360因子（使用自定义配置）
     *
     * ⚠️ 注意：config 参数当前未使用，所有配置均为硬编码
     *
     * @param data K线数据列表（需要至少60根）
     * @param config Alpha360配置（当前未使用）
     * @return 因子计算结果（固定360个因子）
     */
    public static AlphaFactorResult calculate(List<Candlestick> data, Alpha360Config config) {
        if (data == null || data.isEmpty()) {
            log.warn("K线数据为空，无法计算Alpha360因子");
            return null;
        }

        // TODO: 使用 config.getLookbackDays() 代替 TIME_WINDOW
        if (data.size() < TIME_WINDOW) {
            log.warn("数据不足，需要至少{}根K线，当前只有{}根", TIME_WINDOW, data.size());
            return null;
        }

        Candlestick current = data.get(data.size() - 1);
        String symbol = current.getSymbol();
        long timestamp = current.getTimestamp();

        AlphaFactorResult result = AlphaFactorResult.builder()
            .symbol(symbol)
            .timestamp(timestamp)
            .build();

        // 归一化基准值
        double currentClose = current.getClose();
        double currentVolume = current.getVolume();

        // ⚠️ 以下代码硬编码了6个特征系列和60天窗口
        // TODO: 改为动态循环 config.getPriceFields() 和使用 config.getLookbackDays()

        // 1. CLOSE系列 (day59 -> day0)
        // TODO: 改为 if (config.getPriceFields().contains("CLOSE"))
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "CLOSE" + day;
            double value = calculatePriceFactor(data, "CLOSE", day, currentClose);
            result.addFactor(factorName, value);
        }

        // 2. OPEN系列
        // TODO: 改为 if (config.getPriceFields().contains("OPEN"))
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "OPEN" + day;
            double value = calculatePriceFactor(data, "OPEN", day, currentClose);
            result.addFactor(factorName, value);
        }

        // 3. HIGH系列
        // TODO: 改为 if (config.getPriceFields().contains("HIGH"))
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "HIGH" + day;
            double value = calculatePriceFactor(data, "HIGH", day, currentClose);
            result.addFactor(factorName, value);
        }

        // 4. LOW系列
        // TODO: 改为 if (config.getPriceFields().contains("LOW"))
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "LOW" + day;
            double value = calculatePriceFactor(data, "LOW", day, currentClose);
            result.addFactor(factorName, value);
        }

        // 5. VWAP系列
        // TODO: 改为 if (config.getPriceFields().contains("VWAP"))
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "VWAP" + day;
            double value = calculatePriceFactor(data, "VWAP", day, currentClose);
            result.addFactor(factorName, value);
        }

        // 6. VOLUME系列
        // TODO: 改为 if (config.isIncludeVolume())
        for (int day = TIME_WINDOW - 1; day >= 0; day--) {
            String factorName = "VOLUME" + day;
            double value = calculateVolumeFactor(data, day, currentVolume);
            result.addFactor(factorName, value);
        }

        return result;
    }

    /**
     * 计算单个价格因子
     */
    private static double calculatePriceFactor(
            List<Candlestick> data,
            String feature,
            int dayOffset,
            double currentClose) {

        try {
            int index = data.size() - TIME_WINDOW + dayOffset;
            if (index < 0 || index >= data.size()) {
                return 0.0;
            }

            Candlestick candle = data.get(index);

            double value = switch (feature.toUpperCase()) {
                case "CLOSE" -> candle.getClose();
                case "OPEN" -> candle.getOpen();
                case "HIGH" -> candle.getHigh();
                case "LOW" -> candle.getLow();
                case "VWAP" -> candle.getVwap();
                default -> {
                    log.warn("未知的价格特征: {}", feature);
                    yield 0.0;
                }
            };

            return value / currentClose;

        } catch (Exception e) {
            log.error("计算价格因子失败: feature={}, dayOffset={}, error={}", feature, dayOffset, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 计算单个成交量因子
     */
    private static double calculateVolumeFactor(
            List<Candlestick> data,
            int dayOffset,
            double currentVolume) {

        try {
            int index = data.size() - TIME_WINDOW + dayOffset;
            if (index < 0 || index >= data.size()) {
                return 0.0;
            }

            long targetVolume = data.get(index).getVolume();
            return targetVolume / (currentVolume + EPSILON);

        } catch (Exception e) {
            log.error("计算成交量因子失败: dayOffset={}, error={}", dayOffset, e.getMessage());
            return 0.0;
        }
    }

    /**
     * 批量计算Alpha360因子（滑动窗口）
     */
    public static AlphaDataset calculateBatch(List<Candlestick> data, Alpha360Config config) {
        if (data == null || data.isEmpty()) {
            log.warn("K线数据为空，无法批量计算Alpha360因子");
            return null;
        }

        AlphaDataset dataset = new AlphaDataset(
            AlphaFeatureVector.AlphaType.ALPHA360,
            Alpha360FactorOrder.getDefaultOrder()
        );

        if (data.size() < TIME_WINDOW) {
            log.warn("数据不足，至少需要{}根K线，当前只有{}根", TIME_WINDOW, data.size());
            return dataset;
        }

        for (int i = TIME_WINDOW; i <= data.size(); i++) {
            List<Candlestick> window = data.subList(0, i);
            AlphaFactorResult factorResult = calculate(window, config);

            if (factorResult != null) {
                AlphaFeatureVector featureVector = factorResult.toFeatureVector(
                    AlphaFeatureVector.AlphaType.ALPHA360
                );
                dataset.addFeature(featureVector);
            }
        }

        return dataset;
    }
}
