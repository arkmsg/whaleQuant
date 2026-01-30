package com.whaleal.quant.optimization.scorer;

import com.whaleal.quant.backtest.result.BacktestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 复合评分器
 * 用于组合多个评分器的结果
 *
 * @author whaleal
 * @version 1.0.0
 */
public class CompositeScorer implements StrategyScorer {

    private final List<StrategyScorer> scorers;
    private final List<Double> weights;

    /**
     * 构造方法
     * @param scorers 评分器列表
     * @param weights 权重列表
     */
    public CompositeScorer(List<StrategyScorer> scorers, List<Double> weights) {
        if (scorers.size() != weights.size()) {
            throw new IllegalArgumentException("Scorers and weights must have the same size");
        }
        this.scorers = scorers;
        this.weights = weights;
    }

    /**
     * 构造方法
     * 使用等权重
     * @param scorers 评分器列表
     */
    public CompositeScorer(List<StrategyScorer> scorers) {
        this.scorers = scorers;
        this.weights = new ArrayList<>();
        double weight = 1.0 / scorers.size();
        for (int i = 0; i < scorers.size(); i++) {
            weights.add(weight);
        }
    }

    @Override
    public double score(BacktestResult result) {
        double totalScore = 0.0;
        double totalWeight = 0.0;

        for (int i = 0; i < scorers.size(); i++) {
            StrategyScorer scorer = scorers.get(i);
            double weight = weights.get(i);
            double score = scorer.score(result);
            totalScore += score * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? totalScore / totalWeight : 0.0;
    }

    @Override
    public String getName() {
        StringBuilder name = new StringBuilder("CompositeScorer[");
        for (int i = 0; i < scorers.size(); i++) {
            if (i > 0) {
                name.append(", ");
            }
            name.append(scorers.get(i).getName()).append("(")
                 .append(String.format("%.2f", weights.get(i))).append(")");
        }
        name.append("]");
        return name.toString();
    }
}