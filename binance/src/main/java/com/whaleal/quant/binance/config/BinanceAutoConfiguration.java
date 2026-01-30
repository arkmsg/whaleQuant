package com.whaleal.quant.binance.config;

import com.whaleal.quant.binance.BinanceSDK;
import com.whaleal.quant.binance.circuit.CircuitBreakerConfig;
import com.whaleal.quant.binance.sync.PositionCache;
import com.whaleal.quant.binance.sync.PositionSyncService;
import com.whaleal.quant.binance.util.BinanceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 币安SDK自动配置
 *
 * @author Ark Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(BinanceProperties.class)
@ConditionalOnProperty(prefix = "com.whaleal.quant.binance", name = "api-key")
@EnableScheduling
@Import(CircuitBreakerConfig.class)
public class BinanceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BinanceConfig binanceConfig(BinanceProperties properties) {
        System.out.println("初始化币安Config: testnet=" + properties.isTestnet());

        return BinanceConfig.builder()
                .apiKey(properties.getApiKey())
                .secretKey(properties.getSecretKey())
                .baseUrl(properties.getBaseUrl())
                .testnet(properties.isTestnet())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public BinanceSDK binanceSDK(BinanceProperties properties) {
        System.out.println("初始化币安SDK: testnet=" + properties.isTestnet());

        return BinanceSDK.builder()
                .apiKey(properties.getApiKey())
                .secretKey(properties.getSecretKey())
                .testnet(properties.isTestnet())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public PositionCache positionCache() {
        System.out.println("初始化币安持仓缓存");
        return new PositionCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public PositionSyncService positionSyncService(PositionCache positionCache) {
        System.out.println("初始化币安持仓同步服务");
        return new PositionSyncService(positionCache);
    }
}

