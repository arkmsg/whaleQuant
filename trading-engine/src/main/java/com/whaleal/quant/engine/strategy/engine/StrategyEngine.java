package com.whaleal.quant.strategy.engine;

import com.google.common.eventbus.Subscribe;
import com.whaleal.quant.strategy.core.Strategy;
import com.whaleal.quant.strategy.event.EventBusManager;
import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.strategy.event.OrderEvent;
import com.whaleal.quant.strategy.event.PositionEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略引擎
 * 
 * <p>负责管理策略的生命周期和事件分发
 * 
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Slf4j
public class StrategyEngine {
    
    private Map<String, Strategy> strategies = new ConcurrentHashMap<>();
    private EventBusManager eventBusManager;
    
    public StrategyEngine() {
        this.eventBusManager = EventBusManager.getInstance();
        registerEventHandlers();
    }
    
    private void registerEventHandlers() {
        eventBusManager.register(this);
    }
    
    public void addStrategy(String strategyId, Strategy strategy) {
        strategies.put(strategyId, strategy);
        log.info("策略添加: {}", strategyId);
    }
    
    public void removeStrategy(String strategyId) {
        strategies.remove(strategyId);
        log.info("策略移除: {}", strategyId);
    }
    
    public void startAllStrategies() {
        strategies.values().forEach(Strategy::start);
        log.info("所有策略已启动");
    }
    
    public void stopAllStrategies() {
        strategies.values().forEach(Strategy::stop);
        log.info("所有策略已停止");
    }
    
    @Subscribe
    public void onMarketDataEvent(MarketDataEvent event) {
        strategies.values().forEach(strategy -> {
            try {
                strategy.onMarketData(event);
            } catch (Exception e) {
                log.error("策略处理市场数据事件失败: {}", e.getMessage(), e);
            }
        });
    }
    
    @Subscribe
    public void onOrderEvent(OrderEvent event) {
        strategies.values().forEach(strategy -> {
            try {
                strategy.onOrderUpdate(event);
            } catch (Exception e) {
                log.error("策略处理订单事件失败: {}", e.getMessage(), e);
            }
        });
    }
    
    @Subscribe
    public void onPositionEvent(PositionEvent event) {
        strategies.values().forEach(strategy -> {
            try {
                strategy.onPositionUpdate(event);
            } catch (Exception e) {
                log.error("策略处理持仓事件失败: {}", e.getMessage(), e);
            }
        });
    }
    
    public void publishMarketDataEvent(MarketDataEvent event) {
        eventBusManager.post(event);
    }
    
    public void publishOrderEvent(OrderEvent event) {
        eventBusManager.post(event);
    }
    
    public void publishPositionEvent(PositionEvent event) {
        eventBusManager.post(event);
    }
}