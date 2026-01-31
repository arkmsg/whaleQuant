package com.whaleal.quant.strategy.autoconfigure;

import com.whaleal.quant.strategy.engine.StrategyEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 策略引擎自动配置
 * 
 * <p>Spring Boot自动配置类，用于初始化策略引擎
 * 
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class StrategyAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public StrategyEngine strategyEngine() {
        StrategyEngine engine = new StrategyEngine();
        log.info("策略引擎自动配置已启用");
        log.info("支持的策略类型: 技术指标策略、趋势策略、均值回归策略");
        log.info("功能模块: 策略管理、回测引擎、实盘执行");
        return engine;
    }
}