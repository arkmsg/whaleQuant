package com.whaleal.quant.binance.model;

/**
 * 持仓方向枚举（合约专用）
 *
 * @author Binance SDK Team
 */
public enum PositionSideEnum {
    /**
     * 单向持仓模式
     */
    BOTH,

    /**
     * 多头（做多）
     */
    LONG,

    /**
     * 空头（做空）
     */
    SHORT;
}

