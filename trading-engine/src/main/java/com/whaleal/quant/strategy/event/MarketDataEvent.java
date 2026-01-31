package com.whaleal.quant.strategy.event;

import com.whaleal.quant.model.Bar;
import com.whaleal.quant.model.Ticker;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
     * 交易对符号
     */
    private String symbol;

    /**
     * 行情数据
     */
    private Ticker ticker;

    /**
     * K线数据列表
     */
    private List<Bar> bars;

    /**
     * 事件时间戳
     */
    private long timestamp;

    /**
     * 数据来源
     */
    private String source;

    // 使用@Builder注解生成的构造函数
}
