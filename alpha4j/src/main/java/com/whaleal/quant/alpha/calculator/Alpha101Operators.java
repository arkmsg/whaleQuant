package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Alpha101算子库
 *
 * 实现WorldQuant Alpha101所需的所有基础算子
 * 包括Alpha158没有的算子：covariance, product, signedpower, decay_linear等
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Operators {

    private static final double EPSILON = 1e-12;

    // ==================== 时间序列算子 ====================

    /**
     * delay - 延迟d天
     * delay(x, d) = x[t-d]
     */
    public static List<Double> delay(List<Double> data, int d) {
        if (data == null || data.size() <= d) {
            return new ArrayList<>();
        }
        return new ArrayList<>(data.subList(0, data.size() - d));
    }

    /**
     * delta - d天变化
     * delta(x, d) = x[t] - x[t-d]
     */
    public static List<Double> delta(List<Double> data, int d) {
        if (data == null || data.size() <= d) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = d; i < data.size(); i++) {
            result.add(data.get(i) - data.get(i - d));
        }
        return result;
    }

    /**
     * ts_sum - 时间序列求和
     */
    public static List<Double> ts_sum(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += data.get(j);
            }
            result.add(sum);
        }
        return result;
    }

    /**
     * ts_min - 时间序列最小值
     */
    public static List<Double> ts_min(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double min = Double.MAX_VALUE;
            for (int j = i - window + 1; j <= i; j++) {
                min = Math.min(min, data.get(j));
            }
            result.add(min);
        }
        return result;
    }

    /**
     * ts_max - 时间序列最大值
     */
    public static List<Double> ts_max(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double max = -Double.MAX_VALUE;
            for (int j = i - window + 1; j <= i; j++) {
                max = Math.max(max, data.get(j));
            }
            result.add(max);
        }
        return result;
    }

    /**
     * ts_argmax - 最大值出现的位置
     */
    public static List<Double> ts_argmax(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double max = -Double.MAX_VALUE;
            int argmax = 0;
            for (int j = i - window + 1; j <= i; j++) {
                if (data.get(j) > max) {
                    max = data.get(j);
                    argmax = j - (i - window + 1);
                }
            }
            result.add((double) argmax);
        }
        return result;
    }

    /**
     * ts_argmin - 最小值出现的位置
     */
    public static List<Double> ts_argmin(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double min = Double.MAX_VALUE;
            int argmin = 0;
            for (int j = i - window + 1; j <= i; j++) {
                if (data.get(j) < min) {
                    min = data.get(j);
                    argmin = j - (i - window + 1);
                }
            }
            result.add((double) argmin);
        }
        return result;
    }

    /**
     * ts_rank - 时间序列排名
     * 返回当前值在窗口内的排名（百分位）
     */
    public static List<Double> ts_rank(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double currentValue = data.get(i);
            int rank = 0;
            for (int j = i - window + 1; j <= i; j++) {
                if (data.get(j) < currentValue) {
                    rank++;
                }
            }
            result.add(rank / (double) (window - 1));
        }
        return result;
    }

    // ==================== 统计算子 ====================

    /**
     * stddev - 标准差
     */
    public static List<Double> stddev(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += data.get(j);
            }
            double mean = sum / window;

            double variance = 0;
            for (int j = i - window + 1; j <= i; j++) {
                double diff = data.get(j) - mean;
                variance += diff * diff;
            }
            result.add(Math.sqrt(variance / window));
        }
        return result;
    }

    /**
     * correlation - 相关系数
     */
    public static List<Double> correlation(List<Double> x, List<Double> y, int window) {
        if (x == null || y == null || x.size() != y.size() || x.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < x.size(); i++) {
            double sumX = 0, sumY = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sumX += x.get(j);
                sumY += y.get(j);
            }
            double meanX = sumX / window;
            double meanY = sumY / window;

            double numerator = 0, denomX = 0, denomY = 0;
            for (int j = i - window + 1; j <= i; j++) {
                double diffX = x.get(j) - meanX;
                double diffY = y.get(j) - meanY;
                numerator += diffX * diffY;
                denomX += diffX * diffX;
                denomY += diffY * diffY;
            }

            double denom = Math.sqrt(denomX * denomY);
            result.add(denom < EPSILON ? 0 : numerator / denom);
        }
        return result;
    }

    /**
     * covariance - 协方差 (Alpha101独有)
     */
    public static List<Double> covariance(List<Double> x, List<Double> y, int window) {
        if (x == null || y == null || x.size() != y.size() || x.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < x.size(); i++) {
            double sumX = 0, sumY = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sumX += x.get(j);
                sumY += y.get(j);
            }
            double meanX = sumX / window;
            double meanY = sumY / window;

            double covar = 0;
            for (int j = i - window + 1; j <= i; j++) {
                covar += (x.get(j) - meanX) * (y.get(j) - meanY);
            }
            result.add(covar / window);
        }
        return result;
    }

    // ==================== 横截面算子 ====================

    /**
     * rank - 横截面排名（百分位）
     * 注意：这里是单时间序列的rank，实际应用中是横截面所有股票的rank
     */
    public static List<Double> rank(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            double currentValue = data.get(i);
            int rank = 0;
            for (int j = 0; j < data.size(); j++) {
                if (data.get(j) < currentValue) {
                    rank++;
                }
            }
            result.add(rank / (double) (data.size() - 1));
        }
        return result;
    }

    /**
     * scale - 缩放归一化
     * scale(x, a) = a * x / sum(|x|)
     */
    public static List<Double> scale(List<Double> data, double a) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        double sumAbs = 0;
        for (double v : data) {
            sumAbs += Math.abs(v);
        }

        List<Double> result = new ArrayList<>();
        if (sumAbs < EPSILON) {
            for (int i = 0; i < data.size(); i++) {
                result.add(0.0);
            }
        } else {
            for (double v : data) {
                result.add(a * v / sumAbs);
            }
        }
        return result;
    }

    /**
     * scale - 缩放归一化 (默认k=1)
     */
    public static List<Double> scale(List<Double> data) {
        return scale(data, 1.0);
    }

    // ==================== 数学算子 ====================

    /**
     * signedpower - 符号幂 (Alpha101独有)
     * signedpower(x, a) = sign(x) * |x|^a
     */
    public static List<Double> signedpower(List<Double> data, double a) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (double v : data) {
            double sign = v >= 0 ? 1 : -1;
            result.add(sign * Math.pow(Math.abs(v), a));
        }
        return result;
    }

    /**
     * signedpower - 单值符号幂 (Alpha101独有)
     * signedpower(x, a) = sign(x) * |x|^a
     */
    public static double signedpower(double value, double a) {
        double sign = value >= 0 ? 1 : -1;
        return sign * Math.pow(Math.abs(value), a);
    }

    /**
     * product - 乘积 (Alpha101独有)
     */
    public static List<Double> product(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double prod = 1.0;
            for (int j = i - window + 1; j <= i; j++) {
                prod *= data.get(j);
            }
            result.add(prod);
        }
        return result;
    }

    /**
     * decay_linear - 线性衰减加权移动平均 (Alpha101独有)
     * 权重从1到d线性递增，最近的数据权重最大
     */
    public static List<Double> decay_linear(List<Double> data, int d) {
        if (data == null || data.size() < d) {
            return new ArrayList<>();
        }

        // 计算权重总和: 1 + 2 + ... + d = d*(d+1)/2
        double weightSum = d * (d + 1) / 2.0;

        List<Double> result = new ArrayList<>();
        for (int i = d - 1; i < data.size(); i++) {
            double weightedSum = 0;
            for (int j = 0; j < d; j++) {
                // 权重从1到d，最近的权重最大
                double weight = (j + 1) / weightSum;
                weightedSum += data.get(i - d + 1 + j) * weight;
            }
            result.add(weightedSum);
        }
        return result;
    }

    // ==================== 辅助函数 ====================

    /**
     * sign - 符号函数
     */
    public static List<Double> sign(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (double v : data) {
            if (v > EPSILON) {
                result.add(1.0);
            } else if (v < -EPSILON) {
                result.add(-1.0);
            } else {
                result.add(0.0);
            }
        }
        return result;
    }

    /**
     * sign - 单值符号函数
     */
    public static double sign(double value) {
        if (value > EPSILON) {
            return 1.0;
        } else if (value < -EPSILON) {
            return -1.0;
        } else {
            return 0.0;
        }
    }

    /**
     * abs - 绝对值
     */
    public static List<Double> abs(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (double v : data) {
            result.add(Math.abs(v));
        }
        return result;
    }

    /**
     * log - 自然对数
     */
    public static List<Double> log(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (double v : data) {
            result.add(v > EPSILON ? Math.log(v) : 0.0);
        }
        return result;
    }

    /**
     * sma - 简单移动平均（用于计算adv20）
     */
    public static List<Double> sma(List<Double> data, int window) {
        if (data == null || data.size() < window) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();
        for (int i = window - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += data.get(j);
            }
            result.add(sum / window);
        }
        return result;
    }

    // ==================== 数据提取辅助方法 ====================

    /**
     * 从K线列表中提取收盘价
     */
    public static List<Double> extractClose(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            result.add(k.getClose());
        }
        return result;
    }

    /**
     * 从K线列表中提取开盘价
     */
    public static List<Double> extractOpen(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            result.add(k.getOpen());
        }
        return result;
    }

    /**
     * 从K线列表中提取最高价
     */
    public static List<Double> extractHigh(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            result.add(k.getHigh());
        }
        return result;
    }

    /**
     * 从K线列表中提取最低价
     */
    public static List<Double> extractLow(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            result.add(k.getLow());
        }
        return result;
    }

    /**
     * 从K线列表中提取成交量
     */
    public static List<Double> extractVolume(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            result.add((double) k.getVolume());
        }
        return result;
    }

    /**
     * 计算VWAP（成交量加权平均价格）
     */
    public static List<Double> extractVwap(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (Candlestick k : data) {
            // VWAP = amount / volume
            // 这里使用 (high + low + close) / 3 作为近似
            result.add((k.getHigh() + k.getLow() + k.getClose()) / 3.0);
        }
        return result;
    }

    /**
     * 计算收益率
     */
    public static List<Double> calculateReturns(List<Candlestick> data) {
        List<Double> result = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            double prevClose = data.get(i - 1).getClose();
            double currentClose = data.get(i).getClose();
            result.add((currentClose - prevClose) / prevClose);
        }
        return result;
    }
}

