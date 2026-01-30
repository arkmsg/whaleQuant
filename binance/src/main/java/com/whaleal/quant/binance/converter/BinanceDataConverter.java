package com.whaleal.quant.binance.converter;

import com.whaleal.quant.binance.model.KlineResp;
import com.whaleal.quant.binance.model.TickerResp;

import com.whaleal.quant.base.model.Bar;
import com.whaleal.quant.base.model.Ticker;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 币安SDK数据转换器
 * 将币安官方SDK的数据类型转换为我们统一的数据模型
 *
 * @author binance-sdk
 * @version 1.0.0
 */
@UtilityClass
public class BinanceDataConverter {

    /**
     * 转换币安K线数据为我们的Bar
     *
     * 币安K线数据格式（数组）：
     * [
     *   [
     *     1499040000000,      // 开盘时间
     *     "0.01634790",       // 开盘价
     *     "0.80000000",       // 最高价
     *     "0.01575800",       // 最低价
     *     "0.01577100",       // 收盘价
     *     "148976.11427815",  // 成交量
     *     1499644799999,      // 收盘时间
     *     "2434.19055334",    // 成交额
     *     308,                // 成交笔数
     *     "1756.87402397",    // 主动买入成交量
     *     "28.46694368",      // 主动买入成交额
     *     "17928899.62484339" // 忽略
     *   ]
     * ]
     *
     * @param klineData 币安K线数据数组
     * @return 我们统一的Bar数据模型
     */
    public Bar toBar(List<Object> klineData) {
        if (klineData == null || klineData.size() < 11) {
            return null;
        }

        try {
            Bar bar = new Bar();
            bar.setTimestamp(java.time.Instant.ofEpochMilli(Long.parseLong(klineData.get(0).toString())));
            bar.setOpen(new java.math.BigDecimal(klineData.get(1).toString()));
            bar.setHigh(new java.math.BigDecimal(klineData.get(2).toString()));
            bar.setLow(new java.math.BigDecimal(klineData.get(3).toString()));
            bar.setClose(new java.math.BigDecimal(klineData.get(4).toString()));
            bar.setVolume(new java.math.BigDecimal(klineData.get(5).toString()));
            bar.setAmount(new java.math.BigDecimal(klineData.get(7).toString()));
            return bar;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 批量转换币安K线数据列表为我们的Bar列表
     *
     * @param klineDataList 币安K线数据列表
     * @return 我们统一的Bar数据模型列表
     */
    public List<Bar> toBarList(List<List<Object>> klineDataList) {
        if (klineDataList == null || klineDataList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Bar> result = new ArrayList<>(klineDataList.size());
        for (List<Object> klineData : klineDataList) {
            Bar bar = toBar(klineData);
            if (bar != null) {
                result.add(bar);
            }
        }
        return result;
    }

    /**
     * 从JSON字符串解析K线数据
     *
     * @param jsonResponse 币安API返回的JSON字符串
     * @return 我们统一的Bar数据模型列表
     */
    public List<Bar> parseKlineResponse(String jsonResponse) {
        // 这里可以添加JSON解析逻辑
        // 简化处理，假设已经在外部解析好了
        return new ArrayList<>();
    }

    /**
     * 转换币安TickerResp为统一的Ticker模型
     *
     * @param tickerResp 币安行情响应
     * @return 统一的Ticker模型
     */
    public Ticker convertToTicker(TickerResp tickerResp) {
        if (tickerResp == null) {
            return null;
        }

        Ticker ticker = new Ticker();
        ticker.setSymbol(tickerResp.getSymbol());
        ticker.setTimestamp(Instant.now());
        return ticker;
    }

    /**
     * 转换币安KlineResp列表为统一的Bar模型列表
     *
     * @param symbol 交易对
     * @param klineResps 币安K线响应列表
     * @return 统一的Bar模型列表
     */
    public List<Bar> convertToBars(String symbol, List<KlineResp> klineResps) {
        if (klineResps == null || klineResps.isEmpty()) {
            return new ArrayList<>();
        }

        List<Bar> bars = new ArrayList<>(klineResps.size());
        for (KlineResp klineResp : klineResps) {
            Bar bar = new Bar();
            bar.setSymbol(symbol);
            bar.setTimestamp(Instant.now());
            bars.add(bar);
        }
        return bars;
    }
}

