package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.model.trading.Position;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 对账结果
 * 用于存储对账结果
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class ReconciliationResult {
    /**
     * 对账 ID
     */
    private final String reconciliationId;

    /**
     * 对账时间
     */
    private final Instant reconciliationTime;

    /**
     * 本地预估位
     */
    private List<Position> localPositions;

    /**
     * 交易所 API 位
     */
    private List<Position> exchangePositions;

    /**
     * 成交回报累加位
     */
    private List<Position> tradePositions;

    /**
     * 差异映射
     */
    private Map<String, PositionDiscrepancy> discrepancies;

    /**
     * 是否有差异
     */
    private boolean hasDiscrepancies;

    /**
     * 获取差异数量
     * @return 差异数量
     */
    public int getDiscrepancyCount() {
        return discrepancies != null ? discrepancies.size() : 0;
    }

    /**
     * 获取本地预估位数量
     * @return 本地预估位数量
     */
    public int getLocalPositionCount() {
        return localPositions != null ? localPositions.size() : 0;
    }

    /**
     * 获取交易所 API 位数量
     * @return 交易所 API 位数量
     */
    public int getExchangePositionCount() {
        return exchangePositions != null ? exchangePositions.size() : 0;
    }

    /**
     * 获取成交回报累加位数量
     * @return 成交回报累加位数量
     */
    public int getTradePositionCount() {
        return tradePositions != null ? tradePositions.size() : 0;
    }
}
