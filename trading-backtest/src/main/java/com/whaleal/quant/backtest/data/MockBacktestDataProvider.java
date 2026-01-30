package com.whaleal.quant.backtest.data;

import com.whaleal.quant.base.model.Bar;
import com.whaleal.quant.base.model.Ticker;
import com.whaleal.quant.engine.strategy.event.MarketDataEvent;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 模拟回测数据提供者
 * 用于生成模拟的市场数据，方便测试回测系统
 *
 * @author whaleal
 * @version 1.0.0
 */
public class MockBacktestDataProvider implements BacktestDataProvider {

    private Map<LocalDateTime, Map<String, List<MarketDataEvent>>> marketDataEvents;
    private Set<LocalDateTime> tradingDays;

    @Override
    public void loadData(Set<String> symbols, LocalDateTime startDate, LocalDateTime endDate) {
        marketDataEvents = new HashMap<>();
        tradingDays = new HashSet<>();

        // 生成交易日期
        LocalDateTime currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // 模拟工作日为交易日
            if (currentDate.getDayOfWeek().getValue() < 6) {
                tradingDays.add(currentDate);
                generateMarketDataEvents(currentDate, symbols);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    private void generateMarketDataEvents(LocalDateTime date, Set<String> symbols) {
        Map<String, List<MarketDataEvent>> dailyEvents = new HashMap<>();

        for (String symbol : symbols) {
            List<MarketDataEvent> events = new ArrayList<>();
            
            // 生成模拟的K线数据
            List<Bar> bars = generateMockBars(symbol, date);
            
            // 生成模拟的实时行情数据
            Ticker ticker = generateMockTicker(symbol, date);
            
            // 创建市场数据事件
            MarketDataEvent event = new MarketDataEvent(symbol, ticker, bars);
            events.add(event);
            
            dailyEvents.put(symbol, events);
        }

        marketDataEvents.put(date, dailyEvents);
    }

    private List<Bar> generateMockBars(String symbol, LocalDateTime date) {
        List<Bar> bars = new ArrayList<>();
        Random random = new Random(symbol.hashCode() + date.getDayOfYear());

        // 生成最近30天的K线数据
        LocalDateTime barDate = date.minusDays(29);
        double basePrice = 100.0 + random.nextDouble() * 50.0;

        for (int i = 0; i < 30; i++) {
            double open = basePrice;
            double high = open + random.nextDouble() * 2.0;
            double low = open - random.nextDouble() * 2.0;
            double close = low + random.nextDouble() * (high - low);
            double volume = 1000000.0 + random.nextDouble() * 9000000.0;

            Bar bar = new Bar();
            bar.setSymbol(symbol);
            bar.setOpen(open);
            bar.setHigh(high);
            bar.setLow(low);
            bar.setClose(close);
            bar.setVolume(volume);
            bar.setTimestamp(barDate);

            bars.add(bar);

            basePrice = close;
            barDate = barDate.plusDays(1);
        }

        return bars;
    }

    private Ticker generateMockTicker(String symbol, LocalDateTime date) {
        Random random = new Random(symbol.hashCode() + date.getDayOfYear());
        
        Ticker ticker = new Ticker();
        ticker.setSymbol(symbol);
        ticker.setPrice(100.0 + random.nextDouble() * 50.0);
        ticker.setOpen(100.0 + random.nextDouble() * 50.0);
        ticker.setHigh(ticker.getPrice() + random.nextDouble() * 2.0);
        ticker.setLow(ticker.getPrice() - random.nextDouble() * 2.0);
        ticker.setVolume(1000000.0 + random.nextDouble() * 9000000.0);
        ticker.setTimestamp(date);

        return ticker;
    }

    @Override
    public Map<String, List<MarketDataEvent>> getMarketDataEvents(LocalDateTime date) {
        return marketDataEvents.getOrDefault(date, Collections.emptyMap());
    }

    @Override
    public boolean isTradingDay(LocalDateTime date) {
        return tradingDays.contains(date);
    }

    @Override
    public LocalDateTime getNextTradingDay(LocalDateTime date) {
        LocalDateTime nextDay = date.plusDays(1);
        while (!isTradingDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    @Override
    public LocalDateTime getPreviousTradingDay(LocalDateTime date) {
        LocalDateTime prevDay = date.minusDays(1);
        while (!isTradingDay(prevDay)) {
            prevDay = prevDay.minusDays(1);
        }
        return prevDay;
    }

    @Override
    public List<LocalDateTime> getTradingDays(LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> days = new ArrayList<>();
        for (LocalDateTime date : tradingDays) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                days.add(date);
            }
        }
        Collections.sort(days);
        return days;
    }

    @Override
    public void cleanup() {
        if (marketDataEvents != null) {
            marketDataEvents.clear();
        }
        if (tradingDays != null) {
            tradingDays.clear();
        }
    }
}