package com.whaleal.quant.alpha.calculator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101 Group2: Alpha#21-40
 *
 * 基于 alphas/alphas101.py 实现
 * 相关性和价量因子组
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Group2 {

    private static final double EPSILON = 1e-12;

    /**
     * 计算指定的Alpha因子
     */
    public static Double calculate(int alphaNumber, List<Double> close, List<Double> open,
                                   List<Double> high, List<Double> low, List<Double> volume,
                                   List<Double> vwap, List<Double> returns, List<Double> adv20) {
        switch (alphaNumber) {
            case 21: return alpha021(close, volume, adv20);
            case 22: return alpha022(high, close, volume);
            case 23: return alpha023(high);
            case 24: return alpha024(close);
            case 25: return alpha025(close, high, returns, vwap, adv20);
            case 26: return alpha026(high, volume);
            case 27: return alpha027(volume, vwap);
            case 28: return alpha028(high, low, close, adv20);
            case 29: return alpha029(close, returns);
            case 30: return alpha030(close, volume);
            case 31: return alpha031(close, low, adv20);
            case 32: return alpha032(close, vwap);
            case 33: return alpha033(open, close);
            case 34: return alpha034(close, returns);
            case 35: return alpha035(close, high, low, volume, returns);
            case 36: return alpha036(open, close, volume, vwap, adv20, returns);
            case 37: return alpha037(open, close);
            case 38: return alpha038(open, close);
            case 39: return alpha039(open, close, volume, returns, adv20);
            case 40: return alpha040(close, high, volume);
            default:
                return 0.0;
        }
    }

    /**
     * Alpha#21: ((((sum(close, 8) / 8) + stddev(close, 8)) < (sum(close, 2) / 2)) ? (-1 * 1) :
     *            (((sum(close,2) / 2) < ((sum(close, 8) / 8) - stddev(close, 8))) ? 1 :
     *            (((1 < (volume / adv20)) || ((volume /adv20) == 1)) ? 1 : (-1 * 1))))
     */
    private static Double alpha021(List<Double> close, List<Double> volume, List<Double> adv20) {
        if (close.size() < 8 || volume.isEmpty() || adv20.isEmpty()) {
            return 0.0;
        }

        List<Double> sma8 = sma(close, 8);
        List<Double> sma2 = sma(close, 2);
        List<Double> std8 = stddev(close, 8);

        if (sma8.isEmpty() || sma2.isEmpty() || std8.isEmpty()) {
            return 0.0;
        }

        double s8 = sma8.get(sma8.size() - 1);
        double s2 = sma2.get(sma2.size() - 1);
        double std = std8.get(std8.size() - 1);
        double vol = volume.get(volume.size() - 1);
        double adv = adv20.get(adv20.size() - 1);

        // cond_1: sma(close, 8) + stddev(close, 8) < sma(close, 2)
        boolean cond1 = (s8 + std) < s2;

        // cond_2: sma(close, 2) < sma(close, 8) - stddev(close, 8)
        boolean cond2 = s2 < (s8 - std);

        // cond_3: sma(volume, 20) / volume < 1
        boolean cond3 = adv / vol < 1.0;

        // return (cond_1 | ((~cond_1) & (~cond_2) & (~cond_3))).astype('int')*(-2)+1
        if (cond1 || ((!cond1) && (!cond2) && (!cond3))) {
            return -1.0;
        } else {
            return 1.0;
        }
    }

    /**
     * Alpha#22: (-1 * (delta(correlation(high, volume, 5), 5) * rank(stddev(close, 20))))
     */
    private static Double alpha022(List<Double> high, List<Double> close, List<Double> volume) {
        List<Double> corr = correlation(high, volume, 5);
        List<Double> deltaCorr = delta(corr, 5);
        List<Double> std = stddev(close, 20);
        List<Double> ranked = rank(std);

        if (deltaCorr.isEmpty() || ranked.isEmpty()) {
            return 0.0;
        }

        double dc = deltaCorr.get(deltaCorr.size() - 1);
        double r = ranked.get(ranked.size() - 1);

        return -1 * dc * r;
    }

    /**
     * Alpha#23: (((sum(high, 20) / 20) < high) ? (-1 * delta(high, 2)) : 0)
     */
    private static Double alpha023(List<Double> high) {
        if (high.size() < 20) {
            return 0.0;
        }

        List<Double> smaHigh = sma(high, 20);
        if (smaHigh.isEmpty()) {
            return 0.0;
        }

        double avgHigh = smaHigh.get(smaHigh.size() - 1);
        double currentHigh = high.get(high.size() - 1);

        if (avgHigh < currentHigh) {
            List<Double> deltaHigh = delta(high, 2);
            if (!deltaHigh.isEmpty()) {
            return -1 * deltaHigh.get(deltaHigh.size() - 1);
            }
        }

        return 0.0;
    }

    /**
     * Alpha#24: ((((delta((sum(close, 100) / 100), 100) / delay(close, 100)) < 0.05) ||
     *            ((delta((sum(close, 100) / 100), 100) / delay(close, 100)) == 0.05)) ?
     *            (-1 * (close - ts_min(close,100))) : (-1 * delta(close, 3)))
     */
    private static Double alpha024(List<Double> close) {
        if (close.size() < 200) {
            return 0.0;
        }

        List<Double> smaClose = sma(close, 100);
        List<Double> deltaSma = delta(smaClose, 100);
        List<Double> delayClose = delay(close, 100);

        if (deltaSma.isEmpty() || delayClose.isEmpty()) {
            return 0.0;
        }

        double ds = deltaSma.get(deltaSma.size() - 1);
        double dc = delayClose.get(delayClose.size() - 1);

        if (Math.abs(dc) < EPSILON) {
            dc = EPSILON;
        }

        double ratio = ds / dc;

        if (ratio <= 0.05) {
            List<Double> tsMin = ts_min(close, 100);
            if (!tsMin.isEmpty()) {
                double minVal = tsMin.get(tsMin.size() - 1);
                return -1 * (close.get(close.size() - 1) - minVal);
            }
        }

        List<Double> delta3 = delta(close, 3);
        if (!delta3.isEmpty()) {
            return -1 * delta3.get(delta3.size() - 1);
        }

        return 0.0;
    }

    /**
     * Alpha#25: rank(((((-1 * returns) * adv20) * vwap) * (high - close)))
     */
    private static Double alpha025(List<Double> close, List<Double> high, List<Double> returns,
                                  List<Double> vwap, List<Double> adv20) {
        int minSize = Math.min(Math.min(Math.min(returns.size(), adv20.size()),
                                        Math.min(vwap.size(), high.size())), close.size());

        if (minSize == 0) {
            return 0.0;
        }

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int retIdx = returns.size() - minSize + i;
            int advIdx = adv20.size() - minSize + i;
            int vwapIdx = vwap.size() - minSize + i;
            int highIdx = high.size() - minSize + i;
            int closeIdx = close.size() - minSize + i;

            double val = ((-1 * returns.get(retIdx)) * adv20.get(advIdx)) *
                           vwap.get(vwapIdx) * (high.get(highIdx) - close.get(closeIdx));
            result.add(val);
        }

        List<Double> ranked = rank(result);
        return ranked.isEmpty() ? 0.0 : ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#26: (-1 * ts_max(correlation(ts_rank(volume, 5), ts_rank(high, 5), 5), 3))
     */
    private static Double alpha026(List<Double> high, List<Double> volume) {
        List<Double> tsrVol = ts_rank(volume, 5);
        List<Double> tsrHigh = ts_rank(high, 5);
        List<Double> corr = correlation(tsrVol, tsrHigh, 5);
        List<Double> tsMax = ts_max(corr, 3);

        return tsMax.isEmpty() ? 0.0 : -1 * tsMax.get(tsMax.size() - 1);
    }

    /**
     * Alpha#27: ((0.5 < rank((sum(correlation(rank(volume), rank(vwap), 6), 2) / 2.0))) ? (-1 * 1) : 1)
     * Python实现: sign((rank(sma(correlation(rank(volume), rank(vwap), 6), 2) / 2.0) - 0.5) * (-2))
     */
    private static Double alpha027(List<Double> volume, List<Double> vwap) {
        List<Double> rankVol = rank(volume);
        List<Double> rankVwap = rank(vwap);
        List<Double> corr = correlation(rankVol, rankVwap, 6);
        List<Double> smaCorr = sma(corr, 2);

        if (smaCorr.isEmpty()) {
            return 0.0;
        }

        List<Double> divided = new ArrayList<>();
        for (double val : smaCorr) {
            divided.add(val / 2.0);
        }

        List<Double> ranked = rank(divided);
        if (ranked.isEmpty()) {
            return 0.0;
        }

        double r = ranked.get(ranked.size() - 1);
        double signVal = sign((r - 0.5) * (-2));
        return signVal;
    }

    /**
     * Alpha#28: scale(((correlation(adv20, low, 5) + ((high + low) / 2)) - close))
     */
    private static Double alpha028(List<Double> high, List<Double> low, List<Double> close,
                                  List<Double> adv20) {
        List<Double> corr = correlation(adv20, low, 5);

        int minSize = Math.min(Math.min(Math.min(corr.size(), high.size()), low.size()), close.size());
        if (minSize == 0) {
            return 0.0;
        }

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int corrIdx = corr.size() - minSize + i;
            int hlIdx = high.size() - minSize + i;
            int closeIdx = close.size() - minSize + i;

            double c = Double.isNaN(corr.get(corrIdx)) || Double.isInfinite(corr.get(corrIdx)) ?
                       0.0 : corr.get(corrIdx);
            double val = (c + (high.get(hlIdx) + low.get(hlIdx)) / 2.0) - close.get(closeIdx);
            result.add(val);
        }

        List<Double> scaled = scale(result, 1.0);
        return scaled.isEmpty() ? 0.0 : scaled.get(scaled.size() - 1);
    }

    /**
     * Alpha#29: (min(product(rank(rank(scale(log(sum(ts_min(rank(rank((-1 * rank(delta((close - 1),5))))), 2), 1))))), 1), 5) +
     *            ts_rank(delay((-1 * returns), 6), 5))
     * 简化: ts_min(rank(rank(scale(log(ts_sum(rank(rank(-1 * rank(delta((close - 1), 5)))), 2))))), 5) +
     *      ts_rank(delay((-1 * returns), 6), 5)
     */
    private static Double alpha029(List<Double> close, List<Double> returns) {
        // Part 1: ts_min(rank(rank(scale(log(ts_sum(rank(rank(-1 * rank(delta((close - 1), 5)))), 2))))), 5)
        List<Double> closeMinus1 = new ArrayList<>();
        for (double c : close) {
            closeMinus1.add(c - 1.0);
        }

        List<Double> delta5 = delta(closeMinus1, 5);
        List<Double> rank1 = rank(delta5);
        List<Double> negRank1 = new ArrayList<>();
        for (double r : rank1) {
            negRank1.add(-1 * r);
        }
        List<Double> rank2 = rank(negRank1);
        List<Double> rank3 = rank(rank2);
        List<Double> tsSum2 = ts_sum(rank3, 2);
        List<Double> logged = log(tsSum2);
        List<Double> scaled = scale(logged);
        List<Double> rank4 = rank(scaled);
        List<Double> rank5 = rank(rank4);
        List<Double> tsMin5 = ts_min(rank5, 5);

        // Part 2: ts_rank(delay((-1 * returns), 6), 5)
        List<Double> negRet = new ArrayList<>();
        for (double r : returns) {
            negRet.add(-1 * r);
        }
        List<Double> delayRet = delay(negRet, 6);
        List<Double> tsRankRet = ts_rank(delayRet, 5);

        if (tsMin5.isEmpty() || tsRankRet.isEmpty()) {
            return 0.0;
        }

        return tsMin5.get(tsMin5.size() - 1) + tsRankRet.get(tsRankRet.size() - 1);
    }

    /**
     * Alpha#30: (((1.0 - rank(((sign((close - delay(close, 1))) + sign((delay(close, 1) - delay(close, 2)))) +
     *            sign((delay(close, 2) - delay(close, 3)))))) * sum(volume, 5)) / sum(volume, 20))
     */
    private static Double alpha030(List<Double> close, List<Double> volume) {
        List<Double> delay1 = delay(close, 1);
        List<Double> delay2 = delay(close, 2);
        List<Double> delay3 = delay(close, 3);

        int minSize = Math.min(Math.min(Math.min(close.size(), delay1.size()),
                                        Math.min(delay2.size(), delay3.size())), volume.size());

        if (minSize == 0) {
            return 0.0;
        }

        List<Double> signSum = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int idx = close.size() - minSize + i;
            int d1Idx = delay1.size() - minSize + i;
            int d2Idx = delay2.size() - minSize + i;
            int d3Idx = delay3.size() - minSize + i;

            double s1 = sign(close.get(idx) - delay1.get(d1Idx));
            double s2 = sign(delay1.get(d1Idx) - delay2.get(d2Idx));
            double s3 = sign(delay2.get(d2Idx) - delay3.get(d3Idx));
            signSum.add(s1 + s2 + s3);
        }

        List<Double> ranked = rank(signSum);
        List<Double> tsSum5 = ts_sum(volume, 5);
        List<Double> tsSum20 = ts_sum(volume, 20);

        if (ranked.isEmpty() || tsSum5.isEmpty() || tsSum20.isEmpty()) {
            return 0.0;
        }

        double r = ranked.get(ranked.size() - 1);
        double sum5 = tsSum5.get(tsSum5.size() - 1);
        double sum20 = tsSum20.get(tsSum20.size() - 1);

        if (Math.abs(sum20) < EPSILON) {
            return 0.0;
        }

        return ((1.0 - r) * sum5) / sum20;
    }

    /**
     * Alpha#31: ((rank(rank(rank(decay_linear((-1 * rank(rank(delta(close, 10)))), 10)))) +
     *            rank((-1 *delta(close, 3)))) + sign(scale(correlation(adv20, low, 12))))
     */
    private static Double alpha031(List<Double> close, List<Double> low, List<Double> adv20) {
        // Part 1: rank(rank(rank(decay_linear((-1 * rank(rank(delta(close, 10)))), 10))))
        List<Double> delta10 = delta(close, 10);
        List<Double> rank1 = rank(delta10);
        List<Double> rank2 = rank(rank1);
        List<Double> negRank2 = new ArrayList<>();
        for (double r : rank2) {
            negRank2.add(-1 * r);
        }
        List<Double> decayed = decay_linear(negRank2, 10);
        List<Double> rank3 = rank(decayed);
        List<Double> rank4 = rank(rank3);
        List<Double> rank5 = rank(rank4);

        // Part 2: rank((-1 * delta(close, 3)))
        List<Double> delta3 = delta(close, 3);
        List<Double> negDelta3 = new ArrayList<>();
        for (double d : delta3) {
            negDelta3.add(-1 * d);
        }
        List<Double> rank6 = rank(negDelta3);

        // Part 3: sign(scale(correlation(adv20, low, 12)))
        List<Double> corr = correlation(adv20, low, 12);
        List<Double> scaled = scale(corr, 1.0);

        if (rank5.isEmpty() || rank6.isEmpty() || scaled.isEmpty()) {
            return 0.0;
        }

        double scaledVal = scaled.get(scaled.size() - 1);
        double signScaled = sign(scaledVal);

        return rank5.get(rank5.size() - 1) + rank6.get(rank6.size() - 1) + signScaled;
    }

    /**
     * Alpha#32: (scale(((sum(close, 7) / 7) - close)) + (20 * scale(correlation(vwap, delay(close, 5),230))))
     */
    private static Double alpha032(List<Double> close, List<Double> vwap) {
        // Part 1: scale(((sum(close, 7) / 7) - close))
        List<Double> sma7 = sma(close, 7);
        int minSize1 = Math.min(sma7.size(), close.size());
        List<Double> diff = new ArrayList<>();
        for (int i = 0; i < minSize1; i++) {
            int closeIdx = close.size() - minSize1 + i;
            diff.add(sma7.get(i) - close.get(closeIdx));
        }
        List<Double> scaled1 = scale(diff, 1.0);

        // Part 2: 20 * scale(correlation(vwap, delay(close, 5), 230))
        List<Double> delayClose = delay(close, 5);
        List<Double> corr = correlation(vwap, delayClose, 230);
        List<Double> scaled2 = scale(corr, 1.0);

        if (scaled1.isEmpty() || scaled2.isEmpty()) {
            return 0.0;
        }

        return scaled1.get(scaled1.size() - 1) + (20 * scaled2.get(scaled2.size() - 1));
    }

    /**
     * Alpha#33: rank((-1 * ((1 - (open / close))^1)))
     */
    private static Double alpha033(List<Double> open, List<Double> close) {
        int minSize = Math.min(open.size(), close.size());
        if (minSize == 0) {
            return 0.0;
        }

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int openIdx = open.size() - minSize + i;
            int closeIdx = close.size() - minSize + i;

            double c = close.get(closeIdx);
            if (Math.abs(c) < EPSILON) {
                c = EPSILON;
            }

            double val = -1 * (1.0 - (open.get(openIdx) / c));
            result.add(val);
        }

        List<Double> ranked = rank(result);
        return ranked.isEmpty() ? 0.0 : ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#34: rank(((1 - rank((stddev(returns, 2) / stddev(returns, 5)))) + (1 - rank(delta(close, 1)))))
     */
    private static Double alpha034(List<Double> close, List<Double> returns) {
        // Part 1: 1 - rank((stddev(returns, 2) / stddev(returns, 5)))
        List<Double> std2 = stddev(returns, 2);
        List<Double> std5 = stddev(returns, 5);

        int minSize = Math.min(std2.size(), std5.size());
        if (minSize == 0) {
            return 0.0;
        }

        List<Double> ratio = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            double s5 = std5.get(i);
            if (Math.abs(s5) < EPSILON) {
                s5 = EPSILON;
            }
            ratio.add(std2.get(i) / s5);
        }

        List<Double> rank1 = rank(ratio);

        // Part 2: 1 - rank(delta(close, 1))
        List<Double> delta1 = delta(close, 1);
        List<Double> rank2 = rank(delta1);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        double r1 = rank1.get(rank1.size() - 1);
        double r2 = rank2.get(rank2.size() - 1);

        double combined = (1.0 - r1) + (1.0 - r2);
        return combined;
    }

    /**
     * Alpha#35: ((Ts_Rank(volume, 32) * (1 - Ts_Rank(((close + high) - low), 16))) * (1 -Ts_Rank(returns, 32)))
     */
    private static Double alpha035(List<Double> close, List<Double> high, List<Double> low,
                                  List<Double> volume, List<Double> returns) {
        List<Double> tsRankVol = ts_rank(volume, 32);

        // ((close + high) - low)
        int minSize = Math.min(Math.min(close.size(), high.size()), low.size());
        List<Double> chl = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int idx = close.size() - minSize + i;
            chl.add((close.get(idx) + high.get(idx)) - low.get(idx));
        }
        List<Double> tsRankCHL = ts_rank(chl, 16);

        List<Double> tsRankRet = ts_rank(returns, 32);

        if (tsRankVol.isEmpty() || tsRankCHL.isEmpty() || tsRankRet.isEmpty()) {
            return 0.0;
        }

        double tv = tsRankVol.get(tsRankVol.size() - 1);
        double tchl = tsRankCHL.get(tsRankCHL.size() - 1);
        double tr = tsRankRet.get(tsRankRet.size() - 1);

        return (tv * (1.0 - tchl)) * (1.0 - tr);
        }

    /**
     * Alpha#36: (((((2.21 * rank(correlation((close - open), delay(volume, 1), 15))) + (0.7 * rank((open- close)))) +
     *            (0.73 * rank(Ts_Rank(delay((-1 * returns), 6), 5)))) + rank(abs(correlation(vwap,adv20, 6)))) +
     *            (0.6 * rank((((sum(close, 200) / 200) - open) * (close - open)))))
     */
    private static Double alpha036(List<Double> open, List<Double> close, List<Double> volume,
                                  List<Double> vwap, List<Double> adv20, List<Double> returns) {
        // Part 1: 2.21 * rank(correlation((close - open), delay(volume, 1), 15))
        int minSize = Math.min(close.size(), open.size());
        List<Double> priceDiff = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            priceDiff.add(close.get(i) - open.get(i));
        }

        List<Double> delayedVol = delay(volume, 1);
        List<Double> corr1 = correlation(priceDiff, delayedVol, 15);
        List<Double> rank1 = rank(corr1);

        // Part 2: 0.7 * rank((open - close))
        List<Double> openClose = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            openClose.add(open.get(i) - close.get(i));
        }
        List<Double> rank2 = rank(openClose);

        // Part 3: 0.73 * rank(ts_rank(delay((-1 * returns), 6), 5))
        List<Double> negRet = new ArrayList<>();
        for (double r : returns) {
            negRet.add(-1 * r);
        }
        List<Double> delayedRet = delay(negRet, 6);
        List<Double> tsRankRet = ts_rank(delayedRet, 5);
        List<Double> rank3 = rank(tsRankRet);

        // Part 4: rank(abs(correlation(vwap, adv20, 6)))
        List<Double> corr2 = correlation(vwap, adv20, 6);
        List<Double> absCorr = abs(corr2);
        List<Double> rank4 = rank(absCorr);

        // Part 5: 0.6 * rank((((sum(close, 200) / 200) - open) * (close - open)))
        List<Double> smaClose = sma(close, 200);
        List<Double> part5 = new ArrayList<>();
        int size5 = Math.min(Math.min(smaClose.size(), open.size()), close.size());
        for (int i = 0; i < size5; i++) {
            int openIdx = open.size() - size5 + i;
            int closeIdx = close.size() - size5 + i;
            double val = (smaClose.get(i) - open.get(openIdx)) * (close.get(closeIdx) - open.get(openIdx));
            part5.add(val);
        }
        List<Double> rank5 = rank(part5);

        // Combine all parts
        if (rank1.isEmpty() || rank2.isEmpty() || rank3.isEmpty() || rank4.isEmpty() || rank5.isEmpty()) {
            return 0.0;
        }

        return (2.21 * rank1.get(rank1.size() - 1)) +
               (0.7 * rank2.get(rank2.size() - 1)) +
               (0.73 * rank3.get(rank3.size() - 1)) +
               rank4.get(rank4.size() - 1) +
               (0.6 * rank5.get(rank5.size() - 1));
    }

    /**
     * Alpha#37: (rank(correlation(delay((open - close), 1), close, 200)) + rank((open - close)))
     */
    private static Double alpha037(List<Double> open, List<Double> close) {
        int minSize = Math.min(open.size(), close.size());
        List<Double> openClose = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            openClose.add(open.get(i) - close.get(i));
        }

        List<Double> delayOC = delay(openClose, 1);
        List<Double> corr = correlation(delayOC, close, 200);
        List<Double> rank1 = rank(corr);
        List<Double> rank2 = rank(openClose);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return rank1.get(rank1.size() - 1) + rank2.get(rank2.size() - 1);
    }

    /**
     * Alpha#38: ((-1 * rank(Ts_Rank(open, 10))) * rank((close / open)))
     */
    private static Double alpha038(List<Double> open, List<Double> close) {
        List<Double> tsRankOpen = ts_rank(open, 10);
        List<Double> rank1 = rank(tsRankOpen);

        int minSize = Math.min(open.size(), close.size());
        List<Double> ratio = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int openIdx = open.size() - minSize + i;
            int closeIdx = close.size() - minSize + i;
            double o = open.get(openIdx);
            if (Math.abs(o) < EPSILON) {
                o = EPSILON;
            }
            ratio.add(close.get(closeIdx) / o);
        }
        List<Double> rank2 = rank(ratio);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        return -1 * rank1.get(rank1.size() - 1) * rank2.get(rank2.size() - 1);
    }

    /**
     * Alpha#39: ((-1 * rank((delta(close, 7) * (1 - rank(decay_linear((volume / adv20), 9)))))) *
     *            (1 +rank(sum(returns, 250))))
     */
    private static Double alpha039(List<Double> open, List<Double> close, List<Double> volume,
                                  List<Double> returns, List<Double> adv20) {
        // Part 1: delta(close, 7)
        List<Double> delta7 = delta(close, 7);

        // Part 2: 1 - rank(decay_linear((volume / adv20), 9))
        int minSize = Math.min(volume.size(), adv20.size());
        List<Double> volRatio = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            int volIdx = volume.size() - minSize + i;
            int advIdx = adv20.size() - minSize + i;
            double adv = adv20.get(advIdx);
            if (Math.abs(adv) < EPSILON) {
                adv = EPSILON;
            }
            volRatio.add(volume.get(volIdx) / adv);
        }

        List<Double> decayed = decay_linear(volRatio, 9);
        List<Double> ranked1 = rank(decayed);

        // Part 3: 1 + rank(sum(returns, 250))
        List<Double> sumRet = ts_sum(returns, 250);
        List<Double> ranked2 = rank(sumRet);

        if (delta7.isEmpty() || ranked1.isEmpty() || ranked2.isEmpty()) {
            return 0.0;
        }

        double d7 = delta7.get(delta7.size() - 1);
        double r1 = ranked1.get(ranked1.size() - 1);
        double r2 = ranked2.get(ranked2.size() - 1);

        double part1 = -1 * (d7 * (1.0 - r1));
        List<Double> part1List = new ArrayList<>();
        part1List.add(part1);
        List<Double> rankedPart1 = rank(part1List);

        if (rankedPart1.isEmpty()) {
            return 0.0;
        }

        return -1 * rankedPart1.get(0) * (1.0 + r2);
    }

    /**
     * Alpha#40: ((-1 * rank(stddev(high, 10))) * correlation(high, volume, 10))
     */
    private static Double alpha040(List<Double> close, List<Double> high, List<Double> volume) {
        List<Double> std = stddev(high, 10);
        List<Double> ranked = rank(std);
        List<Double> corr = correlation(high, volume, 10);

        if (ranked.isEmpty() || corr.isEmpty()) {
            return 0.0;
        }

        return -1 * ranked.get(ranked.size() - 1) * corr.get(corr.size() - 1);
    }
}
