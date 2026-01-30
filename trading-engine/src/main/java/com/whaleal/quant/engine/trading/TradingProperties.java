package com.whaleal.quant.trading;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Trading 配置属性
 * 
 * <p>用于配置Trading的各种参数
 * 
 * @author trading
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {
    
    /**
     * 数据来源配置
     */
    private DataSourceConfig dataSource;
    
    /**
     * 策略配置
     */
    private StrategyConfig strategy;
    
    /**
     * Binance配置
     */
    private BinanceConfig binance;
    
    /**
     * Longport配置
     */
    private LongportConfig longport;
    
    @Data
    public static class DataSourceConfig {
        private String defaultSource;
        private boolean enableCache;
        private int cacheExpireMinutes;
    }
    
    @Data
    public static class StrategyConfig {
        private boolean enableBacktest;
        private String backtestDataPath;
        private int maxConcurrentStrategies;
    }
    
    @Data
    public static class BinanceConfig {
        private String apiKey;
        private String secretKey;
        private String baseUrl;
        private boolean enableRateLimit;
        private int rateLimitPerSecond;
    }
    
    @Data
    public static class LongportConfig {
        private String appKey;
        private String appSecret;
        private String accessToken;
        private String environment;
    }
}