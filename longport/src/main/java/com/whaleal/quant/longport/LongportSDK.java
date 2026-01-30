package com.whaleal.quant.longport;

import com.longport.Config;
import com.longport.ConfigBuilder;
import com.longport.quote.QuoteContext;
import com.longport.trade.TradeContext;
import com.whaleal.quant.longport.service.QuoteService;
import com.whaleal.quant.longport.service.TradeService;
import lombok.extern.slf4j.Slf4j;

/**
 * 长桥SDK - 主入口类
 * 
 * <p>基于Builder模式的SDK封装，提供优雅、易用的API设计。
 * 支持24小时行情数据获取（盘前、盘中、盘后、夜盘）。
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * try (LongportSDK sdk = LongportSDK.builder()
 *         .appKey("your_app_key")
 *         .appSecret("your_app_secret")
 *         .accessToken("your_token")
 *         .enableOvernight(true)
 *         .quietMode(true)
 *         .build()) {
 *     
 *     // 获取行情
 *     SecurityQuoteResp quote = sdk.quote().getRealtimeQuote("AAPL.US");
 *     
 *     // 下单
 *     OrderResp order = sdk.trade().submitOrder()
 *         .symbol("AAPL.US")
 *         .side(OrderSide.BUY)
 *         .quantity(100)
 *         .orderType(OrderType.MARKET)
 *         .execute();
 * }
 * }</pre>
 * 
 * @author Longport SDK Team
 * @version 1.0.0
 */
@Slf4j
public class LongportSDK implements AutoCloseable {
    
    private final Config config;
    private final QuoteContext quoteContext;
    private final TradeContext tradeContext;
    
    private final QuoteService quoteService;
    private final TradeService tradeService;
    
    private final boolean enableOvernight;
    private final boolean quietMode;
    
    private LongportSDK(Builder builder) throws Exception {
        log.debug("初始化长桥SDK...");
        
        this.enableOvernight = builder.enableOvernight;
        this.quietMode = builder.quietMode;
        
        // 构建配置
        ConfigBuilder configBuilder = new ConfigBuilder(
            builder.appKey, 
            builder.appSecret, 
            builder.accessToken
        );
        
        if (enableOvernight) {
            configBuilder.enableOvernight();
        }
        
        if (quietMode) {
            configBuilder.dontPrintQuotePackages();
        }
        
        this.config = configBuilder.build();
        
        // 创建上下文
        this.quoteContext = QuoteContext.create(config).get();
        this.tradeContext = TradeContext.create(config).get();
        
        // 初始化服务
        this.quoteService = new QuoteService(quoteContext, enableOvernight);
        this.tradeService = new TradeService(tradeContext);
        
        log.debug("长桥SDK初始化成功 [夜盘: {}, 静默: {}]", 
                enableOvernight ? "开" : "关", 
                quietMode ? "开" : "关");
    }
    
    /**
     * 获取行情服务
     * 
     * @return 行情服务实例
     */
    public QuoteService quote() {
        return quoteService;
    }
    
    /**
     * 获取交易服务
     * 
     * @return 交易服务实例
     */
    public TradeService trade() {
        return tradeService;
    }
    
    /**
     * 获取原始QuoteContext（高级用户使用）
     * 
     * @return QuoteContext实例
     */
    public QuoteContext getQuoteContext() {
        return quoteContext;
    }
    
    /**
     * 获取原始TradeContext（高级用户使用）
     * 
     * @return TradeContext实例
     */
    public TradeContext getTradeContext() {
        return tradeContext;
    }
    
    /**
     * 是否启用夜盘行情
     * 
     * @return true表示已启用
     */
    public boolean isOvernightEnabled() {
        return enableOvernight;
    }
    
    /**
     * 是否启用静默模式
     * 
     * @return true表示已启用
     */
    public boolean isQuietMode() {
        return quietMode;
    }
    
    @Override
    public void close() {
        log.debug("关闭长桥SDK...");
        
        if (quoteContext != null) {
            try {
                quoteContext.close();
            } catch (Exception e) {
                log.warn("关闭QuoteContext失败: {}", e.getMessage());
            }
        }
        
        if (tradeContext != null) {
            try {
                tradeContext.close();
            } catch (Exception e) {
                log.warn("关闭TradeContext失败: {}", e.getMessage());
            }
        }
        
        if (config != null) {
            try {
                config.close();
            } catch (Exception e) {
                log.warn("关闭Config失败: {}", e.getMessage());
            }
        }
        
        log.debug("长桥SDK已关闭");
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
     * Builder类 - 用于构建LongportSDK实例
     */
    public static class Builder {
        private String appKey;
        private String appSecret;
        private String accessToken;
        private boolean enableOvernight = true;  // 默认启用夜盘
        private boolean quietMode = true;        // 默认静默模式
        
        /**
         * 设置App Key
         * 
         * @param appKey 长桥App Key
         * @return Builder实例
         */
        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }
        
        /**
         * 设置App Secret
         * 
         * @param appSecret 长桥App Secret
         * @return Builder实例
         */
        public Builder appSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }
        
        /**
         * 设置Access Token
         * 
         * @param accessToken 长桥Access Token
         * @return Builder实例
         */
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        /**
         * 启用/禁用夜盘行情
         * 
         * <p>夜盘行情包括：
         * <ul>
         *   <li>盘前交易 (04:00-09:30 ET)</li>
         *   <li>盘后交易 (16:00-20:00 ET)</li>
         *   <li>夜盘交易 (20:00-04:00 ET)</li>
         * </ul>
         * 
         * @param enable true表示启用（默认true）
         * @return Builder实例
         */
        public Builder enableOvernight(boolean enable) {
            this.enableOvernight = enable;
            return this;
        }
        
        /**
         * 启用/禁用静默模式
         * 
         * <p>静默模式会禁止SDK打印行情包日志，保持日志清晰
         * 
         * @param quiet true表示启用（默认true）
         * @return Builder实例
         */
        public Builder quietMode(boolean quiet) {
            this.quietMode = quiet;
            return this;
        }
        
        /**
         * 构建LongportSDK实例
         * 
         * @return LongportSDK实例
         * @throws IllegalArgumentException 如果必需参数为空
         * @throws Exception 如果初始化失败
         */
        public LongportSDK build() throws Exception {
            validate();
            return new LongportSDK(this);
        }
        
        private void validate() {
            if (appKey == null || appKey.trim().isEmpty()) {
                throw new IllegalArgumentException("appKey 不能为空");
            }
            if (appSecret == null || appSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("appSecret 不能为空");
            }
            if (accessToken == null || accessToken.trim().isEmpty()) {
                throw new IllegalArgumentException("accessToken 不能为空");
            }
        }
    }
}

