package com.whaleal.quant.strategy.event;

import com.whaleal.quant.trading.model.trading.Position;
import lombok.Builder;
import lombok.Data;

/**
 * 持仓事件
 *
 * <p>表示持仓状态更新事件
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Data
@Builder
public class PositionEvent {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 持仓
     */
    private Position position;

    /**
     * 事件类型 UPDATE/INITIALIZE
     */
    private String eventType;

    /**
     * 事件时间戳
     */
    private long timestamp;

    /**
     * 来源
     */
    private String source;
}
