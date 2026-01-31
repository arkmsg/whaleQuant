package com.whaleal.quant.model.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易信号模型
 *
 * <p>表示一个交易信号，用于触发交易执行
 *
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class TradeSignal {

    /**
     * 信号ID
     */
    private String signalId;

    /**
     * 交易对/股票代码
     */
    private String symbol;

    /**
     * 交易方向 BUY/SELL
     */
    private String side;

    /**
     * 信号类型 ENTRY/EXIT
     */
    private String signalType;

    /**
     * 推荐价格
     */
    private BigDecimal price;

    /**
     * 推荐数量
     */
    private BigDecimal quantity;

    /**
     * 信号强度 0-100
     */
    private int strength;

    /**
     * 策略ID
     */
    private String strategyId;

    /**
     * 生成时间
     */
    private Instant timestamp;

    /**
     * 来源
     */
    private String source;
}
