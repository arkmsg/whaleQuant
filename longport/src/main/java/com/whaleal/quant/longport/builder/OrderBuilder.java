package com.whaleal.quant.longport.builder;

import com.longport.trade.*;
import com.whaleal.quant.longport.model.OrderResp;
import com.whaleal.quant.longport.model.OrderSideEnum;
import com.whaleal.quant.longport.model.OrderTypeEnum;
import com.whaleal.quant.longport.model.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 订单构建器
 *
 * <p>使用Builder模式构建和提交订单。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 限价单
 * OrderResp order = tradeService.submitOrder()
 *     .symbol("AAPL.US")
 *     .side(OrderSide.BUY)
 *     .quantity(100)
 *     .orderType(OrderType.LIMIT)
 *     .price(150.00)
 *     .execute();
 *
 * // 市价单
 * OrderResp order = tradeService.submitOrder()
 *     .symbol("TSLA.US")
 *     .side(OrderSide.SELL)
 *     .quantity(50)
 *     .orderType(OrderType.MARKET)
 *     .execute();
 * }</pre>
 *
 * @author Longport SDK Team
 */
@Slf4j
public class OrderBuilder {

    private final TradeContext tradeContext;

    private String symbol;
    private OrderSide side;
    private OrderType orderType = OrderType.LO; // 默认限价单
    private long quantity;
    private BigDecimal price;
    private TimeInForceType timeInForce = TimeInForceType.Day; // 默认当日有效
    private String remark;

    public OrderBuilder(TradeContext tradeContext) {
        this.tradeContext = tradeContext;
    }

    /**
     * 设置股票代码
     *
     * @param symbol 股票代码 (例如: "AAPL.US")
     * @return OrderBuilder
     */
    public OrderBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * 设置买卖方向
     *
     * @param side 买卖方向 (Buy/Sell)
     * @return OrderBuilder
     */
    public OrderBuilder side(OrderSide side) {
        this.side = side;
        return this;
    }

    /**
     * 设置买入方向
     *
     * @return OrderBuilder
     */
    public OrderBuilder buy() {
        this.side = OrderSide.Buy;
        return this;
    }

    /**
     * 设置卖出方向
     *
     * @return OrderBuilder
     */
    public OrderBuilder sell() {
        this.side = OrderSide.Sell;
        return this;
    }

    /**
     * 设置订单类型
     *
     * @param orderType 订单类型 (LO限价/MO市价)
     * @return OrderBuilder
     */
    public OrderBuilder orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    /**
     * 设置为限价单
     *
     * @return OrderBuilder
     */
    public OrderBuilder limitOrder() {
        this.orderType = OrderType.LO;
        return this;
    }

    /**
     * 设置为市价单
     *
     * @return OrderBuilder
     */
    public OrderBuilder marketOrder() {
        this.orderType = OrderType.MO;
        return this;
    }

    /**
     * 设置数量
     *
     * @param quantity 股票数量
     * @return OrderBuilder
     */
    public OrderBuilder quantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * 设置价格（限价单必需）
     *
     * @param price 价格
     * @return OrderBuilder
     */
    public OrderBuilder price(double price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }

    /**
     * 设置价格（限价单必需）
     *
     * @param price 价格
     * @return OrderBuilder
     */
    public OrderBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    /**
     * 设置有效期类型
     *
     * @param timeInForce 有效期类型
     * @return OrderBuilder
     */
    public OrderBuilder timeInForce(TimeInForceType timeInForce) {
        this.timeInForce = timeInForce;
        return this;
    }

    /**
     * 设置为当日有效（默认）
     *
     * @return OrderBuilder
     */
    public OrderBuilder dayOrder() {
        this.timeInForce = TimeInForceType.Day;
        return this;
    }

    /**
     * 设置为Good Till Cancel（取消前有效）
     *
     * @return OrderBuilder
     */
    public OrderBuilder gtcOrder() {
        this.timeInForce = TimeInForceType.GoodTilCanceled;
        return this;
    }

    /**
     * 设置订单备注
     *
     * @param remark 备注
     * @return OrderBuilder
     */
    public OrderBuilder remark(String remark) {
        this.remark = remark;
        return this;
    }

    /**
     * 执行下单
     *
     * @return 订单响应
     * @throws Exception 如果下单失败
     */
    public OrderResp execute() throws Exception {
        validate();

        SubmitOrderOptions options = new SubmitOrderOptions(
            symbol,
            orderType,
            side,
            BigDecimal.valueOf(quantity),
            timeInForce
        );

        // 限价单需要设置价格
        if (orderType == OrderType.LO) {
            if (price == null) {
                throw new IllegalArgumentException("限价单必须设置价格");
            }
            options.setSubmittedPrice(price);
        }

        // 设置备注
        if (remark != null && !remark.trim().isEmpty()) {
            options.setRemark(remark);
        }

        // 提交订单
        SubmitOrderResponse response = tradeContext.submitOrder(options).get();

        log.debug("下单成功: {} {} {} @ {} | 订单ID: {}",
                side,
                symbol,
                quantity,
                price != null ? "$" + price : "市价",
                response.getOrderId());

        return OrderResp.builder()
                .orderId(response.getOrderId())
                .symbol(symbol)
                .side(OrderSideEnum.fromLongport(side))
                .orderType(OrderTypeEnum.fromLongport(orderType))
                .submittedPrice(price)
                .submittedQuantity(BigDecimal.valueOf(quantity))
                .status(OrderStatusEnum.NEW)
                .build();
    }

    /**
     * 验证必填参数
     */
    private void validate() {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
        if (side == null) {
            throw new IllegalArgumentException("买卖方向不能为空");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("订单类型不能为空");
        }
    }
}

