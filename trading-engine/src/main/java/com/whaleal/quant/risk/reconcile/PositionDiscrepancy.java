package com.whaleal.quant.risk.reconcile;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓差异
 * 用于存储持仓差异信息
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class PositionDiscrepancy {
    /**
     * 交易对
     */
    private final String symbol;

    /**
     * 本地预估位数量
     */
    private final BigDecimal localQuantity;

    /**
     * 交易所 API 位数量
     */
    private final BigDecimal exchangeQuantity;

    /**
     * 成交回报累加位数量
     */
    private final BigDecimal tradeQuantity;

    /**
     * 本地预估位金额
     */
    private final BigDecimal localAmount;

    /**
     * 交易所 API 位金额
     */
    private final BigDecimal exchangeAmount;

    /**
     * 成交回报累加位金额
     */
    private final BigDecimal tradeAmount;

    /**
     * 是否有数量差异
     */
    private final boolean hasQuantityDiscrepancy;

    /**
     * 是否有金额差异
     */
    private final boolean hasAmountDiscrepancy;

    /**
     * 获取本地预估位与交易所 API 位的数量差异
     * @return 数量差异
     */
    public BigDecimal getLocalExchangeQuantityDiff() {
        return localQuantity.subtract(exchangeQuantity).abs();
    }

    /**
     * 获取本地预估位与成交回报累加位的数量差异
     * @return 数量差异
     */
    public BigDecimal getLocalTradeQuantityDiff() {
        return localQuantity.subtract(tradeQuantity).abs();
    }

    /**
     * 获取交易所 API 位与成交回报累加位的数量差异
     * @return 数量差异
     */
    public BigDecimal getExchangeTradeQuantityDiff() {
        return exchangeQuantity.subtract(tradeQuantity).abs();
    }

    /**
     * 获取本地预估位与交易所 API 位的金额差异
     * @return 金额差异
     */
    public BigDecimal getLocalExchangeAmountDiff() {
        return localAmount.subtract(exchangeAmount).abs();
    }

    /**
     * 获取本地预估位与成交回报累加位的金额差异
     * @return 金额差异
     */
    public BigDecimal getLocalTradeAmountDiff() {
        return localAmount.subtract(tradeAmount).abs();
    }

    /**
     * 获取交易所 API 位与成交回报累加位的金额差异
     * @return 金额差异
     */
    public BigDecimal getExchangeTradeAmountDiff() {
        return exchangeAmount.subtract(tradeAmount).abs();
    }
}
