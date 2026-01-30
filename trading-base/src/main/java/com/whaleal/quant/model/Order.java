package com.whaleal.quant.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 订单模型
 * 表示用户的交易订单信息
 *
 * @author whaleal
 * @version 1.0.0
 */
public class Order {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * 订单类型
     */
    private OrderType type;

    /**
     * 订单方向
     */
    private OrderSide side;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 订单数量
     */
    private BigDecimal quantity;

    /**
     * 订单价格
     */
    private BigDecimal price;

    /**
     * 已成交数量
     */
    private BigDecimal executedQuantity;

    /**
     * 平均成交价格
     */
    private BigDecimal avgPrice;

    /**
     * 订单时间
     */
    private Instant createdAt;

    /**
     * 最后更新时间
     */
    private Instant updatedAt;

    /**
     * 订单类型枚举
     */
    public enum OrderType {
        LIMIT,
        MARKET,
        STOP,
        STOP_MARKET,
        TAKE_PROFIT,
        TAKE_PROFIT_MARKET,
        TRAILING_STOP_MARKET
    }

    /**
     * 订单方向枚举
     */
    public enum OrderSide {
        BUY,
        SELL
    }

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        NEW,
        PARTIALLY_FILLED,
        FILLED,
        CANCELED,
        PENDING_CANCEL,
        REJECTED,
        EXPIRED
    }

    // Getter and Setter methods
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

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public BigDecimal getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(BigDecimal executedQuantity) {
        this.executedQuantity = executedQuantity;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
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
     * 获取市场
     * @return 市场
     */
    public String getMarket() {
        return "BINANCE";
    }

    /**
     * 获取额外参数
     * @return 额外参数
     */
    public java.util.Map<String, Object> getExtraParams() {
        return new java.util.HashMap<>();
    }
}
