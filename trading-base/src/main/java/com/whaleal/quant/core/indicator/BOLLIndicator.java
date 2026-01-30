package com.whaleal.quant.core.indicator;

import java.util.ArrayList;
import java.util.List;
import com.whaleal.quant.core.model.Bar;

public class BOLLIndicator implements TechnicalIndicator {
    
    private int period = 20;
    private double multiplier = 2.0;
    
    public BOLLIndicator() {
    }
    
    public BOLLIndicator(int period, double multiplier) {
        this.period = period;
        this.multiplier = multiplier;
    }
    
    @Override
    public double calculate(List<Bar> bars) {
        if (bars.size() < period) {
            return 0;
        }
        
        List<Double> closes = new ArrayList<>();
        for (Bar bar : bars) {
            closes.add(bar.getClose().doubleValue());
        }
        
        double middleBand = calculateSMA(closes, period);
        double standardDeviation = calculateStandardDeviation(closes, period, middleBand);
        
        double upperBand = middleBand + (multiplier * standardDeviation);
        double lowerBand = middleBand - (multiplier * standardDeviation);
        
        double currentPrice = bars.get(bars.size() - 1).getClose().doubleValue();
        return (currentPrice - lowerBand) / (upperBand - lowerBand);
    }
    
    private double calculateSMA(List<Double> values, int period) {
        double sum = 0;
        for (int i = values.size() - period; i < values.size(); i++) {
            sum += values.get(i);
        }
        return sum / period;
    }
    
    private double calculateStandardDeviation(List<Double> values, int period, double mean) {
        double sum = 0;
        for (int i = values.size() - period; i < values.size(); i++) {
            double diff = values.get(i) - mean;
            sum += diff * diff;
        }
        return Math.sqrt(sum / period);
    }
    
    public double getUpperBand(List<Bar> bars) {
        if (bars.size() < period) {
            return 0;
        }
        
        List<Double> closes = new ArrayList<>();
        for (Bar bar : bars) {
            closes.add(bar.getClose().doubleValue());
        }
        
        double middleBand = calculateSMA(closes, period);
        double standardDeviation = calculateStandardDeviation(closes, period, middleBand);
        
        return middleBand + (multiplier * standardDeviation);
    }
    
    public double getMiddleBand(List<Bar> bars) {
        if (bars.size() < period) {
            return 0;
        }
        
        List<Double> closes = new ArrayList<>();
        for (Bar bar : bars) {
            closes.add(bar.getClose().doubleValue());
        }
        
        return calculateSMA(closes, period);
    }
    
    public double getLowerBand(List<Bar> bars) {
        if (bars.size() < period) {
            return 0;
        }
        
        List<Double> closes = new ArrayList<>();
        for (Bar bar : bars) {
            closes.add(bar.getClose().doubleValue());
        }
        
        double middleBand = calculateSMA(closes, period);
        double standardDeviation = calculateStandardDeviation(closes, period, middleBand);
        
        return middleBand - (multiplier * standardDeviation);
    }
}
