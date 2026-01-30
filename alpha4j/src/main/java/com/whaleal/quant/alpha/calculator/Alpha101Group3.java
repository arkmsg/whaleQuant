package com.whaleal.quant.alpha.calculator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101 Group3: Alpha#41-60
 *
 * 价量因子和复杂统计因子组
 *
 * ⚠️ 未实现因子说明：
 *
 * 1️⃣ WorldQuant公开版本中缺失的因子（4个）：
 *    - Alpha#48: ❌ 未实现 - WorldQuant官方实现中不可用
 *    - Alpha#56: ❌ 未实现 - WorldQuant官方实现中不可用
 *    - Alpha#58: ❌ 未实现 - WorldQuant官方实现中不可用，且需要行业中性化
 *    - Alpha#59: ❌ 未实现 - WorldQuant官方实现中不可用，且需要行业中性化
 *
 * 📌 预留扩展：
 *    - 这些因子的原始公式未公开
 *    - 如果获得完整公式，可参考其他因子实现
 *    - 建议：使用 Alpha101 其他因子替代
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Group3 {

    private static final double EPSILON = 1e-12;

    /**
     * 计算指定的Alpha因子
     */
    public static Double calculate(int alphaNumber, List<Double> close, List<Double> open,
                                   List<Double> high, List<Double> low, List<Double> volume,
                                   List<Double> vwap, List<Double> returns, List<Double> adv20) {
        switch (alphaNumber) {
            case 41: return alpha041(high, low, vwap);
            case 42: return alpha042(close, vwap);
            case 43: return alpha043(close, volume, adv20);
            case 44: return alpha044(high, volume);
            case 45: return alpha045(close, volume);
            case 46: return alpha046(close);
            case 47: return alpha047(close, high, volume, vwap, adv20);
            case 48:
                // ⚠️ 未实现：WorldQuant公开实现中缺失
                // 📌 预留：如获得完整公式可在此实现
                log.warn("Alpha#48 未实现 - WorldQuant公开版本中不可用");
                return Double.NaN;
            case 49: return alpha049(close);
            case 50: return alpha050(volume, vwap);
            case 51: return alpha051(close);
            case 52: return alpha052(close, high, low, volume, returns);
            case 53: return alpha053(close, high, low);
            case 54: return alpha054(open, close, high, low);
            case 55: return alpha055(open, close, high, low, volume);
            case 56:
                // ⚠️ 未实现：WorldQuant公开实现中缺失
                // 📌 预留：如获得完整公式可在此实现
                log.warn("Alpha#56 未实现 - WorldQuant公开版本中不可用");
                return Double.NaN;
            case 57: return alpha057(close, vwap);
            case 58:
                // ⚠️ 未实现：WorldQuant公开实现中缺失 + 需要行业中性化
                // 📌 预留：需要行业分类数据支持 IndNeutralize 算子
                // 🔧 实现说明：需要传入行业分类参数（如申万行业、中信行业等）
                log.warn("Alpha#58 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 59:
                // ⚠️ 未实现：WorldQuant公开实现中缺失 + 需要行业中性化
                // 📌 预留：需要行业分类数据支持 IndNeutralize 算子
                // 🔧 实现说明：需要传入行业分类参数（如申万行业、中信行业等）
                log.warn("Alpha#59 未实现 - 需要行业中性化（IndNeutralize）");
                return Double.NaN;
            case 60: return alpha060(close, high, low, volume);
            default:
                return 0.0;
        }
    }

    /**
     * Alpha#41: pow((high * low), 0.5) - vwap
     */
    private static Double alpha041(List<Double> high, List<Double> low, List<Double> vwap) {
        if (high.isEmpty() || low.isEmpty() || vwap.isEmpty()) {
            return 0.0;
        }

        int minSize = Math.min(Math.min(high.size(), low.size()), vwap.size());
        int highIdx = high.size() - 1;
        int lowIdx = low.size() - 1;
        int vwapIdx = vwap.size() - 1;

        double sqrtHL = Math.sqrt(high.get(highIdx) * low.get(lowIdx));
        return sqrtHL - vwap.get(vwapIdx);
    }

    /**
     * Alpha#42: rank((vwap - close)) / rank((vwap + close))
     */
    private static Double alpha042(List<Double> close, List<Double> vwap) {
        List<Double> diff = new ArrayList<>();
        List<Double> sum = new ArrayList<>();

        int minSize = Math.min(close.size(), vwap.size());
        for (int i = 0; i < minSize; i++) {
            diff.add(vwap.get(i) - close.get(i));
            sum.add(vwap.get(i) + close.get(i));
        }

        List<Double> rank1 = rank(diff);
        List<Double> rank2 = rank(sum);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return rank1.get(rank1.size() - 1) / (rank2.get(rank2.size() - 1) + EPSILON);
    }

    /**
     * Alpha#43: ts_rank(volume / adv20, 20) * ts_rank((-1 * delta(close, 7)), 8)
     */
    private static Double alpha043(List<Double> close, List<Double> volume, List<Double> adv20) {
        // Part 1: ts_rank(volume / adv20, 20)
        List<Double> volRatio = new ArrayList<>();
        int minSize = Math.min(volume.size(), adv20.size());
        for (int i = 0; i < minSize; i++) {
            int volIdx = volume.size() - minSize + i;
            volRatio.add(volume.get(volIdx) / (adv20.get(i) + EPSILON));
        }
        List<Double> tsRank1 = ts_rank(volRatio, 20);

        // Part 2: ts_rank((-1 * delta(close, 7)), 8)
        List<Double> deltaClose = delta(close, 7);
        List<Double> negDelta = new ArrayList<>();
        for (double d : deltaClose) {
            negDelta.add(-1 * d);
        }
        List<Double> tsRank2 = ts_rank(negDelta, 8);

        if (tsRank1.isEmpty() || tsRank2.isEmpty()) {
            return 0.0;
        }

        return tsRank1.get(tsRank1.size() - 1) * tsRank2.get(tsRank2.size() - 1);
    }

    /**
     * Alpha#44: (-1 * correlation(high, rank(volume), 5))
     */
    private static Double alpha044(List<Double> high, List<Double> volume) {
        List<Double> rankVol = rank(volume);
        List<Double> corr = correlation(high, rankVol, 5);
        return corr.isEmpty() ? 0.0 : -1 * corr.get(corr.size() - 1);
    }

    /**
     * Alpha#45: (-1 * ((rank((sum(delay(close, 5), 20) / 20)) * correlation(close, volume, 2)) *
     *                   rank(correlation(sum(close, 5), sum(close, 20), 2))))
     */
    private static Double alpha045(List<Double> close, List<Double> volume) {
        // Part 1: rank(sma(delay(close, 5), 20))
        List<Double> delayedClose = delay(close, 5);
        List<Double> smaClose = sma(delayedClose, 20);
        List<Double> rank1 = rank(smaClose);

        // Part 2: correlation(close, volume, 2)
        List<Double> corr1 = correlation(close, volume, 2);

        // Part 3: rank(correlation(ts_sum(close, 5), ts_sum(close, 20), 2))
        List<Double> sum5 = ts_sum(close, 5);
        List<Double> sum20 = ts_sum(close, 20);
        List<Double> corr2 = correlation(sum5, sum20, 2);
        List<Double> rank2 = rank(corr2);

        if (rank1.isEmpty() || corr1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return -1 * rank1.get(rank1.size() - 1) *
                    corr1.get(corr1.size() - 1) *
                    rank2.get(rank2.size() - 1);
    }

    /**
     * Alpha#46: Conditional expression based on close momentum
     */
    private static Double alpha046(List<Double> close) {
        if (close.size() < 21) {
            return 0.0;
        }

        List<Double> delay10 = delay(close, 10);
        List<Double> delay20 = delay(close, 20);

        if (delay10.isEmpty() || delay20.isEmpty()) {
            return 0.0;
        }

        double c = close.get(close.size() - 1);
        double d10 = delay10.get(delay10.size() - 1);
        double d20 = delay20.get(delay20.size() - 1);

        double inner = ((d20 - d10) / 10.0) - ((d10 - c) / 10.0);

        List<Double> deltaClose = delta(close, 1);
        if (deltaClose.isEmpty()) {
            return 0.0;
        }
        double delta1 = deltaClose.get(deltaClose.size() - 1);

        if (inner > 0.25) {
            return -1.0;
        } else if (inner < 0) {
            return 1.0;
        } else {
            return -1.0 * delta1;
        }
    }

    /**
     * Alpha#47: ((((rank((1 / close)) * volume) / adv20) * ((high * rank((high - close))) / (sma(high, 5) / 5))) -
     *            rank((vwap - delay(vwap, 5))))
     */
    private static Double alpha047(List<Double> close, List<Double> high, List<Double> volume,
                                  List<Double> vwap, List<Double> adv20) {
        // Part 1: rank((1 / close))
        List<Double> invClose = new ArrayList<>();
        for (double c : close) {
            invClose.add(1.0 / (c + EPSILON));
        }
        List<Double> rank1 = rank(invClose);

        // Part 2: (rank * volume) / adv20
        List<Double> part1 = new ArrayList<>();
        int minSize = Math.min(Math.min(rank1.size(), volume.size()), adv20.size());
        for (int i = 0; i < minSize; i++) {
            int volIdx = volume.size() - minSize + i;
            int adv20Idx = adv20.size() - minSize + i;
            part1.add((rank1.get(i) * volume.get(volIdx)) / (adv20.get(adv20Idx) + EPSILON));
        }

        // Part 3: (high - close)
        List<Double> highClose = new ArrayList<>();
        minSize = Math.min(high.size(), close.size());
        for (int i = 0; i < minSize; i++) {
            highClose.add(high.get(i) - close.get(i));
        }
        List<Double> rank2 = rank(highClose);

        // Part 4: sma(high, 5) / 5
        List<Double> smaHigh = sma(high, 5);
        List<Double> avgHigh = new ArrayList<>();
        for (double s : smaHigh) {
            avgHigh.add(s / 5.0);
        }

        // Part 5: high * rank / avgHigh
        List<Double> part2 = new ArrayList<>();
        minSize = Math.min(Math.min(high.size(), rank2.size()), avgHigh.size());
        for (int i = 0; i < minSize; i++) {
            int highIdx = high.size() - minSize + i;
            part2.add((high.get(highIdx) * rank2.get(i)) / (avgHigh.get(i) + EPSILON));
        }

        // Part 6: part1 * part2
        List<Double> combined = new ArrayList<>();
        minSize = Math.min(part1.size(), part2.size());
        for (int i = 0; i < minSize; i++) {
            int p1Idx = part1.size() - minSize + i;
            combined.add(part1.get(p1Idx) * part2.get(i));
        }

        // Part 7: rank((vwap - delay(vwap, 5)))
        List<Double> delayedVwap = delay(vwap, 5);
        List<Double> vwapDiff = new ArrayList<>();
        minSize = Math.min(vwap.size(), delayedVwap.size());
        for (int i = 0; i < minSize; i++) {
            int vwapIdx = vwap.size() - minSize + i;
            vwapDiff.add(vwap.get(vwapIdx) - delayedVwap.get(i));
        }
        List<Double> rank3 = rank(vwapDiff);

        if (combined.isEmpty() || rank3.isEmpty()) {
            return 0.0;
        }

        return combined.get(combined.size() - 1) - rank3.get(rank3.size() - 1);
    }

    /**
     * Alpha#49: Conditional expression based on close momentum
     */
    private static Double alpha049(List<Double> close) {
        if (close.size() < 21) {
            return 0.0;
        }

        List<Double> delay10 = delay(close, 10);
        List<Double> delay20 = delay(close, 20);

        if (delay10.isEmpty() || delay20.isEmpty()) {
            return 0.0;
        }

        double c = close.get(close.size() - 1);
        double d10 = delay10.get(delay10.size() - 1);
        double d20 = delay20.get(delay20.size() - 1);

        double inner = ((d20 - d10) / 10.0) - ((d10 - c) / 10.0);

        List<Double> deltaClose = delta(close, 1);
        if (deltaClose.isEmpty()) {
            return 0.0;
        }
        double delta1 = deltaClose.get(deltaClose.size() - 1);

        if (inner < -0.1) {
            return 1.0;
        } else {
            return -1.0 * delta1;
        }
    }

    /**
     * Alpha#50: (-1 * ts_max(rank(correlation(rank(volume), rank(vwap), 5)), 5))
     */
    private static Double alpha050(List<Double> volume, List<Double> vwap) {
        List<Double> rankVol = rank(volume);
        List<Double> rankVwap = rank(vwap);
        List<Double> corr = correlation(rankVol, rankVwap, 5);
        List<Double> ranked = rank(corr);
        List<Double> tsMax = ts_max(ranked, 5);

        return tsMax.isEmpty() ? 0.0 : -1 * tsMax.get(tsMax.size() - 1);
    }

    /**
     * Alpha#51: Conditional expression based on close momentum
     */
    private static Double alpha051(List<Double> close) {
        if (close.size() < 21) {
            return 0.0;
        }

        List<Double> delay10 = delay(close, 10);
        List<Double> delay20 = delay(close, 20);

        if (delay10.isEmpty() || delay20.isEmpty()) {
            return 0.0;
        }

        double c = close.get(close.size() - 1);
        double d10 = delay10.get(delay10.size() - 1);
        double d20 = delay20.get(delay20.size() - 1);

        double inner = ((d20 - d10) / 10.0) - ((d10 - c) / 10.0);

        List<Double> deltaClose = delta(close, 1);
        if (deltaClose.isEmpty()) {
            return 0.0;
        }
        double delta1 = deltaClose.get(deltaClose.size() - 1);

        if (inner < -0.05) {
            return 1.0;
        } else {
            return -1.0 * delta1;
        }
    }

    /**
     * Alpha#52: (((-1 * delta(ts_min(low, 5), 5)) * rank(((sum(returns, 240) - sum(returns, 20)) / 220))) *
     *            ts_rank(volume, 5))
     */
    private static Double alpha052(List<Double> close, List<Double> high, List<Double> low,
                                  List<Double> volume, List<Double> returns) {
        // Part 1: -1 * delta(ts_min(low, 5), 5)
        List<Double> tsMinLow = ts_min(low, 5);
        List<Double> deltaTsMin = delta(tsMinLow, 5);

        // Part 2: (sum(returns, 240) - sum(returns, 20)) / 220
        List<Double> sum240 = ts_sum(returns, 240);
        List<Double> sum20 = ts_sum(returns, 20);
        List<Double> retDiff = new ArrayList<>();
        int minSize = Math.min(sum240.size(), sum20.size());
        for (int i = 0; i < minSize; i++) {
            int s240Idx = sum240.size() - minSize + i;
            retDiff.add((sum240.get(s240Idx) - sum20.get(i)) / 220.0);
        }
        List<Double> ranked = rank(retDiff);

        // Part 3: ts_rank(volume, 5)
        List<Double> tsRankVol = ts_rank(volume, 5);

        if (deltaTsMin.isEmpty() || ranked.isEmpty() || tsRankVol.isEmpty()) {
            return 0.0;
        }

        return ((-1 * deltaTsMin.get(deltaTsMin.size() - 1)) *
                ranked.get(ranked.size() - 1)) *
                tsRankVol.get(tsRankVol.size() - 1);
    }

    /**
     * Alpha#53: -1 * delta((((close - low) - (high - close)) / (close - low + EPSILON)), 9)
     */
    private static Double alpha053(List<Double> close, List<Double> high, List<Double> low) {
        List<Double> inner = new ArrayList<>();
        int minSize = Math.min(Math.min(close.size(), high.size()), low.size());

        for (int i = 0; i < minSize; i++) {
            double c = close.get(i);
            double h = high.get(i);
            double l = low.get(i);
            double numerator = (c - l) - (h - c);
            double denominator = c - l + EPSILON;
            inner.add(numerator / denominator);
        }

        List<Double> deltaInner = delta(inner, 9);
        return deltaInner.isEmpty() ? 0.0 : -1 * deltaInner.get(deltaInner.size() - 1);
    }

    /**
     * Alpha#54: -1 * (low - close) * (open^5) / ((low - high) * (close^5) + EPSILON)
     */
    private static Double alpha054(List<Double> open, List<Double> close, List<Double> high, List<Double> low) {
        if (open.isEmpty() || close.isEmpty() || high.isEmpty() || low.isEmpty()) {
            return 0.0;
        }

        int idx = close.size() - 1;
        double o = open.get(idx);
        double c = close.get(idx);
        double h = high.get(idx);
        double l = low.get(idx);

        double numerator = (l - c) * Math.pow(o, 5);
        double denominator = (l - h) * Math.pow(c, 5) + EPSILON;

        return -1 * numerator / denominator;
    }

    /**
     * Alpha#55: (-1 * correlation(rank(((close - ts_min(low, 12)) / (ts_max(high, 12) - ts_min(low, 12)))),
     *                              rank(volume), 6))
     */
    private static Double alpha055(List<Double> open, List<Double> close, List<Double> high,
                                  List<Double> low, List<Double> volume) {
        // Part 1: (close - ts_min(low, 12)) / (ts_max(high, 12) - ts_min(low, 12))
        List<Double> tsMinLow = ts_min(low, 12);
        List<Double> tsMaxHigh = ts_max(high, 12);

        List<Double> inner = new ArrayList<>();
        int minSize = Math.min(Math.min(close.size(), tsMinLow.size()), tsMaxHigh.size());
        for (int i = 0; i < minSize; i++) {
            int closeIdx = close.size() - minSize + i;
            double numerator = close.get(closeIdx) - tsMinLow.get(i);
            double denominator = tsMaxHigh.get(i) - tsMinLow.get(i) + EPSILON;
            inner.add(numerator / denominator);
        }

        // Part 2: rank(inner)
        List<Double> rank1 = rank(inner);

        // Part 3: rank(volume)
        List<Double> rank2 = rank(volume);

        // Part 4: correlation
        List<Double> corr = correlation(rank1, rank2, 6);

        return corr.isEmpty() ? 0.0 : -1 * corr.get(corr.size() - 1);
    }

    /**
     * Alpha#57: (0 - (1 * ((close - vwap) / decay_linear(rank(ts_argmax(close, 30)), 2))))
     */
    private static Double alpha057(List<Double> close, List<Double> vwap) {
        // Part 1: ts_argmax(close, 30)
        List<Double> tsArgmax = ts_argmax(close, 30);

        // Part 2: rank
        List<Double> ranked = rank(tsArgmax);

        // Part 3: decay_linear(rank, 2)
        List<Double> decayed = decay_linear(ranked, 2);

        // Part 4: (close - vwap)
        List<Double> closeVwap = new ArrayList<>();
        int minSize = Math.min(close.size(), vwap.size());
        for (int i = 0; i < minSize; i++) {
            closeVwap.add(close.get(i) - vwap.get(i));
        }

        if (closeVwap.isEmpty() || decayed.isEmpty()) {
            return 0.0;
        }

        double diff = closeVwap.get(closeVwap.size() - 1);
        double decay = decayed.get(decayed.size() - 1);

        return 0.0 - (1.0 * (diff / (decay + EPSILON)));
    }

    /**
     * Alpha#60: - ((2 * scale(rank(inner))) - scale(rank(ts_argmax(close, 10))))
     * inner = ((close - low) - (high - close)) * volume / (high - low + EPSILON)
     */
    private static Double alpha060(List<Double> close, List<Double> high, List<Double> low, List<Double> volume) {
        // Part 1: inner = ((close - low) - (high - close)) * volume / (high - low)
        List<Double> inner = new ArrayList<>();
        int minSize = Math.min(Math.min(Math.min(close.size(), high.size()), low.size()), volume.size());

        for (int i = 0; i < minSize; i++) {
            double c = close.get(i);
            double h = high.get(i);
            double l = low.get(i);
            double v = volume.get(i);
            double numerator = ((c - l) - (h - c)) * v;
            double denominator = h - l + EPSILON;
            inner.add(numerator / denominator);
        }

        // Part 2: rank(inner)
        List<Double> rank1 = rank(inner);

        // Part 3: scale(rank1)
        List<Double> scaled1 = scale(rank1, 1.0);

        // Part 4: ts_argmax(close, 10)
        List<Double> tsArgmax = ts_argmax(close, 10);

        // Part 5: rank(ts_argmax)
        List<Double> rank2 = rank(tsArgmax);

        // Part 6: scale(rank2)
        List<Double> scaled2 = scale(rank2, 1.0);

        if (scaled1.isEmpty() || scaled2.isEmpty()) {
            return 0.0;
        }

        return -1 * ((2.0 * scaled1.get(scaled1.size() - 1)) - scaled2.get(scaled2.size() - 1));
    }
}

