package com.whaleal.quant.alpha;

import java.util.ArrayList;
import java.util.List;

/**
 * Alpha101因子顺序定义
 *
 * 严格按照WorldQuant官方顺序：Alpha#1 到 Alpha#101
 *
 * @author arkmsg
 */
public class Alpha101FactorOrder {

    /**
     * 获取Alpha101因子的标准顺序
     *
     * @param config 配置
     * @return 因子名称列表，按Alpha#1到Alpha#101顺序
     */
    public static List<String> getFactorOrder(Alpha101Config config) {
        List<String> order = new ArrayList<>();

        for (int i = 1; i <= 101; i++) {
            if (config.useAlpha(i)) {
                order.add("alpha" + String.format("%03d", i));
            }
        }

        return order;
    }

    /**
     * 获取默认的因子顺序（全部101个）
     */
    public static List<String> getDefaultOrder() {
        return getFactorOrder(Alpha101Config.createDefault());
    }

    /**
     * 获取因子的显示名称
     */
    public static String getFactorDisplayName(int alphaNumber) {
        return "Alpha#" + alphaNumber;
    }

    /**
     * Alpha101因子的官方公式（前20个示例）
     */
    public static String getFactorFormula(int alphaNumber) {
        switch (alphaNumber) {
            case 1:
                return "(rank(Ts_ArgMax(SignedPower(((returns < 0) ? stddev(returns, 20) : close), 2.), 5)) -0.5)";
            case 2:
                return "(-1 * correlation(rank(delta(log(volume), 2)), rank(((close - open) / open)), 6))";
            case 3:
                return "(-1 * correlation(rank(open), rank(volume), 10))";
            case 4:
                return "(-1 * Ts_Rank(rank(low), 9))";
            case 5:
                return "(rank((open - (sum(vwap, 10) / 10))) * (-1 * abs(rank((close - vwap)))))";
            case 6:
                return "(-1 * correlation(open, volume, 10))";
            case 7:
                return "((adv20 < volume) ? ((-1 * ts_rank(abs(delta(close, 7)), 60)) * sign(delta(close, 7))) : (-1* 1))";
            case 8:
                return "(-1 * rank(((sum(open, 5) * sum(returns, 5)) - delay((sum(open, 5) * sum(returns, 5)),10))))";
            case 9:
                return "((0 < ts_min(delta(close, 1), 5)) ? delta(close, 1) : ((ts_max(delta(close, 1), 5) < 0) ?delta(close, 1) : (-1 * delta(close, 1))))";
            case 10:
                return "rank(((0 < ts_min(delta(close, 1), 4)) ? delta(close, 1) : ((ts_max(delta(close, 1), 4) < 0)? delta(close, 1) : (-1 * delta(close, 1)))))";
            case 11:
                return "((rank(ts_max((vwap - close), 3)) + rank(ts_min((vwap - close), 3))) *rank(delta(volume, 3)))";
            case 12:
                return "(sign(delta(volume, 1)) * (-1 * delta(close, 1)))";
            case 13:
                return "(-1 * rank(covariance(rank(close), rank(volume), 5)))";
            case 14:
                return "((-1 * rank(delta(returns, 3))) * correlation(open, volume, 10))";
            case 15:
                return "(-1 * sum(rank(correlation(rank(high), rank(volume), 3)), 3))";
            case 16:
                return "(-1 * rank(covariance(rank(high), rank(volume), 5)))";
            case 17:
                return "(((-1 * rank(ts_rank(close, 10))) * rank(delta(delta(close, 1), 1))) *rank(ts_rank((volume / adv20), 5)))";
            case 18:
                return "(-1 * rank(((stddev(abs((close - open)), 5) + (close - open)) + correlation(close, open,10))))";
            case 19:
                return "((-1 * sign(((close - delay(close, 7)) + delta(close, 7)))) * (1 + rank((1 + sum(returns,250)))))";
            case 20:
                return "(((-1 * rank((open - delay(high, 1)))) * rank((open - delay(close, 1)))) * rank((open -delay(low, 1))))";
            default:
                return "Alpha#" + alphaNumber + " formula";
        }
    }
}

