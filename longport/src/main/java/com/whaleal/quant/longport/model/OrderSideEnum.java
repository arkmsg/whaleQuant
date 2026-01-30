package com.whaleal.quant.longport.model;

import com.longport.trade.OrderSide;

/**
 * 订单方向枚举
 * 
 * @author arkmsg
 */
public enum OrderSideEnum {
    
    /**
     * 未知
     */
    UNKNOWN("Unknown", "未知"),
    
    /**
     * 买入
     */
    BUY("Buy", "买入"),
    
    /**
     * 卖出
     */
    SELL("Sell", "卖出");
    
    private final String key;
    private final String description;
    
    OrderSideEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 从长桥SDK的OrderSide转换
     */
    public static OrderSideEnum fromLongport(OrderSide side) {
        if (side == null) {
            return null;
        }
        switch (side) {
            case Buy:
                return BUY;
            case Sell:
                return SELL;
            case Unknown:
            default:
                return UNKNOWN;
        }
    }
    
    /**
     * 转换为长桥SDK的OrderSide
     */
    public OrderSide toLongport() {
        switch (this) {
            case BUY:
                return OrderSide.Buy;
            case SELL:
                return OrderSide.Sell;
            case UNKNOWN:
            default:
                return OrderSide.Unknown;
        }
    }
}

