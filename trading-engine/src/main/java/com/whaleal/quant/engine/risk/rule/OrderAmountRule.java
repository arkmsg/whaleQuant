package com.whaleal.quant.risk.rule;

import com.whaleal.quant.base.model.Order;
import com.whaleal.quant.base.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.OrderAmountExceededException;
import java.math.BigDecimal;

public class OrderAmountRule implements RiskRule {
    @Override
    public void checkOrder(Order order, RiskConfig config) {
        if (config.getMaxOrderAmount() != null) {
            BigDecimal orderAmount = order.getPrice().multiply(order.getQuantity());
            if (orderAmount.compareTo(config.getMaxOrderAmount()) > 0) {
                throw new OrderAmountExceededException(
                        "Order amount exceeded: " + orderAmount + ", max allowed: " + config.getMaxOrderAmount()
                );
            }
        }
    }

    @Override
    public void checkPosition(Position position, RiskConfig config) {
        // 持仓检查不涉及订单金额限制
    }

    @Override
    public String getName() {
        return "OrderAmountRule";
    }
}
