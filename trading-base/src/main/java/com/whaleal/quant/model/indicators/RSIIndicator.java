package com.whaleal.quant.model.indicators;

import java.util.List;
import com.whaleal.quant.model.Bar;

public class RSIIndicator implements TechnicalIndicator {

    private int period = 14;

    public RSIIndicator() {
    }

    public RSIIndicator(int period) {
        this.period = period;
    }

    @Override
    public double calculate(List<Bar> bars) {
        if (bars.size() < period + 1) {
            return 0;
        }

        double[] changes = new double[bars.size() - 1];
        for (int i = 1; i < bars.size(); i++) {
            changes[i - 1] = bars.get(i).getClose().doubleValue() - bars.get(i - 1).getClose().doubleValue();
        }

        double avgGain = 0;
        double avgLoss = 0;

        for (int i = 0; i < period; i++) {
            if (changes[i] > 0) {
                avgGain += changes[i];
            } else {
                avgLoss += Math.abs(changes[i]);
            }
        }

        avgGain /= period;
        avgLoss /= period;

        for (int i = period; i < changes.length; i++) {
            if (changes[i] > 0) {
                avgGain = (avgGain * (period - 1) + changes[i]) / period;
                avgLoss = (avgLoss * (period - 1)) / period;
            } else {
                avgGain = (avgGain * (period - 1)) / period;
                avgLoss = (avgLoss * (period - 1) + Math.abs(changes[i])) / period;
            }
        }

        if (avgLoss == 0) {
            return 100;
        }

        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }
}
