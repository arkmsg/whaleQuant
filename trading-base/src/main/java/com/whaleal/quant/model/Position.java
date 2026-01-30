package com.whaleal.quant.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 持仓模型
 * 表示用户在某个交易对的持仓信息
 *
 * @author whaleal
 * @version 1.0.0
 */
public class Position {

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * 持仓方向
     */
    private PositionSide side;

    /**
     * 持仓数量
     */
    private BigDecimal quantity;

    /**
     * 平均持仓价格
     */
    private BigDecimal avgPrice;

    /**
     * 市值
     */
    private BigDecimal marketValue;

    /**
     * 盈亏
     */
    private BigDecimal profitLoss;

    /**
     * 持仓时间
     */
    private Instant createdAt;

    /**
     * 最后更新时间
     */
    private Instant updatedAt;

    /**
     * 持仓方向枚举
     */
    public enum PositionSide {
        LONG,
        SHORT
    }

    // Getter and Setter methods
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public PositionSide getSide() {
        return side;
    }

    public void setSide(PositionSide side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取当前价格
     * @return 当前价格
     */
    public BigDecimal getCurrentPrice() {
        return avgPrice;
    }
}
