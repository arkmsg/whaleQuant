package com.whaleal.quant.model.indicators;

import java.util.ArrayList;
import java.util.List;
import com.whaleal.quant.model.Bar;

public class MACDIndicator implements TechnicalIndicator {

    private int fastPeriod = 12;
    private int slowPeriod = 26;
    private int signalPeriod = 9;

    public MACDIndicator() {
    }

    public MACDIndicator(int fastPeriod, int slowPeriod, int signalPeriod) {
        this.fastPeriod = fastPeriod;
        this.slowPeriod = slowPeriod;
        this.signalPeriod = signalPeriod;
    }

    @Override
    public double calculate(List<Bar> bars) {
        if (bars.size() < slowPeriod + signalPeriod) {
            return 0;
        }

        List<Double> emaFast = calculateEMA(bars, fastPeriod);
        List<Double> emaSlow = calculateEMA(bars, slowPeriod);
        List<Double> macdLine = new ArrayList<>();

        for (int i = 0; i < emaFast.size(); i++) {
            macdLine.add(emaFast.get(i) - emaSlow.get(i));
        }

        List<Double> signalLine = calculateEMAFromValues(macdLine, signalPeriod);
        List<Double> histogram = new ArrayList<>();

        for (int i = 0; i < signalLine.size(); i++) {
            histogram.add(macdLine.get(i + (macdLine.size() - signalLine.size())) - signalLine.get(i));
        }

        return histogram.isEmpty() ? 0 : histogram.get(histogram.size() - 1);
    }

    private List<Double> calculateEMA(List<Bar> bars, int period) {
        List<Double> ema = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);

        double firstEma = 0;
        for (int i = 0; i < period; i++) {
            firstEma += bars.get(i).getClose().doubleValue();
        }
        firstEma /= period;
        ema.add(firstEma);

        for (int i = period; i < bars.size(); i++) {
            double currentPrice = bars.get(i).getClose().doubleValue();
            double previousEma = ema.get(ema.size() - 1);
            double currentEma = (currentPrice - previousEma) * multiplier + previousEma;
            ema.add(currentEma);
        }

        return ema;
    }

    private List<Double> calculateEMAFromValues(List<Double> values, int period) {
        List<Double> ema = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);

        double firstEma = 0;
        for (int i = 0; i < period; i++) {
            firstEma += values.get(i);
        }
        firstEma /= period;
        ema.add(firstEma);

        for (int i = period; i < values.size(); i++) {
            double currentValue = values.get(i);
            double previousEma = ema.get(ema.size() - 1);
            double currentEma = (currentValue - previousEma) * multiplier + previousEma;
            ema.add(currentEma);
        }

        return ema;
    }
}
