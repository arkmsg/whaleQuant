package com.whaleal.quant.model.trading;

import com.whaleal.quant.model.trading.Order;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * 订单构建器
 *
 * <p>使用 Builder 模式构建订单对象，使订单创建更加灵活和可读</p>
 *
 * @author whaleal
 * @version 1.0.0
 */
public class OrderBuilder {
    private String symbol;
    private String side;
    private String type;
    private double price;
    private int quantity;
    private String timeInForce;
    private String clientOrderId;
    private boolean simulation;
    private String source;

    /**
     * 创建一个新的订单构建器
     *
     * @return 订单构建器
     */
    public static OrderBuilder newBuilder() {
        return new OrderBuilder();
    }

    /**
     * 设置交易对/股票代码
     *
     * @param symbol 交易对/股票代码
     * @return 订单构建器
     */
    public OrderBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * 设置订单方向（买入）
     *
     * @return 订单构建器
     */
    public OrderBuilder buy() {
        this.side = "BUY";
        return this;
    }

    /**
     * 设置订单方向（卖出）
     *
     * @return 订单构建器
     */
    public OrderBuilder sell() {
        this.side = "SELL";
        return this;
    }

    /**
     * 设置订单方向
     *
     * @param side 订单方向
     * @return 订单构建器
     */
    public OrderBuilder side(String side) {
        this.side = side;
        return this;
    }

    /**
     * 设置订单类型（限价单）
     *
     * @return 订单构建器
     */
    public OrderBuilder limitOrder() {
        this.type = "LIMIT";
        return this;
    }

    /**
     * 设置订单类型（市价单）
     *
     * @return 订单构建器
     */
    public OrderBuilder marketOrder() {
        this.type = "MARKET";
        return this;
    }

    /**
     * 设置订单类型
     *
     * @param type 订单类型
     * @return 订单构建器
     */
    public OrderBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * 设置订单价格
     *
     * @param price 订单价格
     * @return 订单构建器
     */
    public OrderBuilder price(double price) {
        this.price = price;
        return this;
    }

    /**
     * 设置订单数量
     *
     * @param quantity 订单数量
     * @return 订单构建器
     */
    public OrderBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * 设置有效期（当日有效）
     *
     * @return 订单构建器
     */
    public OrderBuilder dayOrder() {
        this.timeInForce = "DAY";
        return this;
    }

    /**
     * 设置有效期
     *
     * @param timeInForce 有效期
     * @return 订单构建器
     */
    public OrderBuilder timeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
        return this;
    }

    /**
     * 设置客户端订单ID
     *
     * @param clientOrderId 客户端订单ID
     * @return 订单构建器
     */
    public OrderBuilder clientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
        return this;
    }

    /**
     * 设置为模拟交易
     *
     * @return 订单构建器
     */
    public OrderBuilder simulation() {
        this.simulation = true;
        return this;
    }

    /**
     * 设置为实盘交易
     *
     * @return 订单构建器
     */
    public OrderBuilder real() {
        this.simulation = false;
        return this;
    }

    /**
     * 设置是否为模拟交易
     *
     * @param simulation 是否为模拟交易
     * @return 订单构建器
     */
    public OrderBuilder simulation(boolean simulation) {
        this.simulation = simulation;
        return this;
    }

    /**
     * 设置订单来源
     *
     * @param source 订单来源
     * @return 订单构建器
     */
    public OrderBuilder source(String source) {
        this.source = source;
        return this;
    }

    /**
     * 构建订单对象
     *
     * @return 订单对象
     */
    public Order build() {
        // 生成默认的客户端订单ID
        if (clientOrderId == null) {
            clientOrderId = "O" + System.currentTimeMillis();
        }

        // 设置默认值
        if (timeInForce == null) {
            timeInForce = "DAY";
        }

        if (source == null) {
            source = "STRATEGY";
        }

        // 构建订单对象
        Order order = Order.builder()
            .orderId(clientOrderId)
            .clientOrderId(clientOrderId)
            .symbol(symbol)
            .side(side)
            .type(type)
            .status("NEW")
            .price(BigDecimal.valueOf(price))
            .quantity(BigDecimal.valueOf(quantity))
            .executedQty(BigDecimal.ZERO)
            .executedAmount(BigDecimal.ZERO)
            .avgPrice(BigDecimal.ZERO)
            .timestamp(Instant.now())
            .updateTime(Instant.now())
            .source(source)
            .market("BINANCE")
            .build();

        return order;
    }

    /**
     * 执行订单构建并返回订单对象
     *
     * @return 订单对象
     */
    public Order execute() {
        return build();
    }
}
