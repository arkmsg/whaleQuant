package com.whaleal.quant.binance.service;

import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.binance.connector.client.spot.rest.model.Interval;
import com.whaleal.quant.binance.util.BinanceConfig;
import com.whaleal.quant.base.model.Bar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * K线服务 - 返回统一的 Candlestick 模型
 *
 * @author binance-sdk
 */
public class KlineService {

    private static final Logger log = LoggerFactory.getLogger(KlineService.class);

    private final SpotRestApi spotRestApi;
    private final BinanceConfig config;

    public KlineService(SpotRestApi spotRestApi, BinanceConfig config) {
        this.spotRestApi = spotRestApi;
        this.config = config;
    }

    /**
     * 获取K线数据（统一模型）
     *
     * @param symbol   交易对
     * @param interval 周期
     * @param limit    数量
     * @return Bar 列表
     */
    public List<Bar> getKlines(String symbol, String interval, int limit) {
        try {
            Interval sdkInterval = Interval.valueOf(interval.toUpperCase());
            var response = spotRestApi.klines(symbol, sdkInterval, null, null, null, limit);

            List<Bar> result = new ArrayList<>();
            if (response.getData() != null) {
                for (var kline : response.getData()) {
                    result.add(convertToBar(symbol, interval, kline));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("获取K线失败: {} {}", symbol, interval, e);
            throw new RuntimeException("获取K线失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取历史K线数据
     *
     * @param symbol    交易对
     * @param interval  周期
     * @param startTime 开始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @param limit     数量限制
     * @return Bar 列表
     */
    public List<Bar> getHistoryKlines(String symbol, String interval,
                                               Long startTime, Long endTime, int limit) {
        try {
            Interval sdkInterval = Interval.valueOf(interval.toUpperCase());
            var response = spotRestApi.klines(symbol, sdkInterval, startTime, endTime, null, limit);

            List<Bar> result = new ArrayList<>();
            if (response.getData() != null) {
                for (var kline : response.getData()) {
                    result.add(convertToBar(symbol, interval, kline));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("获取历史K线失败: {} {} {}-{}", symbol, interval, startTime, endTime, e);
            throw new RuntimeException("获取历史K线失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量获取历史数据（自动分页）
     *
     * @param symbol    交易对
     * @param interval  周期
     * @param startTime 开始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @return 所有K线数据
     */
    public List<Bar> fetchAllHistory(String symbol, String interval,
                                              Long startTime, Long endTime) {
        List<Bar> allKlines = new ArrayList<>();
        Long currentStart = startTime;
        int batchSize = 1000; // 币安单次最大1000条

        while (currentStart < endTime) {
            List<Bar> batch = getHistoryKlines(symbol, interval,
                    currentStart, endTime, batchSize);

            if (batch.isEmpty()) {
                break;
            }

            allKlines.addAll(batch);

            // 更新起始时间为最后一条K线的时间+1
            Bar last = batch.get(batch.size() - 1);
            currentStart = last.getEndTime().toEpochMilli() + 1; // 转毫秒

            // 如果返回数量小于请求数量，说明已经没有更多数据
            if (batch.size() < batchSize) {
                break;
            }

            // 限速：避免触发API限制
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("获取历史K线完成: {} {} 共{}条", symbol, interval, allKlines.size());
        return allKlines;
    }

    /**
     * 转换为统一 Bar 模型
     */
    private Bar convertToBar(String symbol, String interval, Object klineData) {
        // 币安K线格式: [openTime, open, high, low, close, volume, closeTime, quoteVolume, trades, ...]
        if (klineData instanceof List<?> list && list.size() >= 8) {
            return Bar.builder()
                    .symbol(symbol)
                    .open(new java.math.BigDecimal(list.get(1).toString()))
                    .high(new java.math.BigDecimal(list.get(2).toString()))
                    .low(new java.math.BigDecimal(list.get(3).toString()))
                    .close(new java.math.BigDecimal(list.get(4).toString()))
                    .volume(new java.math.BigDecimal(list.get(5).toString()))
                    .amount(new java.math.BigDecimal(list.get(7).toString())) // quoteVolume
                    .interval(interval)
                    .startTime(java.time.Instant.ofEpochMilli(((Number) list.get(0)).longValue()))
                    .endTime(java.time.Instant.ofEpochMilli(((Number) list.get(6)).longValue()))
                    .source("BINANCE")
                    .build();
        }
        throw new IllegalArgumentException("无效的K线数据格式");
    }

    /**
     * 检查服务是否可用
     */
    public boolean isAvailable() {
        try {
            spotRestApi.ping();
            return true;
        } catch (Exception e) {
            log.warn("Binance 服务不可用: {}", e.getMessage());
            return false;
        }
    }
}

