package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.whaleal.quant.base.model.Ticker;

import java.math.BigDecimal;

/**
 * 24小时价格变动统计响应
 *
 * @author Binance SDK Team
 */
public class TickerResp {

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 价格变动
     */
    @JsonProperty("priceChange")
    private BigDecimal priceChange;

    /**
     * 价格变动百分比
     */
    @JsonProperty("priceChangePercent")
    private BigDecimal priceChangePercent;

    /**
     * 加权平均价
     */
    @JsonProperty("weightedAvgPrice")
    private BigDecimal weightedAvgPrice;

    /**
     * 最新价格
     */
    @JsonProperty("lastPrice")
    private BigDecimal lastPrice;

    /**
     * 最新成交量
     */
    @JsonProperty("lastQty")
    private BigDecimal lastQty;

    /**
     * 开盘价
     */
    @JsonProperty("openPrice")
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    @JsonProperty("highPrice")
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    @JsonProperty("lowPrice")
    private BigDecimal lowPrice;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 成交额
     */
    @JsonProperty("quoteVolume")
    private BigDecimal quoteVolume;

    /**
     * 统计开始时间
     */
    @JsonProperty("openTime")
    private Long openTime;

    /**
     * 统计关闭时间
     */
    @JsonProperty("closeTime")
    private Long closeTime;

    /**
     * 首笔交易ID
     */
    @JsonProperty("firstId")
    private Long firstId;

    /**
     * 末笔交易ID
     */
    @JsonProperty("lastId")
    private Long lastId;

    /**
     * 成交笔数
     */
    private Long count;

    // Getter methods
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(BigDecimal priceChange) {
        this.priceChange = priceChange;
    }

    public BigDecimal getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(BigDecimal priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public BigDecimal getWeightedAvgPrice() {
        return weightedAvgPrice;
    }

    public void setWeightedAvgPrice(BigDecimal weightedAvgPrice) {
        this.weightedAvgPrice = weightedAvgPrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getLastQty() {
        return lastQty;
    }

    public void setLastQty(BigDecimal lastQty) {
        this.lastQty = lastQty;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public Long getFirstId() {
        return firstId;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * 转换为统一 Ticker 模型
     *
     * @return 统一 Ticker 模型
     */
    public Ticker toTicker() {
        return Ticker.builder()
                .symbol(symbol)
                .timestamp(closeTime != null ? java.time.Instant.ofEpochMilli(closeTime) : java.time.Instant.now())
                .lastPrice(lastPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
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
     * Builder类 - 用于构建TickerResp实例
     */
    public static class Builder {
        private String symbol;
        private BigDecimal priceChange;
        private BigDecimal priceChangePercent;
        private BigDecimal weightedAvgPrice;
        private BigDecimal lastPrice;
        private BigDecimal lastQty;
        private BigDecimal openPrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal volume;
        private BigDecimal quoteVolume;
        private Long openTime;
        private Long closeTime;
        private Long firstId;
        private Long lastId;
        private Long count;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder priceChange(BigDecimal priceChange) {
            this.priceChange = priceChange;
            return this;
        }

        public Builder priceChangePercent(BigDecimal priceChangePercent) {
            this.priceChangePercent = priceChangePercent;
            return this;
        }

        public Builder weightedAvgPrice(BigDecimal weightedAvgPrice) {
            this.weightedAvgPrice = weightedAvgPrice;
            return this;
        }

        public Builder lastPrice(BigDecimal lastPrice) {
            this.lastPrice = lastPrice;
            return this;
        }

        public Builder lastQty(BigDecimal lastQty) {
            this.lastQty = lastQty;
            return this;
        }

        public Builder openPrice(BigDecimal openPrice) {
            this.openPrice = openPrice;
            return this;
        }

        public Builder highPrice(BigDecimal highPrice) {
            this.highPrice = highPrice;
            return this;
        }

        public Builder lowPrice(BigDecimal lowPrice) {
            this.lowPrice = lowPrice;
            return this;
        }

        public Builder volume(BigDecimal volume) {
            this.volume = volume;
            return this;
        }

        public Builder quoteVolume(BigDecimal quoteVolume) {
            this.quoteVolume = quoteVolume;
            return this;
        }

        public Builder openTime(Long openTime) {
            this.openTime = openTime;
            return this;
        }

        public Builder closeTime(Long closeTime) {
            this.closeTime = closeTime;
            return this;
        }

        public Builder firstId(Long firstId) {
            this.firstId = firstId;
            return this;
        }

        public Builder lastId(Long lastId) {
            this.lastId = lastId;
            return this;
        }

        public Builder count(Long count) {
            this.count = count;
            return this;
        }

        public TickerResp build() {
            TickerResp tickerResp = new TickerResp();
            tickerResp.setSymbol(symbol);
            tickerResp.setPriceChange(priceChange);
            tickerResp.setPriceChangePercent(priceChangePercent);
            tickerResp.setWeightedAvgPrice(weightedAvgPrice);
            tickerResp.setLastPrice(lastPrice);
            tickerResp.setLastQty(lastQty);
            tickerResp.setOpenPrice(openPrice);
            tickerResp.setHighPrice(highPrice);
            tickerResp.setLowPrice(lowPrice);
            tickerResp.setVolume(volume);
            tickerResp.setQuoteVolume(quoteVolume);
            tickerResp.setOpenTime(openTime);
            tickerResp.setCloseTime(closeTime);
            tickerResp.setFirstId(firstId);
            tickerResp.setLastId(lastId);
            tickerResp.setCount(count);
            return tickerResp;
        }
    }
}

