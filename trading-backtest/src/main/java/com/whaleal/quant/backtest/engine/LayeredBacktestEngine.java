package com.whaleal.quant.backtest.engine;

import com.whaleal.quant.backtest.data.BacktestDataProvider;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.strategy.core.StrategyEngine;
import com.whaleal.quant.model.Bar;
import java.util.List;

public class LayeredBacktestEngine {

    private BacktestEngine highFrequencyEngine;
    private BacktestEngine standardEngine;
    private BacktestEngine lowFrequencyEngine;

    /**
     * 构造方法
     * @param dataProvider 数据提供者
     * @param strategyEngine 策略引擎
     */
    public LayeredBacktestEngine(BacktestDataProvider dataProvider, StrategyEngine strategyEngine) {
        this.highFrequencyEngine = BacktestEngine.builder()
                .config(new BacktestConfig())
                .dataProvider(dataProvider)
                .strategyEngine(strategyEngine)
                .build();

        this.standardEngine = BacktestEngine.builder()
                .config(new BacktestConfig())
                .dataProvider(dataProvider)
                .strategyEngine(strategyEngine)
                .build();

        this.lowFrequencyEngine = BacktestEngine.builder()
                .config(new BacktestConfig())
                .dataProvider(dataProvider)
                .strategyEngine(strategyEngine)
                .build();
    }

    public BacktestResult runHighFrequencyBacktest() {
        return highFrequencyEngine.run();
    }

    public BacktestResult runStandardBacktest() {
        return standardEngine.run();
    }

    public BacktestResult runLowFrequencyBacktest() {
        return lowFrequencyEngine.run();
    }

    public BacktestResult runHighFrequencyBacktest(List<Bar> bars, BacktestConfig config) {
        return highFrequencyEngine.run();
    }

    public BacktestResult runStandardBacktest(List<Bar> bars, BacktestConfig config) {
        return standardEngine.run();
    }

    public BacktestResult runLowFrequencyBacktest(List<Bar> bars, BacktestConfig config) {
        return lowFrequencyEngine.run();
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
