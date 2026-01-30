package com.whaleal.quant.backtest.engine;

import com.whaleal.quant.backtest.result.BacktestResult;

import java.time.LocalDateTime;

/**
 * 回测监听器
 * 用于监听回测过程中的各种事件
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface BacktestListener {

    /**
     * 回测开始事件
     * @param engine 回测引擎
     */
    default void onBacktestStart(BacktestEngine engine) {
    }

    /**
     * 数据加载事件
     * @param engine 回测引擎
     */
    default void onDataLoading(BacktestEngine engine) {
    }

    /**
     * 回测开始执行事件
     * @param engine 回测引擎
     */
    default void onBacktestStarting(BacktestEngine engine) {
    }

    /**
     * 每日结束事件
     * @param engine 回测引擎
     * @param date 日期
     */
    default void onEndOfDay(BacktestEngine engine, LocalDateTime date) {
    }

    /**
     * 结果生成事件
     * @param engine 回测引擎
     */
    default void onResultGenerating(BacktestEngine engine) {
    }

    /**
     * 回测完成事件
     * @param engine 回测引擎
     * @param result 回测结果
     */
    default void onBacktestComplete(BacktestEngine engine, BacktestResult result) {
    }

    /**
     * 回测错误事件
     * @param engine 回测引擎
     * @param e 异常
     */
    default void onBacktestError(BacktestEngine engine, Exception e) {
    }
}