package com.whaleal.quant.binance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 币安SDK配置属性
 *
 * @author Ark Team
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "com.whaleal.quant.binance")
public class BinanceProperties {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * Secret Key
     */
    private String secretKey;

    /**
     * 基础URL（现货）
     */
    private String baseUrl = "https://api.binance.com";

    /**
     * 是否为测试网（默认false）
     */
    private Boolean testnet = false;

    /**
     * 连接超时时间（毫秒，默认10秒）
     */
    private Long connectTimeout = 10_000L;

    /**
     * 读取超时时间（毫秒，默认30秒）
     */
    private Long readTimeout = 30_000L;

    /**
     * 写入超时时间（毫秒，默认10秒）
     */
    private Long writeTimeout = 10_000L;

    // Getter methods
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Boolean getTestnet() {
        return testnet;
    }

    public void setTestnet(Boolean testnet) {
        this.testnet = testnet;
    }

    public boolean isTestnet() {
        return testnet != null ? testnet : false;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}

