package com.whaleal.quant.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 行情数据模型
 * 表示某个交易对的实时行情信息
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class Ticker {

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * 当前价格
     */
    private BigDecimal price;

    /**
     * 最新价格
     */
    private BigDecimal lastPrice;

    /**
     * 24小时最高价
     */
    private BigDecimal high24h;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 24小时最低价
     */
    private BigDecimal low24h;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 24小时成交量
     */
    private BigDecimal volume24h;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 24小时成交额
     */
    private BigDecimal amount24h;

    /**
     * 成交额
     */
    private BigDecimal amount;

    /**
     * 24小时涨跌幅
     */
    private BigDecimal change24h;

    /**
     * 24小时涨跌额
     */
    private BigDecimal priceChange;

    /**
     * 24小时涨跌百分比
     */
    private BigDecimal changePercent24h;

    /**
     * 24小时涨跌百分比
     */
    private BigDecimal priceChangePercent;

    /**
     * 开盘价
     */
    private BigDecimal open24h;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 前收盘价
     */
    private BigDecimal prevClose;

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
