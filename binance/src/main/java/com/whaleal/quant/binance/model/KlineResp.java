package com.whaleal.quant.binance.model;

import com.whaleal.quant.base.model.Bar;

import java.math.BigDecimal;

/**
 * K线数据响应
 *
 * @author Binance SDK Team
 */
public class KlineResp {

    /**
     * 开盘时间
     */
    private Long openTime;

    /**
     * 开盘价
     */
    private BigDecimal open;

    /**
     * 最高价
     */
    private BigDecimal high;

    /**
     * 最低价
     */
    private BigDecimal low;

    /**
     * 收盘价
     */
    private BigDecimal close;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 收盘时间
     */
    private Long closeTime;

    /**
     * 成交额
     */
    private BigDecimal quoteVolume;

    /**
     * 成交笔数
     */
    private Integer numberOfTrades;

    /**
     * 主动买入成交量
     */
    private BigDecimal takerBuyBaseVolume;

    /**
     * 主动买入成交额
     */
    private BigDecimal takerBuyQuoteVolume;

    // Getter and Setter methods
    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Integer getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(Integer numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public BigDecimal getTakerBuyBaseVolume() {
        return takerBuyBaseVolume;
    }

    public void setTakerBuyBaseVolume(BigDecimal takerBuyBaseVolume) {
        this.takerBuyBaseVolume = takerBuyBaseVolume;
    }

    public BigDecimal getTakerBuyQuoteVolume() {
        return takerBuyQuoteVolume;
    }

    public void setTakerBuyQuoteVolume(BigDecimal takerBuyQuoteVolume) {
        this.takerBuyQuoteVolume = takerBuyQuoteVolume;
    }

    /**
     * 转换为统一 Bar 模型
     *
     * @param symbol 交易对 (如 BTCUSDT)
     * @return 统一 K 线模型
     */
    public Bar toBar(String symbol) {
        return Bar.builder()
                .symbol(symbol)
                .startTime(openTime != null ? java.time.Instant.ofEpochMilli(openTime) : null)
                .endTime(closeTime != null ? java.time.Instant.ofEpochMilli(closeTime) : null)
                .open(open)
                .high(high)
                .low(low)
                .close(close)
                .volume(volume)
                .amount(quoteVolume)
                .source("BINANCE")
                .build();
    }

    /**
     * 创建Builder实例
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder类 - 用于构建KlineResp实例
     */
    public static class Builder {
        private Long openTime;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private BigDecimal volume;
        private Long closeTime;
        private BigDecimal quoteVolume;
        private Integer numberOfTrades;
        private BigDecimal takerBuyBaseVolume;
        private BigDecimal takerBuyQuoteVolume;

        public Builder openTime(Long openTime) {
            this.openTime = openTime;
            return this;
        }

        public Builder open(BigDecimal open) {
            this.open = open;
            return this;
        }

        public Builder high(BigDecimal high) {
            this.high = high;
            return this;
        }

        public Builder low(BigDecimal low) {
            this.low = low;
            return this;
        }

        public Builder close(BigDecimal close) {
            this.close = close;
            return this;
        }

        public Builder volume(BigDecimal volume) {
            this.volume = volume;
            return this;
        }

        public Builder closeTime(Long closeTime) {
            this.closeTime = closeTime;
            return this;
        }

        public Builder quoteVolume(BigDecimal quoteVolume) {
            this.quoteVolume = quoteVolume;
            return this;
        }

        public Builder numberOfTrades(Integer numberOfTrades) {
            this.numberOfTrades = numberOfTrades;
            return this;
        }

        public Builder takerBuyBaseVolume(BigDecimal takerBuyBaseVolume) {
            this.takerBuyBaseVolume = takerBuyBaseVolume;
            return this;
        }

        public Builder takerBuyQuoteVolume(BigDecimal takerBuyQuoteVolume) {
            this.takerBuyQuoteVolume = takerBuyQuoteVolume;
            return this;
        }

        public KlineResp build() {
            KlineResp klineResp = new KlineResp();
            klineResp.setOpenTime(openTime);
            klineResp.setOpen(open);
            klineResp.setHigh(high);
            klineResp.setLow(low);
            klineResp.setClose(close);
            klineResp.setVolume(volume);
            klineResp.setCloseTime(closeTime);
            klineResp.setQuoteVolume(quoteVolume);
            klineResp.setNumberOfTrades(numberOfTrades);
            klineResp.setTakerBuyBaseVolume(takerBuyBaseVolume);
            klineResp.setTakerBuyQuoteVolume(takerBuyQuoteVolume);
            return klineResp;
        }
    }
}

