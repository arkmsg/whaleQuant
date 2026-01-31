package com.whaleal.quant.model.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 盈亏模型
 *
 * <p>表示交易的盈亏信息
 *
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class PnL {

    /**
     * 盈亏ID
     */
    private String pnlId;

    /**
     * 交易对/股票代码
     */
    private String symbol;

    /**
     * 未实现盈亏
     */
    private BigDecimal unrealizedPnL;

    /**
     * 已实现盈亏
     */
    private BigDecimal realizedPnL;

    /**
     * 总盈亏
     */
    private BigDecimal totalPnL;

    /**
     * 盈亏百分比
     */
    private BigDecimal pnlPercent;

    /**
     * 持仓市值
     */
    private BigDecimal marketValue;

    /**
     * 持仓成本
     */
    private BigDecimal costBasis;

    /**
     * 计算时间
     */
    private Instant timestamp;

    /**
     * 来源
     */
    private String source;
}
