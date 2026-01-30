package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 滚动统计因子计算器
 *
 * 基于Qlib Alpha158实现29种滚动统计算子
 * 所有计算使用double类型，确保与Qlib完全一致
 *
 * @author arkmsg
 */
@Slf4j
public class RollingStatCalculator {

    private static final double EPSILON = 1e-12; // Qlib防除零常量

    /**
     * 计算所有滚动统计因子
     *
     * @param data K线数据（至少需要60根）
     * @param windows 窗口期列表
     * @param operators 算子列表（null表示全部）
     * @return 因子名称到值的映射
     */
    public static Map<String, Double> calculateAll(
            List<Candlestick> data,
            List<Integer> windows,
            List<String> operators) {

        Map<String, Double> factors = new LinkedHashMap<>();

        if (data == null || data.size() < Collections.max(windows)) {
            log.warn("数据不足，需要至少{}根K线", Collections.max(windows));
            return factors;
        }

        // 获取最新的close价格和volume（用于归一化）
        double currentClose = data.get(data.size() - 1).getClose();
        double currentVolume = data.get(data.size() - 1).getVolume();

        // 按Qlib顺序计算各个算子
        if (shouldCalculate("ROC", operators)) {
            for (Integer window : windows) {
                factors.put("ROC" + window, calculateROC(data, window, currentClose));
            }
        }

        if (shouldCalculate("MA", operators)) {
            for (Integer window : windows) {
                factors.put("MA" + window, calculateMA(data, window, currentClose));
            }
        }

        if (shouldCalculate("STD", operators)) {
            for (Integer window : windows) {
                factors.put("STD" + window, calculateSTD(data, window, currentClose));
            }
        }

        if (shouldCalculate("BETA", operators)) {
            for (Integer window : windows) {
                factors.put("BETA" + window, calculateBETA(data, window));
            }
        }

        if (shouldCalculate("RSQR", operators)) {
            for (Integer window : windows) {
                factors.put("RSQR" + window, calculateRSQR(data, window));
            }
        }

        if (shouldCalculate("RESI", operators)) {
            for (Integer window : windows) {
                factors.put("RESI" + window, calculateRESI(data, window, currentClose));
            }
        }

        if (shouldCalculate("MAX", operators)) {
            for (Integer window : windows) {
                factors.put("MAX" + window, calculateMAX(data, window, currentClose));
            }
        }

        if (shouldCalculate("MIN", operators)) {
            for (Integer window : windows) {
                factors.put("MIN" + window, calculateMIN(data, window, currentClose));
            }
        }

        if (shouldCalculate("QTLU", operators)) {
            for (Integer window : windows) {
                factors.put("QTLU" + window, calculateQTLU(data, window, currentClose));
            }
        }

        if (shouldCalculate("QTLD", operators)) {
            for (Integer window : windows) {
                factors.put("QTLD" + window, calculateQTLD(data, window, currentClose));
            }
        }

        if (shouldCalculate("RANK", operators)) {
            for (Integer window : windows) {
                factors.put("RANK" + window, calculateRANK(data, window));
            }
        }

        if (shouldCalculate("RSV", operators)) {
            for (Integer window : windows) {
                factors.put("RSV" + window, calculateRSV(data, window));
            }
        }

        if (shouldCalculate("IMAX", operators)) {
            for (Integer window : windows) {
                factors.put("IMAX" + window, calculateIMAX(data, window));
            }
        }

        if (shouldCalculate("IMIN", operators)) {
            for (Integer window : windows) {
                factors.put("IMIN" + window, calculateIMIN(data, window));
            }
        }

        if (shouldCalculate("IMXD", operators)) {
            for (Integer window : windows) {
                factors.put("IMXD" + window, calculateIMXD(data, window));
            }
        }

        if (shouldCalculate("CORR", operators)) {
            for (Integer window : windows) {
                factors.put("CORR" + window, calculateCORR(data, window));
            }
        }

        if (shouldCalculate("CORD", operators)) {
            for (Integer window : windows) {
                factors.put("CORD" + window, calculateCORD(data, window));
            }
        }

        if (shouldCalculate("CNTP", operators)) {
            for (Integer window : windows) {
                factors.put("CNTP" + window, calculateCNTP(data, window, currentClose));
            }
        }

        if (shouldCalculate("CNTN", operators)) {
            for (Integer window : windows) {
                factors.put("CNTN" + window, calculateCNTN(data, window, currentClose));
            }
        }

        if (shouldCalculate("CNTD", operators)) {
            for (Integer window : windows) {
                factors.put("CNTD" + window, calculateCNTD(data, window, currentClose));
            }
        }

        if (shouldCalculate("SUMP", operators)) {
            for (Integer window : windows) {
                factors.put("SUMP" + window, calculateSUMP(data, window, currentClose));
            }
        }

        if (shouldCalculate("SUMN", operators)) {
            for (Integer window : windows) {
                factors.put("SUMN" + window, calculateSUMN(data, window, currentClose));
            }
        }

        if (shouldCalculate("SUMD", operators)) {
            for (Integer window : windows) {
                factors.put("SUMD" + window, calculateSUMD(data, window, currentClose));
            }
        }

        if (shouldCalculate("VMA", operators)) {
            for (Integer window : windows) {
                factors.put("VMA" + window, calculateVMA(data, window, currentVolume));
            }
        }

        if (shouldCalculate("VSTD", operators)) {
            for (Integer window : windows) {
                factors.put("VSTD" + window, calculateVSTD(data, window, currentVolume));
            }
        }

        if (shouldCalculate("WVMA", operators)) {
            for (Integer window : windows) {
                factors.put("WVMA" + window, calculateWVMA(data, window));
            }
        }

        if (shouldCalculate("VSUMP", operators)) {
            for (Integer window : windows) {
                factors.put("VSUMP" + window, calculateVSUMP(data, window, currentVolume));
            }
        }

        if (shouldCalculate("VSUMN", operators)) {
            for (Integer window : windows) {
                factors.put("VSUMN" + window, calculateVSUMN(data, window, currentVolume));
            }
        }

        if (shouldCalculate("VSUMD", operators)) {
            for (Integer window : windows) {
                factors.put("VSUMD" + window, calculateVSUMD(data, window, currentVolume));
            }
        }

        return factors;
    }

    private static boolean shouldCalculate(String operator, List<String> operators) {
        return operators == null || operators.contains(operator);
    }

    // ========== 基础统计算子 ==========

    /**
     * ROC: Rate of Change (变化率)
     * Qlib: ($close / Ref($close, d) - 1) / $close
     */
    public static double calculateROC(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        double currentPrice = data.get(data.size() - 1).getClose();
        double pastPrice = data.get(data.size() - window).getClose();

        if (pastPrice == 0) return Double.NaN;

        double roc = (currentPrice / pastPrice - 1.0) / currentClose;
        return roc;
    }

    /**
     * MA: Moving Average (移动平均)
     * Qlib: Mean($close, d) / $close
     */
    public static double calculateMA(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            sum += data.get(i).getClose();
        }

        double mean = sum / window;
        return mean / currentClose;
    }

    /**
     * STD: Standard Deviation (标准差)
     * Qlib: Std($close, d) / $close
     */
    public static double calculateSTD(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        // 计算均值
        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            sum += data.get(i).getClose();
        }
        double mean = sum / window;

        // 计算方差
        double variance = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            double diff = data.get(i).getClose() - mean;
            variance += diff * diff;
        }
        variance = variance / window;

        // 标准差
        double std = Math.sqrt(variance);
        return std / currentClose;
    }

    /**
     * BETA: 线性回归斜率
     * Qlib: slope of linear regression
     */
    public static double calculateBETA(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        // 准备x和y数据
        double[] x = new double[window];
        double[] y = new double[window];

        for (int i = 0; i < window; i++) {
            x[i] = i;
            y[i] = data.get(data.size() - window + i).getClose();
        }

        return linearRegressionSlope(x, y);
    }

    /**
     * RSQR: R-squared (R²决定系数)
     * Qlib: R-squared of linear regression
     */
    public static double calculateRSQR(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double[] x = new double[window];
        double[] y = new double[window];

        for (int i = 0; i < window; i++) {
            x[i] = i;
            y[i] = data.get(data.size() - window + i).getClose();
        }

        return linearRegressionRSquared(x, y);
    }

    /**
     * RESI: 线性回归残差
     * Qlib: ($close - predicted_value) / $close
     */
    public static double calculateRESI(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        double[] x = new double[window];
        double[] y = new double[window];

        for (int i = 0; i < window; i++) {
            x[i] = i;
            y[i] = data.get(data.size() - window + i).getClose();
        }

        // 计算回归系数
        double slope = linearRegressionSlope(x, y);
        double intercept = linearRegressionIntercept(x, y, slope);

        // 预测最后一个点
        double predictedValue = slope * (window - 1) + intercept;
        double actualValue = data.get(data.size() - 1).getClose();

        return (actualValue - predictedValue) / currentClose;
    }

    /**
     * MAX: 最大值
     * Qlib: Max($high, d) / $close
     */
    public static double calculateMAX(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        double max = Double.NEGATIVE_INFINITY;
        for (int i = data.size() - window; i < data.size(); i++) {
            max = Math.max(max, data.get(i).getHigh());
        }

        return max / currentClose;
    }

    /**
     * MIN: 最小值
     * Qlib: Min($low, d) / $close
     */
    public static double calculateMIN(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window) return Double.NaN;

        double min = Double.POSITIVE_INFINITY;
        for (int i = data.size() - window; i < data.size(); i++) {
            min = Math.min(min, data.get(i).getLow());
        }

        return min / currentClose;
    }

    /**
     * QTLU: 上四分位数
     * Qlib: Quantile($close, d, 0.8) / $close
     */
    public static double calculateQTLU(List<Candlestick> data, int window, double currentClose) {
        return calculateQuantile(data, window, 0.8, currentClose);
    }

    /**
     * QTLD: 下四分位数
     * Qlib: Quantile($close, d, 0.2) / $close
     */
    public static double calculateQTLD(List<Candlestick> data, int window, double currentClose) {
        return calculateQuantile(data, window, 0.2, currentClose);
    }

    /**
     * RANK: 排名百分比
     * Qlib: Rank($close, d) - 当前收盘价在窗口期内的分位排名，范围[0, 1]
     *
     * 计算逻辑：统计窗口内有多少价格低于当前价格，然后除以窗口大小
     * - 如果当前价格最低：rank=0/window=0.0
     * - 如果当前价格最高：rank=window/window=1.0
     */
    public static double calculateRANK(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double currentPrice = data.get(data.size() - 1).getClose();

        int rank = 0;
        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() < currentPrice) {
                rank++;
            }
        }

        // 返回归一化的rank（0到1之间），表示当前价格在窗口内的分位排名
        return (double) rank / window;
    }

    /**
     * RSV: Raw Stochastic Value
     * Qlib: ($close - Min($low, d)) / (Max($high, d) - Min($low, d) + 1e-12)
     */
    public static double calculateRSV(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double currentClose = data.get(data.size() - 1).getClose();

        double maxHigh = Double.NEGATIVE_INFINITY;
        double minLow = Double.POSITIVE_INFINITY;

        for (int i = data.size() - window; i < data.size(); i++) {
            maxHigh = Math.max(maxHigh, data.get(i).getHigh());
            minLow = Math.min(minLow, data.get(i).getLow());
        }

        double denominator = maxHigh - minLow + EPSILON;
        return (currentClose - minLow) / denominator;
    }

    /**
     * IMAX: 最大值位置（归一化）
     * Qlib: IdxMax($high, d) / d
     */
    public static double calculateIMAX(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double maxValue = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;

        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getHigh() > maxValue) {
                maxValue = data.get(i).getHigh();
                maxIndex = i - (data.size() - window);
            }
        }

        return (double) maxIndex / window;
    }

    /**
     * IMIN: 最小值位置（归一化）
     * Qlib: IdxMin($low, d) / d
     */
    public static double calculateIMIN(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double minValue = Double.POSITIVE_INFINITY;
        int minIndex = 0;

        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getLow() < minValue) {
                minValue = data.get(i).getLow();
                minIndex = i - (data.size() - window);
            }
        }

        return (double) minIndex / window;
    }

    /**
     * IMXD: 最大值位置差
     * Qlib: (IdxMax($high, d) - IdxMin($low, d)) / d
     */
    public static double calculateIMXD(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double maxValue = Double.NEGATIVE_INFINITY;
        double minValue = Double.POSITIVE_INFINITY;
        int maxIndex = 0;
        int minIndex = 0;

        for (int i = data.size() - window; i < data.size(); i++) {
            int relativeIndex = i - (data.size() - window);

            if (data.get(i).getHigh() > maxValue) {
                maxValue = data.get(i).getHigh();
                maxIndex = relativeIndex;
            }

            if (data.get(i).getLow() < minValue) {
                minValue = data.get(i).getLow();
                minIndex = relativeIndex;
            }
        }

        return (double) (maxIndex - minIndex) / window;
    }

    // ========== 相关性算子 ==========

    /**
     * CORR: 价格与成交量相关性
     * Qlib: Corr($close, Log($volume+1), d)
     */
    public static double calculateCORR(List<Candlestick> data, int window) {
        if (data.size() < window) return Double.NaN;

        double[] prices = new double[window];
        double[] volumes = new double[window];

        for (int i = 0; i < window; i++) {
            prices[i] = data.get(data.size() - window + i).getClose();
            volumes[i] = Math.log(data.get(data.size() - window + i).getVolume() + 1);
        }

        return correlation(prices, volumes);
    }

    /**
     * CORD: 价格变化率与成交量变化率相关性
     * Qlib: Corr($close/Ref($close,1), Log($volume/Ref($volume,1)+1), d)
     */
    public static double calculateCORD(List<Candlestick> data, int window) {
        if (data.size() < window + 1) return Double.NaN;

        double[] priceChanges = new double[window];
        double[] volumeChanges = new double[window];

        for (int i = 0; i < window; i++) {
            int idx = data.size() - window + i;

            double closeChange = data.get(idx).getClose() / (data.get(idx - 1).getClose() + EPSILON);
            double volumeChange = data.get(idx).getVolume() / (data.get(idx - 1).getVolume() + EPSILON);

            priceChanges[i] = closeChange;
            volumeChanges[i] = Math.log(volumeChange + 1);
        }

        return correlation(priceChanges, volumeChanges);
    }

    // ========== 计数算子 ==========

    /**
     * CNTP: 价格上涨天数占比
     * Qlib: Count($close > Ref($close, 1), d) / d
     */
    public static double calculateCNTP(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        int count = 0;
        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() > data.get(i - 1).getClose()) {
                count++;
            }
        }

        return (double) count / window;
    }

    /**
     * CNTN: 价格下跌天数占比
     * Qlib: Count($close < Ref($close, 1), d) / d
     */
    public static double calculateCNTN(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        int count = 0;
        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() < data.get(i - 1).getClose()) {
                count++;
            }
        }

        return (double) count / window;
    }

    /**
     * CNTD: 价格上涨与下跌天数差占比
     * Qlib: (CNTP - CNTN) / d
     */
    public static double calculateCNTD(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        int upCount = 0;
        int downCount = 0;

        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() > data.get(i - 1).getClose()) {
                upCount++;
            } else if (data.get(i).getClose() < data.get(i - 1).getClose()) {
                downCount++;
            }
        }

        return (double) (upCount - downCount) / window;
    }

    // ========== 求和算子 ==========

    /**
     * SUMP: 价格上涨幅度之和
     * Qlib: Sum(Max($close-Ref($close,1), 0), d) / $close
     */
    public static double calculateSUMP(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            sum += Math.max(change, 0.0);
        }

        return sum / currentClose;
    }

    /**
     * SUMN: 价格下跌幅度之和（绝对值）
     * Qlib: Sum(Abs(Min($close-Ref($close,1), 0)), d) / $close
     */
    public static double calculateSUMN(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            sum += Math.abs(Math.min(change, 0.0));
        }

        return sum / currentClose;
    }

    /**
     * SUMD: 价格上涨与下跌幅度差
     * Qlib: (SUMP - SUMN) / $close
     */
    public static double calculateSUMD(List<Candlestick> data, int window, double currentClose) {
        if (data.size() < window + 1) return Double.NaN;

        double sumUp = 0.0;
        double sumDown = 0.0;

        for (int i = data.size() - window; i < data.size(); i++) {
            double change = data.get(i).getClose() - data.get(i - 1).getClose();
            if (change > 0) {
                sumUp += change;
            } else {
                sumDown += Math.abs(change);
            }
        }

        return (sumUp - sumDown) / currentClose;
    }

    // ========== 成交量算子 ==========

    /**
     * VMA: 成交量移动平均
     * Qlib: Mean($volume, d) / $volume
     */
    public static double calculateVMA(List<Candlestick> data, int window, double currentVolume) {
        if (data.size() < window) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            sum += data.get(i).getVolume();
        }

        double mean = sum / window;
        return mean / currentVolume;
    }

    /**
     * VSTD: 成交量标准差
     * Qlib: Std($volume, d) / $volume
     */
    public static double calculateVSTD(List<Candlestick> data, int window, double currentVolume) {
        if (data.size() < window) return Double.NaN;

        // 计算均值
        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            sum += data.get(i).getVolume();
        }
        double mean = sum / window;

        // 计算方差
        double variance = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            double diff = data.get(i).getVolume() - mean;
            variance += diff * diff;
        }
        variance = variance / window;

        // 标准差
        double std = Math.sqrt(variance);
        return std / currentVolume;
    }

    /**
     * WVMA: 成交量加权的价格变化波动率
     * Qlib: Std(Abs($close/Ref($close, 1)-1)*$volume, d) / (Mean(Abs($close/Ref($close, 1)-1)*$volume, d) + 1e-12)
     */
    public static double calculateWVMA(List<Candlestick> data, int window) {
        if (data.size() < window + 1) return Double.NaN;

        // 计算加权价格变化序列
        double[] weightedChanges = new double[window];

        for (int i = 0; i < window; i++) {
            int idx = data.size() - window + i;
            double priceChange = Math.abs(data.get(idx).getClose() / data.get(idx - 1).getClose() - 1.0);
            double volume = data.get(idx).getVolume();
            weightedChanges[i] = priceChange * volume;
        }

        // 计算均值
        double sum = 0.0;
        for (double value : weightedChanges) {
            sum += value;
        }
        double mean = sum / window;

        // 计算标准差
        double variance = 0.0;
        for (double value : weightedChanges) {
            double diff = value - mean;
            variance += diff * diff;
        }
        double std = Math.sqrt(variance / window);

        return std / (mean + EPSILON);
    }

    /**
     * VSUMP: 成交量上涨时的成交量之和
     * Qlib: Sum($volume * ($close > Ref($close, 1)), d) / $volume
     */
    public static double calculateVSUMP(List<Candlestick> data, int window, double currentVolume) {
        if (data.size() < window + 1) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() > data.get(i - 1).getClose()) {
                sum += data.get(i).getVolume();
            }
        }

        return sum / currentVolume;
    }

    /**
     * VSUMN: 成交量下跌时的成交量之和
     * Qlib: Sum($volume * ($close < Ref($close, 1)), d) / $volume
     */
    public static double calculateVSUMN(List<Candlestick> data, int window, double currentVolume) {
        if (data.size() < window + 1) return Double.NaN;

        double sum = 0.0;
        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() < data.get(i - 1).getClose()) {
                sum += data.get(i).getVolume();
            }
        }

        return sum / currentVolume;
    }

    /**
     * VSUMD: 成交量上涨与下跌差
     * Qlib: (VSUMP - VSUMN) / $volume
     */
    public static double calculateVSUMD(List<Candlestick> data, int window, double currentVolume) {
        if (data.size() < window + 1) return Double.NaN;

        double sumUp = 0.0;
        double sumDown = 0.0;

        for (int i = data.size() - window; i < data.size(); i++) {
            if (data.get(i).getClose() > data.get(i - 1).getClose()) {
                sumUp += data.get(i).getVolume();
            } else if (data.get(i).getClose() < data.get(i - 1).getClose()) {
                sumDown += data.get(i).getVolume();
            }
        }

        return (sumUp - sumDown) / currentVolume;
    }

    // ========== 辅助方法 ==========

    private static double calculateQuantile(List<Candlestick> data, int window, double quantile, double currentClose) {
        if (data.size() < window) return Double.NaN;

        List<Double> values = new ArrayList<>();
        for (int i = data.size() - window; i < data.size(); i++) {
            values.add(data.get(i).getClose());
        }

        Collections.sort(values);
        int index = (int) Math.ceil(quantile * window) - 1;
        index = Math.max(0, Math.min(index, window - 1));

        return values.get(index) / currentClose;
    }

    /**
     * 线性回归斜率
     */
    private static double linearRegressionSlope(double[] x, double[] y) {
        int n = x.length;

        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (Math.abs(denominator) < EPSILON) return 0.0;

        return (n * sumXY - sumX * sumY) / denominator;
    }

    /**
     * 线性回归截距
     */
    private static double linearRegressionIntercept(double[] x, double[] y, double slope) {
        int n = x.length;

        double sumX = 0.0, sumY = 0.0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
        }

        return (sumY - slope * sumX) / n;
    }

    /**
     * 线性回归R²
     */
    private static double linearRegressionRSquared(double[] x, double[] y) {
        int n = x.length;

        // 计算y的均值
        double sumY = 0.0;
        for (double value : y) {
            sumY += value;
        }
        double meanY = sumY / n;

        // 计算回归系数
        double slope = linearRegressionSlope(x, y);
        double intercept = linearRegressionIntercept(x, y, slope);

        // 计算SST和SSR
        double sst = 0.0, ssr = 0.0;
        for (int i = 0; i < n; i++) {
            double predicted = slope * x[i] + intercept;
            ssr += (predicted - meanY) * (predicted - meanY);
            sst += (y[i] - meanY) * (y[i] - meanY);
        }

        if (Math.abs(sst) < EPSILON) return 0.0;
        return ssr / sst;
    }

    /**
     * 相关系数
     */
    private static double correlation(double[] x, double[] y) {
        int n = x.length;

        double sumX = 0.0, sumY = 0.0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
        }
        double meanX = sumX / n;
        double meanY = sumY / n;

        double numerator = 0.0, sumX2 = 0.0, sumY2 = 0.0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            numerator += dx * dy;
            sumX2 += dx * dx;
            sumY2 += dy * dy;
        }

        double denominator = Math.sqrt(sumX2 * sumY2);
        if (Math.abs(denominator) < EPSILON) return 0.0;

        return numerator / denominator;
    }
}
