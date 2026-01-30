package com.whaleal.quant.core.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 行情数据模型
 * 表示某个交易对的实时行情信息
 *
 * @author whaleal
 * @version 1.0.0
 */
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
     * 24小时最高价
     */
    private BigDecimal high24h;

    /**
     * 24小时最低价
     */
    private BigDecimal low24h;

    /**
     * 24小时成交量
     */
    private BigDecimal volume24h;

    /**
     * 24小时成交额
     */
    private BigDecimal amount24h;

    /**
     * 24小时涨跌幅
     */
    private BigDecimal change24h;

    /**
     * 24小时涨跌百分比
     */
    private BigDecimal changePercent24h;

    /**
     * 开盘价
     */
    private BigDecimal open24h;

    /**
     * 前收盘价
     */
    private BigDecimal prevClose;

    /**
     * 时间戳
     */
    private Instant timestamp;

    // Getter and Setter methods
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getHigh24h() {
        return high24h;
    }

    public void setHigh24h(BigDecimal high24h) {
        this.high24h = high24h;
    }

    public BigDecimal getLow24h() {
        return low24h;
    }

    public void setLow24h(BigDecimal low24h) {
        this.low24h = low24h;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public BigDecimal getAmount24h() {
        return amount24h;
    }

    public void setAmount24h(BigDecimal amount24h) {
        this.amount24h = amount24h;
    }

    public BigDecimal getChange24h() {
        return change24h;
    }

    public void setChange24h(BigDecimal change24h) {
        this.change24h = change24h;
    }

    public BigDecimal getChangePercent24h() {
        return changePercent24h;
    }

    public void setChangePercent24h(BigDecimal changePercent24h) {
        this.changePercent24h = changePercent24h;
    }

    public BigDecimal getOpen24h() {
        return open24h;
    }

    public void setOpen24h(BigDecimal open24h) {
        this.open24h = open24h;
    }

    public BigDecimal getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(BigDecimal prevClose) {
        this.prevClose = prevClose;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
