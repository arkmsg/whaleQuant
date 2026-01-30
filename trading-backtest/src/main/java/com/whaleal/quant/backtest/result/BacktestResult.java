package com.whaleal.quant.backtest.result;

import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.engine.trading.model.trading.Order;
import com.whaleal.quant.engine.trading.model.trading.Position;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回测结果
 * 用于存储和展示回测的结果数据
 *
 * @author whaleal
 * @version 1.0.0
 */
public class BacktestResult {

    /**
     * 回测配置
     */
    private final BacktestConfig config;

    /**
     * 订单映射
     */
    private final Map<String, List<Order>> orders;

    /**
     * 持仓映射
     */
    private final Map<String, List<Position>> positions;

    /**
     * 盈亏映射
     */
    private final Map<String, Double> pnl;

    /**
     * 性能指标映射
     */
    private final Map<String, Double> metrics;

    /**
     * 回测开始日期
     */
    private final LocalDateTime startDate;

    /**
     * 回测结束日期
     */
    private final LocalDateTime endDate;

    /**
     * 交易次数
     */
    private final int tradeCount;

    /**
     * 获胜次数
     */
    private final int winCount;

    /**
     * 失败次数
     */
    private final int lossCount;

    /**
     * 构造方法
     * @param builder Builder
     */
    private BacktestResult(Builder builder) {
        this.config = builder.config;
        this.orders = builder.orders;
        this.positions = builder.positions;
        this.pnl = builder.pnl;
        this.metrics = builder.metrics;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.tradeCount = builder.tradeCount;
        this.winCount = builder.winCount;
        this.lossCount = builder.lossCount;
    }

    /**
     * 获取总收益率
     * @return 总收益率
     */
    public double getTotalReturn() {
        return metrics.getOrDefault("totalReturn", 0.0);
    }

    /**
     * 获取年化收益率
     * @return 年化收益率
     */
    public double getAnnualizedReturn() {
        return metrics.getOrDefault("annualizedReturn", 0.0);
    }

    /**
     * 获取夏普比率
     * @return 夏普比率
     */
    public double getSharpeRatio() {
        return metrics.getOrDefault("sharpeRatio", 0.0);
    }

    /**
     * 获取最大回撤
     * @return 最大回撤
     */
    public double getMaxDrawdown() {
        return metrics.getOrDefault("maxDrawdown", 0.0);
    }

    /**
     * 获取胜率
     * @return 胜率
     */
    public double getWinRate() {
        return tradeCount > 0 ? (double) winCount / tradeCount : 0.0;
    }

    /**
     * 获取平均盈亏比
     * @return 平均盈亏比
     */
    public double getProfitLossRatio() {
        return metrics.getOrDefault("profitLossRatio", 0.0);
    }

    /**
     * 生成回测报告
     * @return 回测报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 回测报告 ===\n");
        report.append("回测期间: " + startDate + " 到 " + endDate + "\n");
        report.append("初始资金: " + config.getInitialCapital() + "\n");
        report.append("最终资金: " + (config.getInitialCapital() * (1 + getTotalReturn())) + "\n");
        report.append("总收益率: " + (getTotalReturn() * 100) + "%\n");
        report.append("年化收益率: " + (getAnnualizedReturn() * 100) + "%\n");
        report.append("夏普比率: " + getSharpeRatio() + "\n");
        report.append("最大回撤: " + (getMaxDrawdown() * 100) + "%\n");
        report.append("交易次数: " + tradeCount + "\n");
        report.append("获胜次数: " + winCount + "\n");
        report.append("失败次数: " + lossCount + "\n");
        report.append("胜率: " + (getWinRate() * 100) + "%\n");
        report.append("平均盈亏比: " + getProfitLossRatio() + "\n");
        report.append("================\n");
        return report.toString();
    }

    // Getters

    public BacktestConfig getConfig() {
        return config;
    }

    public Map<String, List<Order>> getOrders() {
        return orders;
    }

    public Map<String, List<Position>> getPositions() {
        return positions;
    }

    public Map<String, Double> getPnl() {
        return pnl;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
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

    /**
     * Builder 类
     */
    public static class Builder {
        private BacktestConfig config;
        private Map<String, List<Order>> orders;
        private Map<String, List<Position>> positions;
        private Map<String, Double> pnl;
        private Map<String, Double> metrics;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int tradeCount;
        private int winCount;
        private int lossCount;

        public Builder() {
            this.orders = new HashMap<>();
            this.positions = new HashMap<>();
            this.pnl = new HashMap<>();
            this.metrics = new HashMap<>();
            this.tradeCount = 0;
            this.winCount = 0;
            this.lossCount = 0;
        }

        public Builder config(BacktestConfig config) {
            this.config = config;
            return this;
        }

        public Builder orders(Map<String, List<Order>> orders) {
            this.orders = orders;
            return this;
        }

        public Builder positions(Map<String, List<Position>> positions) {
            this.positions = positions;
            return this;
        }

        public Builder pnl(Map<String, Double> pnl) {
            this.pnl = pnl;
            return this;
        }

        public Builder metrics(Map<String, Double> metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder tradeCount(int tradeCount) {
            this.tradeCount = tradeCount;
            return this;
        }

        public Builder winCount(int winCount) {
            this.winCount = winCount;
            return this;
        }

        public Builder lossCount(int lossCount) {
            this.lossCount = lossCount;
            return this;
        }

        public BacktestResult build() {
            if (config == null) {
                throw new IllegalArgumentException("Config is required");
            }
            if (startDate == null) {
                startDate = config.getStartDate();
            }
            if (endDate == null) {
                endDate = config.getEndDate();
            }
            return new BacktestResult(this);
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