package com.whaleal.quant.binance.util;

/**
 * 币安SDK配置
 *
 * @author Binance SDK Team
 */
public class BinanceConfig {

    /**
     * 现货生产环境URL
     */
    public static final String PRODUCTION_BASE_URL = "https://api.binance.com";

    /**
     * 现货测试网URL
     */
    public static final String TESTNET_BASE_URL = "https://testnet.binance.vision";

    /**
     * 合约生产环境URL（USDT本位永续）
     */
    public static final String FUTURES_BASE_URL = "https://fapi.binance.com";

    /**
     * 合约测试网URL（USDT本位永续）
     */
    public static final String FUTURES_TESTNET_BASE_URL = "https://testnet.binancefuture.com";

    /**
     * 币本位合约生产环境URL
     */
    public static final String COIN_FUTURES_BASE_URL = "https://dapi.binance.com";

    /**
     * 币本位合约测试网URL
     */
    public static final String COIN_FUTURES_TESTNET_BASE_URL = "https://testnet.binancefuture.com";

    /**
     * API Key
     */
    private final String apiKey;

    /**
     * Secret Key
     */
    private final String secretKey;

    /**
     * 基础URL（现货）
     */
    private final String baseUrl;

    /**
     * 是否为测试网
     */
    private final boolean testnet;

    /**
     * 连接超时时间（毫秒）
     */
    private final long connectTimeout;

    /**
     * 读取超时时间（毫秒）
     */
    private final long readTimeout;

    /**
     * 写入超时时间（毫秒）
     */
    private final long writeTimeout;

    /**
     * 私有构造器
     */
    private BinanceConfig(Builder builder) {
        this.apiKey = builder.apiKey;
        this.secretKey = builder.secretKey;
        this.baseUrl = builder.baseUrl;
        this.testnet = builder.testnet;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
    }

    // Getter methods
    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isTestnet() {
        return testnet;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
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
     * Builder类 - 用于构建BinanceConfig实例
     */
    public static class Builder {
        private String apiKey;
        private String secretKey;
        private String baseUrl;
        private boolean testnet = false;
        private long connectTimeout = 10_000;
        private long readTimeout = 30_000;
        private long writeTimeout = 10_000;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder testnet(boolean testnet) {
            this.testnet = testnet;
            return this;
        }

        public Builder connectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder writeTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public BinanceConfig build() {
            return new BinanceConfig(this);
        }
    }
}

