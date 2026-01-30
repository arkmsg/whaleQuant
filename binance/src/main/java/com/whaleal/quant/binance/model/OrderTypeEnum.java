package com.whaleal.quant.binance.model;

/**
 * 订单类型枚举
 *
 * @author Binance SDK Team
 */
public enum OrderTypeEnum {
    /**
     * 限价单
     */
    LIMIT,

    /**
     * 市价单
     */
    MARKET,

    /**
     * 止损限价单
     */
    STOP_LOSS_LIMIT,

    /**
     * 止盈限价单
     */
    TAKE_PROFIT_LIMIT,

    /**
     * 限价做市商单
     */
    LIMIT_MAKER;
}

