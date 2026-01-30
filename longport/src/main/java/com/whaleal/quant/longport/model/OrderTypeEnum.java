package com.whaleal.quant.longport.model;

import com.longport.trade.OrderType;

/**
 * 订单类型枚举
 * 
 * @author arkmsg
 */
public enum OrderTypeEnum {
    
    /**
     * 未知
     */
    UNKNOWN("Unknown", "未知"),
    
    /**
     * 限价单 (Limit Order)
     */
    LO("LO", "限价单"),
    
    /**
     * 增强限价单 (Enhanced Limit Order)
     */
    ELO("ELO", "增强限价单"),
    
    /**
     * 市价单 (Market Order)
     */
    MO("MO", "市价单"),
    
    /**
     * 竞价单 (At Auction Order)
     */
    AO("AO", "竞价单"),
    
    /**
     * 竞价限价单 (At Auction Limit Order)
     */
    ALO("ALO", "竞价限价单"),
    
    /**
     * 碎股单 (Odd Lot Order)
     */
    ODD("ODD", "碎股单"),
    
    /**
     * 限价离市单 (Limit If Touched)
     */
    LIT("LIT", "限价离市单"),
    
    /**
     * 市价离市单 (Market If Touched)
     */
    MIT("MIT", "市价离市单"),
    
    /**
     * 追踪止损限价单(跟踪金额) (Trailing Stop Limit by Amount)
     */
    TSLPAMT("TSLPAMT", "追踪止损限价(金额)"),
    
    /**
     * 追踪止损限价单(跟踪百分比) (Trailing Stop Limit by Percent)
     */
    TSLPPCT("TSLPPCT", "追踪止损限价(百分比)"),
    
    /**
     * 追踪止损市价单(跟踪金额) (Trailing Stop Market by Amount)
     */
    TSMAMT("TSMAMT", "追踪止损市价(金额)"),
    
    /**
     * 追踪止损市价单(跟踪百分比) (Trailing Stop Market by Percent)
     */
    TSMPCT("TSMPCT", "追踪止损市价(百分比)"),
    
    /**
     * 止损限价单 (Stop Limit Order)
     */
    SLO("SLO", "止损限价单");
    
    private final String key;
    private final String description;
    
    OrderTypeEnum(String key, String description) {
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
     * 从长桥SDK的OrderType转换
     */
    public static OrderTypeEnum fromLongport(OrderType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case LO:
                return LO;
            case ELO:
                return ELO;
            case MO:
                return MO;
            case AO:
                return AO;
            case ALO:
                return ALO;
            case ODD:
                return ODD;
            case LIT:
                return LIT;
            case MIT:
                return MIT;
            case TSLPAMT:
                return TSLPAMT;
            case TSLPPCT:
                return TSLPPCT;
            case TSMAMT:
                return TSMAMT;
            case TSMPCT:
                return TSMPCT;
            case SLO:
                return SLO;
            case Unknown:
            default:
                return UNKNOWN;
        }
    }
    
    /**
     * 转换为长桥SDK的OrderType
     */
    public OrderType toLongport() {
        switch (this) {
            case LO:
                return OrderType.LO;
            case ELO:
                return OrderType.ELO;
            case MO:
                return OrderType.MO;
            case AO:
                return OrderType.AO;
            case ALO:
                return OrderType.ALO;
            case ODD:
                return OrderType.ODD;
            case LIT:
                return OrderType.LIT;
            case MIT:
                return OrderType.MIT;
            case TSLPAMT:
                return OrderType.TSLPAMT;
            case TSLPPCT:
                return OrderType.TSLPPCT;
            case TSMAMT:
                return OrderType.TSMAMT;
            case TSMPCT:
                return OrderType.TSMPCT;
            case SLO:
                return OrderType.SLO;
            case UNKNOWN:
            default:
                return OrderType.Unknown;
        }
    }
}

