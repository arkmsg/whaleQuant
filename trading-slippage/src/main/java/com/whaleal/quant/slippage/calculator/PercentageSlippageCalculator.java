package com.whaleal.quant.slippage.calculator;

import java.math.BigDecimal;

/**
 * 固定百分比滑点计算器
 * 基于固定百分比计算滑点
 *
 * @author whaleal
 * @version 1.0.0
 */
public class PercentageSlippageCalculator implements SlippageCalculator {

    private final BigDecimal slippagePercentage;

    /**
     * 构造方法
     * @param slippagePercentage 滑点百分比
     */
    public PercentageSlippageCalculator(double slippagePercentage) {
        this.slippagePercentage = BigDecimal.valueOf(slippagePercentage);
    }

    /**
     * 构造方法
     * 使用默认滑点百分比 0.1%
     */
    public PercentageSlippageCalculator() {
        this(0.001);
    }

    @Override
    public BigDecimal calculateExecutionPrice(Order order, BigDecimal marketPrice) {
        BigDecimal slippage = calculateSlippage(order, marketPrice);

        if ("BUY".equals(order.getSide())) {
            // 买入时，执行价格高于市场价格
            return marketPrice.add(slippage);
        } else if ("SELL".equals(order.getSide())) {
            // 卖出时，执行价格低于市场价格
            return marketPrice.subtract(slippage);
        }

        return marketPrice;
    }

    @Override
    public BigDecimal calculateSlippage(Order order, BigDecimal marketPrice) {
        return marketPrice.multiply(slippagePercentage);
    }

    @Override
    public String getName() {
        return "PercentageSlippageCalculator";
    }
}
