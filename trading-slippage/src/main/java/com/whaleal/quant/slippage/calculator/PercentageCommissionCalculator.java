package com.whaleal.quant.slippage.calculator;

import com.whaleal.quant.base.model.Order;

import java.math.BigDecimal;

/**
 * 百分比手续费计算器
 * 基于交易金额的百分比计算手续费
 *
 * @author whaleal
 * @version 1.0.0
 */
public class PercentageCommissionCalculator implements CommissionCalculator {

    private final BigDecimal commissionRate;
    private final BigDecimal minimumCommission;

    /**
     * 构造方法
     * @param commissionRate 手续费率
     * @param minimumCommission 最低手续费
     */
    public PercentageCommissionCalculator(double commissionRate, double minimumCommission) {
        this.commissionRate = BigDecimal.valueOf(commissionRate);
        this.minimumCommission = BigDecimal.valueOf(minimumCommission);
    }

    /**
     * 构造方法
     * 使用默认值：手续费率 0.03%，最低手续费 0.1
     */
    public PercentageCommissionCalculator() {
        this(0.0003, 0.1);
    }

    @Override
    public BigDecimal calculateCommission(Order order, BigDecimal executionPrice) {
        BigDecimal orderValue = executionPrice.multiply(order.getQuantity());
        BigDecimal commission = orderValue.multiply(commissionRate);
        
        // 确保手续费不低于最低值
        if (commission.compareTo(minimumCommission) < 0) {
            return minimumCommission;
        }
        
        return commission;
    }

    @Override
    public String getName() {
        return "PercentageCommissionCalculator";
    }
}