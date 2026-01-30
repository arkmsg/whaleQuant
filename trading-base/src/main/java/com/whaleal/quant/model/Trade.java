package com.whaleal.quant.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易记录模型
 * 表示用户的交易成交信息
 *
 * @author whaleal
 * @version 1.0.0
 */
public class Trade {

    /**
     * 交易ID
     */
    private String tradeId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * 交易方向
     */
    private TradeType side;

    /**
     * 交易数量
     */
    private BigDecimal quantity;

    /**
     * 交易价格
     */
    private BigDecimal price;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易时间
     */
    private Instant timestamp;

    /**
     * 交易类型
     */
    private TradeType type;

    /**
     * 交易类型枚举
     */
    public enum TradeType {
        BUY,
        SELL
    }

    // Getter and Setter methods
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TradeType getSide() {
        return side;
    }

    public void setSide(TradeType side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }
}
