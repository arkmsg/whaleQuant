package com.whaleal.quant.trading.model.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 成交明细模型
 * 
 * <p>表示一笔订单的成交明细
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class Fill {
    
    /**
     * 成交ID
     */
    private String fillId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 交易对/股票代码
     */
    private String symbol;
    
    /**
     * 成交价格
     */
    private BigDecimal price;
    
    /**
     * 成交数量
     */
    private BigDecimal quantity;
    
    /**
     * 成交金额
     */
    private BigDecimal amount;
    
    /**
     * 手续费
     */
    private BigDecimal fee;
    
    /**
     * 手续费资产
     */
    private String feeAsset;
    
    /**
     * 成交时间
     */
    private Instant timestamp;
    
    /**
     * 来源
     */
    private String source;
}