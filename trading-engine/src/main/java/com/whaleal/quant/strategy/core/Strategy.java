package com.whaleal.quant.strategy.core;

import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.strategy.event.OrderEvent;
import com.whaleal.quant.strategy.event.PositionEvent;

/**
 * 策略接口
 *
 * <p>所有策略必须实现的核心接口
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
public interface Strategy {

    /**
     * 初始化策略
     *
     * @param strategyId 策略ID
     * @param strategyName 策略名称
     */
    void initialize(String strategyId, String strategyName);

    /**
     * 处理市场数据事件
     *
     * @param event 市场数据事件
     */
    void onMarketData(MarketDataEvent event);

    /**
     * 处理订单更新事件
     *
     * @param event 订单事件
     */
    void onOrderUpdate(OrderEvent event);

    /**
     * 处理持仓更新事件
     *
     * @param event 持仓事件
     */
    void onPositionUpdate(PositionEvent event);

    /**
     * 启动策略
     */
    void start();

    /**
     * 停止策略
     */
    void stop();

    /**
     * 获取策略ID
     *
     * @return 策略ID
     */
    String getStrategyId();

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    String getStrategyName();
}
