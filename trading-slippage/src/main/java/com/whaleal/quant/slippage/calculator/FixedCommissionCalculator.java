package com.whaleal.quant.slippage.calculator;

import com.whaleal.quant.base.model.Order;

import java.math.BigDecimal;

/**
 * 固定费用手续费计算器
 * 按固定金额收取手续费
 *
 * @author whaleal
 * @version 1.0.0
 */
public class FixedCommissionCalculator implements CommissionCalculator {

    private final BigDecimal fixedCommission;

    /**
     * 构造方法
     * @param fixedCommission 固定手续费
     */
    public FixedCommissionCalculator(double fixedCommission) {
        this.fixedCommission = BigDecimal.valueOf(fixedCommission);
    }

    /**
     * 构造方法
     * 使用默认固定手续费 1.0
     */
    public FixedCommissionCalculator() {
        this(1.0);
    }

    @Override
    public BigDecimal calculateCommission(Order order, BigDecimal executionPrice) {
        return fixedCommission;
    }

    @Override
    public String getName() {
        return "FixedCommissionCalculator";
    }
}