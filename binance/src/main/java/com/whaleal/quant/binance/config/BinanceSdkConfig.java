package com.whaleal.quant.binance.config;

/**
 * Binance SDK 高级配置
 *
 * <p>提供更精细的配置选项，满足企业级应用需求。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * BinanceSdkConfig config = BinanceSdkConfig.builder()
 *     .requestTimeout(10000)
 *     .maxRetries(3)
 *     .retryDelay(1000)
 *     .enableMetrics(true)
 *     .build();
 * }</pre>
 *
 * @author Binance SDK Team
 * @version 1.0.0
 */
public class BinanceSdkConfig {

    /**
     * 请求超时时间（毫秒）
     * 默认: 10000ms (10秒)
     */
    private int requestTimeout;

    /**
     * 连接超时时间（毫秒）
     * 默认: 5000ms (5秒)
     */
    private int connectionTimeout;

    /**
     * 最大重试次数
     * 默认: 3次
     */
    private int maxRetries;

    /**
     * 重试延迟（毫秒）
     * 默认: 1000ms (1秒)
     */
    private long retryDelay;

    /**
     * 是否启用指数退避
     * 默认: true
     */
    private boolean exponentialBackoff;

    /**
     * 是否启用性能指标收集
     * 默认: false
     */
    private boolean enableMetrics;

    /**
     * 是否启用请求日志
     * 默认: false（生产环境建议关闭）
     */
    private boolean enableRequestLogging;

    /**
     * 是否启用响应日志
     * 默认: false（生产环境建议关闭）
     */
    private boolean enableResponseLogging;

    /**
     * 连接池最大连接数
     * 默认: 20
     */
    private int maxConnections;

    /**
     * 连接池最大空闲时间（毫秒）
     * 默认: 300000ms (5分钟)
     */
    private long connectionMaxIdleTime;

    /**
     * 是否启用失败回调
     * 默认: false
     */
    private boolean enableFailureCallback;

    /**
     * 私有构造器
     */
    private BinanceSdkConfig(Builder builder) {
        this.requestTimeout = builder.requestTimeout;
        this.connectionTimeout = builder.connectionTimeout;
        this.maxRetries = builder.maxRetries;
        this.retryDelay = builder.retryDelay;
        this.exponentialBackoff = builder.exponentialBackoff;
        this.enableMetrics = builder.enableMetrics;
        this.enableRequestLogging = builder.enableRequestLogging;
        this.enableResponseLogging = builder.enableResponseLogging;
        this.maxConnections = builder.maxConnections;
        this.connectionMaxIdleTime = builder.connectionMaxIdleTime;
        this.enableFailureCallback = builder.enableFailureCallback;
    }

    // Getter methods
    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public boolean isExponentialBackoff() {
        return exponentialBackoff;
    }

    public void setExponentialBackoff(boolean exponentialBackoff) {
        this.exponentialBackoff = exponentialBackoff;
    }

    public boolean isEnableMetrics() {
        return enableMetrics;
    }

    public void setEnableMetrics(boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    public boolean isEnableRequestLogging() {
        return enableRequestLogging;
    }

    public void setEnableRequestLogging(boolean enableRequestLogging) {
        this.enableRequestLogging = enableRequestLogging;
    }

    public boolean isEnableResponseLogging() {
        return enableResponseLogging;
    }

    public void setEnableResponseLogging(boolean enableResponseLogging) {
        this.enableResponseLogging = enableResponseLogging;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public long getConnectionMaxIdleTime() {
        return connectionMaxIdleTime;
    }

    public void setConnectionMaxIdleTime(long connectionMaxIdleTime) {
        this.connectionMaxIdleTime = connectionMaxIdleTime;
    }

    public boolean isEnableFailureCallback() {
        return enableFailureCallback;
    }

    public void setEnableFailureCallback(boolean enableFailureCallback) {
        this.enableFailureCallback = enableFailureCallback;
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
     * 创建默认配置
     */
    public static BinanceSdkConfig defaultConfig() {
        return BinanceSdkConfig.builder().build();
    }

    /**
     * 创建生产环境配置
     */
    public static BinanceSdkConfig productionConfig() {
        return BinanceSdkConfig.builder()
                .requestTimeout(15000)
                .connectionTimeout(10000)
                .maxRetries(5)
                .retryDelay(2000)
                .exponentialBackoff(true)
                .enableMetrics(true)
                .enableRequestLogging(false)
                .enableResponseLogging(false)
                .maxConnections(50)
                .build();
    }

    /**
     * 创建开发环境配置
     */
    public static BinanceSdkConfig developmentConfig() {
        return BinanceSdkConfig.builder()
                .requestTimeout(30000)
                .connectionTimeout(10000)
                .maxRetries(2)
                .retryDelay(500)
                .exponentialBackoff(false)
                .enableMetrics(true)
                .enableRequestLogging(true)
                .enableResponseLogging(true)
                .maxConnections(10)
                .build();
    }

    /**
     * 验证配置
     */
    public void validate() {
        if (requestTimeout <= 0) {
            throw new IllegalArgumentException("requestTimeout必须大于0");
        }
        if (connectionTimeout <= 0) {
            throw new IllegalArgumentException("connectionTimeout必须大于0");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries不能为负数");
        }
        if (retryDelay < 0) {
            throw new IllegalArgumentException("retryDelay不能为负数");
        }
        if (maxConnections <= 0) {
            throw new IllegalArgumentException("maxConnections必须大于0");
        }
        if (connectionMaxIdleTime <= 0) {
            throw new IllegalArgumentException("connectionMaxIdleTime必须大于0");
        }
    }

    /**
     * Builder类 - 用于构建BinanceSdkConfig实例
     */
    public static class Builder {
        private int requestTimeout = 10000;
        private int connectionTimeout = 5000;
        private int maxRetries = 3;
        private long retryDelay = 1000;
        private boolean exponentialBackoff = true;
        private boolean enableMetrics = false;
        private boolean enableRequestLogging = false;
        private boolean enableResponseLogging = false;
        private int maxConnections = 20;
        private long connectionMaxIdleTime = 300000;
        private boolean enableFailureCallback = false;

        public Builder requestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder retryDelay(long retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        public Builder exponentialBackoff(boolean exponentialBackoff) {
            this.exponentialBackoff = exponentialBackoff;
            return this;
        }

        public Builder enableMetrics(boolean enableMetrics) {
            this.enableMetrics = enableMetrics;
            return this;
        }

        public Builder enableRequestLogging(boolean enableRequestLogging) {
            this.enableRequestLogging = enableRequestLogging;
            return this;
        }

        public Builder enableResponseLogging(boolean enableResponseLogging) {
            this.enableResponseLogging = enableResponseLogging;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder connectionMaxIdleTime(long connectionMaxIdleTime) {
            this.connectionMaxIdleTime = connectionMaxIdleTime;
            return this;
        }

        public Builder enableFailureCallback(boolean enableFailureCallback) {
            this.enableFailureCallback = enableFailureCallback;
            return this;
        }

        public BinanceSdkConfig build() {
            return new BinanceSdkConfig(this);
        }
    }
}

