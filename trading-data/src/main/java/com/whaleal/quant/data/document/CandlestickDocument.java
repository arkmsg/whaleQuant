package com.whaleal.quant.data.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * K线数据文档
 * 用于MongoDB的文档映射
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "candlestick")
public class CandlestickDocument {
    
    /**
     * 主键
     */
    @MongoId
    private String id;
    
    /**
     * 交易对/股票代码
     */
    @Field("symbol")
    private String symbol;
    
    /**
     * K线周期
     */
    @Field("interval")
    private String interval;
    
    /**
     * 开始时间
     */
    @Field("start_time")
    private Instant startTime;
    
    /**
     * 结束时间
     */
    @Field("end_time")
    private Instant endTime;
    
    /**
     * 开盘价
     */
    @Field("open")
    private BigDecimal open;
    
    /**
     * 最高价
     */
    @Field("high")
    private BigDecimal high;
    
    /**
     * 最低价
     */
    @Field("low")
    private BigDecimal low;
    
    /**
     * 收盘价
     */
    @Field("close")
    private BigDecimal close;
    
    /**
     * 成交量
     */
    @Field("volume")
    private BigDecimal volume;
    
    /**
     * 成交额
     */
    @Field("amount")
    private BigDecimal amount;
    
    /**
     * 数据来源
     */
    @Field("source")
    private String source;
    
    /**
     * 创建时间
     */
    @Field("created_at")
    private Instant createdAt;
    
    /**
     * 更新时间
     */
    @Field("updated_at")
    private Instant updatedAt;
    
    /**
     * 预处理方法，设置创建时间和更新时间
     */
    public void preProcess() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
}
