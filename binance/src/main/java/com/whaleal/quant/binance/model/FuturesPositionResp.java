package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 合约持仓信息
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class FuturesPositionResp {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 持仓方向
     */
    @JsonProperty("positionSide")
    private String positionSide;

    /**
     * 持仓数量（正数=多，负数=空）
     */
    @JsonProperty("positionAmt")
    private BigDecimal positionAmt;

    /**
     * 持仓成本价
     */
    @JsonProperty("entryPrice")
    private BigDecimal entryPrice;

    /**
     * 未实现盈亏
     */
    @JsonProperty("unRealizedProfit")
    private BigDecimal unRealizedProfit;

    /**
     * 强平价格
     */
    @JsonProperty("liquidationPrice")
    private BigDecimal liquidationPrice;

    /**
     * 杠杆倍数
     */
    private Integer leverage;

    /**
     * 保证金模式（全仓/逐仓）
     */
    @JsonProperty("marginType")
    private String marginType;

    /**
     * 逐仓保证金
     */
    @JsonProperty("isolatedMargin")
    private BigDecimal isolatedMargin;

    /**
     * 标记价格
     */
    @JsonProperty("markPrice")
    private BigDecimal markPrice;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private Long updateTime;
}

