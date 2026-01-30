package com.whaleal.quant.data.repository;

import com.whaleal.quant.data.document.CandlestickDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

/**
 * K线数据仓库
 * 用于MongoDB的CRUD操作
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface CandlestickRepository extends MongoRepository<CandlestickDocument, String> {
    
    /**
     * 根据交易对、K线周期和时间范围查询K线数据
     *
     * @param symbol 交易对/股票代码
     * @param interval K线周期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return K线数据列表
     */
    List<CandlestickDocument> findBySymbolAndIntervalAndStartTimeBetween(
            String symbol, String interval, Instant startTime, Instant endTime
    );
    
    /**
     * 根据交易对和K线周期查询最新的K线数据
     *
     * @param symbol 交易对/股票代码
     * @param interval K线周期
     * @param limit 限制数量
     * @return K线数据列表
     */
    @Query(sort = "{ 'startTime' : -1 }")
    List<CandlestickDocument> findTopBySymbolAndIntervalOrderByStartTimeDesc(
            String symbol, String interval, org.springframework.data.domain.Pageable pageable
    );
    
    /**
     * 删除指定交易对和K线周期的K线数据
     *
     * @param symbol 交易对/股票代码
     * @param interval K线周期
     */
    void deleteBySymbolAndInterval(String symbol, String interval);
}
