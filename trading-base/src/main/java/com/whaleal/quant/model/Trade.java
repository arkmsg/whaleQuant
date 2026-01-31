package com.whaleal.quant.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易记录模型
 * 表示用户的交易成交信息
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class Trade {

    /**
     * 交易ID
     */
    private String tradeId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 交易对符号
     */
    private String symbol;

    /**
     * 交易方向
     */
    private TradeType side;

    /**
     * 交易方向 BUY/SELL
     */
    private String sideString;

    /**
     * 交易数量
     */
    private BigDecimal quantity;

    /**
     * 交易价格
     */
    private BigDecimal price;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 手续费资产
     */
    private String feeAsset;

    /**
     * 交易时间
     */
    private Instant timestamp;

    /**
     * 交易类型
     */
    private TradeType type;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 交易类型枚举
     */
    public enum TradeType {
        BUY,
        SELL
    }
}
