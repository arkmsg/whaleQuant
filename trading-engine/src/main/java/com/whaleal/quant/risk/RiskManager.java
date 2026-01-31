package com.whaleal.quant.risk;

import com.whaleal.quant.model.trading.Order;
import com.whaleal.quant.model.trading.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.RiskException;
import com.whaleal.quant.risk.rule.RiskRule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RiskManager {
    private final List<RiskRule> rules;
    private final RiskConfig config;

    // 虚拟持仓管理：跟踪在途订单
    private final Map<String, BigDecimal> pendingBuyQuantity = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> pendingSellQuantity = new ConcurrentHashMap<>();

    // 风险状态管理
    private boolean circuitBroken;
    private String circuitBreakReason;
    private LocalDateTime circuitBreakTime;

    // 风险指标
    private final Map<String, RiskIndicator> riskIndicators;

    public RiskManager(RiskConfig config) {
        this.config = config;
        this.rules = new ArrayList<>();
        this.circuitBroken = false;
        this.riskIndicators = new ConcurrentHashMap<>();
    }

    public void addRule(RiskRule rule) {
        rules.add(rule);
    }

    public void removeRule(RiskRule rule) {
        rules.remove(rule);
    }

    /**
     * 事前风控：检查订单是否符合风控规则
     * @param order 订单对象
     * @throws RiskException 风控检查失败时抛出异常
     */
    public void checkOrder(Order order) throws RiskException {
        // 检查熔断状态
        if (circuitBroken) {
            throw new RiskException("System is in circuit break state: " + circuitBreakReason);
        }

        // 检查基本风控规则
        for (RiskRule rule : rules) {
            rule.checkOrder(order, config);
        }

        // 检查虚拟持仓，防止超买超卖
        checkVirtualPosition(order);

        // 更新风险指标
        updateRiskIndicators(order.getSymbol());
    }

    /**
     * 检查虚拟持仓，防止超买超卖
     * @param order 订单对象
     * @throws RiskException 虚拟持仓检查失败时抛出异常
     */
    private void checkVirtualPosition(Order order) throws RiskException {
        String symbol = order.getSymbol();
        BigDecimal orderQuantity = order.getQuantity();

        if ("BUY".equals(order.getSide())) {
            // 买入：检查是否超过最大持仓限制
            BigDecimal pendingBuy = pendingBuyQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            BigDecimal totalBuy = pendingBuy.add(orderQuantity);

            if (config.getMaxSinglePositionValue() != null) {
                // 检查单个持仓价值限制
                BigDecimal positionValue = totalBuy.multiply(order.getPrice());
                if (positionValue.compareTo(config.getMaxSinglePositionValue()) > 0) {
                    throw new RiskException("Single position value exceeded: " + positionValue + ", max allowed: " + config.getMaxSinglePositionValue());
                }
            }
        } else if ("SELL".equals(order.getSide())) {
            // 卖出：检查虚拟持仓是否足够
            BigDecimal pendingSell = pendingSellQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            BigDecimal totalSell = pendingSell.add(orderQuantity);

            // 这里可以添加具体的卖出限制检查
        }
    }

    /**
     * 事中风控：检查持仓是否符合风控规则
     * @param position 持仓对象
     * @throws RiskException 风控检查失败时抛出异常
     */
    public void checkPosition(Position position) throws RiskException {
        // 检查熔断状态
        if (circuitBroken) {
            throw new RiskException("System is in circuit break state: " + circuitBreakReason);
        }

        for (RiskRule rule : rules) {
            rule.checkPosition(position, config);
        }

        // 更新风险指标
        updateRiskIndicators(position.getSymbol());
    }

    /**
     * 批量检查所有持仓
     * @param positions 持仓列表
     * @throws RiskException 风控检查失败时抛出异常
     */
    public void checkAllPositions(List<Position> positions) throws RiskException {
        for (Position position : positions) {
            checkPosition(position);
        }
    }

    /**
     * 更新在途订单数量
     * @param order 订单对象
     */
    public void updatePendingQuantity(Order order) {
        String symbol = order.getSymbol();
        BigDecimal quantity = order.getQuantity();

        if ("BUY".equals(order.getSide())) {
            BigDecimal pendingBuy = pendingBuyQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            pendingBuyQuantity.put(symbol, pendingBuy.add(quantity));
        } else if ("SELL".equals(order.getSide())) {
            BigDecimal pendingSell = pendingSellQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            pendingSellQuantity.put(symbol, pendingSell.add(quantity));
        }

        // 更新风险指标
        updateRiskIndicators(symbol);
    }

    /**
     * 释放在途订单数量
     * @param order 订单对象
     */
    public void releasePendingQuantity(Order order) {
        String symbol = order.getSymbol();
        BigDecimal quantity = order.getQuantity();

        if ("BUY".equals(order.getSide())) {
            BigDecimal pendingBuy = pendingBuyQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            BigDecimal newPendingBuy = pendingBuy.subtract(quantity);
            if (newPendingBuy.compareTo(BigDecimal.ZERO) <= 0) {
                pendingBuyQuantity.remove(symbol);
            } else {
                pendingBuyQuantity.put(symbol, newPendingBuy);
            }
        } else if ("SELL".equals(order.getSide())) {
            BigDecimal pendingSell = pendingSellQuantity.getOrDefault(symbol, BigDecimal.ZERO);
            BigDecimal newPendingSell = pendingSell.subtract(quantity);
            if (newPendingSell.compareTo(BigDecimal.ZERO) <= 0) {
                pendingSellQuantity.remove(symbol);
            } else {
                pendingSellQuantity.put(symbol, newPendingSell);
            }
        }

        // 更新风险指标
        updateRiskIndicators(symbol);
    }

    /**
     * 获取虚拟持仓数量
     * @param symbol 交易对/股票代码
     * @return 虚拟持仓数量
     */
    public BigDecimal getVirtualPosition(String symbol) {
        // 这里需要结合实际持仓和在途订单计算虚拟持仓
        // 暂时返回在途订单的净数量
        BigDecimal pendingBuy = pendingBuyQuantity.getOrDefault(symbol, BigDecimal.ZERO);
        BigDecimal pendingSell = pendingSellQuantity.getOrDefault(symbol, BigDecimal.ZERO);
        return pendingBuy.subtract(pendingSell);
    }

    /**
     * 计算总风险指标
     * @return 总风险评分，范围 0-100，值越大风险越高
     */
    public int calculateTotalRiskScore() {
        int riskScore = 0;

        // 检查熔断状态
        if (circuitBroken) {
            return 100;
        }

        // 检查在途订单风险
        for (Map.Entry<String, BigDecimal> entry : pendingBuyQuantity.entrySet()) {
            String symbol = entry.getKey();
            BigDecimal quantity = entry.getValue();
            // 简单计算风险评分
            if (quantity.compareTo(BigDecimal.valueOf(1000)) > 0) {
                riskScore += 10;
            }
        }

        // 检查规则数量
        if (rules.isEmpty()) {
            riskScore += 20;
        }

        // 限制风险评分范围
        return Math.min(100, Math.max(0, riskScore));
    }

    /**
     * 更新风险指标
     * @param symbol 交易对
     */
    private void updateRiskIndicators(String symbol) {
        RiskIndicator indicator = riskIndicators.computeIfAbsent(symbol, k -> new RiskIndicator());
        indicator.update();
    }

    /**
     * 获取风险指标
     * @param symbol 交易对
     * @return 风险指标
     */
    public RiskIndicator getRiskIndicator(String symbol) {
        return riskIndicators.get(symbol);
    }

    /**
     * 触发风控熔断
     * @param reasonCode 熔断原因代码
     * @param reasonMessage 熔断原因描述
     */
    public void triggerCircuitBreak(String reasonCode, String reasonMessage) {
        // 实现熔断逻辑
        // 1. 记录熔断事件
        System.err.println("[风控熔断] 原因代码: " + reasonCode + ", 原因: " + reasonMessage);

        // 2. 设置熔断状态
        this.circuitBroken = true;
        this.circuitBreakReason = reasonMessage;
        this.circuitBreakTime = LocalDateTime.now();

        // 3. 可以在这里添加具体的熔断操作，例如：
        // - 停止接受新订单
        // - 取消所有未成交订单
        // - 通知策略引擎进入安全模式

        // 4. 可以考虑将熔断状态持久化，以便系统恢复后能够知道历史熔断情况
    }

    /**
     * 恢复风控熔断
     */
    public void recoverFromCircuitBreak() {
        this.circuitBroken = false;
        this.circuitBreakReason = null;
        this.circuitBreakTime = null;
        System.out.println("[风控恢复] 系统已从熔断状态恢复");
    }

    /**
     * 检查是否处于熔断状态
     * @return 是否处于熔断状态
     */
    public boolean isCircuitBroken() {
        return circuitBroken;
    }

    /**
     * 获取熔断原因
     * @return 熔断原因
     */
    public String getCircuitBreakReason() {
        return circuitBreakReason;
    }

    /**
     * 获取熔断时间
     * @return 熔断时间
     */
    public LocalDateTime getCircuitBreakTime() {
        return circuitBreakTime;
    }

    public RiskConfig getConfig() {
        return config;
    }

    public List<RiskRule> getRules() {
        return List.copyOf(rules);
    }

    /**
     * 风险指标类
     */
    public static class RiskIndicator {
        private LocalDateTime lastUpdateTime;
        private int riskLevel;
        private boolean isHealthy;

        public RiskIndicator() {
            this.lastUpdateTime = LocalDateTime.now();
            this.riskLevel = 0;
            this.isHealthy = true;
        }

        public void update() {
            this.lastUpdateTime = LocalDateTime.now();
            // 简单更新逻辑，实际应该根据具体风险指标计算
            this.riskLevel = (int) (Math.random() * 100);
            this.isHealthy = riskLevel < 70;
        }

        public LocalDateTime getLastUpdateTime() {
            return lastUpdateTime;
        }

        public int getRiskLevel() {
            return riskLevel;
        }

        public boolean isHealthy() {
            return isHealthy;
        }

        @Override
        public String toString() {
            return "RiskIndicator{" +
                    "lastUpdateTime=" + lastUpdateTime +
                    ", riskLevel=" + riskLevel +
                    ", isHealthy=" + isHealthy +
                    '}';
        }
    }
}
