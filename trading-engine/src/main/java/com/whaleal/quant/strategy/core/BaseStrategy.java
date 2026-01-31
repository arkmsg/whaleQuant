package com.whaleal.quant.strategy.core;

import com.whaleal.quant.strategy.event.EventBusManager;
import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.strategy.event.OrderEvent;
import com.whaleal.quant.strategy.event.PositionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 策略基类
 *
 * <p>所有策略的基类，提供通用的策略生命周期管理
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Slf4j
public abstract class BaseStrategy implements Strategy {

    protected String strategyId;
    protected String strategyName;
    protected EventBusManager eventBusManager;

    @Override
    public void initialize(String strategyId, String strategyName) {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.eventBusManager = EventBusManager.getInstance();
        log.info("策略初始化: {}", strategyName);
    }

    @Override
    public void onMarketData(MarketDataEvent event) {
        // 子类实现
    }

    @Override
    public void onOrderUpdate(OrderEvent event) {
        // 子类实现
    }

    @Override
    public void onPositionUpdate(PositionEvent event) {
        // 子类实现
    }

    @Override
    public void start() {
        log.info("策略启动: {}", strategyName);
    }

    @Override
    public void stop() {
        log.info("策略停止: {}", strategyName);
    }

    @Override
    public String getStrategyId() {
        return strategyId;
    }

    @Override
    public String getStrategyName() {
        return strategyName;
    }
}
