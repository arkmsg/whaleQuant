package com.whaleal.quant.slippage.calculator;

import com.whaleal.quant.base.model.Order;

import java.math.BigDecimal;

/**
 * 基于订单大小的滑点计算器
 * 考虑市场流动性对滑点的影响
 *
 * @author whaleal
 * @version 1.0.0
 */
public class VolumeBasedSlippageCalculator implements SlippageCalculator {

    private final BigDecimal baseSlippagePercentage;
    private final BigDecimal volumeImpactFactor;

    /**
     * 构造方法
     * @param baseSlippagePercentage 基础滑点百分比
     * @param volumeImpactFactor 成交量影响因子
     */
    public VolumeBasedSlippageCalculator(double baseSlippagePercentage, double volumeImpactFactor) {
        this.baseSlippagePercentage = BigDecimal.valueOf(baseSlippagePercentage);
        this.volumeImpactFactor = BigDecimal.valueOf(volumeImpactFactor);
    }

    /**
     * 构造方法
     * 使用默认值：基础滑点 0.05%，成交量影响因子 0.001
     */
    public VolumeBasedSlippageCalculator() {
        this(0.0005, 0.001);
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
        BigDecimal orderValue = order.getPrice().multiply(order.getQuantity());
        BigDecimal marketVolume = getMarketVolume(order.getSymbol());
        
        // 计算订单占市场成交量的比例
        BigDecimal volumeRatio = orderValue.divide(marketVolume, 6, BigDecimal.ROUND_HALF_UP);
        
        // 计算基础滑点
        BigDecimal baseSlippage = marketPrice.multiply(baseSlippagePercentage);
        
        // 计算成交量影响的滑点
        BigDecimal volumeImpact = marketPrice.multiply(volumeRatio).multiply(volumeImpactFactor);
        
        // 总滑点
        return baseSlippage.add(volumeImpact);
    }

    /**
     * 获取市场成交量
     * @param symbol 交易对
     * @return 市场成交量
     */
    private BigDecimal getMarketVolume(String symbol) {
        // 这里应该从市场数据中获取实际的成交量
        // 暂时返回一个默认值 10,000,000
        return BigDecimal.valueOf(10000000);
    }

    @Override
    public String getName() {
        return "VolumeBasedSlippageCalculator";
    }
}