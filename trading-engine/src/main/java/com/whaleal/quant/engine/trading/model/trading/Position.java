package com.whaleal.quant.trading.model.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 持仓模型
 * 
 * <p>表示当前持有的资产头寸
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class Position {
    
    /**
     * 持仓ID
     */
    private String positionId;
    
    /**
     * 交易对/股票代码
     */
    private String symbol;
    
    /**
     * 持仓数量
     */
    private BigDecimal quantity;
    
    /**
     * 平均成本价
     */
    private BigDecimal averagePrice;
    
    /**
     * 当前价格
     */
    private BigDecimal currentPrice;
    
    /**
     * 未实现盈亏
     */
    private BigDecimal unrealizedPnL;
    
    /**
     * 已实现盈亏
     */
    private BigDecimal realizedPnL;
    
    /**
     * 持仓市值
     */
    private BigDecimal marketValue;
    
    /**
     * 持仓方向 LONG/SHORT
     */
    private String direction;
    
    /**
     * 持仓时间
     */
    private Instant createTime;
    
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