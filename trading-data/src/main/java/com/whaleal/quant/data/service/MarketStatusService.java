package com.whaleal.quant.data.service;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.ZoneId;

/**
 * 市场状态服务
 *
 * <p>用于识别不同市场的状态，如盘前、交易中、盘后、闭市</p>
 *
 * @author whaleal
 * @version 1.0.0
 */
@Service
public class MarketStatusService {

    /**
     * 市场状态枚举
     */
    public enum MarketStatus {
        PRE_MARKET("盘前"),
        TRADING("交易中"),
        POST_MARKET("盘后"),
        CLOSED("闭市");

        private final String description;

        MarketStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 获取美股市场状态
     *
     * @return 市场状态
     */
    public MarketStatus getUSMarketStatus() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime etTime = now.withZoneSameInstant(ZoneId.of("America/New_York"));
        int hour = etTime.getHour();
        int minute = etTime.getMinute();

        return getMarketStatus(hour, minute);
    }

    /**
     * 根据小时和分钟获取市场状态
     *
     * @param hour 小时
     * @param minute 分钟
     * @return 市场状态
     */
    private MarketStatus getMarketStatus(int hour, int minute) {
        if (hour < 4) {
            return MarketStatus.CLOSED;
        } else if (hour < 9 || (hour == 9 && minute < 30)) {
            return MarketStatus.PRE_MARKET;
        } else if (hour < 16) {
            return MarketStatus.TRADING;
        } else if (hour < 20) {
            return MarketStatus.POST_MARKET;
        } else {
            return MarketStatus.CLOSED;
        }
    }

    /**
     * 判断是否为正常交易时间
     *
     * @return 是否为正常交易时间
     */
    public boolean isRegularTradingHours() {
        return getUSMarketStatus() == MarketStatus.TRADING;
    }

    /**
     * 判断是否为盘前时间
     *
     * @return 是否为盘前时间
     */
    public boolean isPreMarketHours() {
        return getUSMarketStatus() == MarketStatus.PRE_MARKET;
    }

    /**
     * 判断是否为盘后时间
     *
     * @return 是否为盘后时间
     */
    public boolean isPostMarketHours() {
        return getUSMarketStatus() == MarketStatus.POST_MARKET;
    }

    /**
     * 判断是否为闭市时间
     *
     * @return 是否为闭市时间
     */
    public boolean isClosed() {
        return getUSMarketStatus() == MarketStatus.CLOSED;
    }

    /**
     * 根据市场状态获取推荐的数据源
     *
     * @return 推荐的数据源
     */
    public String getRecommendedDataSource() {
        MarketStatus status = getUSMarketStatus();
        return switch (status) {
            case TRADING -> "KLINE";
            case PRE_MARKET, POST_MARKET -> "REALTIME";
            case CLOSED -> "HISTORICAL";
        };
    }
}
