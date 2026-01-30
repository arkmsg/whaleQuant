package com.whaleal.quant.risk.config;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RiskConfig {
    // 单笔订单金额限制
    private BigDecimal maxOrderAmount;
    
    // 单笔订单数量限制
    private BigDecimal maxOrderQuantity;
    
    // 价格偏离度限制（百分比）
    private BigDecimal priceDeviationThreshold;
    
    // 总仓位限制
    private BigDecimal maxTotalPositionValue;
    
    // 单个标的仓位限制
    private BigDecimal maxSinglePositionValue;
    
    // 最大回撤限制（百分比）
    private BigDecimal maxDrawdownThreshold;
    
    // 单日最大亏损限制
    private BigDecimal maxDailyLoss;
    
    // 单日最大交易次数
    private Integer maxDailyTrades;
}
