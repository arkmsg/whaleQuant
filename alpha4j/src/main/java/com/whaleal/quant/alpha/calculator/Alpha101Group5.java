package com.whaleal.quant.alpha.calculator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101 Group5: Alpha#81-101
 *
 * 综合因子组（最后21个因子）
 *
 * ⚠️ 未实现因子说明：
 *
 * 需要行业中性化（IndNeutralize）的因子（8个）：
 *    - Alpha#82: ❌ 未实现 - 需要行业中性化
 *    - Alpha#87: ❌ 未实现 - 需要行业中性化
 *    - Alpha#89: ❌ 未实现 - 需要行业中性化
 *    - Alpha#90: ❌ 未实现 - 需要行业中性化
 *    - Alpha#91: ❌ 未实现 - 需要行业中性化
 *    - Alpha#93: ❌ 未实现 - 需要行业中性化
 *    - Alpha#97: ❌ 未实现 - 需要行业中性化
 *    - Alpha#100: ❌ 未实现 - 需要行业中性化
 *
 * 📌 预留扩展：
 *    行业中性化（IndNeutralize）算子的实现需求：
 *    1. 行业分类数据（申万/中信/GICS等标准）
 *    2. 批量股票数据（需要多只股票同时计算）
 *    3. 行业内标准化：value_neutralized = (value - mean_industry) / std_industry
 *
 * 🔧 实现说明：
 *    IndNeutralize 是横截面算子，需要：
 *    - 修改计算接口支持多只股票
 *    - 传入行业分类映射
 *    - 对每个行业分别计算均值和标准差
 *    - 返回中性化后的值
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Group5 {

    private static final double EPSILON = 1e-12;

    /**
     * 计算指定的Alpha因子
     */
    public static Double calculate(int alphaNumber, List<Double> close, List<Double> open,
                                   List<Double> high, List<Double> low, List<Double> volume,
                                   List<Double> vwap, List<Double> returns, List<Double> adv20) {
        switch (alphaNumber) {
            case 81: return alpha081(vwap, volume);
            case 82:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#82 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 83: return alpha083(close, high, low, vwap, volume);
            case 84: return alpha084(close, vwap);
            case 85: return alpha085(close, high, low, volume);
            case 86: return alpha086(open, close, vwap, volume);
            case 87:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#87 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 88: return alpha088(open, close, high, low, volume);
            case 89:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#89 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 90:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#90 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 91:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#91 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 92: return alpha092(open, close, high, low, volume);
            case 93:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#93 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 94: return alpha094(vwap, volume);
            case 95: return alpha095(open, high, low, volume);
            case 96: return alpha096(close, vwap, volume);
            case 97:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#97 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 98: return alpha098(open, vwap, volume);
            case 99: return alpha099(high, low, volume);
            case 100:
                // ⚠️ 未实现：需要行业中性化（IndNeutralize）
                // 📌 预留：需要批量股票数据和行业分类
                log.warn("Alpha#100 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 101: return alpha101(open, close, high, low);
            default:
                return 0.0;
        }
    }

    /**
     * Alpha#81: ((rank(Log(product(rank((rank(correlation(vwap, sum(adv10, 49.6054),
     *            8.47743))^4)), 14.9655))) < rank(correlation(rank(vwap), rank(volume), 5.07914))) * -1)
     */
    private static Double alpha081(List<Double> vwap, List<Double> volume) {
        List<Double> adv10 = sma(volume, 10);

        // Part 1: rank(log(product(rank((rank(correlation(vwap, sum(adv10, 50), 8))^4)), 15)))
        List<Double> sumAdv10 = ts_sum(adv10, 50);
        List<Double> corr = correlation(vwap, sumAdv10, 8);
        List<Double> rank1 = rank(corr);
        List<Double> powered = new ArrayList<>();
        for (double r : rank1) {
            powered.add(Math.pow(r, 4));
        }
        List<Double> rank2 = rank(powered);
        List<Double> prod = product(rank2, 15);
        List<Double> logged = log(prod);
        List<Double> part1 = rank(logged);

        // Part 2: rank(correlation(rank(vwap), rank(volume), 5))
        List<Double> rankVwap = rank(vwap);
        List<Double> rankVol = rank(volume);
        List<Double> corr2 = correlation(rankVwap, rankVol, 5);
        List<Double> part2 = rank(corr2);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return ((part1.get(part1.size() - 1) < part2.get(part2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#83: ((rank(delay(((high - low) / (sum(close, 5) / 5)), 2)) * rank(rank(volume))) /
     *            (((high - low) / (sum(close, 5) / 5)) / (vwap - close)))
     */
    private static Double alpha083(List<Double> close, List<Double> high, List<Double> low,
                                   List<Double> vwap, List<Double> volume) {
        // Part 1: (high - low) / (sum(close, 5) / 5)
        List<Double> sumClose = ts_sum(close, 5);
        List<Double> avgClose = new ArrayList<>();
        for (double s : sumClose) {
            avgClose.add(s / 5.0);
        }

        List<Double> ratio = new ArrayList<>();
        int minSize = Math.min(Math.min(high.size(), low.size()), avgClose.size());
        for (int i = 0; i < minSize; i++) {
            int highIdx = high.size() - minSize + i;
            int lowIdx = low.size() - minSize + i;
            double numerator = high.get(highIdx) - low.get(lowIdx);
            double denominator = avgClose.get(i) + EPSILON;
            ratio.add(numerator / denominator);
        }

        // Part 2: rank(delay(ratio, 2)) * rank(rank(volume))
        List<Double> delayedRatio = delay(ratio, 2);
        List<Double> rank1 = rank(delayedRatio);
        List<Double> rank2 = rank(volume);
        List<Double> rank3 = rank(rank2);

        // Part 3: ratio / (vwap - close)
        List<Double> diff = new ArrayList<>();
        minSize = Math.min(vwap.size(), close.size());
        for (int i = 0; i < minSize; i++) {
            diff.add((vwap.get(i) - close.get(i)) + EPSILON);
        }

        List<Double> denominator = new ArrayList<>();
        minSize = Math.min(ratio.size(), diff.size());
        for (int i = 0; i < minSize; i++) {
            int ratioIdx = ratio.size() - minSize + i;
            int diffIdx = diff.size() - minSize + i;
            denominator.add(ratio.get(ratioIdx) / diff.get(diffIdx));
        }

        if (rank1.isEmpty() || rank3.isEmpty() || denominator.isEmpty()) {
            return 0.0;
        }

        double numeratorVal = rank1.get(rank1.size() - 1) * rank3.get(rank3.size() - 1);
        double denominatorVal = denominator.get(denominator.size() - 1) + EPSILON;

        return numeratorVal / denominatorVal;
    }

    /**
     * Alpha#84: SignedPower(Ts_Rank((vwap - ts_max(vwap, 15.3217)), 20.7127), delta(close, 4.96796))
     */
    private static Double alpha084(List<Double> close, List<Double> vwap) {
        // Part 1: ts_rank((vwap - ts_max(vwap, 15)), 21)
        List<Double> tsMaxVwap = ts_max(vwap, 15);
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < Math.min(vwap.size(), tsMaxVwap.size()); i++) {
            int vwapIdx = vwap.size() - tsMaxVwap.size() + i;
            diff.add(vwap.get(vwapIdx) - tsMaxVwap.get(i));
        }
        List<Double> tsRank = ts_rank(diff, 21);

        // Part 2: delta(close, 5)
        List<Double> deltaClose = delta(close, 5);

        if (tsRank.isEmpty() || deltaClose.isEmpty()) {
            return 0.0;
        }

        double base = tsRank.get(tsRank.size() - 1);
        double exponent = deltaClose.get(deltaClose.size() - 1);

        return signedpower(base, exponent);
    }

    /**
     * Alpha#85: (rank(correlation(((high * 0.876703) + (close * (1 - 0.876703))), adv30, 9.61331))^
     *            rank(correlation(Ts_Rank(((high + low) / 2), 3.70596), Ts_Rank(volume, 10.1595), 7.11408)))
     */
    private static Double alpha085(List<Double> close, List<Double> high, List<Double> low, List<Double> volume) {
        List<Double> adv30 = sma(volume, 30);

        // Part 1: rank(correlation((high * 0.876703 + close * (1 - 0.876703)), adv30, 10))
        List<Double> weighted = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), close.size()); i++) {
            weighted.add((high.get(i) * 0.876703) + (close.get(i) * (1 - 0.876703)));
        }
        List<Double> corr1 = correlation(weighted, adv30, 10);
        List<Double> rank1 = rank(corr1);

        // Part 2: rank(correlation(ts_rank((high + low) / 2, 4), ts_rank(volume, 10), 7))
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> tsRank1 = ts_rank(midPrice, 4);
        List<Double> tsRank2 = ts_rank(volume, 10);
        List<Double> corr2 = correlation(tsRank1, tsRank2, 7);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return Math.pow(rank1.get(rank1.size() - 1), rank2.get(rank2.size() - 1));
    }

    /**
     * Alpha#86: ((Ts_Rank(correlation(close, sum(adv20, 14.7444), 6.00049), 20.4195) <
     *            rank(((open + close) - (vwap + open)))) * -1)
     */
    private static Double alpha086(List<Double> open, List<Double> close, List<Double> vwap, List<Double> volume) {
        List<Double> adv20 = sma(volume, 20);

        // Part 1: ts_rank(correlation(close, sma(adv20, 15), 6), 20)
        List<Double> sumAdv20 = sma(adv20, 15);
        List<Double> corr = correlation(close, sumAdv20, 6);
        List<Double> tsRank = ts_rank(corr, 20);

        // Part 2: rank((open + close) - (vwap + open)) * 20
        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(Math.min(open.size(), close.size()), vwap.size());
        for (int i = 0; i < minSize; i++) {
            diff.add((open.get(i) + close.get(i)) - (vwap.get(i) + open.get(i)));
        }
        List<Double> ranked = rank(diff);

        // 乘以20使双方处于同一水平
        List<Double> scaled = new ArrayList<>();
        for (double r : ranked) {
            scaled.add(r * 20);
        }

        if (tsRank.isEmpty() || scaled.isEmpty()) {
            return 0.0;
        }

        return ((tsRank.get(tsRank.size() - 1) < scaled.get(scaled.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#88: min(rank(decay_linear(((rank(open) + rank(low)) - (rank(high) + rank(close))), 8.06882)),
     *            Ts_Rank(decay_linear(correlation(Ts_Rank(close, 8.44728), Ts_Rank(adv60, 20.6966),
     *            8.01266), 6.65053), 2.61957))
     */
    private static Double alpha088(List<Double> open, List<Double> close, List<Double> high,
                                   List<Double> low, List<Double> volume) {
        List<Double> adv60 = sma(volume, 60);

        // Part 1: rank(decay_linear((rank(open) + rank(low)) - (rank(high) + rank(close)), 8))
        List<Double> rankOpen = rank(open);
        List<Double> rankLow = rank(low);
        List<Double> rankHigh = rank(high);
        List<Double> rankClose = rank(close);

        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(Math.min(rankOpen.size(), rankLow.size()),
                               Math.min(rankHigh.size(), rankClose.size()));
        for (int i = 0; i < minSize; i++) {
            int idx = rankOpen.size() - minSize + i;
            diff.add((rankOpen.get(idx) + rankLow.get(idx)) - (rankHigh.get(idx) + rankClose.get(idx)));
        }
        List<Double> decayed1 = decay_linear(diff, 8);
        List<Double> part1 = rank(decayed1);

        // Part 2: ts_rank(decay_linear(correlation(ts_rank(close, 8), ts_rank(adv60, 21), 8), 7), 3)
        List<Double> tsRankClose = ts_rank(close, 8);
        List<Double> tsRankAdv60 = ts_rank(adv60, 21);
        List<Double> corr = correlation(tsRankClose, tsRankAdv60, 8);
        List<Double> decayed2 = decay_linear(corr, 7);
        List<Double> part2 = ts_rank(decayed2, 3);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return Math.min(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#92: min(Ts_Rank(decay_linear(((((high + low) / 2) + close) < (low + open)), 14.7221), 18.8683),
     *            Ts_Rank(decay_linear(correlation(rank(low), rank(adv30), 7.58555), 6.94024), 6.80584))
     */
    private static Double alpha092(List<Double> open, List<Double> close, List<Double> high, List<Double> low, List<Double> volume) {
        List<Double> adv30 = sma(volume, 30);

        // Part 1: ts_rank(decay_linear((((high + low) / 2) + close) < (low + open), 15), 19)
        List<Double> cond = new ArrayList<>();
        int minSize = Math.min(Math.min(Math.min(Math.min(high.size(), low.size()), close.size()), open.size()), open.size());
        for (int i = 0; i < minSize; i++) {
            double mid = (high.get(i) + low.get(i)) / 2.0;
            double left = mid + close.get(i);
            double right = low.get(i) + open.get(i);
            cond.add((left < right) ? 1.0 : 0.0);
        }
        List<Double> decayed1 = decay_linear(cond, 15);
        List<Double> part1 = ts_rank(decayed1, 19);

        // Part 2: ts_rank(decay_linear(correlation(rank(low), rank(adv30), 8), 7), 7)
        List<Double> rankLow = rank(low);
        List<Double> rankAdv30 = rank(adv30);
        List<Double> corr = correlation(rankLow, rankAdv30, 8);
        List<Double> decayed2 = decay_linear(corr, 7);
        List<Double> part2 = ts_rank(decayed2, 7);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return Math.min(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#94: ((rank((vwap - ts_min(vwap, 11.5783)))^Ts_Rank(correlation(Ts_Rank(vwap, 19.6462),
     *            Ts_Rank(adv60, 4.02992), 18.0926), 2.70756)) * -1)
     */
    private static Double alpha094(List<Double> vwap, List<Double> volume) {
        List<Double> adv60 = sma(volume, 60);

        // Part 1: rank(vwap - ts_min(vwap, 12))
        List<Double> tsMinVwap = ts_min(vwap, 12);
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < Math.min(vwap.size(), tsMinVwap.size()); i++) {
            int vwapIdx = vwap.size() - tsMinVwap.size() + i;
            diff.add(vwap.get(vwapIdx) - tsMinVwap.get(i));
        }
        List<Double> rank1 = rank(diff);

        // Part 2: ts_rank(correlation(ts_rank(vwap, 20), ts_rank(adv60, 4), 18), 3)
        List<Double> tsRankVwap = ts_rank(vwap, 20);
        List<Double> tsRankAdv60 = ts_rank(adv60, 4);
        List<Double> corr = correlation(tsRankVwap, tsRankAdv60, 18);
        List<Double> tsRank = ts_rank(corr, 3);

        if (rank1.isEmpty() || tsRank.isEmpty()) {
            return 0.0;
        }

        return Math.pow(rank1.get(rank1.size() - 1), tsRank.get(tsRank.size() - 1)) * -1;
    }

    /**
     * Alpha#95: (rank((open - ts_min(open, 12.4105))) < Ts_Rank((rank(correlation(sum(((high + low) / 2),
     *            19.1351), sum(adv40, 19.1351), 12.8742))^5), 11.7584))
     */
    private static Double alpha095(List<Double> open, List<Double> high, List<Double> low, List<Double> volume) {
        List<Double> adv40 = sma(volume, 40);

        // Part 1: rank(open - ts_min(open, 12)) * 12
        List<Double> tsMinOpen = ts_min(open, 12);
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < Math.min(open.size(), tsMinOpen.size()); i++) {
            int openIdx = open.size() - tsMinOpen.size() + i;
            diff.add(open.get(openIdx) - tsMinOpen.get(i));
        }
        List<Double> rank1 = rank(diff);

        // 乘以12使双方处于同一水平
        List<Double> scaled = new ArrayList<>();
        for (double r : rank1) {
            scaled.add(r * 12);
        }

        // Part 2: ts_rank((rank(correlation(sma((high + low) / 2, 19), sma(adv40, 19), 13))^5), 12)
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> sumMid = sma(midPrice, 19);
        List<Double> sumAdv40 = sma(adv40, 19);
        List<Double> corr = correlation(sumMid, sumAdv40, 13);
        List<Double> ranked = rank(corr);
        List<Double> powered = new ArrayList<>();
        for (double r : ranked) {
            powered.add(Math.pow(r, 5));
        }
        List<Double> tsRank = ts_rank(powered, 12);

        if (scaled.isEmpty() || tsRank.isEmpty()) {
            return 0.0;
        }

        return (scaled.get(scaled.size() - 1) < tsRank.get(tsRank.size() - 1)) ? 1.0 : 0.0;
    }

    /**
     * Alpha#96: (max(Ts_Rank(decay_linear(correlation(rank(vwap), rank(volume), 3.83878), 4.16783), 8.38151),
     *            Ts_Rank(decay_linear(Ts_ArgMax(correlation(Ts_Rank(close, 7.45404), Ts_Rank(adv60, 4.13242),
     *            3.65459), 12.6556), 14.0365), 13.4143)) * -1)
     */
    private static Double alpha096(List<Double> close, List<Double> vwap, List<Double> volume) {
        List<Double> adv60 = sma(volume, 60);

        // Part 1: ts_rank(decay_linear(correlation(rank(vwap), rank(volume), 4), 4), 8)
        List<Double> rankVwap = rank(vwap);
        List<Double> rankVol = rank(volume);
        List<Double> corr1 = correlation(rankVwap, rankVol, 4);
        List<Double> decayed1 = decay_linear(corr1, 4);
        List<Double> part1 = ts_rank(decayed1, 8);

        // Part 2: ts_rank(decay_linear(ts_argmax(correlation(ts_rank(close, 7), ts_rank(adv60, 4), 4), 13), 14), 13)
        List<Double> tsRankClose = ts_rank(close, 7);
        List<Double> tsRankAdv60 = ts_rank(adv60, 4);
        List<Double> corr2 = correlation(tsRankClose, tsRankAdv60, 4);
        List<Double> tsArgmax = ts_argmax(corr2, 13);
        List<Double> decayed2 = decay_linear(tsArgmax, 14);
        List<Double> part2 = ts_rank(decayed2, 13);

        if (part1.isEmpty() || part2.isEmpty()) {
            return 0.0;
        }

        return -1 * Math.max(part1.get(part1.size() - 1), part2.get(part2.size() - 1));
    }

    /**
     * Alpha#98: (rank(decay_linear(correlation(vwap, sum(adv5, 26.4719), 4.58418), 7.18088)) -
     *            rank(decay_linear(Ts_Rank(Ts_ArgMin(correlation(rank(open), rank(adv15), 20.8187),
     *            8.62571), 6.95668), 8.07206)))
     */
    private static Double alpha098(List<Double> open, List<Double> vwap, List<Double> volume) {
        List<Double> adv5 = sma(volume, 5);
        List<Double> adv15 = sma(volume, 15);

        // Part 1: rank(decay_linear(correlation(vwap, sma(adv5, 26), 5), 7))
        List<Double> sumAdv5 = sma(adv5, 26);
        List<Double> corr1 = correlation(vwap, sumAdv5, 5);
        List<Double> decayed1 = decay_linear(corr1, 7);
        List<Double> rank1 = rank(decayed1);

        // Part 2: rank(decay_linear(ts_rank(ts_argmin(correlation(rank(open), rank(adv15), 21), 9), 7), 8))
        List<Double> rankOpen = rank(open);
        List<Double> rankAdv15 = rank(adv15);
        List<Double> corr2 = correlation(rankOpen, rankAdv15, 21);
        List<Double> tsArgmin = ts_argmin(corr2, 9);
        List<Double> tsRank = ts_rank(tsArgmin, 7);
        List<Double> decayed2 = decay_linear(tsRank, 8);
        List<Double> rank2 = rank(decayed2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return rank1.get(rank1.size() - 1) - rank2.get(rank2.size() - 1);
    }

    /**
     * Alpha#99: ((rank(correlation(sum(((high + low) / 2), 19.8975), sum(adv60, 19.8975), 8.8136)) <
     *            rank(correlation(low, volume, 6.28259))) * -1)
     */
    private static Double alpha099(List<Double> high, List<Double> low, List<Double> volume) {
        List<Double> adv60 = sma(volume, 60);

        // Part 1: rank(correlation(ts_sum((high + low) / 2, 20), ts_sum(adv60, 20), 9))
        List<Double> midPrice = new ArrayList<>();
        for (int i = 0; i < Math.min(high.size(), low.size()); i++) {
            midPrice.add((high.get(i) + low.get(i)) / 2.0);
        }
        List<Double> sumMid = ts_sum(midPrice, 20);
        List<Double> sumAdv60 = ts_sum(adv60, 20);
        List<Double> corr1 = correlation(sumMid, sumAdv60, 9);
        List<Double> rank1 = rank(corr1);

        // Part 2: rank(correlation(low, volume, 6))
        List<Double> corr2 = correlation(low, volume, 6);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return ((rank1.get(rank1.size() - 1) < rank2.get(rank2.size() - 1)) ? 1.0 : 0.0) * -1;
    }

    /**
     * Alpha#101: ((close - open) / ((high - low) + 0.001))
     *
     * 这是最简单的Alpha因子，表示实体占总波动的比例
     */
    private static Double alpha101(List<Double> open, List<Double> close, List<Double> high, List<Double> low) {
        if (open.isEmpty() || close.isEmpty() || high.isEmpty() || low.isEmpty()) {
            return 0.0;
        }

        int idx = close.size() - 1;
        double numerator = close.get(idx) - open.get(idx);
        double denominator = high.get(idx) - low.get(idx) + 0.001;

        return numerator / denominator;
    }
}

