package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 合约订单响应
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class FuturesOrderResp {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 订单ID
     */
    @JsonProperty("orderId")
    private Long orderId;

    /**
     * 客户端订单ID
     */
    @JsonProperty("clientOrderId")
    private String clientOrderId;

    /**
     * 订单价格
     */
    private BigDecimal price;

    /**
     * 原始数量
     */
    @JsonProperty("origQty")
    private BigDecimal origQty;

    /**
     * 已成交数量
     */
    @JsonProperty("executedQty")
    private BigDecimal executedQty;

    /**
     * 累计成交金额
     */
    @JsonProperty("cumQuote")
    private BigDecimal cumQuote;

    /**
     * 订单状态
     */
    private String status;

    /**
     * Time in Force
     */
    @JsonProperty("timeInForce")
    private String timeInForce;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 买卖方向
     */
    private String side;

    /**
     * 持仓方向
     */
    @JsonProperty("positionSide")
    private String positionSide;

    /**
     * 止盈止损价格
     */
    @JsonProperty("stopPrice")
    private BigDecimal stopPrice;

    /**
     * 条件触发价格类型
     */
    @JsonProperty("workingType")
    private String workingType;

    /**
     * 只减仓
     */
    @JsonProperty("reduceOnly")
    private Boolean reduceOnly;

    /**
     * 订单更新时间
     */
    @JsonProperty("updateTime")
    private Long updateTime;

    /**
     * 平均成交价
     */
    @JsonProperty("avgPrice")
    private BigDecimal avgPrice;
}

