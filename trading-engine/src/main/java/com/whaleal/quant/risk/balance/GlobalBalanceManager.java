package com.whaleal.quant.risk.balance;

import com.whaleal.quant.model.Order;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局资金管理器
 * 实现影子账户和资金水位线逻辑
 * 管理多交易所、多币种的资金账户
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
public class GlobalBalanceManager {

    // 交易所资金账户：exchange -> currency -> Balance
    private final Map<String, Map<String, Balance>> exchangeBalances = new ConcurrentHashMap<>();

    // 冻结资金：orderId -> FrozenFunds
    private final Map<String, FrozenFunds> frozenFunds = new ConcurrentHashMap<>();

    // 资金水位线配置
    private final BalanceConfig config;

    public GlobalBalanceManager(BalanceConfig config) {
        this.config = config;
    }

    /**
     * 更新交易所资金余额
     * @param exchange 交易所名称
     * @param currency 货币代码
     * @param available 可用资金
     * @param total 总资金
     */
    public void updateBalance(String exchange, String currency, BigDecimal available, BigDecimal total) {
        exchangeBalances.computeIfAbsent(exchange, k -> new ConcurrentHashMap<>())
                .put(currency, new Balance(available, total));
        log.info("Updated balance for {}:{}, available={}, total={}", exchange, currency, available, total);
    }

    /**
     * 获取交易所资金余额
     * @param exchange 交易所名称
     * @param currency 货币代码
     * @return 资金余额
     */
    public Balance getBalance(String exchange, String currency) {
        Map<String, Balance> balances = exchangeBalances.get(exchange);
        if (balances != null) {
            return balances.get(currency);
        }
        return Balance.ZERO;
    }

    /**
     * 冻结资金
     * @param order 订单
     * @param amount 冻结金额
     * @param currency 货币代码
     * @return 是否冻结成功
     */
    public boolean freezeFunds(Order order, BigDecimal amount, String currency) {
        String exchange = order.getMarket();
        Balance balance = getBalance(exchange, currency);

        // 计算可用资金（总可用资金 - 已冻结资金）
        BigDecimal alreadyFrozen = getFrozenAmount(exchange, currency);
        BigDecimal actualAvailable = balance.getAvailable().subtract(alreadyFrozen);

        if (actualAvailable.compareTo(amount) < 0) {
            log.warn("Insufficient funds to freeze: required={}, available={} for {}:{}",
                    amount, actualAvailable, exchange, currency);
            return false;
        }

        // 冻结资金
        frozenFunds.put(order.getOrderId(), new FrozenFunds(order, amount, currency));
        log.info("Frozen funds for order {}: {} {}", order.getOrderId(), amount, currency);
        return true;
    }

    /**
     * 释放冻结资金
     * @param orderId 订单ID
     */
    public void releaseFrozenFunds(String orderId) {
        FrozenFunds funds = frozenFunds.remove(orderId);
        if (funds != null) {
            log.info("Released frozen funds for order {}: {} {}",
                    orderId, funds.getAmount(), funds.getCurrency());
        }
    }

    /**
     * 获取已冻结资金总额
     * @param exchange 交易所名称
     * @param currency 货币代码
     * @return 已冻结资金
     */
    public BigDecimal getFrozenAmount(String exchange, String currency) {
        return frozenFunds.values().stream()
                .filter(funds -> funds.getOrder().getMarket().equals(exchange) &&
                        funds.getCurrency().equals(currency))
                .map(FrozenFunds::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 检查资金水位线
     * @param exchange 交易所名称
     * @param currency 货币代码
     * @return 是否低于水位线
     */
    public boolean isBelowWatermark(String exchange, String currency) {
        Balance balance = getBalance(exchange, currency);
        BigDecimal total = balance.getTotal();
        BigDecimal watermark = total.multiply(config.getWatermarkThreshold());
        return balance.getAvailable().compareTo(watermark) < 0;
    }

    /**
     * 获取全局资金摘要
     * @return 资金摘要
     */
    public BalanceSummary getBalanceSummary() {
        Map<String, Map<String, Balance>> summary = new HashMap<>();
        Map<String, Map<String, BigDecimal>> frozenSummary = new HashMap<>();

        // 构建资金摘要
        for (Map.Entry<String, Map<String, Balance>> entry : exchangeBalances.entrySet()) {
            String exchange = entry.getKey();
            summary.put(exchange, entry.getValue());

            // 构建冻结资金摘要
            Map<String, BigDecimal> frozenByCurrency = new HashMap<>();
            for (String currency : entry.getValue().keySet()) {
                frozenByCurrency.put(currency, getFrozenAmount(exchange, currency));
            }
            frozenSummary.put(exchange, frozenByCurrency);
        }

        return new BalanceSummary(summary, frozenSummary);
    }

    /**
     * 资金余额类
     */
    public static class Balance {
        public static final Balance ZERO = new Balance(BigDecimal.ZERO, BigDecimal.ZERO);

        private final BigDecimal available;
        private final BigDecimal total;

        public Balance(BigDecimal available, BigDecimal total) {
            this.available = available;
            this.total = total;
        }

        public BigDecimal getAvailable() {
            return available;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    /**
     * 冻结资金类
     */
    private static class FrozenFunds {
        private final Order order;
        private final BigDecimal amount;
        private final String currency;

        public FrozenFunds(Order order, BigDecimal amount, String currency) {
            this.order = order;
            this.amount = amount;
            this.currency = currency;
        }

        public Order getOrder() {
            return order;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }
    }

    /**
     * 资金摘要类
     */
    public static class BalanceSummary {
        private final Map<String, Map<String, Balance>> balances;
        private final Map<String, Map<String, BigDecimal>> frozenFunds;

        public BalanceSummary(Map<String, Map<String, Balance>> balances,
                            Map<String, Map<String, BigDecimal>> frozenFunds) {
            this.balances = balances;
            this.frozenFunds = frozenFunds;
        }

        public Map<String, Map<String, Balance>> getBalances() {
            return balances;
        }

        public Map<String, Map<String, BigDecimal>> getFrozenFunds() {
            return frozenFunds;
        }
    }
}
