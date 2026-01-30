package com.whaleal.quant.trading.enums;

/**
 * 数据来源枚举
 * 
 * <p>定义支持的市场数据来源
 * 
 * @author trading
 * @version 1.0.0
 */
public enum DataSource {
    
    /**
     * Binance交易所
     */
    BINANCE,
    
    /**
     * Longport交易所
     */
    LONGPORT,
    
    /**
     * 本地数据
     */
    LOCAL,
    
    /**
     * 其他数据源
     */
    OTHER
}