package com.whaleal.quant.slippage.calculator;

import com.whaleal.quant.base.model.Order;

import java.math.BigDecimal;

/**
 * 滑点计算器
 * 用于计算订单执行时的滑点
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface SlippageCalculator {

    /**
     * 计算滑点后的执行价格
     * @param order 订单
     * @param marketPrice 市场价格
     * @return 滑点后的执行价格
     */
    BigDecimal calculateExecutionPrice(Order order, BigDecimal marketPrice);

    /**
     * 计算滑点值
     * @param order 订单
     * @param marketPrice 市场价格
     * @return 滑点值
     */
    BigDecimal calculateSlippage(Order order, BigDecimal marketPrice);

    /**
     * 获取滑点计算器名称
     * @return 滑点计算器名称
     */
    String getName();
}