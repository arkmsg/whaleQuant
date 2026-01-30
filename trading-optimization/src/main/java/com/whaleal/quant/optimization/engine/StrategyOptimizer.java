package com.whaleal.quant.optimization.engine;

import com.whaleal.quant.backtest.data.BacktestDataProvider;
import com.whaleal.quant.backtest.engine.BacktestEngine;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.engine.strategy.core.StrategyEngine;
import com.whaleal.quant.optimization.param.ParamSet;
import com.whaleal.quant.optimization.param.ParamSpace;
import com.whaleal.quant.optimization.result.OptimizationResult;
import com.whaleal.quant.optimization.scorer.StrategyScorer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 策略优化器
 * 用于执行策略参数搜索和优化
 *
 * @author whaleal
 * @version 1.0.0
 */
public class StrategyOptimizer {

    private final ParamSpace paramSpace;
    private final StrategyScorer scorer;
    private final Function<ParamSet, StrategyEngine> strategyFactory;
    private final BacktestDataProvider dataProvider;
    private final BacktestConfig backtestConfig;

    private int parallelThreads;
    private boolean parallelEnabled;

    /**
     * 构造方法
     * @param paramSpace 参数空间
     * @param scorer 评分器
     * @param strategyFactory 策略工厂函数
     * @param dataProvider 数据提供者
     * @param backtestConfig 回测配置
     */
    public StrategyOptimizer(ParamSpace paramSpace, StrategyScorer scorer, Function<ParamSet, StrategyEngine> strategyFactory, BacktestDataProvider dataProvider, BacktestConfig backtestConfig) {
        this.paramSpace = paramSpace;
        this.scorer = scorer;
        this.strategyFactory = strategyFactory;
        this.dataProvider = dataProvider;
        this.backtestConfig = backtestConfig;
        this.parallelThreads = Runtime.getRuntime().availableProcessors();
        this.parallelEnabled = true;
    }

    /**
     * 设置并行线程数
     * @param parallelThreads 并行线程数
     * @return 策略优化器
     */
    public StrategyOptimizer setParallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
        return this;
    }

    /**
     * 设置是否启用并行优化
     * @param parallelEnabled 是否启用并行优化
     * @return 策略优化器
     */
    public StrategyOptimizer setParallelEnabled(boolean parallelEnabled) {
        this.parallelEnabled = parallelEnabled;
        return this;
    }

    /**
     * 执行优化
     * @return 优化结果
     */
    public OptimizationResult optimize() {
        List<ParamSet> paramSets = paramSpace.generateParamSets();
        List<OptimizationCandidate> candidates = new ArrayList<>();

        if (parallelEnabled && paramSets.size() > 1) {
            // 并行执行优化
            candidates = executeParallelOptimization(paramSets);
        } else {
            // 串行执行优化
            candidates = executeSerialOptimization(paramSets);
        }

        // 按评分排序
        candidates.sort(Comparator.comparingDouble(OptimizationCandidate::getScore).reversed());

        // 创建优化结果
        return new OptimizationResult(candidates);
    }

    /**
     * 串行执行优化
     * @param paramSets 参数组合列表
     * @return 优化候选列表
     */
    private List<OptimizationCandidate> executeSerialOptimization(List<ParamSet> paramSets) {
        List<OptimizationCandidate> candidates = new ArrayList<>();

        for (ParamSet paramSet : paramSets) {
            OptimizationCandidate candidate = evaluateParamSet(paramSet);
            candidates.add(candidate);
        }

        return candidates;
    }

    /**
     * 并行执行优化
     * @param paramSets 参数组合列表
     * @return 优化候选列表
     */
    private List<OptimizationCandidate> executeParallelOptimization(List<ParamSet> paramSets) {
        List<OptimizationCandidate> candidates = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(parallelThreads);
        List<Future<OptimizationCandidate>> futures = new ArrayList<>();

        try {
            // 提交所有任务
            for (ParamSet paramSet : paramSets) {
                Future<OptimizationCandidate> future = executor.submit(() -> evaluateParamSet(paramSet));
                futures.add(future);
            }

            // 收集结果
            for (Future<OptimizationCandidate> future : futures) {
                try {
                    OptimizationCandidate candidate = future.get();
                    candidates.add(candidate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return candidates;
    }

    /**
     * 评估参数组合
     * @param paramSet 参数组合
     * @return 优化候选
     */
    private OptimizationCandidate evaluateParamSet(ParamSet paramSet) {
        try {
            // 使用参数创建策略引擎
            StrategyEngine strategyEngine = strategyFactory.apply(paramSet);

            // 创建回测引擎
            BacktestEngine backtestEngine = BacktestEngine.builder()
                    .config(backtestConfig)
                    .dataProvider(dataProvider)
                    .strategyEngine(strategyEngine)
                    .build();

            // 运行回测
            BacktestResult result = backtestEngine.run();

            // 计算评分
            double score = scorer.score(result);

            // 创建优化候选
            return new OptimizationCandidate(paramSet, result, score);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果回测失败，返回评分很低的候选
            return new OptimizationCandidate(paramSet, null, 0.0);
        }
    }

    /**
     * 优化候选
     * 用于存储参数组合、回测结果和评分
     */
    public static class OptimizationCandidate {
        private final ParamSet paramSet;
        private final BacktestResult result;
        private final double score;

        public OptimizationCandidate(ParamSet paramSet, BacktestResult result, double score) {
            this.paramSet = paramSet;
            this.result = result;
            this.score = score;
        }

        public ParamSet getParamSet() {
            return paramSet;
        }

        public BacktestResult getResult() {
            return result;
        }

        public double getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "OptimizationCandidate{" +
                    "paramSet=" + paramSet +
                    ", score=" + score +
                    '}';
        }
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private ParamSpace paramSpace;
        private StrategyScorer scorer;
        private Function<ParamSet, StrategyEngine> strategyFactory;
        private BacktestDataProvider dataProvider;
        private BacktestConfig backtestConfig;
        private int parallelThreads;
        private boolean parallelEnabled;

        public Builder paramSpace(ParamSpace paramSpace) {
            this.paramSpace = paramSpace;
            return this;
        }

        public Builder scorer(StrategyScorer scorer) {
            this.scorer = scorer;
            return this;
        }

        public Builder strategyFactory(Function<ParamSet, StrategyEngine> strategyFactory) {
            this.strategyFactory = strategyFactory;
            return this;
        }

        public Builder dataProvider(BacktestDataProvider dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }

        public Builder backtestConfig(BacktestConfig backtestConfig) {
            this.backtestConfig = backtestConfig;
            return this;
        }

        public Builder parallelThreads(int parallelThreads) {
            this.parallelThreads = parallelThreads;
            return this;
        }

        public Builder parallelEnabled(boolean parallelEnabled) {
            this.parallelEnabled = parallelEnabled;
            return this;
        }

        public StrategyOptimizer build() {
            if (paramSpace == null) {
                throw new IllegalArgumentException("Param space is required");
            }
            if (scorer == null) {
                throw new IllegalArgumentException("Scorer is required");
            }
            if (strategyFactory == null) {
                throw new IllegalArgumentException("Strategy factory is required");
            }
            if (dataProvider == null) {
                throw new IllegalArgumentException("Data provider is required");
            }
            if (backtestConfig == null) {
                backtestConfig = new BacktestConfig();
            }

            StrategyOptimizer optimizer = new StrategyOptimizer(paramSpace, scorer, strategyFactory, dataProvider, backtestConfig);
            if (parallelThreads > 0) {
                optimizer.setParallelThreads(parallelThreads);
            }
            if (parallelEnabled) {
                optimizer.setParallelEnabled(parallelEnabled);
            }

            return optimizer;
        }
    }

    /**
     * 创建 Builder
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}