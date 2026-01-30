package com.whaleal.quant.provider;

import com.whaleal.quant.model.Bar;
import com.whaleal.quant.model.Ticker;
import com.whaleal.quant.enums.Interval;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

/**
 * 行情数据提供者接口
 * 定义了获取行情数据的方法
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface MarketProvider {

    /**
     * 获取实时行情
     *
     * @param symbol 交易对符号
     * @return 行情数据
     */
    Ticker getTicker(String symbol);

    /**
     * 批量获取实时行情
     *
     * @param symbols 交易对符号列表
     * @return 行情数据列表
     */
    List<Ticker> getTickers(List<String> symbols);

    /**
     * 获取K线数据
     *
     * @param symbol   交易对符号
     * @param interval 时间间隔
     * @param limit    数量限制
     * @return K线数据列表
     */
    List<Bar> getKlines(String symbol, Interval interval, int limit);

    /**
     * 获取指定时间范围的K线数据
     *
     * @param symbol   交易对符号
     * @param interval 时间间隔
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @param limit    数量限制
     * @return K线数据列表
     */
    List<Bar> getKlines(String symbol, Interval interval, Instant startTime, Instant endTime, int limit);

    /**
     * 订阅实时行情
     *
     * @param symbol   交易对符号
     * @param callback 回调函数
     */
    void subscribeTicker(String symbol, Consumer<Ticker> callback);

    /**
     * 订阅K线数据
     *
     * @param symbol   交易对符号
     * @param interval 时间间隔
     * @param callback 回调函数
     */
    void subscribeKline(String symbol, Interval interval, Consumer<Bar> callback);

    /**
     * 取消订阅实时行情
     *
     * @param symbol 交易对符号
     */
    void unsubscribeTicker(String symbol);

    /**
     * 取消订阅K线数据
     *
     * @param symbol   交易对符号
     * @param interval 时间间隔
     */
    void unsubscribeKline(String symbol, Interval interval);

    /**
     * 关闭提供者
     */
    void close();
}
