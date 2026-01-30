package com.whaleal.quant.data.service;

import com.whaleal.quant.core.model.Bar;
import com.whaleal.quant.data.document.CandlestickDocument;
import com.whaleal.quant.data.repository.CandlestickRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据服务类
 * 封装对MongoDB的访问，并使用多级缓存提升性能
 * 适配最新的SDK版本
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
@Service
public class DataService {
    
    private static final String CACHE_NAME = "candlestick";
    
    @Autowired
    private CandlestickRepository candlestickRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private MarketStatusService marketStatusService;
    
    /**
     * 获取历史K线数据
     * 优先从缓存获取，缓存未命中则从MongoDB获取
     *
     * @param symbol 交易对/股票代码
     * @param interval K线周期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return K线数据列表
     */
    public List<Bar> getHistory(String symbol, String interval, Instant startTime, Instant endTime) {
        // 构建缓存键
        String cacheKey = buildCacheKey(symbol, interval, startTime, endTime);
        
        // 尝试从缓存获取
        Optional<List<Bar>> cachedData = cacheService.get(CACHE_NAME, cacheKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }
        
        // 缓存未命中，从MongoDB获取
        log.debug("从MongoDB获取历史数据: {} {}, {} - {}", symbol, interval, startTime, endTime);
        
        List<CandlestickDocument> documents = candlestickRepository.findBySymbolAndIntervalAndStartTimeBetween(
                symbol, interval, startTime, endTime
        );
        
        // 转换为Bar模型
        List<Bar> bars = documents.stream()
                .map(this::convertToBar)
                .collect(Collectors.toList());
        
        // 将数据放入缓存
        cacheService.put(CACHE_NAME, cacheKey, bars);
        
        return bars;
    }
    
    /**
     * 保存K线数据
     * 同时保存到MongoDB和缓存
     *
     * @param bar K线数据
     */
    public void saveBar(Bar bar) {
        // 转换为Document
        CandlestickDocument document = convertToDocument(bar);
        
        // 调用preProcess方法设置时间戳
        document.preProcess();
        
        // 保存到MongoDB
        candlestickRepository.save(document);
        log.debug("保存K线数据到MongoDB: {} {}, {}", bar.getSymbol(), bar.getInterval(), bar.getTimestamp());
        
        // 更新缓存
        String cacheKey = buildCacheKey(bar.getSymbol(), bar.getInterval().toString(), bar.getTimestamp(), bar.getTimestamp());
        cacheService.evict(CACHE_NAME, cacheKey);
        log.debug("更新缓存: {}", cacheKey);
    }
    
    /**
     * 批量保存K线数据
     * 同时保存到MongoDB和缓存
     *
     * @param bars K线数据列表
     */
    public void saveBars(List<Bar> bars) {
        if (bars.isEmpty()) {
            return;
        }
        
        // 转换为Document
        List<CandlestickDocument> documents = bars.stream()
                .map(this::convertToDocument)
                .peek(CandlestickDocument::preProcess) // 调用preProcess方法设置时间戳
                .collect(Collectors.toList());
        
        // 批量保存到MongoDB
        candlestickRepository.saveAll(documents);
        log.debug("批量保存K线数据到MongoDB: {}条", bars.size());
        
        // 清除相关缓存
        for (Bar bar : bars) {
            String cacheKey = buildCacheKey(bar.getSymbol(), bar.getInterval().toString(), bar.getTimestamp(), bar.getTimestamp());
            cacheService.evict(CACHE_NAME, cacheKey);
        }
    }
    
    /**
     * 将Bar模型转换为CandlestickDocument
     *
     * @param bar Bar模型
     * @return CandlestickDocument
     */
    private CandlestickDocument convertToDocument(Bar bar) {
        CandlestickDocument document = new CandlestickDocument();
        document.setSymbol(bar.getSymbol());
        document.setInterval(bar.getInterval().toString());
        document.setStartTime(bar.getTimestamp());
        document.setEndTime(bar.getTimestamp());
        document.setOpen(bar.getOpen());
        document.setHigh(bar.getHigh());
        document.setLow(bar.getLow());
        document.setClose(bar.getClose());
        document.setVolume(bar.getVolume());
        document.setAmount(bar.getAmount());
        document.setSource("DATA_SERVICE");
        return document;
    }
    
    /**
     * 将CandlestickDocument转换为Bar模型
     *
     * @param document CandlestickDocument
     * @return Bar模型
     */
    private Bar convertToBar(CandlestickDocument document) {
        Bar bar = new Bar();
        bar.setSymbol(document.getSymbol());
        bar.setOpen(document.getOpen());
        bar.setHigh(document.getHigh());
        bar.setLow(document.getLow());
        bar.setClose(document.getClose());
        bar.setVolume(document.getVolume());
        bar.setAmount(document.getAmount());
        bar.setTimestamp(document.getStartTime());
        return bar;
    }
    
    /**
     * 构建缓存键
     *
     * @param symbol 交易对/股票代码
     * @param interval K线周期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 缓存键
     */
    private String buildCacheKey(String symbol, String interval, Instant startTime, Instant endTime) {
        return String.format("%s:%s:%d:%d", symbol, interval, startTime.toEpochMilli(), endTime.toEpochMilli());
    }

    /**
     * 根据市场状态获取推荐的数据源
     *
     * @return 推荐的数据源
     */
    public String getRecommendedDataSource() {
        return marketStatusService.getRecommendedDataSource();
    }

    /**
     * 获取当前市场状态
     *
     * @return 市场状态描述
     */
    public String getCurrentMarketStatus() {
        return marketStatusService.getUSMarketStatus().getDescription();
    }

    /**
     * 判断是否为正常交易时间
     *
     * @return 是否为正常交易时间
     */
    public boolean isRegularTradingHours() {
        return marketStatusService.isRegularTradingHours();
    }
}
