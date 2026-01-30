package com.whaleal.quant.slippage.calculator;

import com.whaleal.quant.base.model.Order;

import java.math.BigDecimal;

/**
 * 手续费计算器
 * 用于计算交易手续费
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface CommissionCalculator {

    /**
     * 计算手续费
     * @param order 订单
     * @param executionPrice 执行价格
     * @return 手续费
     */
    BigDecimal calculateCommission(Order order, BigDecimal executionPrice);

    /**
     * 获取手续费计算器名称
     * @return 手续费计算器名称
     */
    String getName();
}