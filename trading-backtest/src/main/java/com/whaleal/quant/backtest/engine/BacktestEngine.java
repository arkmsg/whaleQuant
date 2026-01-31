package com.whaleal.quant.backtest.engine;

import com.whaleal.quant.backtest.data.BacktestDataProvider;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.model.BacktestContext;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.strategy.core.StrategyEngine;
import com.whaleal.quant.engine.trading.model.trading.Order;
import com.whaleal.quant.engine.trading.model.trading.Position;
import com.whaleal.quant.strategy.event.MarketDataEvent;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 回测引擎
 * 负责执行策略回测，管理回测过程，生成回测结果
 *
 * @author whaleal
 * @version 1.0.0
 */
public class BacktestEngine {

    private final BacktestConfig config;
    private final BacktestDataProvider dataProvider;
    private final StrategyEngine strategyEngine;
    private final List<BacktestListener> listeners;

    private BacktestContext context;
    private boolean isRunning;

    /**
     * 构造方法
     * @param config 回测配置
     * @param dataProvider 数据提供者
     * @param strategyEngine 策略引擎
     */
    public BacktestEngine(BacktestConfig config, BacktestDataProvider dataProvider, StrategyEngine strategyEngine) {
        this.config = config;
        this.dataProvider = dataProvider;
        this.strategyEngine = strategyEngine;
        this.listeners = new ArrayList<>();
        this.isRunning = false;
    }

    /**
     * 添加回测监听器
     * @param listener 回测监听器
     */
    public void addListener(BacktestListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除回测监听器
     * @param listener 回测监听器
     */
    public void removeListener(BacktestListener listener) {
        listeners.remove(listener);
    }

    /**
     * 执行回测
     * @return 回测结果
     */
    public BacktestResult run() {
        if (isRunning) {
            throw new IllegalStateException("Backtest is already running");
        }

        try {
            isRunning = true;
            notifyStart();

            // 初始化回测上下文
            context = new BacktestContext(config);

            // 加载回测数据
            notifyDataLoading();
            dataProvider.loadData(config.getSymbols(), config.getStartDate(), config.getEndDate());

            // 执行回测
            notifyBacktestStarting();
            executeBacktest();

            // 生成回测结果
            notifyResultGenerating();
            BacktestResult result = generateResult();

            notifyComplete(result);
            return result;

        } catch (Exception e) {
            notifyError(e);
            throw new RuntimeException("Backtest failed", e);
        } finally {
            isRunning = false;
        }
    }

    /**
     * 执行回测核心逻辑
     */
    private void executeBacktest() {
        LocalDateTime currentDate = config.getStartDate();
        LocalDateTime endDate = config.getEndDate();

        while (!currentDate.isAfter(endDate)) {
            // 处理当日数据
            processDailyData(currentDate);

            // 移动到下一个交易日
            currentDate = getNextTradingDay(currentDate);
        }
    }

    /**
     * 处理每日数据
     * @param date 日期
     */
    private void processDailyData(LocalDateTime date) {
        // 获取当日所有股票的市场数据
        Map<String, List<MarketDataEvent>> dailyEvents = dataProvider.getMarketDataEvents(date);

        // 并行处理每个股票的事件
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(10, dailyEvents.size()));

        try {
            for (Map.Entry<String, List<MarketDataEvent>> entry : dailyEvents.entrySet()) {
                String symbol = entry.getKey();
                List<MarketDataEvent> events = entry.getValue();

                executor.submit(() -> {
                    for (MarketDataEvent event : events) {
                        strategyEngine.onMarketData(event);
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Backtest interrupted", e);
        } finally {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
        }

        // 每日结束时的处理
        endOfDayProcessing(date);
    }

    /**
     * 每日结束时的处理
     * @param date 日期
     */
    private void endOfDayProcessing(LocalDateTime date) {
        // 更新回测上下文
        context.updateDailyStats(date);

        // 触发每日结束事件
        notifyEndOfDay(date);
    }

    /**
     * 获取下一个交易日
     * @param currentDate 当前日期
     * @return 下一个交易日
     */
    private LocalDateTime getNextTradingDay(LocalDateTime currentDate) {
        LocalDateTime nextDate = currentDate.plusDays(1);
        while (!dataProvider.isTradingDay(nextDate)) {
            nextDate = nextDate.plusDays(1);
        }
        return nextDate;
    }

    /**
     * 生成回测结果
     * @return 回测结果
     */
    private BacktestResult generateResult() {
        // 收集回测数据
        Map<String, List<Order>> orders = context.getOrders();
        Map<String, List<Position>> positions = context.getPositions();
        Map<String, Double> pnl = context.getPnL();
        Map<String, Double> metrics = context.getMetrics();

        // 创建回测结果
        return BacktestResult.builder()
                .config(config)
                .orders(orders)
                .positions(positions)
                .pnl(pnl)
                .metrics(metrics)
                .startDate(config.getStartDate())
                .endDate(config.getEndDate())
                .build();
    }

    /**
     * 通知回测开始
     */
    private void notifyStart() {
        for (BacktestListener listener : listeners) {
            listener.onBacktestStart(this);
        }
    }

    /**
     * 通知数据加载
     */
    private void notifyDataLoading() {
        for (BacktestListener listener : listeners) {
            listener.onDataLoading(this);
        }
    }

    /**
     * 通知回测开始
     */
    private void notifyBacktestStarting() {
        for (BacktestListener listener : listeners) {
            listener.onBacktestStarting(this);
        }
    }

    /**
     * 通知结果生成
     */
    private void notifyResultGenerating() {
        for (BacktestListener listener : listeners) {
            listener.onResultGenerating(this);
        }
    }

    /**
     * 通知回测完成
     * @param result 回测结果
     */
    private void notifyComplete(BacktestResult result) {
        for (BacktestListener listener : listeners) {
            listener.onBacktestComplete(this, result);
        }
    }

    /**
     * 通知回测错误
     * @param e 异常
     */
    private void notifyError(Exception e) {
        for (BacktestListener listener : listeners) {
            listener.onBacktestError(this, e);
        }
    }

    /**
     * 通知每日结束
     * @param date 日期
     */
    private void notifyEndOfDay(LocalDateTime date) {
        for (BacktestListener listener : listeners) {
            listener.onEndOfDay(this, date);
        }
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private BacktestConfig config;
        private BacktestDataProvider dataProvider;
        private StrategyEngine strategyEngine;

        public Builder config(BacktestConfig config) {
            this.config = config;
            return this;
        }

        public Builder dataProvider(BacktestDataProvider dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }

        public Builder strategyEngine(StrategyEngine strategyEngine) {
            this.strategyEngine = strategyEngine;
            return this;
        }

        public BacktestEngine build() {
            if (config == null) {
                config = new BacktestConfig();
            }
            if (dataProvider == null) {
                throw new IllegalArgumentException("Data provider is required");
            }
            if (strategyEngine == null) {
                throw new IllegalArgumentException("Strategy engine is required");
            }
            return new BacktestEngine(config, dataProvider, strategyEngine);
        }
    }

    /**
     * 创建 Builder
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
