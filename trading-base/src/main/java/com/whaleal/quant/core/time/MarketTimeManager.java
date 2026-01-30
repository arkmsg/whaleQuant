package com.whaleal.quant.core.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketTimeManager {
    
    private final Map<String, MarketTimeInfo> marketTimeInfoMap = new ConcurrentHashMap<>();
    
    public MarketTimeManager() {
        initMarketTimeInfo();
    }
    
    private void initMarketTimeInfo() {
        // 美股市场时间
        marketTimeInfoMap.put("US", new MarketTimeInfo(
            "America/New_York",
            new int[]{9, 30}, new int[]{16, 0}, // 正常交易时间 9:30-16:00 EST
            new int[]{4, 0}, new int[]{9, 30},  // 盘前交易 4:00-9:30 EST
            new int[]{16, 0}, new int[]{20, 0}  // 盘后交易 16:00-20:00 EST
        ));
        
        // 港股市场时间
        marketTimeInfoMap.put("HK", new MarketTimeInfo(
            "Asia/Hong_Kong",
            new int[]{9, 30}, new int[]{12, 0}, // 上午盘 9:30-12:00 HKT
            new int[]{13, 0}, new int[]{16, 0}, // 下午盘 13:00-16:00 HKT
            new int[]{9, 0}, new int[]{9, 30}   // 集合竞价 9:00-9:30 HKT
        ));
        
        // A股市场时间
        marketTimeInfoMap.put("CN", new MarketTimeInfo(
            "Asia/Shanghai",
            new int[]{9, 30}, new int[]{11, 30}, // 上午盘 9:30-11:30 CST
            new int[]{13, 0}, new int[]{15, 0}   // 下午盘 13:00-15:00 CST
        ));
        
        // 加密货币市场时间（24小时）
        marketTimeInfoMap.put("CRYPTO", new MarketTimeInfo(
            "UTC",
            new int[]{0, 0}, new int[]{23, 59}  // 24小时交易
        ));
    }
    
    public void addMarketTimeInfo(String market, MarketTimeInfo marketTimeInfo) {
        marketTimeInfoMap.put(market, marketTimeInfo);
    }
    
    public MarketTimeInfo getMarketTimeInfo(String market) {
        return marketTimeInfoMap.get(market);
    }
    
    public boolean isMarketOpen(String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of(marketTimeInfo.getTimeZone()));
        return isTradingDay(now, market) && isInTradingHours(now, marketTimeInfo);
    }
    
    public TradingSession getTradingSession(String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return TradingSession.NOT_TRADING;
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of(marketTimeInfo.getTimeZone()));
        if (!isTradingDay(now, market)) {
            return TradingSession.NOT_TRADING;
        }
        
        return getTradingSession(now, marketTimeInfo);
    }
    
    public TradingSession getTradingSession(LocalDateTime dateTime, String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return TradingSession.NOT_TRADING;
        }
        
        ZoneId zoneId = ZoneId.of(marketTimeInfo.getTimeZone());
        LocalDateTime localDateTime = dateTime.atZone(zoneId).toLocalDateTime();
        
        if (!isTradingDay(localDateTime, market)) {
            return TradingSession.NOT_TRADING;
        }
        
        return getTradingSession(localDateTime, marketTimeInfo);
    }
    
    private boolean isTradingDay(LocalDateTime dateTime, String market) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        
        // 周末不交易（加密货币除外）
        if (!"CRYPTO".equals(market) && 
            (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
            return false;
        }
        
        // TODO: 添加节假日判断
        return true;
    }
    
    private boolean isInTradingHours(LocalDateTime dateTime, MarketTimeInfo marketTimeInfo) {
        TradingSession session = getTradingSession(dateTime, marketTimeInfo);
        return session != TradingSession.NOT_TRADING;
    }
    
    private TradingSession getTradingSession(LocalDateTime dateTime, MarketTimeInfo marketTimeInfo) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int totalMinutes = hour * 60 + minute;
        
        // 检查盘前交易
        if (marketTimeInfo.getPreMarketStart() != null && marketTimeInfo.getPreMarketEnd() != null) {
            int preMarketStartMinutes = marketTimeInfo.getPreMarketStart()[0] * 60 + marketTimeInfo.getPreMarketStart()[1];
            int preMarketEndMinutes = marketTimeInfo.getPreMarketEnd()[0] * 60 + marketTimeInfo.getPreMarketEnd()[1];
            if (totalMinutes >= preMarketStartMinutes && totalMinutes <= preMarketEndMinutes) {
                return TradingSession.PRE_MARKET;
            }
        }
        
        // 检查上午盘
        int morningStartMinutes = marketTimeInfo.getMorningStart()[0] * 60 + marketTimeInfo.getMorningStart()[1];
        int morningEndMinutes = marketTimeInfo.getMorningEnd()[0] * 60 + marketTimeInfo.getMorningEnd()[1];
        if (totalMinutes >= morningStartMinutes && totalMinutes <= morningEndMinutes) {
            // 开盘后30分钟为开盘时段
            if (totalMinutes < morningStartMinutes + 30) {
                return TradingSession.OPENING;
            }
            // 收盘前30分钟为收盘时段
            if (totalMinutes > morningEndMinutes - 30) {
                return TradingSession.CLOSING;
            }
            return TradingSession.REGULAR;
        }
        
        // 检查下午盘
        if (marketTimeInfo.getAfternoonStart() != null && marketTimeInfo.getAfternoonEnd() != null) {
            int afternoonStartMinutes = marketTimeInfo.getAfternoonStart()[0] * 60 + marketTimeInfo.getAfternoonStart()[1];
            int afternoonEndMinutes = marketTimeInfo.getAfternoonEnd()[0] * 60 + marketTimeInfo.getAfternoonEnd()[1];
            if (totalMinutes >= afternoonStartMinutes && totalMinutes <= afternoonEndMinutes) {
                // 开盘后30分钟为开盘时段
                if (totalMinutes < afternoonStartMinutes + 30) {
                    return TradingSession.OPENING;
                }
                // 收盘前30分钟为收盘时段
                if (totalMinutes > afternoonEndMinutes - 30) {
                    return TradingSession.CLOSING;
                }
                return TradingSession.REGULAR;
            }
        }
        
        // 检查盘后交易
        if (marketTimeInfo.getAfterHoursStart() != null && marketTimeInfo.getAfterHoursEnd() != null) {
            int afterHoursStartMinutes = marketTimeInfo.getAfterHoursStart()[0] * 60 + marketTimeInfo.getAfterHoursStart()[1];
            int afterHoursEndMinutes = marketTimeInfo.getAfterHoursEnd()[0] * 60 + marketTimeInfo.getAfterHoursEnd()[1];
            if (totalMinutes >= afterHoursStartMinutes && totalMinutes <= afterHoursEndMinutes) {
                return TradingSession.AFTER_HOURS;
            }
        }
        
        return TradingSession.NOT_TRADING;
    }
    
    public String getTimeZone(String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return "UTC";
        }
        return marketTimeInfo.getTimeZone();
    }
    
    public LocalDateTime getMarketTime(String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.now(ZoneId.of(marketTimeInfo.getTimeZone()));
    }
    
    public boolean isTradingDay(String market) {
        MarketTimeInfo marketTimeInfo = marketTimeInfoMap.get(market);
        if (marketTimeInfo == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of(marketTimeInfo.getTimeZone()));
        return isTradingDay(now, market);
    }
}
