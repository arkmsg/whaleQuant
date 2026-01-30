package com.whaleal.quant.engine.risk.rule;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.engine.risk.config.RiskConfig;
import com.whaleal.quant.engine.risk.exception.RiskException;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仓位集中度规则
 * 用于限制单个标的的仓位占比，防止过度集中投资
 *
 * @author whaleal
 * @version 1.0.0
 */
public class PositionConcentrationRule implements RiskRule {

    private final BigDecimal maxConcentrationPercentage;
    private final List<Position> allPositions;

    /**
     * 构造方法
     * @param maxConcentrationPercentage 最大集中度百分比
     * @param allPositions 所有持仓列表
     */
    public PositionConcentrationRule(double maxConcentrationPercentage, List<Position> allPositions) {
        this.maxConcentrationPercentage = BigDecimal.valueOf(maxConcentrationPercentage);
        this.allPositions = allPositions;
    }

    /**
     * 构造方法
     * 使用默认最大集中度 0.3 (30%)
     * @param allPositions 所有持仓列表
     */
    public PositionConcentrationRule(List<Position> allPositions) {
        this(0.3, allPositions);
    }

    @Override
    public void checkOrder(Order order, RiskConfig config) {
        // 计算当前总持仓价值
        BigDecimal totalPositionValue = calculateTotalPositionValue();

        // 计算订单执行后的新持仓价值
        BigDecimal orderValue = order.getPrice().multiply(order.getQuantity());
        BigDecimal newTotalValue = totalPositionValue.add(orderValue);

        // 检查是否超过总仓位限制
        if (config.getMaxTotalPositionValue() != null &&
            newTotalValue.compareTo(config.getMaxTotalPositionValue()) > 0) {
            throw new RiskException("Total position value exceeded: " + newTotalValue +
                                   ", max allowed: " + config.getMaxTotalPositionValue());
        }

        // 检查单个标的集中度
        if (newTotalValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal concentrationPercentage = orderValue.divide(newTotalValue, 4, BigDecimal.ROUND_HALF_UP);
            if (concentrationPercentage.compareTo(maxConcentrationPercentage) > 0) {
                throw new RiskException("Position concentration exceeded: " + concentrationPercentage.multiply(BigDecimal.valueOf(100)) + "%" +
                                       ", max allowed: " + maxConcentrationPercentage.multiply(BigDecimal.valueOf(100)) + "%");
            }
        }
    }

    @Override
    public void checkPosition(Position position, RiskConfig config) {
        // 计算当前总持仓价值
        BigDecimal totalPositionValue = calculateTotalPositionValue();

        // 计算当前持仓的价值
        BigDecimal positionValue = position.getCurrentPrice().multiply(position.getQuantity());

        // 检查单个标的集中度
        if (totalPositionValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal concentrationPercentage = positionValue.divide(totalPositionValue, 4, BigDecimal.ROUND_HALF_UP);
            if (concentrationPercentage.compareTo(maxConcentrationPercentage) > 0) {
                throw new RiskException("Position concentration exceeded: " + concentrationPercentage.multiply(BigDecimal.valueOf(100)) + "%" +
                                       ", max allowed: " + maxConcentrationPercentage.multiply(BigDecimal.valueOf(100)) + "%");
            }
        }
    }

    /**
     * 计算总持仓价值
     * @return 总持仓价值
     */
    private BigDecimal calculateTotalPositionValue() {
        return allPositions.stream()
                .map(position -> position.getCurrentPrice().multiply(position.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
