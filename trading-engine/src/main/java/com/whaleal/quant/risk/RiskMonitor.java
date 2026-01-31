package com.whaleal.quant.risk;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.RiskException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 风险监控器
 * 用于实时监控风险指标并生成风险报告
 *
 * @author whaleal
 * @version 1.0.0
 */
public class RiskMonitor {

    private final RiskManager riskManager;
    private final RiskConfig config;
    private final List<Position> positions;

    private final Map<String, RiskMetric> riskMetrics;
    private final Map<String, LocalDateTime> lastRiskCheckTime;

    /**
     * 构造方法
     * @param riskManager 风险管理器
     * @param config 风险配置
     * @param positions 持仓列表
     */
    public RiskMonitor(RiskManager riskManager, RiskConfig config, List<Position> positions) {
        this.riskManager = riskManager;
        this.config = config;
        this.positions = positions;
        this.riskMetrics = new ConcurrentHashMap<>();
        this.lastRiskCheckTime = new ConcurrentHashMap<>();
    }

    /**
     * 监控订单风险
     * @param order 订单
     * @return 风险状态
     */
    public RiskStatus monitorOrder(Order order) {
        try {
            riskManager.checkOrder(order);
            updateRiskMetrics(order.getSymbol(), "ORDER_CHECK", true, null);
            return RiskStatus.OK;
        } catch (RiskException e) {
            updateRiskMetrics(order.getSymbol(), "ORDER_CHECK", false, e.getMessage());
            return RiskStatus.VIOLATION;
        }
    }

    /**
     * 监控持仓风险
     * @param position 持仓
     * @return 风险状态
     */
    public RiskStatus monitorPosition(Position position) {
        try {
            riskManager.checkPosition(position);
            updateRiskMetrics(position.getSymbol(), "POSITION_CHECK", true, null);
            return RiskStatus.OK;
        } catch (RiskException e) {
            updateRiskMetrics(position.getSymbol(), "POSITION_CHECK", false, e.getMessage());
            return RiskStatus.VIOLATION;
        }
    }

    /**
     * 监控所有持仓风险
     * @return 风险状态
     */
    public RiskStatus monitorAllPositions() {
        RiskStatus overallStatus = RiskStatus.OK;

        for (Position position : positions) {
            RiskStatus status = monitorPosition(position);
            if (status == RiskStatus.VIOLATION) {
                overallStatus = RiskStatus.VIOLATION;
            }
        }

        return overallStatus;
    }

    /**
     * 生成风险报告
     * @return 风险报告
     */
    public String generateRiskReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 风险监控报告 ===\n");
        report.append("报告生成时间: " + LocalDateTime.now() + "\n");
        report.append("总持仓数量: " + positions.size() + "\n");
        report.append("\n风险指标:\n");

        for (Map.Entry<String, RiskMetric> entry : riskMetrics.entrySet()) {
            String symbol = entry.getKey();
            RiskMetric metric = entry.getValue();
            report.append(symbol + ": " + metric.getStatus() + ", 最后检查: " + metric.getLastCheckTime() + "\n");
            if (!metric.isHealthy()) {
                report.append("  风险原因: " + metric.getViolationReason() + "\n");
            }
        }

        report.append("\n风险配置:\n");
        report.append("最大单笔订单金额: " + config.getMaxOrderAmount() + "\n");
        report.append("最大总持仓价值: " + config.getMaxTotalPositionValue() + "\n");
        report.append("最大单个持仓价值: " + config.getMaxSinglePositionValue() + "\n");
        report.append("最大价格偏离度: " + config.getPriceDeviationThreshold() + "\n");
        report.append("最大回撤限制: " + config.getMaxDrawdownThreshold() + "\n");
        report.append("单日最大亏损: " + config.getMaxDailyLoss() + "\n");
        report.append("单日最大交易次数: " + config.getMaxDailyTrades() + "\n");

        report.append("================\n");
        return report.toString();
    }

    /**
     * 更新风险指标
     * @param symbol 交易对
     * @param checkType 检查类型
     * @param healthy 是否健康
     * @param violationReason 违规原因
     */
    private void updateRiskMetrics(String symbol, String checkType, boolean healthy, String violationReason) {
        RiskMetric metric = riskMetrics.computeIfAbsent(symbol, k -> new RiskMetric());
        metric.update(healthy, violationReason);
        lastRiskCheckTime.put(symbol, LocalDateTime.now());
    }

    /**
     * 风险状态
     */
    public enum RiskStatus {
        OK,            // 正常
        WARNING,       // 警告
        VIOLATION      // 违规
    }

    /**
     * 风险指标
     */
    private static class RiskMetric {
        private boolean healthy;
        private String violationReason;
        private LocalDateTime lastCheckTime;

        public RiskMetric() {
            this.healthy = true;
            this.violationReason = null;
            this.lastCheckTime = LocalDateTime.now();
        }

        public void update(boolean healthy, String violationReason) {
            this.healthy = healthy;
            this.violationReason = violationReason;
            this.lastCheckTime = LocalDateTime.now();
        }

        public boolean isHealthy() {
            return healthy;
        }

        public String getViolationReason() {
            return violationReason;
        }

        public LocalDateTime getLastCheckTime() {
            return lastCheckTime;
        }

        public String getStatus() {
            return healthy ? "健康" : "违规";
        }
    }

    /**
     * 获取风险管理器
     * @return 风险管理器
     */
    public RiskManager getRiskManager() {
        return riskManager;
    }

    /**
     * 获取风险配置
     * @return 风险配置
     */
    public RiskConfig getConfig() {
        return config;
    }

    /**
     * 获取持仓列表
     * @return 持仓列表
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * 获取风险指标
     * @return 风险指标
     */
    public Map<String, RiskMetric> getRiskMetrics() {
        return riskMetrics;
    }
}
