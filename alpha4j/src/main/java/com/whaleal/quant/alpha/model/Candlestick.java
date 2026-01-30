package com.whaleal.quant.alpha.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * K线数据模型（轻量级，仅用于Alpha因子计算）
 *
 * ⚠️ 重要变更：所有金额字段使用double类型，提升性能10-100倍
 *
 * @author arkmsg
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candlestick {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 时间戳（Unix时间戳，秒）
     */
    private long timestamp;

    /**
     * 开盘价
     */
    private double open;

    /**
     * 最高价
     */
    private double high;

    /**
     * 最低价
     */
    private double low;

    /**
     * 收盘价
     */
    private double close;

    /**
     * 成交量
     */
    private long volume;

    /**
     * 成交额（可选，用于计算VWAP）
     */
    private double amount;

    /**
     * 换手率（可选）
     */
    private double turnoverRate;

    /**
     * 计算VWAP（成交量加权平均价）
     *
     * @return VWAP值
     */
    public double getVwap() {
        if (volume == 0 || amount == 0) {
            return close;
        }
        return amount / volume;
    }

    /**
     * 从 stocks-indicator-sdk 的 Candlestick 转换
     *
     * @param source stocks-indicator-sdk 的 Candlestick
     * @return alpha4j 的高性能 Candlestick
     */
    public static Candlestick from(com.whaleal.quant.indicator.model.Candlestick source) {
        if (source == null) {
            return null;
        }
        return Candlestick.builder()
                .symbol(source.getSymbol())
                .timestamp(source.getTimestamp())
                .open(source.getOpen())
                .high(source.getHigh())
                .low(source.getLow())
                .close(source.getClose())
                .volume(source.getVolume())
                .amount(source.getAmount())
                .turnoverRate(source.getTurnoverRate())
                .build();
    }

    /**
     * 转换为 stocks-indicator-sdk 的 Candlestick
     *
     * @return stocks-indicator-sdk 的 Candlestick
     */
    public com.whaleal.quant.indicator.model.Candlestick toIndicatorCandlestick() {
        return com.whaleal.quant.indicator.model.Candlestick.builder()
                .symbol(symbol)
                .timestamp(timestamp)
                .open(open)
                .high(high)
                .low(low)
                .close(close)
                .volume(volume)
                .amount(amount)
                .turnoverRate(turnoverRate)
                .build();
    }
}
