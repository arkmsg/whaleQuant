package com.whaleal.quant.longport.builder;

import com.longport.quote.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * K线查询构建器
 *
 * <p>使用Builder模式查询K线数据。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 获取最近100根1分钟K线
 * List<Candlestick> klines = quoteService.getKlines()
 *     .symbol("AAPL.US")
 *     .period(Period.Min_1)
 *     .count(100)
 *     .fetch();
 *
 * // 获取日K线（不复权）
 * List<Candlestick> klines = quoteService.getKlines()
 *     .symbol("AAPL.US")
 *     .period(Period.Day)
 *     .count(200)
 *     .noAdjust()
 *     .fetch();
 * }</pre>
 *
 * @author Longport SDK Team
 */
@Slf4j
public class KlineQueryBuilder {

    private final QuoteContext quoteContext;

    private String symbol;
    private Period period = Period.Day; // 默认日K
    private int count = 100; // 默认100根
    private AdjustType adjustType = AdjustType.NoAdjust; // 默认不复权
    private TradeSessions tradeSessions = TradeSessions.All; // 默认包含所有时段

    public KlineQueryBuilder(QuoteContext quoteContext) {
        this.quoteContext = quoteContext;
    }

    /**
     * 设置股票代码
     *
     * @param symbol 股票代码 (例如: "AAPL.US")
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * 设置K线周期
     *
     * @param period K线周期
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder period(Period period) {
        this.period = period;
        return this;
    }

    /**
     * 设置为1分钟K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder oneMinute() {
        this.period = Period.Min_1;
        return this;
    }

    /**
     * 设置为5分钟K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder fiveMinute() {
        this.period = Period.Min_5;
        return this;
    }

    /**
     * 设置为15分钟K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder fifteenMinute() {
        this.period = Period.Min_15;
        return this;
    }

    /**
     * 设置为30分钟K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder thirtyMinute() {
        this.period = Period.Min_30;
        return this;
    }

    /**
     * 设置为60分钟K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder sixtyMinute() {
        this.period = Period.Min_60;
        return this;
    }

    /**
     * 设置为日K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder daily() {
        this.period = Period.Day;
        return this;
    }

    /**
     * 设置为周K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder weekly() {
        this.period = Period.Week;
        return this;
    }

    /**
     * 设置为月K线
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder monthly() {
        this.period = Period.Month;
        return this;
    }

    /**
     * 设置K线数量
     *
     * @param count K线数量
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder count(int count) {
        this.count = count;
        return this;
    }

    /**
     * 设置复权类型
     *
     * @param adjustType 复权类型
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder adjustType(AdjustType adjustType) {
        this.adjustType = adjustType;
        return this;
    }

    /**
     * 不复权（默认）
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder noAdjust() {
        this.adjustType = AdjustType.NoAdjust;
        return this;
    }

    /**
     * 前复权
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder forwardAdjust() {
        this.adjustType = AdjustType.ForwardAdjust;
        return this;
    }

    /**
     * 设置交易时段
     *
     * @param tradeSessions 交易时段
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder tradeSessions(TradeSessions tradeSessions) {
        this.tradeSessions = tradeSessions;
        return this;
    }

    /**
     * 仅日内时段（不包含盘前盘后）
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder intradayOnly() {
        this.tradeSessions = TradeSessions.Intraday;
        return this;
    }

    /**
     * 所有时段（包含盘前盘后夜盘）- 默认
     *
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder allSessions() {
        this.tradeSessions = TradeSessions.All;
        return this;
    }

    /**
     * 执行查询
     *
     * @return K线数据列表
     * @throws Exception 如果查询失败
     */
    public List<Candlestick> fetch() throws Exception {
        validate();

        Candlestick[] candlesticks = quoteContext.getCandlesticks(
            symbol,
            period,
            count,
            adjustType,
            tradeSessions
        ).get();

        if (candlesticks == null || candlesticks.length == 0) {
            log.debug("未获取到K线数据: {} | 周期: {}", symbol, period);
            return Collections.emptyList();
        }

        log.debug("获取K线数据: {} | {} | {} 根", symbol, period, candlesticks.length);
        return Arrays.asList(candlesticks);
    }

    /**
     * 验证必填参数
     */
    private void validate() {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("股票代码不能为空");
        }
        if (period == null) {
            throw new IllegalArgumentException("K线周期不能为空");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("K线数量必须大于0");
        }
    }
}

