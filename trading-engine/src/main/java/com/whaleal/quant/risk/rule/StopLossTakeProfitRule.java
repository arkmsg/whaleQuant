package com.whaleal.quant.risk.rule;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.RiskException;
import java.math.BigDecimal;

/**
 * 止损止盈规则
 * 用于检查持仓是否达到止损或止盈条件
 *
 * @author whaleal
 * @version 1.0.0
 */
public class StopLossTakeProfitRule implements RiskRule {

    private final BigDecimal stopLossPercentage;
    private final BigDecimal takeProfitPercentage;

    /**
     * 构造方法
     * @param stopLossPercentage 止损百分比
     * @param takeProfitPercentage 止盈百分比
     */
    public StopLossTakeProfitRule(double stopLossPercentage, double takeProfitPercentage) {
        this.stopLossPercentage = BigDecimal.valueOf(stopLossPercentage);
        this.takeProfitPercentage = BigDecimal.valueOf(takeProfitPercentage);
    }

    /**
     * 构造方法
     * 使用默认值：止损 0.05，止盈 0.1
     */
    public StopLossTakeProfitRule() {
        this(0.05, 0.1);
    }

    @Override
    public void checkOrder(Order order, RiskConfig config) {
        // 订单检查不涉及止损止盈
    }

    @Override
    public void checkPosition(Position position, RiskConfig config) {
        BigDecimal avgPrice = position.getAvgPrice();
        BigDecimal currentPrice = position.getCurrentPrice();
        
        if (avgPrice == null || currentPrice == null) {
            return;
        }

        BigDecimal priceChangePercentage = currentPrice.subtract(avgPrice)
                .divide(avgPrice, 4, BigDecimal.ROUND_HALF_UP);

        // 检查止损
        if (priceChangePercentage.abs().compareTo(stopLossPercentage) > 0 && 
            ((position.getSide().equals("LONG") && priceChangePercentage.signum() < 0) ||
             (position.getSide().equals("SHORT") && priceChangePercentage.signum() > 0))) {
            throw new RiskException("Position hit stop loss: " + position.getSymbol() + 
                                   ", current price: " + currentPrice + 
                                   ", avg price: " + avgPrice + 
                                   ", change: " + priceChangePercentage.multiply(BigDecimal.valueOf(100)) + "%");
        }

        // 检查止盈
        if (priceChangePercentage.abs().compareTo(takeProfitPercentage) > 0 && 
            ((position.getSide().equals("LONG") && priceChangePercentage.signum() > 0) ||
             (position.getSide().equals("SHORT") && priceChangePercentage.signum() < 0))) {
            throw new RiskException("Position hit take profit: " + position.getSymbol() + 
                                   ", current price: " + currentPrice + 
                                   ", avg price: " + avgPrice + 
                                   ", change: " + priceChangePercentage.multiply(BigDecimal.valueOf(100)) + "%");
        }
    }

    @Override
    public String getName() {
        return "StopLossTakeProfitRule";
    }
}