package com.whaleal.quant.backtest.data;

import com.whaleal.quant.strategy.event.MarketDataEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 回测数据提供者
 * 用于加载和提供回测所需的市场数据
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface BacktestDataProvider {

    /**
     * 加载回测数据
     * @param symbols 交易对符号集合
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    void loadData(Set<String> symbols, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取指定日期的市场数据事件
     * @param date 日期
     * @return 市场数据事件映射
     */
    Map<String, List<MarketDataEvent>> getMarketDataEvents(LocalDateTime date);

    /**
     * 检查指定日期是否为交易日
     * @param date 日期
     * @return 是否为交易日
     */
    boolean isTradingDay(LocalDateTime date);

    /**
     * 获取下一个交易日
     * @param date 日期
     * @return 下一个交易日
     */
    LocalDateTime getNextTradingDay(LocalDateTime date);

    /**
     * 获取前一个交易日
     * @param date 日期
     * @return 前一个交易日
     */
    LocalDateTime getPreviousTradingDay(LocalDateTime date);

    /**
     * 获取交易日期列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易日期列表
     */
    List<LocalDateTime> getTradingDays(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 清理数据
     */
    void cleanup();
}
