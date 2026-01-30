package com.whaleal.quant.trading;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Trading 自动配置
 * 
 * <p>Spring Boot自动配置类，用于整合所有Trading组件
 * 
 * @author trading
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TradingProperties.class)
public class TradingAutoConfiguration {
    
    public TradingAutoConfiguration() {
        log.info("Trading 自动配置已启用");
        log.info("支持的市场: 港股、美股、A股、加密货币");
        log.info("功能模块: 行情数据、技术指标、量化因子、策略回测");
    }
}