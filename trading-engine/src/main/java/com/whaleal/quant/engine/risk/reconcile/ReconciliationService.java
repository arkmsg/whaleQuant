package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.base.model.Order;
import com.whaleal.quant.base.model.Position;
import com.whaleal.quant.base.model.Trade;
import com.whaleal.quant.base.trace.TraceContext;
import com.whaleal.quant.risk.RiskManager;
import com.whaleal.quant.risk.exception.RiskException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 对账服务
 * 对比三个数据源：本地预估位、交易所 API 位、成交回报累加位
 * 当三者不一致时触发风控熔断，防止错单滚动
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
public class ReconciliationService {

    private final RiskManager riskManager;
    private final PositionProvider localPositionProvider;
    private final PositionProvider exchangePositionProvider;
    private final PositionProvider tradeAggregationProvider;
    private final ReconciliationConfig config;

    public ReconciliationService(RiskManager riskManager,
                               PositionProvider localPositionProvider,
                               PositionProvider exchangePositionProvider,
                               PositionProvider tradeAggregationProvider,
                               ReconciliationConfig config) {
        this.riskManager = riskManager;
        this.localPositionProvider = localPositionProvider;
        this.exchangePositionProvider = exchangePositionProvider;
        this.tradeAggregationProvider = tradeAggregationProvider;
        this.config = config;
    }

    /**
     * 执行对账
     * @return 对账结果
     */
    public ReconciliationResult reconcile() {
        TraceContext traceContext = TraceContext.create();
        log.info("[{}] 开始执行对账", traceContext.getTraceId());

        try {
            // 获取三个数据源的持仓
            List<Position> localPositions = localPositionProvider.getPositions();
            List<Position> exchangePositions = exchangePositionProvider.getPositions();
            List<Position> tradePositions = tradeAggregationProvider.getPositions();

            // 执行对账逻辑
            ReconciliationResult result = ReconciliationResult.builder()
                    .reconciliationId(traceContext.getTraceId())
                    .reconciliationTime(Instant.now())
                    .localPositions(localPositions)
                    .exchangePositions(exchangePositions)
                    .tradePositions(tradePositions)
                    .build();

            // 对比三个数据源
            Map<String, PositionDiscrepancy> discrepancies = ReconciliationEngine.reconcile(
                    localPositions, exchangePositions, tradePositions, config
            );

            result.setDiscrepancies(discrepancies);
            result.setHasDiscrepancies(!discrepancies.isEmpty());

            // 如果有不一致，触发风控熔断
            if (!discrepancies.isEmpty()) {
                log.warn("[{}] 对账发现不一致，触发风控熔断，差异数量: {}", traceContext.getTraceId(), discrepancies.size());
                // 触发风控熔断
                riskManager.triggerCircuitBreak("RECONCILIATION_DISCREPANCY", "对账发现不一致，触发风控熔断");
            } else {
                log.info("[{}] 对账完成，未发现不一致", traceContext.getTraceId());
            }

            return result;
        } catch (Exception e) {
            log.error("[{}] 对账执行失败", traceContext.getTraceId(), e);
            throw new RiskException("对账执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行订单对账
     * @param orders 订单列表
     * @return 对账结果
     */
    public OrderReconciliationResult reconcileOrders(List<Order> orders) {
        TraceContext traceContext = TraceContext.create();
        log.info("[{}] 开始执行订单对账", traceContext.getTraceId());

        try {
            // 执行订单对账逻辑
            OrderReconciliationResult result = OrderReconciliationResult.builder()
                    .reconciliationId(traceContext.getTraceId())
                    .reconciliationTime(Instant.now())
                    .orders(orders)
                    .build();

            // 这里可以添加订单对账的具体逻辑
            // 例如对比本地订单状态与交易所订单状态

            log.info("[{}] 订单对账完成", traceContext.getTraceId());
            return result;
        } catch (Exception e) {
            log.error("[{}] 订单对账执行失败", traceContext.getTraceId(), e);
            throw new RiskException("订单对账执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行成交对账
     * @param trades 成交列表
     * @return 对账结果
     */
    public TradeReconciliationResult reconcileTrades(List<Trade> trades) {
        TraceContext traceContext = TraceContext.create();
        log.info("[{}] 开始执行成交对账", traceContext.getTraceId());

        try {
            // 执行成交对账逻辑
            TradeReconciliationResult result = TradeReconciliationResult.builder()
                    .reconciliationId(traceContext.getTraceId())
                    .reconciliationTime(Instant.now())
                    .trades(trades)
                    .build();

            // 这里可以添加成交对账的具体逻辑
            // 例如对比本地成交记录与交易所成交记录

            log.info("[{}] 成交对账完成", traceContext.getTraceId());
            return result;
        } catch (Exception e) {
            log.error("[{}] 成交对账执行失败", traceContext.getTraceId(), e);
            throw new RiskException("成交对账执行失败: " + e.getMessage(), e);
        }
    }
}
