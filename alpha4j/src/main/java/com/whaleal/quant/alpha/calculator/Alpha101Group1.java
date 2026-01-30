package com.whaleal.quant.alpha.calculator;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101 Group1: Alpha#1-20
 *
 * 基础因子组，包含最常用的Alpha因子
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha101Group1 {

    private static final double EPSILON = 1e-12;

    /**
     * 计算指定的Alpha因子
     */
    public static Double calculate(int alphaNumber, List<Double> close, List<Double> open,
                                   List<Double> high, List<Double> low, List<Double> volume,
                                   List<Double> vwap, List<Double> returns, List<Double> adv20) {
        switch (alphaNumber) {
            case 1: return alpha001(close, returns);
            case 2: return alpha002(close, open, volume);
            case 3: return alpha003(open, volume);
            case 4: return alpha004(low);
            case 5: return alpha005(open, close, vwap);
            case 6: return alpha006(open, volume);
            case 7: return alpha007(close, volume, adv20);
            case 8: return alpha008(open, returns);
            case 9: return alpha009(close);
            case 10: return alpha010(close);
            case 11: return alpha011(close, volume, vwap);
            case 12: return alpha012(close, volume);
            case 13: return alpha013(close, volume);
            case 14: return alpha014(open, volume, returns);
            case 15: return alpha015(high, volume);
            case 16: return alpha016(high, volume);
            case 17: return alpha017(close, volume, adv20);
            case 18: return alpha018(close, open);
            case 19: return alpha019(close, returns);
            case 20: return alpha020(open, high, close, low);
            default:
                return 0.0;
        }
    }

    /**
     * Alpha#1: (rank(Ts_ArgMax(SignedPower(((returns < 0) ? stddev(returns, 20) : close), 2.), 5)) -0.5)
     */
    private static Double alpha001(List<Double> close, List<Double> returns) {
        List<Double> inner = new ArrayList<>();
        List<Double> stdReturns = stddev(returns, 20);

        for (int i = 0; i < Math.min(stdReturns.size(), close.size() - returns.size() + stdReturns.size()); i++) {
            int returnsIdx = returns.size() - stdReturns.size() + i;
            int closeIdx = close.size() - stdReturns.size() + i;
            if (returnsIdx >= 0 && returnsIdx < returns.size()) {
                if (returns.get(returnsIdx) < 0) {
                    inner.add(stdReturns.get(i));
                } else {
                    inner.add(close.get(closeIdx));
                }
            }
        }

        List<Double> powered = signedpower(inner, 2.0);
        List<Double> argmax = ts_argmax(powered, 5);
        List<Double> ranked = rank(argmax);

        return ranked.isEmpty() ? 0.0 : ranked.get(ranked.size() - 1) - 0.5;
    }

    /**
     * Alpha#2: (-1 * correlation(rank(delta(log(volume), 2)), rank(((close - open) / open)), 6))
     */
    private static Double alpha002(List<Double> close, List<Double> open, List<Double> volume) {
        List<Double> logVol = log(volume);
        List<Double> deltaLogVol = delta(logVol, 2);
        List<Double> rankDelta = rank(deltaLogVol);

        List<Double> priceChange = new ArrayList<>();
        int minSize = Math.min(close.size(), open.size());
        for (int i = 0; i < minSize; i++) {
            priceChange.add((close.get(i) - open.get(i)) / (open.get(i) + EPSILON));
        }

        List<Double> rankPrice = rank(priceChange);

        int alignSize = Math.min(rankDelta.size(), rankPrice.size());
        List<Double> x = new ArrayList<>(rankDelta.subList(rankDelta.size() - alignSize, rankDelta.size()));
        List<Double> y = new ArrayList<>(rankPrice.subList(rankPrice.size() - alignSize, rankPrice.size()));

        List<Double> corr = correlation(x, y, 6);
        return corr.isEmpty() ? 0.0 : -1 * corr.get(corr.size() - 1);
    }

    /**
     * Alpha#3: (-1 * correlation(rank(open), rank(volume), 10))
     */
    private static Double alpha003(List<Double> open, List<Double> volume) {
        List<Double> rankOpen = rank(open);
        List<Double> rankVol = rank(volume);
        List<Double> corr = correlation(rankOpen, rankVol, 10);
        return corr.isEmpty() ? 0.0 : -1 * corr.get(corr.size() - 1);
    }

    /**
     * Alpha#4: (-1 * Ts_Rank(rank(low), 9))
     */
    private static Double alpha004(List<Double> low) {
        List<Double> rankLow = rank(low);
        List<Double> tsRank = ts_rank(rankLow, 9);
        return tsRank.isEmpty() ? 0.0 : -1 * tsRank.get(tsRank.size() - 1);
    }

    /**
     * Alpha#5: (rank((open - (sum(vwap, 10) / 10))) * (-1 * abs(rank((close - vwap)))))
     */
    private static Double alpha005(List<Double> open, List<Double> close, List<Double> vwap) {
        List<Double> avgVwap = new ArrayList<>();
        List<Double> sumVwap = ts_sum(vwap, 10);
        for (double s : sumVwap) {
            avgVwap.add(s / 10.0);
        }

        List<Double> openMinusVwap = new ArrayList<>();
        int alignSize = Math.min(open.size(), avgVwap.size());
        for (int i = 0; i < alignSize; i++) {
            int openIdx = open.size() - alignSize + i;
            openMinusVwap.add(open.get(openIdx) - avgVwap.get(i));
        }

        List<Double> rank1 = rank(openMinusVwap);

        List<Double> closeMinusVwap = new ArrayList<>();
        int minSize = Math.min(close.size(), vwap.size());
        for (int i = 0; i < minSize; i++) {
            closeMinusVwap.add(close.get(i) - vwap.get(i));
        }

        List<Double> rank2 = rank(closeMinusVwap);

        if (rank1.isEmpty() || rank2.isEmpty()) {
            return 0.0;
        }

        double r1 = rank1.get(rank1.size() - 1);
        double r2 = rank2.get(rank2.size() - 1);

        return r1 * (-1 * Math.abs(r2));
    }

    /**
     * Alpha#6: (-1 * correlation(open, volume, 10))
     */
    private static Double alpha006(List<Double> open, List<Double> volume) {
        List<Double> corr = correlation(open, volume, 10);
        return corr.isEmpty() ? 0.0 : -1 * corr.get(corr.size() - 1);
    }

    /**
     * Alpha#7: ((adv20 < volume) ? ((-1 * ts_rank(abs(delta(close, 7)), 60)) * sign(delta(close, 7))) : (-1))
     */
    private static Double alpha007(List<Double> close, List<Double> volume, List<Double> adv20) {
        if (adv20.isEmpty() || volume.size() < adv20.size()) {
            return 0.0;
        }

        double currentVol = volume.get(volume.size() - 1);
        double currentAdv20 = adv20.get(adv20.size() - 1);

        if (currentAdv20 < currentVol) {
            List<Double> deltaClose = delta(close, 7);
            List<Double> absDelta = abs(deltaClose);
            List<Double> tsRank = ts_rank(absDelta, 60);

            if (tsRank.isEmpty() || deltaClose.isEmpty()) {
                return -1.0;
            }

            double rankVal = tsRank.get(tsRank.size() - 1);
            double deltaVal = deltaClose.get(deltaClose.size() - 1);
            double signVal = deltaVal > EPSILON ? 1.0 : (deltaVal < -EPSILON ? -1.0 : 0.0);

            return (-1 * rankVal) * signVal;
        } else {
            return -1.0;
        }
    }

    /**
     * Alpha#8: (-1 * rank(((sum(open, 5) * sum(returns, 5)) - delay((sum(open, 5) * sum(returns, 5)), 10))))
     */
    private static Double alpha008(List<Double> open, List<Double> returns) {
        List<Double> sumOpen = ts_sum(open, 5);
        List<Double> sumReturns = ts_sum(returns, 5);

        List<Double> product = new ArrayList<>();
        int minSize = Math.min(sumOpen.size(), sumReturns.size());
        for (int i = 0; i < minSize; i++) {
            int openIdx = sumOpen.size() - minSize + i;
            product.add(sumOpen.get(openIdx) * sumReturns.get(i));
        }

        List<Double> delayedProduct = delay(product, 10);

        List<Double> diff = new ArrayList<>();
        minSize = Math.min(product.size(), delayedProduct.size());
        for (int i = 0; i < minSize; i++) {
            int prodIdx = product.size() - minSize + i;
            diff.add(product.get(prodIdx) - delayedProduct.get(i));
        }

        List<Double> ranked = rank(diff);

        return ranked.isEmpty() ? 0.0 : -1 * ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#9: ((0 < ts_min(delta(close, 1), 5)) ? delta(close, 1) : ((ts_max(delta(close, 1), 5) < 0) ? delta(close, 1) : (-1 * delta(close, 1))))
     */
    private static Double alpha009(List<Double> close) {
        List<Double> deltaClose = delta(close, 1);
        List<Double> tsMin = ts_min(deltaClose, 5);
        List<Double> tsMax = ts_max(deltaClose, 5);

        if (deltaClose.isEmpty() || tsMin.isEmpty() || tsMax.isEmpty()) {
            return 0.0;
        }

        double delta = deltaClose.get(deltaClose.size() - 1);
        double minVal = tsMin.get(tsMin.size() - 1);
        double maxVal = tsMax.get(tsMax.size() - 1);

        if (0 < minVal) {
            return delta;
        } else if (maxVal < 0) {
            return delta;
        } else {
            return -1 * delta;
        }
    }

    /**
     * Alpha#10: rank(((0 < ts_min(delta(close, 1), 4)) ? delta(close, 1) : ((ts_max(delta(close, 1), 4) < 0) ? delta(close, 1) : (-1 * delta(close, 1)))))
     */
    private static Double alpha010(List<Double> close) {
        List<Double> deltaClose = delta(close, 1);
        List<Double> tsMin = ts_min(deltaClose, 4);
        List<Double> tsMax = ts_max(deltaClose, 4);

        if (deltaClose.isEmpty() || tsMin.isEmpty() || tsMax.isEmpty()) {
            return 0.0;
        }

        List<Double> condResult = new ArrayList<>();
        int minSize = Math.min(Math.min(deltaClose.size(), tsMin.size()), tsMax.size());

        for (int i = 0; i < minSize; i++) {
            int deltaIdx = deltaClose.size() - minSize + i;
            double delta = deltaClose.get(deltaIdx);
            double minVal = tsMin.get(i);
            double maxVal = tsMax.get(i);

            if (0 < minVal) {
                condResult.add(delta);
            } else if (maxVal < 0) {
                condResult.add(delta);
            } else {
                condResult.add(-1 * delta);
            }
        }

        List<Double> ranked = rank(condResult);
        return ranked.isEmpty() ? 0.0 : ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#11: ((rank(ts_max((vwap - close), 3)) + rank(ts_min((vwap - close), 3))) * rank(delta(volume, 3)))
     */
    private static Double alpha011(List<Double> close, List<Double> volume, List<Double> vwap) {
        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(close.size(), vwap.size());
        for (int i = 0; i < minSize; i++) {
            diff.add(vwap.get(i) - close.get(i));
        }

        List<Double> tsMax = ts_max(diff, 3);
        List<Double> tsMin = ts_min(diff, 3);
        List<Double> deltaVol = delta(volume, 3);

        List<Double> rank1 = rank(tsMax);
        List<Double> rank2 = rank(tsMin);
        List<Double> rank3 = rank(deltaVol);

        if (rank1.isEmpty() || rank2.isEmpty() || rank3.isEmpty()) {
            return 0.0;
        }

        double r1 = rank1.get(rank1.size() - 1);
        double r2 = rank2.get(rank2.size() - 1);
        double r3 = rank3.get(rank3.size() - 1);

        return (r1 + r2) * r3;
    }

    /**
     * Alpha#12: (sign(delta(volume, 1)) * (-1 * delta(close, 1)))
     */
    private static Double alpha012(List<Double> close, List<Double> volume) {
        List<Double> deltaVol = delta(volume, 1);
        List<Double> deltaClose = delta(close, 1);

        if (deltaVol.isEmpty() || deltaClose.isEmpty()) {
            return 0.0;
        }

        double volDelta = deltaVol.get(deltaVol.size() - 1);
        double closeDelta = deltaClose.get(deltaClose.size() - 1);

        double signVal = volDelta > EPSILON ? 1.0 : (volDelta < -EPSILON ? -1.0 : 0.0);

        return signVal * (-1 * closeDelta);
    }

    /**
     * Alpha#13: (-1 * rank(covariance(rank(close), rank(volume), 5)))
     */
    private static Double alpha013(List<Double> close, List<Double> volume) {
        List<Double> rankClose = rank(close);
        List<Double> rankVol = rank(volume);
        List<Double> cov = covariance(rankClose, rankVol, 5);
        List<Double> ranked = rank(cov);
        return ranked.isEmpty() ? 0.0 : -1 * ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#14: ((-1 * rank(delta(returns, 3))) * correlation(open, volume, 10))
     */
    private static Double alpha014(List<Double> open, List<Double> volume, List<Double> returns) {
        List<Double> deltaRet = delta(returns, 3);
        List<Double> ranked = rank(deltaRet);
        List<Double> corr = correlation(open, volume, 10);

        if (ranked.isEmpty() || corr.isEmpty()) {
            return 0.0;
        }

        double r = ranked.get(ranked.size() - 1);
        double c = corr.get(corr.size() - 1);

        return (-1 * r) * c;
    }

    /**
     * Alpha#15: (-1 * sum(rank(correlation(rank(high), rank(volume), 3)), 3))
     */
    private static Double alpha015(List<Double> high, List<Double> volume) {
        List<Double> rankHigh = rank(high);
        List<Double> rankVol = rank(volume);
        List<Double> corr = correlation(rankHigh, rankVol, 3);
        List<Double> ranked = rank(corr);
        List<Double> sum = ts_sum(ranked, 3);
        return sum.isEmpty() ? 0.0 : -1 * sum.get(sum.size() - 1);
    }

    /**
     * Alpha#16: (-1 * rank(covariance(rank(high), rank(volume), 5)))
     */
    private static Double alpha016(List<Double> high, List<Double> volume) {
        List<Double> rankHigh = rank(high);
        List<Double> rankVol = rank(volume);
        List<Double> cov = covariance(rankHigh, rankVol, 5);
        List<Double> ranked = rank(cov);
        return ranked.isEmpty() ? 0.0 : -1 * ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#17: (((-1 * rank(ts_rank(close, 10))) * rank(delta(delta(close, 1), 1))) * rank(ts_rank((volume / adv20), 5)))
     */
    private static Double alpha017(List<Double> close, List<Double> volume, List<Double> adv20) {
        List<Double> tsRankClose = ts_rank(close, 10);
        List<Double> rank1 = rank(tsRankClose);

        List<Double> delta1 = delta(close, 1);
        List<Double> delta2 = delta(delta1, 1);
        List<Double> rank2 = rank(delta2);

        List<Double> volRatio = new ArrayList<>();
        int minSize = Math.min(volume.size(), adv20.size());
        for (int i = 0; i < minSize; i++) {
            int volIdx = volume.size() - minSize + i;
            volRatio.add(volume.get(volIdx) / (adv20.get(i) + EPSILON));
        }

        List<Double> tsRankVol = ts_rank(volRatio, 5);
        List<Double> rank3 = rank(tsRankVol);

        if (rank1.isEmpty() || rank2.isEmpty() || rank3.isEmpty()) {
            return 0.0;
        }

        double r1 = rank1.get(rank1.size() - 1);
        double r2 = rank2.get(rank2.size() - 1);
        double r3 = rank3.get(rank3.size() - 1);

        return ((-1 * r1) * r2) * r3;
    }

    /**
     * Alpha#18: (-1 * rank(((stddev(abs((close - open)), 5) + (close - open)) + correlation(close, open, 10))))
     */
    private static Double alpha018(List<Double> close, List<Double> open) {
        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(close.size(), open.size());
        for (int i = 0; i < minSize; i++) {
            diff.add(close.get(i) - open.get(i));
        }

        List<Double> absDiff = abs(diff);
        List<Double> std = stddev(absDiff, 5);
        List<Double> corr = correlation(close, open, 10);

        List<Double> combined = new ArrayList<>();
        int combSize = Math.min(Math.min(std.size(), diff.size()), corr.size());

        for (int i = 0; i < combSize; i++) {
            int diffIdx = diff.size() - combSize + i;
            int corrIdx = corr.size() - combSize + i;
            combined.add(std.get(i) + diff.get(diffIdx) + corr.get(corrIdx));
        }

        List<Double> ranked = rank(combined);
        return ranked.isEmpty() ? 0.0 : -1 * ranked.get(ranked.size() - 1);
    }

    /**
     * Alpha#19: ((-1 * sign(((close - delay(close, 7)) + delta(close, 7)))) * (1 + rank((1 + sum(returns, 250)))))
     */
    private static Double alpha019(List<Double> close, List<Double> returns) {
        List<Double> delayedClose = delay(close, 7);
        List<Double> deltaClose = delta(close, 7);

        List<Double> diff = new ArrayList<>();
        int minSize = Math.min(close.size(), delayedClose.size());
        for (int i = 0; i < minSize; i++) {
            int closeIdx = close.size() - minSize + i;
            diff.add(close.get(closeIdx) - delayedClose.get(i));
        }

        List<Double> combined = new ArrayList<>();
        minSize = Math.min(diff.size(), deltaClose.size());
        for (int i = 0; i < minSize; i++) {
            int diffIdx = diff.size() - minSize + i;
            combined.add(diff.get(diffIdx) + deltaClose.get(i));
        }

        if (combined.isEmpty() || returns.size() < 250) {
            return 0.0;
        }

        double signVal = combined.get(combined.size() - 1);
        signVal = signVal > EPSILON ? 1.0 : (signVal < -EPSILON ? -1.0 : 0.0);

        List<Double> sumRet = ts_sum(returns, 250);
        if (sumRet.isEmpty()) {
            return 0.0;
        }

        List<Double> onePlusSum = new ArrayList<>();
        for (double s : sumRet) {
            onePlusSum.add(1 + s);
        }

        List<Double> ranked = rank(onePlusSum);
        if (ranked.isEmpty()) {
            return 0.0;
        }

        double r = ranked.get(ranked.size() - 1);

        return (-1 * signVal) * (1 + r);
    }

    /**
     * Alpha#20: (((-1 * rank((open - delay(high, 1)))) * rank((open - delay(close, 1)))) * rank((open - delay(low, 1))))
     */
    private static Double alpha020(List<Double> open, List<Double> high, List<Double> close, List<Double> low) {
        List<Double> delayHigh = delay(high, 1);
        List<Double> delayClose = delay(close, 1);
        List<Double> delayLow = delay(low, 1);

        List<Double> diff1 = new ArrayList<>();
        int minSize = Math.min(open.size(), delayHigh.size());
        for (int i = 0; i < minSize; i++) {
            int openIdx = open.size() - minSize + i;
            diff1.add(open.get(openIdx) - delayHigh.get(i));
        }

        List<Double> diff2 = new ArrayList<>();
        minSize = Math.min(open.size(), delayClose.size());
        for (int i = 0; i < minSize; i++) {
            int openIdx = open.size() - minSize + i;
            diff2.add(open.get(openIdx) - delayClose.get(i));
        }

        List<Double> diff3 = new ArrayList<>();
        minSize = Math.min(open.size(), delayLow.size());
        for (int i = 0; i < minSize; i++) {
            int openIdx = open.size() - minSize + i;
            diff3.add(open.get(openIdx) - delayLow.get(i));
        }

        List<Double> rank1 = rank(diff1);
        List<Double> rank2 = rank(diff2);
        List<Double> rank3 = rank(diff3);

        if (rank1.isEmpty() || rank2.isEmpty() || rank3.isEmpty()) {
            return 0.0;
        }

        double r1 = rank1.get(rank1.size() - 1);
        double r2 = rank2.get(rank2.size() - 1);
        double r3 = rank3.get(rank3.size() - 1);

        return ((-1 * r1) * r2) * r3;
    }
}








