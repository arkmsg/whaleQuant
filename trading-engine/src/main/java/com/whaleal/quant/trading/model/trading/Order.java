package com.whaleal.quant.trading.model.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易订单模型
 * 
 * <p>表示一笔交易委托订单
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class Order {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 客户端订单ID
     */
    private String clientOrderId;
    
    /**
     * 交易对/股票代码
     */
    private String symbol;
    
    /**
     * 交易方向 BUY/SELL
     */
    private String side;
    
    /**
     * 订单类型 MARKET/LIMIT/STOP_LIMIT 等
     */
    private String type;
    
    /**
     * 订单状态 PENDING/FILLED/CANCELLED/REJECTED 等
     */
    private String status;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private BigDecimal quantity;
    
    /**
     * 已成交数量
     */
    private BigDecimal executedQty;
    
    /**
     * 已成交金额
     */
    private BigDecimal executedAmount;
    
    /**
     * 平均成交价格
     */
    private BigDecimal avgPrice;
    
    /**
     * 止损价格
     */
    private BigDecimal stopPrice;
    
    /**
     * 止盈价格
     */
    private BigDecimal takeProfitPrice;
    
    /**
     * 时间戳
     */
    private Instant timestamp;
    
    /**
     * 更新时间
     */
    private Instant updateTime;
    
    /**
     * 来源
     */
    private String source;
    
    /**
     * 市场
     */
    private String market;
}