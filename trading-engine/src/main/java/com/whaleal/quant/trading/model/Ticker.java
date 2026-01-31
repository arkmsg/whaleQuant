package com.whaleal.quant.trading.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 行情快照模型
 * 
 * <p>表示某一时刻的市场报价信息
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class Ticker {
    
    /**
     * 交易对/股票代码
     */
    private String symbol;
    
    /**
     * 最新价格
     */
    private BigDecimal lastPrice;
    
    /**
     * 开盘价
     */
    private BigDecimal openPrice;
    
    /**
     * 最高价
     */
    private BigDecimal highPrice;
    
    /**
     * 最低价
     */
    private BigDecimal lowPrice;
    
    /**
     * 成交量
     */
    private BigDecimal volume;
    
    /**
     * 成交额
     */
    private BigDecimal amount;
    
    /**
     * 24小时涨跌幅
     */
    private BigDecimal priceChangePercent;
    
    /**
     * 24小时涨跌额
     */
    private BigDecimal priceChange;
    
    /**
     * 买一价
     */
    private BigDecimal bidPrice;
    
    /**
     * 买一量
     */
    private BigDecimal bidQty;
    
    /**
     * 卖一价
     */
    private BigDecimal askPrice;
    
    /**
     * 卖一量
     */
    private BigDecimal askQty;
    
    /**
     * 时间戳
     */
    private Instant timestamp;
    
    /**
     * 数据来源
     */
    private String source;
}