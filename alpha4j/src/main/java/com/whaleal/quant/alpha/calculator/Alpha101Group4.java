package com.whaleal.quant.alpha.calculator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101 Group4: Alpha#61-80
 *
 * 高级价量因子组
 *
 * ⚠️ 未实现因子说明：
 *
 * 需要行业中性化（IndNeutralize）的因子（7个）：
 *    - Alpha#63: ❌ 未实现 - 需要行业中性化
 *    - Alpha#67: ❌ 未实现 - 需要行业中性化
 *    - Alpha#69: ❌ 未实现 - 需要行业中性化
 *    - Alpha#70: ❌ 未实现 - 需要行业中性化
 *    - Alpha#76: ❌ 未实现 - 需要行业中性化
 *    - Alpha#79: ❌ 未实现 - 需要行业中性化
 *    - Alpha#80: ❌ 未实现 - 需要行业中性化
 *
 * 📌 预留扩展：
 *    行业中性化（IndNeutralize）需要以下数据：
 *    1. 行业分类数据（如申万行业、中信行业、GICS等）
 *    2. 对每个因子值在同行业内进行标准化
 *    3. 公式：neutralized_value = (value - industry_mean) / industry_std
 *
 * 🔧 实现说明：
 *    需要修改 Alpha101Calculator 的接口，增加行业分类参数：
 *    ```java
 *    public static AlphaFactorResult calculate(
 *        List<Candlestick> data,
 *        Map<String, String> industryMap,  // symbol -> industry
 *        Alpha101Config config
 *    )
 *    ```
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Group4 {

    private static final double EPSILON = 1e-12;

    /**
     * 计算指定的Alpha因子
     */
    public static Double calculate(int alphaNumber, List<Double> close, List<Double> open,
                                   List<Double> high, List<Double> low, List<Double> volume,
                                   List<Double> vwap, List<Double> returns, List<Double> adv20) {
        switch (alphaNumber) {
            case 61: return alpha061(vwap, volume);
            case 62: return alpha062(open, high, low, vwap, volume);
            case 63:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                // 🔧 实现：需要计算同行业内的标准化值
                log.warn("Alpha#63 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 64: return alpha064(open, high, low, vwap, volume);
            case 65: return alpha065(open, vwap, volume);
            case 66: return alpha066(open, high, low, vwap);
            case 67:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#67 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 68: return alpha068(close, high, low, volume);
            case 69:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#69 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 70:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#70 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 71: return alpha071(open, close, low, vwap, volume);
            case 72: return alpha072(high, low, vwap, volume);
            case 73: return alpha073(open, low, vwap);
            case 74: return alpha074(close, high, vwap, volume);
            case 75: return alpha075(low, vwap, volume);
            case 76:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#76 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 77: return alpha077(high, low, vwap, volume);
            case 78: return alpha078(low, vwap, volume);
            case 79:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#79 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 80:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要传入行业分类数据
                log.warn("Alpha#80 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            default:
                return 0.0;
        }
    }

    /**
     * Alpha#61: (rank((vwap - ts_min(vwap, 16.1219))) < rank(correlation(vwap, adv180, 17.9282)))
     */
    private static Double alpha061(List<Double> vwap, List<Double> volume) {
        List<Double> adv180 = sma(volume, 180);

        List<Double> tsMinVwap = ts_min(vwap, 16);
        List<Double> part1 = new ArrayList<>();
        for (int i = 0; i < Math.min(vwap.size(), tsMinVwap.size()); i++) {
            int vwapIdx = vwap.size() - tsMinVwap.size() + i;
            part1.add(vwap.get(vwapIdx) - tsMinVwap.get(i));
        }

        List<Double> rank1 = rank(part1);
        List<Double> corr = correlation(vwap, adv180, 18);
        List<Double> rank2 = rank(corr);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return (rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0;
    }

    /**
     * Alpha#62: ((rank(correlation(vwap, sum(adv20, 22.4101), 9.91009)) <
     *            rank(((rank(open) + rank(open)) < (rank(((high + low) / 2)) + rank(high))))) * -1)
     */
    private static Double alpha062(List<Double> open, List<Double> high, List<Double> low,
                                   List<Double> vwap, List<Double> volume) {
        List<Double> adv20 = sma(volume, 20);
        List<Double> sumAdv20 = sma(adv20, 22);
        List<Double> corr = correlation(vwap, sumAdv20, 10);
        List<Double> rank1 = rank(corr);

        List<Double> rankOpen = rank(open);
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> rankMid = rank(midPrice);
        List<Double> rankHigh = rank(high);

        List<Double> cond = new ArrayList<>();
        int minSize = Math.min(Math.min(rankOpen.size(), rankMid.size()), rankHigh.size());
        for (int i = 0; i < minSize; i++) {
            int openIdx = rankOpen.size() - minSize + i;
            int midIdx = rankMid.size() - minSize + i;
            int highIdx = rankHigh.size() - minSize + i;

            double leftSide = rankOpen.get(openIdx) + rankOpen.get(openIdx);
            double rightSide = rankMid.get(midIdx) + rankHigh.get(highIdx);
            cond.add((leftSide < rightSide) ? 1.0 : 0.0);
        }

        List<Double> rank2 = rank(cond);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return ((rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#64: ((rank(correlation(sum(((open * 0.178404) + (low * (1 - 0.178404))), 12.7054),
     *            sum(adv120, 12.7054), 16.6208)) < rank(delta(((((high + low) / 2) * 0.178404) +
     *            (vwap * (1 - 0.178404))), 3.69741))) * -1)
     */
    private static Double alpha064(List<Double> open, List<Double> high, List<Double> low,
                                   List<Double> vwap, List<Double> volume) {
        List<Double> adv120 = sma(volume, 120);

        // Part 1: sum((open * 0.178404) + (low * (1 - 0.178404)))
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(open.size(), low.size()); i++) {
            weighted.add((open.get(i) * 0.178404) + (low.get(i) * (1 - 0.178404)));
        }
        List<Double> sumWeighted = sma(weighted, 13);

        // Part 2: sum(adv120, 13)
        List<Double> sumAdv120 = sma(adv120, 13);

        // Correlation
        List<Double> corr = correlation(sumWeighted, sumAdv120, 17);
        List<Double> rank1 = rank(corr);

        // Part 3: delta(((high + low) / 2 * 0.178404) + (vwap * (1 - 0.178404)))
        List<Double> midWeighted = new ArrayList<>();
        int minSize = Math.min(Math.min(high.size(), low.size()), vwap.size());
        for (int i = 0; i < minSize; i++) {
            double mid = (high.get(i) + low.get(i)) / 2.0;
            midWeighted.add((mid * 0.178404) + (vwap.get(i) * (1 - 0.178404)));
        }
        List<Double> deltaMid = delta(midWeighted, 4);
        List<Double> rank2 = rank(deltaMid);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return ((rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#65: ((rank(correlation(((open * 0.00817205) + (vwap * (1 - 0.00817205))),
     *            sum(adv60, 8.6911), 6.40374)) < rank((open - ts_min(open, 13.635)))) * -1)
     */
    private static Double alpha065(List<Double> open, List<Double> vwap, List<Double> volume) {
        List<Double> adv60 = sma(volume, 60);

        // Part 1: (open * 0.00817205) + (vwap * (1 - 0.00817205))
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(open.size(), vwap.size()); i++) {
            weighted.add((open.get(i) * 0.00817205) + (vwap.get(i) * (1 - 0.00817205)));
        }

        // Part 2: sum(adv60, 9)
        List<Double> sumAdv60 = sma(adv60, 9);

        // Correlation
        List<Double> corr = correlation(weighted, sumAdv60, 6);
        List<Double> rank1 = rank(corr);

        // Part 3: open - ts_min(open, 14)
        List<Double> tsMinOpen = ts_min(open, 14);
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < Math.min(open.size(), tsMinOpen.size()); i++) {
            int openIdx = open.size() - tsMinOpen.size() + i;
            diff.add(open.get(openIdx) - tsMinOpen.get(i));
        }
        List<Double> rank2 = rank(diff);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return ((rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#66: ((rank(decay_linear(delta(vwap, 3.51013), 7.23052)) +
     *            Ts_Rank(decay_linear(((((low * 0.96633) + (low * (1 - 0.96633))) - vwap) /
     *            (open - ((high + low) / 2))), 11.4157), 6.72611)) * -1)
     */
    private static Double alpha066(List<Double> open, List<Double> high, List<Double> low, List<Double> vwap) {
        // Part 1: decay_linear(delta(vwap, 4), 7)
        List<Double> deltaVwap = delta(vwap, 4);
        List<Double> decayed1 = decay_linear(deltaVwap, 7);
        List<Double> rank1 = rank(decayed1);

        // Part 2: ((low * 0.96633 + low * (1 - 0.96633)) - vwap) / (open - ((high + low) / 2))
        List<Double> inner = new ArrayList<>();
        int minSize = Math.min(Math.min(Math.min(low.size(), vwap.size()), open.size()), high.size());
        for (int i = 0; i < minSize; i++) {
            double weightedLow = (low.get(i) * 0.96633) + (low.get(i) * (1 - 0.96633));
            double mid = (high.get(i) + low.get(i)) / 2.0;
            double numerator = weightedLow - vwap.get(i);
            double denominator = open.get(i) - mid + EPSILON;
            inner.add(numerator / denominator);
        }

        List<Double> decayed2 = decay_linear(inner, 11);
        List<Double> tsRank = ts_rank(decayed2, 7);

        if (rank1.isEmpty() || tsRank.isEmpty()) {
            return 0.0;
        }

        return (rank1.get(rank1.size() - 1) + tsRank.get(tsRank.size() - 1)) * -1;
    }

    /**
     * Alpha#68: ((Ts_Rank(correlation(rank(high), rank(adv15), 8.91644), 13.9333) <
     *            rank(delta(((close * 0.518371) + (low * (1 - 0.518371))), 1.06157))) * -1)
     */
    private static Double alpha068(List<Double> close, List<Double> high, List<Double> low, List<Double> volume) {
        List<Double> adv15 = sma(volume, 15);

        // Part 1: ts_rank(correlation(rank(high), rank(adv15), 9), 14)
        List<Double> rankHigh = rank(high);
        List<Double> rankAdv15 = rank(adv15);
        List<Double> corr = correlation(rankHigh, rankAdv15, 9);
        List<Double> tsRank1 = ts_rank(corr, 14);

        // Part 2: delta(((close * 0.518371) + (low * (1 - 0.518371))), 2) * 14
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(close.size(), low.size()); i++) {
            weighted.add((close.get(i) * 0.518371) + (low.get(i) * (1 - 0.518371)));
        }
        List<Double> deltaWeighted = delta(weighted, 2);

        // 乘以14使双方处于同一水平
        List<Double> scaled = new ArrayList<>();
        for (double d : deltaWeighted) {
            scaled.add(d * 14);
        }
        List<Double> rank1 = rank(scaled);

        if (tsRank1.isEmpty() || rank1.isEmpty()) {
            return 0.0;
        }

        return ((tsRank1.get(tsRank1.size() - 1) < rank1.get(rank1.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#71: max(Ts_Rank(decay_linear(correlation(Ts_Rank(close, 3.43976), Ts_Rank(adv180, 12.0647),
     *                18.0175), 4.20501), 15.6948), Ts_Rank(decay_linear((rank(((low + open) -
     *                (vwap + vwap)))^2), 16.4662), 4.4388))
     */
    private static Double alpha071(List<Double> open, List<Double> close, List<Double> low,
                                   List<Double> vwap, List<Double> volume) {
        List<Double> adv180 = sma(volume, 180);

        // Part 1: ts_rank(decay_linear(correlation(ts_rank(close, 3), ts_rank(adv180, 12), 18), 4), 16)
        List<Double> tsRankClose = ts_rank(close, 3);
        List<Double> tsRankAdv180 = ts_rank(adv180, 12);
        List<Double> corr = correlation(tsRankClose, tsRankAdv180, 18);
        List<Double> decayed1 = decay_linear(corr, 4);
        List<Double> part1 = ts_rank(decayed1, 16);

        // Part 2: ts_rank(decay_linear((rank((low + open) - (vwap + vwap)))^2, 16), 4)
        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(Math.min(low.size(), open.size()), vwap.size());
        for (int i = 0; i < minSize; i++) {
            diff.add((low.get(i) + open.get(i)) - (vwap.get(i) + vwap.get(i)));
        }
        List<Double> ranked = rank(diff);
        List<Double> squared = new ArrayList<>();
        for (double r : ranked) {
            squared.add(r * r);
        }
        List<Double> decayed2 = decay_linear(squared, 16);
        List<Double> part2 = ts_rank(decayed2, 4);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return Math.max(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#72: (rank(decay_linear(correlation(((high + low) / 2), adv40, 8.93345), 10.1519)) /
     *            rank(decay_linear(correlation(Ts_Rank(vwap, 3.72469), Ts_Rank(volume, 18.5188),
     *            6.86671), 2.95011)))
     */
    private static Double alpha072(List<Double> high, List<Double> low, List<Double> vwap, List<Double> volume) {
        List<Double> adv40 = sma(volume, 40);

        // Part 1: rank(decay_linear(correlation((high + low) / 2, adv40, 9), 10))
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> corr1 = correlation(midPrice, adv40, 9);
        List<Double> decayed1 = decay_linear(corr1, 10);
        List<Double> rank1 = rank(decayed1);

        // Part 2: rank(decay_linear(correlation(ts_rank(vwap, 4), ts_rank(volume, 19), 7), 3))
        List<Double> tsRankVwap = ts_rank(vwap, 4);
        List<Double> tsRankVol = ts_rank(volume, 19);
        List<Double> corr2 = correlation(tsRankVwap, tsRankVol, 7);
        List<Double> decayed2 = decay_linear(corr2, 3);
        List<Double> rank2 = rank(decayed2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return rank1.get(rank1.size() - 1) / (rank2.get(rank2.size() - 1) + EPSILON);
    }

    /**
     * Alpha#73: (max(rank(decay_linear(delta(vwap, 4.72775), 2.91864)),
     *            Ts_Rank(decay_linear(((delta(((open * 0.147155) + (low * (1 - 0.147155))), 2.03608) /
     *            ((open * 0.147155) + (low * (1 - 0.147155)))) * -1), 3.33829), 16.7411)) * -1)
     */
    private static Double alpha073(List<Double> open, List<Double> low, List<Double> vwap) {
        // Part 1: rank(decay_linear(delta(vwap, 5), 3))
        List<Double> deltaVwap = delta(vwap, 5);
        List<Double> decayed1 = decay_linear(deltaVwap, 3);
        List<Double> part1 = rank(decayed1);

        // Part 2: ts_rank(decay_linear(((delta((open * 0.147155 + low * (1 - 0.147155)), 2) /
        //                                 (open * 0.147155 + low * (1 - 0.147155))) * -1), 3), 17)
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(open.size(), low.size()); i++) {
            weighted.add((open.get(i) * 0.147155) + (low.get(i) * (1 - 0.147155)));
        }
        List<Double> deltaWeighted = delta(weighted, 2);
        List<Double> ratio = new ArrayList<>();
        for (int i = 0; i < Math.min(deltaWeighted.size(), weighted.size()); i++) {
            int weightedIdx = weighted.size() - deltaWeighted.size() + i;
            ratio.add((deltaWeighted.get(i) / (weighted.get(weightedIdx) + EPSILON)) * -1);
        }
        List<Double> decayed2 = decay_linear(ratio, 3);
        List<Double> part2 = ts_rank(decayed2, 17);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return -1 * Math.max(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#74: ((rank(correlation(close, sum(adv30, 37.4843), 15.1365)) <
     *            rank(correlation(rank(((high * 0.0261661) + (vwap * (1 - 0.0261661)))),
     *            rank(volume), 11.4791))) * -1)
     */
    private static Double alpha074(List<Double> close, List<Double> high, List<Double> vwap, List<Double> volume) {
        List<Double> adv30 = sma(volume, 30);

        // Part 1: rank(correlation(close, sma(adv30, 37), 15))
        List<Double> sumAdv30 = sma(adv30, 37);
        List<Double> corr1 = correlation(close, sumAdv30, 15);
        List<Double> rank1 = rank(corr1);

        // Part 2: rank(correlation(rank((high * 0.0261661 + vwap * (1 - 0.0261661))), rank(volume), 11))
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), vwap.size()); i++) {
            weighted.add((high.get(i) * 0.0261661) + (vwap.get(i) * (1 - 0.0261661)));
        }
        List<Double> rankWeighted = rank(weighted);
        List<Double> rankVol = rank(volume);
        List<Double> corr2 = correlation(rankWeighted, rankVol, 11);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return ((rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#75: (rank(correlation(vwap, volume, 4.24304)) <
     *            rank(correlation(rank(low), rank(adv50), 12.4413)))
     */
    private static Double alpha075(List<Double> low, List<Double> vwap, List<Double> volume) {
        List<Double> adv50 = sma(volume, 50);

        // Part 1: rank(correlation(vwap, volume, 4))
        List<Double> corr1 = correlation(vwap, volume, 4);
        List<Double> rank1 = rank(corr1);

        // Part 2: rank(correlation(rank(low), rank(adv50), 12))
        List<Double> rankLow = rank(low);
        List<Double> rankAdv50 = rank(adv50);
        List<Double> corr2 = correlation(rankLow, rankAdv50, 12);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return (rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0;
    }

    /**
     * Alpha#77: min(rank(decay_linear(((((high + low) / 2) + high) - (vwap + high)), 20.0451)),
     *            rank(decay_linear(correlation(((high + low) / 2), adv40, 3.1614), 5.64125)))
     */
    private static Double alpha077(List<Double> high, List<Double> low, List<Double> vwap, List<Double> volume) {
        List<Double> adv40 = sma(volume, 40);

        // Part 1: rank(decay_linear((((high + low) / 2) + high) - (vwap + high), 20))
        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(Math.min(high.size(), low.size()), vwap.size());
        for (int i = 0; i < minSize; i++) {
            double mid = (high.get(i) + low.get(i)) / 2.0;
            diff.add((mid + high.get(i)) - (vwap.get(i) + high.get(i)));
        }
        List<Double> decayed1 = decay_linear(diff, 20);
        List<Double> part1 = rank(decayed1);

        // Part 2: rank(decay_linear(correlation((high + low) / 2, adv40, 3), 6))
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> corr = correlation(midPrice, adv40, 3);
        List<Double> decayed2 = decay_linear(corr, 6);
        List<Double> part2 = rank(decayed2);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return Math.min(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#78: (rank(correlation(sum(((low * 0.352233) + (vwap * (1 - 0.352233))), 19.7428),
     *            sum(adv40, 19.7428), 6.83313))^rank(correlation(rank(vwap), rank(volume), 5.77492)))
     */
    private static Double alpha078(List<Double> low, List<Double> vwap, List<Double> volume) {
        List<Double> adv40 = sma(volume, 40);

        // Part 1: ts_sum((low * 0.352233 + vwap * (1 - 0.352233)), 20)
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(low.size(), vwap.size()); i++) {
            weighted.add((low.get(i) * 0.352233) + (vwap.get(i) * (1 - 0.352233)));
        }
        List<Double> sumWeighted = ts_sum(weighted, 20);

        // Part 2: ts_sum(adv40, 20)
        List<Double> sumAdv40 = ts_sum(adv40, 20);

        // Correlation and rank
        List<Double> corr1 = correlation(sumWeighted, sumAdv40, 7);
        List<Double> rank1 = rank(corr1);

        // Part 3: rank(correlation(rank(vwap), rank(volume), 6))
        List<Double> rankVwap = rank(vwap);
        List<Double> rankVol = rank(volume);
        List<Double> corr2 = correlation(rankVwap, rankVol, 6);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return Math.pow(rank1.get(rank1.size() - 1), rank2.get(rank2.size() - 1));
    }
}

