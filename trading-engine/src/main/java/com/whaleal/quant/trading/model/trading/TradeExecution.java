package com.whaleal.quant.trading.model.trading;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 交易执行模型
 * 
 * <p>表示一次交易执行的结果
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class TradeExecution {
    
    /**
     * 执行ID
     */
    private String executionId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 交易对/股票代码
     */
    private String symbol;
    
    /**
     * 执行状态 SUCCESS/FAILED/PENDING
     */
    private String status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行时间
     */
    private Instant executionTime;
    
    /**
     * 来源
     */
    private String source;
}