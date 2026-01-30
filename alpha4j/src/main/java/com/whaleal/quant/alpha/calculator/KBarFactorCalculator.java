package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * K线形态因子计算器
 *
 * ⚠️ 重要变更：改用double类型，提升性能10-100倍
 *
 * 实现Qlib Alpha158中的9个K线形态因子：
 * - KMID: K线实体幅度占开盘价的比例 = (close - open) / open
 * - KLEN: K线长度占开盘价的比例 = (high - low) / open
 * - KMID2: K线实体占整体长度的比例 = (close - open) / (high - low + 1e-12)
 * - KUP: 上影线占开盘价的比例 = (high - max(open, close)) / open
 * - KUP2: 上影线占整体长度的比例 = (high - max(open, close)) / (high - low + 1e-12)
 * - KLOW: 下影线占开盘价的比例 = (min(open, close) - low) / open
 * - KLOW2: 下影线占整体长度的比例 = (min(open, close) - low) / (high - low + 1e-12)
 * - KSFT: 收盘价相对中间价的偏移 = (2*close - high - low) / open
 * - KSFT2: 收盘价在当日区间的位置 = (2*close - high - low) / (high - low + 1e-12)
 *
 * @author arkmsg
 */
@Slf4j
public class KBarFactorCalculator {

    /**
     * EPSILON值用于防止除零
     * ⚠️ 与Qlib完全对齐：使用1e-12
     */
    private static final double EPSILON = 1e-12;

    /**
     * 计算所有K线形态因子
     */
    public static Map<String, Double> calculateAll(Candlestick candle) {
        Map<String, Double> factors = new LinkedHashMap<>();

        try {
            factors.put("KMID", calculateKMID(candle));
            factors.put("KLEN", calculateKLEN(candle));
            factors.put("KMID2", calculateKMID2(candle));
            factors.put("KUP", calculateKUP(candle));
            factors.put("KUP2", calculateKUP2(candle));
            factors.put("KLOW", calculateKLOW(candle));
            factors.put("KLOW2", calculateKLOW2(candle));
            factors.put("KSFT", calculateKSFT(candle));
            factors.put("KSFT2", calculateKSFT2(candle));
        } catch (Exception e) {
            log.error("计算K线形态因子失败: {}", e.getMessage(), e);
        }

        return factors;
    }

    /**
     * KMID: K线实体幅度占开盘价的比例
     * 公式: (close - open) / open
     */
    public static double calculateKMID(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();

        if (open == 0) {
            return 0.0;
        }

        return (close - open) / open;
    }

    /**
     * KLEN: K线长度占开盘价的比例
     * 公式: (high - low) / open
     */
    public static double calculateKLEN(Candlestick candle) {
        double open = candle.getOpen();
        double high = candle.getHigh();
        double low = candle.getLow();

        if (open == 0) {
            return 0.0;
        }

        return (high - low) / open;
    }

    /**
     * KMID2: K线实体占整体长度的比例
     * 公式: (close - open) / (high - low + 1e-12)
     */
    public static double calculateKMID2(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double high = candle.getHigh();
        double low = candle.getLow();

        return (close - open) / (high - low + EPSILON);
    }

    /**
     * KUP: 上影线占开盘价的比例
     * 公式: (high - max(open, close)) / open
     */
    public static double calculateKUP(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double high = candle.getHigh();

        if (open == 0) {
            return 0.0;
        }

        return (high - Math.max(open, close)) / open;
    }

    /**
     * KUP2: 上影线占整体长度的比例
     * 公式: (high - max(open, close)) / (high - low + 1e-12)
     */
    public static double calculateKUP2(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double high = candle.getHigh();
        double low = candle.getLow();

        return (high - Math.max(open, close)) / (high - low + EPSILON);
    }

    /**
     * KLOW: 下影线占开盘价的比例
     * 公式: (min(open, close) - low) / open
     */
    public static double calculateKLOW(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double low = candle.getLow();

        if (open == 0) {
            return 0.0;
        }

        return (Math.min(open, close) - low) / open;
    }

    /**
     * KLOW2: 下影线占整体长度的比例
     * 公式: (min(open, close) - low) / (high - low + 1e-12)
     */
    public static double calculateKLOW2(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double high = candle.getHigh();
        double low = candle.getLow();

        return (Math.min(open, close) - low) / (high - low + EPSILON);
    }

    /**
     * KSFT: 收盘价相对中间价的偏移
     * 公式: (2*close - high - low) / open
     */
    public static double calculateKSFT(Candlestick candle) {
        double open = candle.getOpen();
        double close = candle.getClose();
        double high = candle.getHigh();
        double low = candle.getLow();

        if (open == 0) {
            return 0.0;
        }

        return (2 * close - high - low) / open;
    }

    /**
     * KSFT2: 收盘价在当日区间的位置
     * 公式: (2*close - high - low) / (high - low + 1e-12)
     */
    public static double calculateKSFT2(Candlestick candle) {
        double close = candle.getClose();
        double high = candle.getHigh();
        double low = candle.getLow();

        return (2 * close - high - low) / (high - low + EPSILON);
    }
}
