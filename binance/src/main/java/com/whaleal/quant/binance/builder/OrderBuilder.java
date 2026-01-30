package com.whaleal.quant.binance.builder;

import com.whaleal.quant.binance.model.OrderSideEnum;
import com.whaleal.quant.binance.model.OrderTypeEnum;
import com.whaleal.quant.binance.model.OrderResp;
import com.whaleal.quant.binance.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单构建器
 *
 * <p>使用Builder模式构建订单，链式调用，简洁优雅。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * OrderResp order = orderBuilder
 *     .symbol("BTCUSDT")
 *     .buy()
 *     .quantity("0.001")
 *     .limitOrder()
 *     .price("50000.00")
 *     .execute();
 * }</pre>
 *
 * @author Binance SDK Team
 */
@Slf4j
public class OrderBuilder {

    private final HttpClient httpClient;
    private final Map<String, String> params;

    private String symbol;
    private OrderSideEnum side;
    private OrderTypeEnum type;
    private String quantity;
    private String price;
    private String timeInForce;
    private String newClientOrderId;

    public OrderBuilder(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.params = new HashMap<>();
    }

    /**
     * 设置交易对
     *
     * @param symbol 交易对（例如：BTCUSDT）
     * @return OrderBuilder
     */
    public OrderBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * 买入
     *
     * @return OrderBuilder
     */
    public OrderBuilder buy() {
        this.side = OrderSideEnum.BUY;
        return this;
    }

    /**
     * 卖出
     *
     * @return OrderBuilder
     */
    public OrderBuilder sell() {
        this.side = OrderSideEnum.SELL;
        return this;
    }

    /**
     * 设置方向
     *
     * @param side 买卖方向
     * @return OrderBuilder
     */
    public OrderBuilder side(OrderSideEnum side) {
        this.side = side;
        return this;
    }

    /**
     * 限价单
     *
     * @return OrderBuilder
     */
    public OrderBuilder limitOrder() {
        this.type = OrderTypeEnum.LIMIT;
        this.timeInForce = "GTC"; // Good Till Cancel
        return this;
    }

    /**
     * 市价单
     *
     * @return OrderBuilder
     */
    public OrderBuilder marketOrder() {
        this.type = OrderTypeEnum.MARKET;
        return this;
    }

    /**
     * 设置订单类型
     *
     * @param type 订单类型
     * @return OrderBuilder
     */
    public OrderBuilder type(OrderTypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * 设置数量
     *
     * @param quantity 数量
     * @return OrderBuilder
     */
    public OrderBuilder quantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * 设置价格（限价单必填）
     *
     * @param price 价格
     * @return OrderBuilder
     */
    public OrderBuilder price(String price) {
        this.price = price;
        return this;
    }

    /**
     * 设置Time in Force
     *
     * @param timeInForce GTC/IOC/FOK
     * @return OrderBuilder
     */
    public OrderBuilder timeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
        return this;
    }

    /**
     * 设置客户端订单ID
     *
     * @param newClientOrderId 客户端订单ID
     * @return OrderBuilder
     */
    public OrderBuilder newClientOrderId(String newClientOrderId) {
        this.newClientOrderId = newClientOrderId;
        return this;
    }

    /**
     * 执行下单
     *
     * @return 订单响应
     * @throws IOException 如果下单失败
     */
    public OrderResp execute() throws IOException {
        validate();

        params.put("symbol", symbol);
        params.put("side", side.name());
        params.put("type", type.name());
        params.put("quantity", quantity);

        if (price != null) {
            params.put("price", price);
        }

        if (timeInForce != null) {
            params.put("timeInForce", timeInForce);
        }

        if (newClientOrderId != null) {
            params.put("newClientOrderId", newClientOrderId);
        }

        String response = httpClient.post("/api/v3/order", params);
        ObjectMapper mapper = httpClient.getObjectMapper();

        return mapper.readValue(response, OrderResp.class);
    }

    /**
     * 校验参数
     */
    private void validate() {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("交易对(symbol)不能为空");
        }
        if (side == null) {
            throw new IllegalArgumentException("买卖方向(side)不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("订单类型(type)不能为空");
        }
        if (quantity == null || quantity.trim().isEmpty()) {
            throw new IllegalArgumentException("数量(quantity)不能为空");
        }
        if (type == OrderTypeEnum.LIMIT && (price == null || price.trim().isEmpty())) {
            throw new IllegalArgumentException("限价单必须设置价格(price)");
        }
    }
}

