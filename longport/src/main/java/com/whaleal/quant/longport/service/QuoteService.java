package com.whaleal.quant.longport.service;

import com.longport.quote.*;
import com.whaleal.quant.longport.model.MarketTimeSlot;
import com.whaleal.quant.longport.model.SecurityQuoteResp;
import com.whaleal.quant.longport.builder.KlineQueryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行情服务
 * 
 * <p>提供股票行情查询功能，支持24小时行情数据获取。
 * 
 * <h3>核心特性：</h3>
 * <ul>
 *   <li>时段自适应价格获取（盘前/盘中/盘后/夜盘）</li>
 *   <li>K线数据查询（Builder模式）</li>
 *   <li>股票搜索</li>
 *   <li>批量查询</li>
 * </ul>
 * 
 * @author Longport SDK Team
 */
@Slf4j
public class QuoteService {
    
    private final QuoteContext quoteContext;
    private final boolean overnightEnabled;
    
    private static final ZoneId ET_ZONE = ZoneId.of("America/New_York");
    
    public QuoteService(QuoteContext quoteContext, boolean overnightEnabled) {
        this.quoteContext = quoteContext;
        this.overnightEnabled = overnightEnabled;
    }
    
    /**
     * 获取实时报价（自动根据时段选择正确的价格）
     * 
     * <p>时段价格选择逻辑：
     * <ul>
     *   <li>盘前 (04:00-09:30 ET): preMarketQuote.lastDone</li>
     *   <li>盘中 (09:30-16:00 ET): lastDone</li>
     *   <li>盘后 (16:00-20:00 ET): postMarketQuote.lastDone</li>
     *   <li>夜盘 (20:00-04:00 ET): overnightQuote.lastDone</li>
     * </ul>
     * 
     * @param symbol 股票代码 (例如: "AAPL.US", "00700.HK")
     * @return 实时报价响应
     * @throws Exception 如果查询失败
     */
    public SecurityQuoteResp getRealtimeQuote(String symbol) throws Exception {
        SecurityQuote[] quotes = quoteContext.getQuote(new String[]{symbol}).get();
        
        if (quotes == null || quotes.length == 0) {
            throw new IllegalStateException("未获取到股票报价: " + symbol);
        }
        
        SecurityQuote quote = quotes[0];
        ZonedDateTime etTime = ZonedDateTime.now(ET_ZONE);
        
        // 根据时段获取价格
        BigDecimal price = getTimeBasedPrice(quote, etTime);
        MarketTimeSlot timeSlot = getMarketTimeSlot(etTime);
        
        return SecurityQuoteResp.builder()
                .symbol(quote.getSymbol())
                .price(price)
                .lastDone(quote.getLastDone())
                .prevClose(quote.getPrevClose())
                .open(quote.getOpen())
                .high(quote.getHigh())
                .low(quote.getLow())
                .volume(quote.getVolume())
                .turnover(quote.getTurnover())
                .timestamp(quote.getTimestamp())
                .timeSlot(timeSlot)
                .preMarketQuote(quote.getPreMarketQuote())
                .postMarketQuote(quote.getPostMarketQuote())
                .overnightQuote(quote.getOvernightQuote())
                .build();
    }
    
    /**
     * 批量获取实时报价
     * 
     * @param symbols 股票代码列表
     * @return 报价响应列表
     * @throws Exception 如果查询失败
     */
    public List<SecurityQuoteResp> getBatchQuotes(List<String> symbols) throws Exception {
        if (symbols == null || symbols.isEmpty()) {
            return Collections.emptyList();
        }
        
        String[] symbolArray = symbols.toArray(new String[0]);
        SecurityQuote[] quotes = quoteContext.getQuote(symbolArray).get();
        
        if (quotes == null) {
            return Collections.emptyList();
        }
        
        ZonedDateTime etTime = ZonedDateTime.now(ET_ZONE);
        MarketTimeSlot timeSlot = getMarketTimeSlot(etTime);
        
        return Arrays.stream(quotes)
                .map(quote -> SecurityQuoteResp.builder()
                        .symbol(quote.getSymbol())
                        .price(getTimeBasedPrice(quote, etTime))
                        .lastDone(quote.getLastDone())
                        .prevClose(quote.getPrevClose())
                        .open(quote.getOpen())
                        .high(quote.getHigh())
                        .low(quote.getLow())
                        .volume(quote.getVolume())
                        .turnover(quote.getTurnover())
                        .timestamp(quote.getTimestamp())
                        .timeSlot(timeSlot)
                        .preMarketQuote(quote.getPreMarketQuote())
                        .postMarketQuote(quote.getPostMarketQuote())
                        .overnightQuote(quote.getOvernightQuote())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 创建K线查询Builder
     * 
     * <p>使用示例：
     * <pre>{@code
     * List<Candlestick> klines = quoteService.getKlines()
     *     .symbol("AAPL.US")
     *     .period(Period.MIN_1)
     *     .count(100)
     *     .fetch();
     * }</pre>
     * 
     * @return K线查询Builder
     */
    public KlineQueryBuilder getKlines() {
        return new KlineQueryBuilder(quoteContext);
    }
    
    /**
     * 搜索股票
     * 
     * @param keyword 搜索关键词（支持代码、中文名、英文名）
     * @return 搜索结果列表
     * @throws Exception 如果搜索失败
     */
    public List<Security> search(String keyword) throws Exception {
        // 根据关键字判断市场
        com.longport.Market market = keyword.contains(".HK") ? 
            com.longport.Market.HK : com.longport.Market.US;
        
        SecurityListCategory category = SecurityListCategory.Overnight;
        Security[] securities = quoteContext.getSecurityList(market, category).get();
        
        if (securities == null) {
            return Collections.emptyList();
        }
        
        // 模糊搜索：支持代码、中文名、英文名
        final String lowerKeyword = keyword.toLowerCase();
        final String upperKeyword = keyword.toUpperCase();
        
        return Arrays.stream(securities)
            .filter(sec -> {
                // 股票代码匹配（不区分大小写）
                boolean symbolMatch = sec.getSymbol().toUpperCase().contains(upperKeyword);
                
                // 英文名称匹配（不区分大小写）
                boolean nameEnMatch = sec.getNameEn() != null && 
                                     sec.getNameEn().toLowerCase().contains(lowerKeyword);
                
                // 中文名称匹配
                boolean nameCnMatch = sec.getNameCn() != null && 
                                     sec.getNameCn().contains(keyword);
                
                return symbolMatch || nameEnMatch || nameCnMatch;
            })
            .limit(20)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取盘口数据
     * 
     * @param symbol 股票代码
     * @return 盘口数据
     * @throws Exception 如果查询失败
     */
    public SecurityDepth getMarketDepth(String symbol) throws Exception {
        return quoteContext.getDepth(symbol).get();
    }
    
    /**
     * 获取分时数据（默认包含所有时段）
     * 
     * @param symbol 股票代码
     * @return 分时数据列表
     * @throws Exception 如果查询失败
     */
    public List<IntradayLine> getIntraday(String symbol) throws Exception {
        return getIntraday(symbol, TradeSessions.All);
    }
    
    /**
     * 获取分时数据（指定交易时段）
     * 
     * @param symbol 股票代码
     * @param tradeSessions 交易时段（Intraday=仅日内, All=包含盘前盘后）
     * @return 分时数据列表
     * @throws Exception 如果查询失败
     */
    public List<IntradayLine> getIntraday(String symbol, TradeSessions tradeSessions) throws Exception {
        IntradayLine[] lines = quoteContext.getIntraday(symbol, tradeSessions).get();
        return lines != null ? Arrays.asList(lines) : Collections.emptyList();
    }
    
    /**
     * 根据时段获取正确的价格
     * 
     * <p>这是SDK的核心功能之一，自动根据交易时段选择正确的价格字段。
     * 
     * @param quote SecurityQuote对象
     * @param etTime 美东时间
     * @return 当前时段的价格
     */
    private BigDecimal getTimeBasedPrice(SecurityQuote quote, ZonedDateTime etTime) {
        String symbol = quote.getSymbol();
        
        // 港股直接返回最新价
        if (symbol.endsWith(".HK")) {
            return quote.getLastDone();
        }
        
        // 美股根据时段返回对应价格
        if (symbol.endsWith(".US") && overnightEnabled) {
            int hour = etTime.getHour();
            int minute = etTime.getMinute();
            
            try {
                // 盘中时间（9:30 AM - 4:00 PM）
                if ((hour > 9 || (hour == 9 && minute >= 30)) && hour < 16) {
                    return quote.getLastDone();
                }
                // 盘前时间（4:00 AM - 9:30 AM）
                else if (hour >= 4 && (hour < 9 || (hour == 9 && minute < 30))) {
                    PrePostQuote preMarket = quote.getPreMarketQuote();
                    if (preMarket != null && preMarket.getLastDone() != null) {
                        return preMarket.getLastDone();
                    }
                }
                // 盘后时间（4:00 PM - 8:00 PM）
                else if (hour >= 16 && hour < 20) {
                    PrePostQuote postMarket = quote.getPostMarketQuote();
                    if (postMarket != null && postMarket.getLastDone() != null) {
                        return postMarket.getLastDone();
                    }
                }
                // 夜盘时间（8:00 PM - 4:00 AM）
                else {
                    PrePostQuote overnight = quote.getOvernightQuote();
                    if (overnight != null && overnight.getLastDone() != null) {
                        return overnight.getLastDone();
                    }
                }
            } catch (Exception e) {
                log.debug("获取时段价格失败，使用lastDone: {}", symbol);
            }
        }
        
        // 默认返回最新价
        return quote.getLastDone();
    }
    
    /**
     * 获取市场时段标识
     * 
     * @param etTime 美东时间
     * @return 时段枚举
     */
    private MarketTimeSlot getMarketTimeSlot(ZonedDateTime etTime) {
        int hour = etTime.getHour();
        int minute = etTime.getMinute();
        
        if (hour < 4) {
            return MarketTimeSlot.OVERNIGHT;
        } else if (hour < 9 || (hour == 9 && minute < 30)) {
            return MarketTimeSlot.PRE_MARKET;
        } else if (hour < 16) {
            return MarketTimeSlot.REGULAR;
        } else if (hour < 20) {
            return MarketTimeSlot.POST_MARKET;
        } else {
            return MarketTimeSlot.OVERNIGHT;
        }
    }
}

