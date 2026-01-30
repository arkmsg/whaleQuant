package com.whaleal.quant.core.market;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class MarketTimeManager {
    
    private Map<Market, MarketHours> marketHoursMap;
    
    public MarketTimeManager() {
        marketHoursMap = new HashMap<>();
        initializeDefaultMarketHours();
    }
    
    private void initializeDefaultMarketHours() {
        // 美股市场时间
        marketHoursMap.put(Market.US, new MarketHours(
            LocalTime.of(9, 30),
            LocalTime.of(16, 0),
            ZoneId.of("America/New_York")
        ));
        
        // 港股市场时间
        marketHoursMap.put(Market.HK, new MarketHours(
            LocalTime.of(9, 30),
            LocalTime.of(16, 0),
            ZoneId.of("Asia/Hong_Kong")
        ));
        
        // A股市场时间
        marketHoursMap.put(Market.CN, new MarketHours(
            LocalTime.of(9, 30),
            LocalTime.of(15, 0),
            ZoneId.of("Asia/Shanghai")
        ));
        
        // 加密货币市场时间（24小时）
        marketHoursMap.put(Market.CRYPTO, new MarketHours(
            LocalTime.of(0, 0),
            LocalTime.of(23, 59),
            ZoneId.of("UTC")
        ));
    }
    
    public boolean isMarketOpen(Market market) {
        ZonedDateTime now = ZonedDateTime.now(marketHoursMap.get(market).getZoneId());
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        
        // 检查是否是周末
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return market == Market.CRYPTO; // 加密货币市场周末也开放
        }
        
        MarketHours hours = marketHoursMap.get(market);
        return currentTime.isAfter(hours.getOpen()) && currentTime.isBefore(hours.getClose());
    }
    
    public TradingSession getTradingSession(Market market) {
        ZonedDateTime now = ZonedDateTime.now(marketHoursMap.get(market).getZoneId());
        LocalTime currentTime = now.toLocalTime();
        
        MarketHours hours = marketHoursMap.get(market);
        
        if (currentTime.isBefore(hours.getOpen().minusHours(2))) {
            return TradingSession.PRE_MARKET_EARLY;
        } else if (currentTime.isBefore(hours.getOpen())) {
            return TradingSession.PRE_MARKET;
        } else if (currentTime.isAfter(hours.getClose())) {
            return TradingSession.AFTER_HOURS;
        } else if (currentTime.isAfter(hours.getClose().minusMinutes(30))) {
            return TradingSession.CLOSING;
        } else if (currentTime.isBefore(hours.getOpen().plusMinutes(30))) {
            return TradingSession.OPENING;
        } else {
            return TradingSession.REGULAR;
        }
    }
    
    public ZonedDateTime getMarketTime(Market market) {
        return ZonedDateTime.now(marketHoursMap.get(market).getZoneId());
    }
    
    public void setMarketHours(Market market, LocalTime open, LocalTime close, ZoneId zoneId) {
        marketHoursMap.put(market, new MarketHours(open, close, zoneId));
    }
    
    public enum Market {
        US,
        HK,
        CN,
        CRYPTO
    }
    
    public enum TradingSession {
        PRE_MARKET_EARLY,
        PRE_MARKET,
        OPENING,
        REGULAR,
        CLOSING,
        AFTER_HOURS
    }
    
    private static class MarketHours {
        private LocalTime open;
        private LocalTime close;
        private ZoneId zoneId;
        
        public MarketHours(LocalTime open, LocalTime close, ZoneId zoneId) {
            this.open = open;
            this.close = close;
            this.zoneId = zoneId;
        }
        
        public LocalTime getOpen() {
            return open;
        }
        
        public LocalTime getClose() {
            return close;
        }
        
        public ZoneId getZoneId() {
            return zoneId;
        }
    }
}
