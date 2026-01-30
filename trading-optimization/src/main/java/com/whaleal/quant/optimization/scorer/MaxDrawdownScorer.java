package com.whaleal.quant.optimization.scorer;

import com.whaleal.quant.backtest.result.BacktestResult;

/**
 * 最大回撤评分器
 * 基于策略的最大回撤进行评分
 *
 * @author whaleal
 * @version 1.0.0
 */
public class MaxDrawdownScorer implements StrategyScorer {

    @Override
    public double score(BacktestResult result) {
        double maxDrawdown = result.getMaxDrawdown();
        
        // 将最大回撤映射到 [0, 1] 范围
        // 最大回撤通常在 [0, 1] 之间，值越小表示风险越小，评分越高
        double normalizedScore = 1.0 - Math.abs(maxDrawdown);
        return Math.max(0, Math.min(1, normalizedScore));
    }

    @Override
    public String getName() {
        return "MaxDrawdownScorer";
    }
}