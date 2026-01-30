package com.whaleal.quant.backtest.engine;

import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.model.BacktestContext;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.core.model.Bar;
import java.util.List;

public class LayeredBacktestEngine {
    
    private BacktestEngine highFrequencyEngine;
    private BacktestEngine standardEngine;
    private BacktestEngine lowFrequencyEngine;
    
    public LayeredBacktestEngine() {
        this.highFrequencyEngine = new BacktestEngine();
        this.standardEngine = new BacktestEngine();
        this.lowFrequencyEngine = new BacktestEngine();
    }
    
    public BacktestResult runHighFrequencyBacktest(List<Bar> bars, BacktestConfig config) {
        config.setFrequency("30m");
        return highFrequencyEngine.run(bars, config);
    }
    
    public BacktestResult runStandardBacktest(List<Bar> bars, BacktestConfig config) {
        config.setFrequency("1h");
        return standardEngine.run(bars, config);
    }
    
    public BacktestResult runLowFrequencyBacktest(List<Bar> bars, BacktestConfig config) {
        config.setFrequency("4h");
        return lowFrequencyEngine.run(bars, config);
    }
    
    public LayeredBacktestResult runLayeredBacktest(List<Bar> bars, BacktestConfig config) {
        BacktestResult highFreqResult = runHighFrequencyBacktest(bars, config);
        BacktestResult standardResult = runStandardBacktest(bars, config);
        BacktestResult lowFreqResult = runLowFrequencyBacktest(bars, config);
        
        return new LayeredBacktestResult(
            highFreqResult,
            standardResult,
            lowFreqResult,
            calculateOverallScore(highFreqResult, standardResult, lowFreqResult)
        );
    }
    
    private double calculateOverallScore(BacktestResult... results) {
        double totalScore = 0;
        for (BacktestResult result : results) {
            totalScore += result.getSharpeRatio();
        }
        return totalScore / results.length;
    }
    
    public static class LayeredBacktestResult {
        private BacktestResult highFrequencyResult;
        private BacktestResult standardResult;
        private BacktestResult lowFrequencyResult;
        private double overallScore;
        
        public LayeredBacktestResult(BacktestResult highFrequencyResult, BacktestResult standardResult, BacktestResult lowFrequencyResult, double overallScore) {
            this.highFrequencyResult = highFrequencyResult;
            this.standardResult = standardResult;
            this.lowFrequencyResult = lowFrequencyResult;
            this.overallScore = overallScore;
        }
        
        public BacktestResult getHighFrequencyResult() {
            return highFrequencyResult;
        }
        
        public BacktestResult getStandardResult() {
            return standardResult;
        }
        
        public BacktestResult getLowFrequencyResult() {
            return lowFrequencyResult;
        }
        
        public double getOverallScore() {
            return overallScore;
        }
        
        @Override
        public String toString() {
            return "LayeredBacktestResult{" +
                "highFrequencyResult=" + highFrequencyResult +
                ", standardResult=" + standardResult +
                ", lowFrequencyResult=" + lowFrequencyResult +
                ", overallScore=" + overallScore +
                '}';
        }
    }
}
