package com.whaleal.quant.slippage.model;

import com.whaleal.quant.slippage.calculator.SlippageCalculator;
import com.whaleal.quant.slippage.calculator.CommissionCalculator;
import com.whaleal.quant.slippage.config.SlippageConfig;

import java.math.BigDecimal;

/**
 * 执行价格计算器
 * 综合计算订单的执行价格，包括滑点和手续费的影响
 *
 * @author whaleal
 * @version 1.0.0
 */
public class ExecutionPriceCalculator {

    private final SlippageConfig config;
    private final SlippageCalculator slippageCalculator;
    private final CommissionCalculator commissionCalculator;

    /**
     * 构造方法
     * @param config 滑点配置
     */
    public ExecutionPriceCalculator(SlippageConfig config) {
        this.config = config;
        this.slippageCalculator = config.getSlippageCalculator();
        this.commissionCalculator = config.getCommissionCalculator();
    }

    /**
     * 构造方法
     * 使用默认配置
     */
    public ExecutionPriceCalculator() {
        this(SlippageConfig.getDefault());
    }

    /**
     * 计算执行价格
     * @param order 订单
     * @param marketPrice 市场价格
     * @return 执行价格
     */
    public BigDecimal calculateExecutionPrice(Order order, BigDecimal marketPrice) {
        if (!config.isEnableSlippage()) {
            return marketPrice;
        }
        return slippageCalculator.calculateExecutionPrice(order, marketPrice);
    }

    /**
     * 计算手续费
     * @param order 订单
     * @param executionPrice 执行价格
     * @return 手续费
     */
    public BigDecimal calculateCommission(Order order, BigDecimal executionPrice) {
        if (!config.isEnableCommission()) {
            return BigDecimal.ZERO;
        }
        return commissionCalculator.calculateCommission(order, executionPrice);
    }

    /**
     * 计算税费
     * @param order 订单
     * @param executionPrice 执行价格
     * @return 税费
     */
    public BigDecimal calculateTax(Order order, BigDecimal executionPrice) {
        if (!config.isEnableTax()) {
            return BigDecimal.ZERO;
        }
        BigDecimal orderValue = executionPrice.multiply(order.getQuantity());
        return orderValue.multiply(config.getTaxRate());
    }

    /**
     * 计算总成本
     * @param order 订单
     * @param marketPrice 市场价格
     * @return 总成本（包括滑点损失和手续费、税费）
     */
    public BigDecimal calculateTotalCost(Order order, BigDecimal marketPrice) {
        BigDecimal executionPrice = calculateExecutionPrice(order, marketPrice);
        BigDecimal commission = calculateCommission(order, executionPrice);
        BigDecimal tax = calculateTax(order, executionPrice);

        // 计算滑点损失
        BigDecimal slippageLoss = BigDecimal.ZERO;
        if (config.isEnableSlippage()) {
            BigDecimal slippage = slippageCalculator.calculateSlippage(order, marketPrice);
            slippageLoss = slippage.multiply(order.getQuantity());
        }

        // 总成本 = 滑点损失 + 手续费 + 税费
        return slippageLoss.add(commission).add(tax);
    }

    /**
     * 生成执行报告
     * @param order 订单
     * @param marketPrice 市场价格
     * @return 执行报告
     */
    public String generateExecutionReport(Order order, BigDecimal marketPrice) {
        BigDecimal executionPrice = calculateExecutionPrice(order, marketPrice);
        BigDecimal commission = calculateCommission(order, executionPrice);
        BigDecimal tax = calculateTax(order, executionPrice);
        BigDecimal totalCost = calculateTotalCost(order, marketPrice);

        StringBuilder report = new StringBuilder();
        report.append("=== 订单执行报告 ===\n");
        report.append("订单信息: " + order.getSymbol() + " " + order.getSide() + " " + order.getQuantity() + " @ " + order.getPrice() + "\n");
        report.append("市场价格: " + marketPrice + "\n");
        report.append("执行价格: " + executionPrice + "\n");
        report.append("手续费: " + commission + "\n");
        report.append("税费: " + tax + "\n");
        report.append("总成本: " + totalCost + "\n");
        report.append("================\n");
        return report.toString();
    }

    /**
     * 获取滑点配置
     * @return 滑点配置
     */
    public SlippageConfig getConfig() {
        return config;
    }
}
