package com.whaleal.quant.strategy.event;

import com.whaleal.quant.trading.model.Ticker;
import lombok.Builder;
import lombok.Data;

/**
 * 市场数据事件
 *
 * <p>表示市场数据更新事件
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Data
@Builder
public class MarketDataEvent {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 行情数据
     */
    private Ticker ticker;

    /**
     * 事件时间戳
     */
    private long timestamp;

    /**
     * 数据来源
     */
    private String source;
}
