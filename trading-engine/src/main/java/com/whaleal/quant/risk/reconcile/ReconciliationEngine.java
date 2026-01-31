package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.model.Position;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对账引擎
 * 执行具体的对账逻辑，对比三个数据源的持仓数据
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
public class ReconciliationEngine {

    private ReconciliationEngine() {
    }

    /**
     * 执行对账逻辑
     * @param localPositions 本地预估位
     * @param exchangePositions 交易所 API 位
     * @param tradePositions 成交回报累加位
     * @param config 对账配置
     * @return 差异映射
     */
    public static Map<String, PositionDiscrepancy> reconcile(
            List<Position> localPositions,
            List<Position> exchangePositions,
            List<Position> tradePositions,
            ReconciliationConfig config) {

        Map<String, PositionDiscrepancy> discrepancies = new HashMap<>();

        // 将持仓列表转换为映射，以交易对为键
        Map<String, Position> localMap = localPositions.stream()
                .collect(Collectors.toMap(Position::getSymbol, p -> p));

        Map<String, Position> exchangeMap = exchangePositions.stream()
                .collect(Collectors.toMap(Position::getSymbol, p -> p));

        Map<String, Position> tradeMap = tradePositions.stream()
                .collect(Collectors.toMap(Position::getSymbol, p -> p));

        // 合并所有交易对
        Map<String, Position> allPositions = new HashMap<>();
        allPositions.putAll(localMap);
        allPositions.putAll(exchangeMap);
        allPositions.putAll(tradeMap);

        // 对每个交易对执行对账
        for (Map.Entry<String, Position> entry : allPositions.entrySet()) {
            String symbol = entry.getKey();
            Position localPosition = localMap.get(symbol);
            Position exchangePosition = exchangeMap.get(symbol);
            Position tradePosition = tradeMap.get(symbol);

            // 检查三个数据源是否一致
            PositionDiscrepancy discrepancy = checkDiscrepancy(
                    symbol, localPosition, exchangePosition, tradePosition, config);

            if (discrepancy != null) {
                discrepancies.put(symbol, discrepancy);
            }
        }

        return discrepancies;
    }

    /**
     * 检查三个数据源是否一致
     * @param symbol 交易对
     * @param localPosition 本地预估位
     * @param exchangePosition 交易所 API 位
     * @param tradePosition 成交回报累加位
     * @param config 对账配置
     * @return 差异
     */
    private static PositionDiscrepancy checkDiscrepancy(
            String symbol,
            Position localPosition,
            Position exchangePosition,
            Position tradePosition,
            ReconciliationConfig config) {

        // 计算三个数据源的数量差异
        BigDecimal localQuantity = getQuantity(localPosition);
        BigDecimal exchangeQuantity = getQuantity(exchangePosition);
        BigDecimal tradeQuantity = getQuantity(tradePosition);

        // 计算三个数据源的金额差异
        BigDecimal localAmount = getAmount(localPosition);
        BigDecimal exchangeAmount = getAmount(exchangePosition);
        BigDecimal tradeAmount = getAmount(tradePosition);

        // 检查数量差异
        boolean quantityDiscrepancy = checkQuantityDiscrepancy(
                localQuantity, exchangeQuantity, tradeQuantity, config.getPositionQuantityThreshold());

        // 检查金额差异
        boolean amountDiscrepancy = checkAmountDiscrepancy(
                localAmount, exchangeAmount, tradeAmount, config.getPositionAmountThreshold());

        if (quantityDiscrepancy || amountDiscrepancy) {
            return PositionDiscrepancy.builder()
                    .symbol(symbol)
                    .localQuantity(localQuantity)
                    .exchangeQuantity(exchangeQuantity)
                    .tradeQuantity(tradeQuantity)
                    .localAmount(localAmount)
                    .exchangeAmount(exchangeAmount)
                    .tradeAmount(tradeAmount)
                    .hasQuantityDiscrepancy(quantityDiscrepancy)
                    .hasAmountDiscrepancy(amountDiscrepancy)
                    .build();
        }

        return null;
    }

    /**
     * 检查数量差异
     * @param localQuantity 本地预估位数量
     * @param exchangeQuantity 交易所 API 位数量
     * @param tradeQuantity 成交回报累加位数量
     * @param threshold 阈值
     * @return 是否有差异
     */
    private static boolean checkQuantityDiscrepancy(
            BigDecimal localQuantity,
            BigDecimal exchangeQuantity,
            BigDecimal tradeQuantity,
            BigDecimal threshold) {

        // 检查本地预估位与交易所 API 位的差异
        boolean localExchangeDiff = localQuantity.subtract(exchangeQuantity)
                .abs()
                .compareTo(threshold) > 0;

        // 检查本地预估位与成交回报累加位的差异
        boolean localTradeDiff = localQuantity.subtract(tradeQuantity)
                .abs()
                .compareTo(threshold) > 0;

        // 检查交易所 API 位与成交回报累加位的差异
        boolean exchangeTradeDiff = exchangeQuantity.subtract(tradeQuantity)
                .abs()
                .compareTo(threshold) > 0;

        return localExchangeDiff || localTradeDiff || exchangeTradeDiff;
    }

    /**
     * 检查金额差异
     * @param localAmount 本地预估位金额
     * @param exchangeAmount 交易所 API 位金额
     * @param tradeAmount 成交回报累加位金额
     * @param threshold 阈值
     * @return 是否有差异
     */
    private static boolean checkAmountDiscrepancy(
            BigDecimal localAmount,
            BigDecimal exchangeAmount,
            BigDecimal tradeAmount,
            BigDecimal threshold) {

        // 检查本地预估位与交易所 API 位的差异
        boolean localExchangeDiff = localAmount.subtract(exchangeAmount)
                .abs()
                .compareTo(threshold) > 0;

        // 检查本地预估位与成交回报累加位的差异
        boolean localTradeDiff = localAmount.subtract(tradeAmount)
                .abs()
                .compareTo(threshold) > 0;

        // 检查交易所 API 位与成交回报累加位的差异
        boolean exchangeTradeDiff = exchangeAmount.subtract(tradeAmount)
                .abs()
                .compareTo(threshold) > 0;

        return localExchangeDiff || localTradeDiff || exchangeTradeDiff;
    }

    /**
     * 获取持仓数量
     * @param position 持仓
     * @return 数量
     */
    private static BigDecimal getQuantity(Position position) {
        if (position == null) {
            return BigDecimal.ZERO;
        }
        return position.getQuantity() != null ? position.getQuantity() : BigDecimal.ZERO;
    }

    /**
     * 获取持仓金额
     * @param position 持仓
     * @return 金额
     */
    private static BigDecimal getAmount(Position position) {
        if (position == null) {
            return BigDecimal.ZERO;
        }
        if (position.getMarketValue() != null) {
            return position.getMarketValue();
        }
        if (position.getQuantity() != null && position.getCurrentPrice() != null) {
            return position.getQuantity().multiply(position.getCurrentPrice());
        }
        return BigDecimal.ZERO;
    }
}
