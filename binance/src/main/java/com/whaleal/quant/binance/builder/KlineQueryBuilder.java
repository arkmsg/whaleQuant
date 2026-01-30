package com.whaleal.quant.binance.builder;

import com.whaleal.quant.binance.model.KlineIntervalEnum;
import com.whaleal.quant.binance.model.KlineResp;
import com.whaleal.quant.binance.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * K线查询构建器
 *
 * <p>使用Builder模式查询K线数据。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * List<KlineResp> klines = klineQueryBuilder
 *     .symbol("BTCUSDT")
 *     .interval(KlineIntervalEnum.MIN_15)
 *     .limit(100)
 *     .fetch();
 * }</pre>
 *
 * @author Binance SDK Team
 */
@Slf4j
public class KlineQueryBuilder {

    private final HttpClient httpClient;
    private final Map<String, String> params;

    private String symbol;
    private KlineIntervalEnum interval;
    private Long startTime;
    private Long endTime;
    private Integer limit;

    public KlineQueryBuilder(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.params = new HashMap<>();
    }

    /**
     * 设置交易对
     *
     * @param symbol 交易对（例如：BTCUSDT）
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /**
     * 设置时间间隔
     *
     * @param interval K线时间间隔
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder interval(KlineIntervalEnum interval) {
        this.interval = interval;
        return this;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 开始时间戳（毫秒）
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder startTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间戳（毫秒）
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder endTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * 设置数据条数（默认500，最大1000）
     *
     * @param limit 数据条数
     * @return KlineQueryBuilder
     */
    public KlineQueryBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 执行查询
     *
     * @return K线数据列表
     * @throws IOException 如果查询失败
     */
    public List<KlineResp> fetch() throws IOException {
        validate();

        params.put("symbol", symbol);
        params.put("interval", interval.getValue());

        if (startTime != null) {
            params.put("startTime", String.valueOf(startTime));
        }

        if (endTime != null) {
            params.put("endTime", String.valueOf(endTime));
        }

        if (limit != null) {
            params.put("limit", String.valueOf(limit));
        }

        String response = httpClient.get("/api/v3/klines", params);
        ObjectMapper mapper = httpClient.getObjectMapper();

        // 解析响应
        List<List<Object>> rawKlines = mapper.readValue(
                response,
                mapper.getTypeFactory().constructCollectionType(List.class, List.class)
        );

        List<KlineResp> klines = new ArrayList<>();
        for (List<Object> raw : rawKlines) {
            KlineResp kline = KlineResp.builder()
                    .openTime(((Number) raw.get(0)).longValue())
                    .open(new BigDecimal(raw.get(1).toString()))
                    .high(new BigDecimal(raw.get(2).toString()))
                    .low(new BigDecimal(raw.get(3).toString()))
                    .close(new BigDecimal(raw.get(4).toString()))
                    .volume(new BigDecimal(raw.get(5).toString()))
                    .closeTime(((Number) raw.get(6)).longValue())
                    .quoteVolume(new BigDecimal(raw.get(7).toString()))
                    .numberOfTrades(((Number) raw.get(8)).intValue())
                    .takerBuyBaseVolume(new BigDecimal(raw.get(9).toString()))
                    .takerBuyQuoteVolume(new BigDecimal(raw.get(10).toString()))
                    .build();

            klines.add(kline);
        }

        return klines;
    }

    /**
     * 校验参数
     */
    private void validate() {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("交易对(symbol)不能为空");
        }
        if (interval == null) {
            throw new IllegalArgumentException("时间间隔(interval)不能为空");
        }
    }
}

