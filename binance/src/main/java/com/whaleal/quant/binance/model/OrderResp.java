package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单响应
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class OrderResp {

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
     * 订单创建时间
     */
    @JsonProperty("transactTime")
    private Long transactTime;

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
    @JsonProperty("cummulativeQuoteQty")
    private BigDecimal cummulativeQuoteQty;

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
     * 订单更新时间
     */
    @JsonProperty("updateTime")
    private Long updateTime;

    /**
     * 是否为工作订单
     */
    @JsonProperty("isWorking")
    private Boolean isWorking;

    /**
     * 原始报价数量
     */
    @JsonProperty("origQuoteOrderQty")
    private BigDecimal origQuoteOrderQty;
}

