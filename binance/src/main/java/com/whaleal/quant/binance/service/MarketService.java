package com.whaleal.quant.binance.service;

import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.binance.connector.client.spot.rest.model.Interval;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaleal.quant.binance.util.BinanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 行情服务（基于币安官方Spot SDK 7.0.1）
 */
public class MarketService {

    private static final Logger log = LoggerFactory.getLogger(MarketService.class);

    private final SpotRestApi spotRestApi;
    private final BinanceConfig config;
    private final ObjectMapper objectMapper;

    public MarketService(SpotRestApi spotRestApi, BinanceConfig config) {
        this.spotRestApi = spotRestApi;
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取24小时价格变动统计（返回JSON字符串）
     */
    public String getTicker(String symbol) {
        try {
            var response = spotRestApi.ticker24hr(symbol, null, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("获取Ticker失败: {}", symbol, e);
            throw new RuntimeException("获取Ticker失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取最新价格（返回JSON字符串）
     */
    public String getLatestPrice(String symbol) {
        try {
            var response = spotRestApi.tickerPrice(symbol, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("获取最新价格失败: {}", symbol, e);
            throw new RuntimeException("获取最新价格失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取K线数据（返回JSON字符串）
     */
    public String getKlines(String symbol, String interval, Integer limit) {
        try {
            Interval sdkInterval = Interval.valueOf(interval);
            var response = spotRestApi.klines(symbol, sdkInterval, null, null, null, limit);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("获取K线数据失败: {} {}", symbol, interval, e);
            throw new RuntimeException("获取K线数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取深度信息（返回JSON字符串）
     */
    public String getDepth(String symbol, Integer limit) {
        try {
            var response = spotRestApi.depth(symbol, limit);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("获取深度信息失败: {} limit={}", symbol, limit, e);
            throw new RuntimeException("获取深度信息失败: " + e.getMessage(), e);
        }
    }


    /**
     * 获取最优挂单（买一卖一）
     */
    public String getBookTicker(String symbol) {
        try {
            var response = spotRestApi.tickerBookTicker(symbol, null);
            return objectMapper.writeValueAsString(response.getData());
        } catch (Exception e) {
            log.error("获取最优挂单失败: {}", symbol, e);
            throw new RuntimeException("获取最优挂单失败: " + e.getMessage(), e);
        }
    }
}
