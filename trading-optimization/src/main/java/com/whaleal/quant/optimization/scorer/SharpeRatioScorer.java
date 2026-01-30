package com.whaleal.quant.optimization.scorer;

import com.whaleal.quant.backtest.result.BacktestResult;

/**
 * 夏普比率评分器
 * 基于策略的夏普比率进行评分
 *
 * @author whaleal
 * @version 1.0.0
 */
public class SharpeRatioScorer implements StrategyScorer {

    private final double riskFreeRate;

    /**
     * 构造方法
     * @param riskFreeRate 无风险利率
     */
    public SharpeRatioScorer(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

    /**
     * 构造方法
     * 使用默认无风险利率 0.02
     */
    public SharpeRatioScorer() {
        this(0.02);
    }

    @Override
    public double score(BacktestResult result) {
        double sharpeRatio = result.getSharpeRatio();
        
        // 将夏普比率映射到 [0, 1] 范围
        // 夏普比率通常在 [-3, 3] 之间，我们将其映射到 [0, 1]
        double normalizedScore = (sharpeRatio + 3) / 6.0;
        return Math.max(0, Math.min(1, normalizedScore));
    }

    @Override
    public String getName() {
        return "SharpeRatioScorer";
    }
}