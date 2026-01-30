package com.whaleal.quant.longport.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓响应
 *
 * @author Longport SDK Team
 */
@Data
@Builder
public class PositionResp {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 持仓数量
     */
    private BigDecimal quantity;

    /**
     * 可用数量
     */
    private BigDecimal availableQuantity;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 市场
     */
    private String market;

    /**
     * 当前价格（需要单独查询）
     */
    private BigDecimal currentPrice;

    /**
     * 市值
     */
    private BigDecimal marketValue;

    /**
     * 盈亏金额
     */
    private BigDecimal profitLoss;

    /**
     * 盈亏率（%）
     */
    private BigDecimal profitLossRate;

    /**
     * 计算市值
     */
    public BigDecimal calculateMarketValue() {
        if (quantity != null && currentPrice != null) {
            return quantity.multiply(currentPrice);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算盈亏金额
     */
    public BigDecimal calculateProfitLoss() {
        if (quantity != null && currentPrice != null && costPrice != null) {
            return currentPrice.subtract(costPrice).multiply(quantity);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算盈亏率（%）
     */
    public BigDecimal calculateProfitLossRate() {
        if (currentPrice != null && costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            return currentPrice.subtract(costPrice)
                    .divide(costPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}

