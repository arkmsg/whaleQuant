package com.whaleal.quant.backtest.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 回测配置
 * 用于配置回测的各种参数
 *
 * @author whaleal
 * @version 1.0.0
 */
public class BacktestConfig {

    /**
     * 回测起始日期
     */
    private LocalDateTime startDate;

    /**
     * 回测结束日期
     */
    private LocalDateTime endDate;

    /**
     * 回测股票列表
     */
    private Set<String> symbols;

    /**
     * 初始资金
     */
    private double initialCapital;

    /**
     * 每笔交易的固定手续费
     */
    private double fixedCommission;

    /**
     * 手续费率
     */
    private double commissionRate;

    /**
     * 滑点百分比
     */
    private double slippageRate;

    /**
     * 最大持仓数量
     */
    private int maxPositions;

    /**
     * 单个股票最大仓位比例
     */
    private double maxPositionWeight;

    /**
     * 是否启用并行回测
     */
    private boolean parallelEnabled;

    /**
     * 并行线程数
     */
    private int parallelThreads;

    /**
     * 构造方法
     */
    public BacktestConfig() {
        this.startDate = LocalDateTime.now().minusYears(1);
        this.endDate = LocalDateTime.now();
        this.symbols = new HashSet<>();
        this.initialCapital = 100000.0;
        this.fixedCommission = 0.0;
        this.commissionRate = 0.0003;
        this.slippageRate = 0.001;
        this.maxPositions = 10;
        this.maxPositionWeight = 0.2;
        this.parallelEnabled = true;
        this.parallelThreads = Runtime.getRuntime().availableProcessors();
    }

    // Getters and setters

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public BacktestConfig setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public BacktestConfig setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    public BacktestConfig setSymbols(Set<String> symbols) {
        this.symbols = symbols;
        return this;
    }

    public BacktestConfig addSymbol(String symbol) {
        this.symbols.add(symbol);
        return this;
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public BacktestConfig setInitialCapital(double initialCapital) {
        this.initialCapital = initialCapital;
        return this;
    }

    public double getFixedCommission() {
        return fixedCommission;
    }

    public BacktestConfig setFixedCommission(double fixedCommission) {
        this.fixedCommission = fixedCommission;
        return this;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public BacktestConfig setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
        return this;
    }

    public double getSlippageRate() {
        return slippageRate;
    }

    public BacktestConfig setSlippageRate(double slippageRate) {
        this.slippageRate = slippageRate;
        return this;
    }

    public int getMaxPositions() {
        return maxPositions;
    }

    public BacktestConfig setMaxPositions(int maxPositions) {
        this.maxPositions = maxPositions;
        return this;
    }

    public double getMaxPositionWeight() {
        return maxPositionWeight;
    }

    public BacktestConfig setMaxPositionWeight(double maxPositionWeight) {
        this.maxPositionWeight = maxPositionWeight;
        return this;
    }

    public boolean isParallelEnabled() {
        return parallelEnabled;
    }

    public BacktestConfig setParallelEnabled(boolean parallelEnabled) {
        this.parallelEnabled = parallelEnabled;
        return this;
    }

    public int getParallelThreads() {
        return parallelThreads;
    }

    public BacktestConfig setParallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
        return this;
    }
}