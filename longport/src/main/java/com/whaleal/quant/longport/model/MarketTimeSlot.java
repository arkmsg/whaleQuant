package com.whaleal.quant.longport.model;

/**
 * 市场交易时段枚举
 * 
 * @author arkmsg
 */
public enum MarketTimeSlot {
    
    /**
     * 盘前交易 (美东时间 04:00-09:30)
     */
    PRE_MARKET("PRE_MARKET", "盘前交易"),
    
    /**
     * 正常交易时段 (美东时间 09:30-16:00)
     */
    REGULAR("REGULAR", "正常交易"),
    
    /**
     * 盘后交易 (美东时间 16:00-20:00)
     */
    POST_MARKET("POST_MARKET", "盘后交易"),
    
    /**
     * 夜盘交易 (美东时间 20:00-04:00)
     */
    OVERNIGHT("OVERNIGHT", "夜盘交易");
    
    private final String key;
    private final String description;
    
    MarketTimeSlot(String key, String description) {
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
     * 根据key获取枚举值
     * 
     * @param key 字符串key
     * @return 对应的枚举值，如果不存在则返回null
     */
    public static MarketTimeSlot fromKey(String key) {
        if (key == null) {
            return null;
        }
        for (MarketTimeSlot slot : values()) {
            if (slot.key.equals(key)) {
                return slot;
            }
        }
        return null;
    }
}

