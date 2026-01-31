package com.whaleal.quant.strategy.event;

import com.whaleal.quant.trading.model.trading.Order;
import lombok.Builder;
import lombok.Data;

/**
 * 订单事件
 *
 * <p>表示订单状态更新事件
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Data
@Builder
public class OrderEvent {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 订单
     */
    private Order order;

    /**
     * 事件类型 CREATE/UPDATE/FILL/CANCEL
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
