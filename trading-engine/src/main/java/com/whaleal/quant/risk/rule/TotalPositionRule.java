package com.whaleal.quant.risk.rule;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.TotalPositionExceededException;
import java.math.BigDecimal;
import java.util.List;

public class TotalPositionRule implements RiskRule {
    private final List<Position> positions;

    public TotalPositionRule(List<Position> positions) {
        this.positions = positions;
    }

    @Override
    public void checkOrder(Order order, RiskConfig config) {
        if (config.getMaxTotalPositionValue() != null) {
            BigDecimal totalPositionValue = calculateTotalPositionValue();
            // 计算订单执行后的总仓位价值
            BigDecimal orderValue = order.getPrice().multiply(order.getQuantity());
            BigDecimal newTotalValue = totalPositionValue.add(orderValue);
            
            if (newTotalValue.compareTo(config.getMaxTotalPositionValue()) > 0) {
                throw new TotalPositionExceededException(
                        "Total position value will exceed limit after order: " + newTotalValue + ", max allowed: " + config.getMaxTotalPositionValue()
                );
            }
        }
    }

    @Override
    public void checkPosition(Position position, RiskConfig config) {
        if (config.getMaxTotalPositionValue() != null) {
            BigDecimal totalPositionValue = calculateTotalPositionValue();
            if (totalPositionValue.compareTo(config.getMaxTotalPositionValue()) > 0) {
                throw new TotalPositionExceededException(
                        "Total position value exceeded: " + totalPositionValue + ", max allowed: " + config.getMaxTotalPositionValue()
                );
            }
        }
    }

    private BigDecimal calculateTotalPositionValue() {
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Position position : positions) {
            totalValue = totalValue.add(position.getMarketValue());
        }
        return totalValue;
    }

    @Override
    public String getName() {
        return "TotalPositionRule";
    }
}
