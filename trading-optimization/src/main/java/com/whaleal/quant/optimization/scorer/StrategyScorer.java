package com.whaleal.quant.optimization.scorer;

import com.whaleal.quant.backtest.result.BacktestResult;

/**
 * 策略评分器
 * 用于对策略回测结果进行评分
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface StrategyScorer {

    /**
     * 对回测结果进行评分
     * @param result 回测结果
     * @return 评分，范围通常为[0, 1]，值越大表示策略性能越好
     */
    double score(BacktestResult result);

    /**
     * 获取评分器名称
     * @return 评分器名称
     */
    String getName();
}