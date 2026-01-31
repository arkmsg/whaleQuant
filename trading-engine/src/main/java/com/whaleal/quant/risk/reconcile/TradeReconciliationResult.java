package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.model.Trade;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 成交对账结果
 * 用于存储成交对账结果
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class TradeReconciliationResult {
    /**
     * 对账 ID
     */
    private final String reconciliationId;

    /**
     * 对账时间
     */
    private final Instant reconciliationTime;

    /**
     * 成交列表
     */
    private List<Trade> trades;

    /**
     * 获取成交数量
     * @return 成交数量
     */
    public int getTradeCount() {
        return trades != null ? trades.size() : 0;
    }
}
