package com.whaleal.quant.binance.model;

/**
 * 保证金类型枚举（合约专用）
 *
 * @author Binance SDK Team
 */
public enum MarginTypeEnum {
    /**
     * 全仓模式
     */
    CROSSED,

    /**
     * 逐仓模式
     */
    ISOLATED;
}

