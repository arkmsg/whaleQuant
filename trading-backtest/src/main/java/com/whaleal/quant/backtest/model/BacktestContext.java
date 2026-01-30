package com.whaleal.quant.backtest.model;

import com.whaleal.quant.engine.trading.model.trading.Order;
import com.whaleal.quant.engine.trading.model.trading.Position;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 回测上下文
 * 用于管理回测过程中的状态和数据
 *
 * @author whaleal
 * @version 1.0.0
 */
public class BacktestContext {

    /**
     * 回测配置
     */
    private final BacktestConfig config;

    /**
     * 当前资金
     */
    private double currentCapital;

    /**
     * 订单映射
     */
    private final Map<String, List<Order>> orders;

    /**
     * 持仓映射
     */
    private final Map<String, Position> currentPositions;

    /**
     * 历史持仓映射
     */
    private final Map<String, List<Position>> positions;

    /**
     * 盈亏映射
     */
    private final Map<String, Double> pnl;

    /**
     * 每日盈亏
     */
    private final Map<LocalDateTime, Double> dailyPnl;

    /**
     * 性能指标映射
     */
    private final Map<String, Double> metrics;

    /**
     * 交易次数
     */
    private int tradeCount;

    /**
     * 获胜次数
     */
    private int winCount;

    /**
     * 失败次数
     */
    private int lossCount;

    /**
     * 构造方法
     * @param config 回测配置
     */
    public BacktestContext(BacktestConfig config) {
        this.config = config;
        this.currentCapital = config.getInitialCapital();
        this.orders = new HashMap<>();
        this.currentPositions = new HashMap<>();
        this.positions = new HashMap<>();
        this.pnl = new HashMap<>();
        this.dailyPnl = new HashMap<>();
        this.metrics = new HashMap<>();
        this.tradeCount = 0;
        this.winCount = 0;
        this.lossCount = 0;
    }

    /**
     * 更新每日统计信息
     * @param date 日期
     */
    public void updateDailyStats(LocalDateTime date) {
        // 计算当日盈亏
        double dailyProfit = calculateDailyPnl(date);
        dailyPnl.put(date, dailyProfit);

        // 更新总盈亏
        updateTotalPnl();

        // 计算性能指标
        calculateMetrics();
    }

    /**
     * 计算当日盈亏
     * @param date 日期
     * @return 当日盈亏
     */
    private double calculateDailyPnl(LocalDateTime date) {
        // 实现当日盈亏计算逻辑
        // 这里需要根据实际的持仓和价格变化来计算
        return 0.0;
    }

    /**
     * 更新总盈亏
     */
    private void updateTotalPnl() {
        // 实现总盈亏计算逻辑
    }

    /**
     * 计算性能指标
     */
    private void calculateMetrics() {
        // 实现性能指标计算逻辑
        // 包括收益率、夏普比率、最大回撤等
    }

    /**
     * 添加订单
     * @param symbol 交易对符号
     * @param order 订单
     */
    public void addOrder(String symbol, Order order) {
        orders.computeIfAbsent(symbol, k -> new LinkedList<>()).add(order);
        tradeCount++;
    }

    /**
     * 更新持仓
     * @param symbol 交易对符号
     * @param position 持仓
     */
    public void updatePosition(String symbol, Position position) {
        currentPositions.put(symbol, position);
        positions.computeIfAbsent(symbol, k -> new LinkedList<>()).add(position);
    }

    /**
     * 移除持仓
     * @param symbol 交易对符号
     */
    public void removePosition(String symbol) {
        currentPositions.remove(symbol);
    }

    /**
     * 更新盈亏
     * @param symbol 交易对符号
     * @param profit 盈亏
     */
    public void updatePnl(String symbol, double profit) {
        pnl.put(symbol, pnl.getOrDefault(symbol, 0.0) + profit);
        if (profit > 0) {
            winCount++;
        } else if (profit < 0) {
            lossCount++;
        }
    }

    /**
     * 更新资金
     * @param amount 金额变化
     */
    public void updateCapital(double amount) {
        currentCapital += amount;
    }

    // Getters

    public BacktestConfig getConfig() {
        return config;
    }

    public double getCurrentCapital() {
        return currentCapital;
    }

    public Map<String, List<Order>> getOrders() {
        return orders;
    }

    public Map<String, Position> getCurrentPositions() {
        return currentPositions;
    }

    public Map<String, List<Position>> getPositions() {
        return positions;
    }

    public Map<String, Double> getPnL() {
        return pnl;
    }

    public Map<LocalDateTime, Double> getDailyPnl() {
        return dailyPnl;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public double getWinRate() {
        return tradeCount > 0 ? (double) winCount / tradeCount : 0.0;
    }

    public double getTotalPnL() {
        return pnl.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalReturn() {
        return (currentCapital - config.getInitialCapital()) / config.getInitialCapital();
    }
}