package com.whaleal.quant.risk.rule;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.PriceDeviationException;
import java.math.BigDecimal;

public class PriceDeviationRule implements RiskRule {
    private final BigDecimal marketPrice;

    public PriceDeviationRule(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    @Override
    public void checkOrder(Order order, RiskConfig config) {
        if (config.getPriceDeviationThreshold() != null && marketPrice != null) {
            BigDecimal orderPrice = order.getPrice();
            BigDecimal deviation = orderPrice.subtract(marketPrice).abs().divide(marketPrice, BigDecimal.ROUND_HALF_UP);
            BigDecimal threshold = config.getPriceDeviationThreshold().divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);

            if (deviation.compareTo(threshold) > 0) {
                throw new PriceDeviationException(
                        "Price deviation exceeded: " + deviation.multiply(BigDecimal.valueOf(100)) + "%, max allowed: " + config.getPriceDeviationThreshold() + "%"
                );
            }
        }
    }

    @Override
    public void checkPosition(Position position, RiskConfig config) {
        // 持仓检查不涉及价格偏离度限制
    }

    @Override
    public String getName() {
        return "PriceDeviationRule";
    }
}
