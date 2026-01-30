package com.whaleal.quant.risk.autoconfigure;

import com.whaleal.quant.risk.RiskManager;
import com.whaleal.quant.risk.config.RiskConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 风控管理器自动配置
 *
 * @author Ark Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RiskProperties.class)
public class RiskAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RiskConfig riskConfig(RiskProperties properties) {
        log.info("初始化风控配置");
        return RiskConfig.builder()
                .maxOrderAmount(properties.getMaxOrderAmount())
                .maxOrderQuantity(properties.getMaxOrderQuantity())
                .priceDeviationThreshold(properties.getPriceDeviationThreshold())
                .maxTotalPositionValue(properties.getMaxTotalPositionValue())
                .maxSinglePositionValue(properties.getMaxSinglePositionValue())
                .maxDrawdownThreshold(properties.getMaxDrawdownThreshold())
                .maxDailyLoss(properties.getMaxDailyLoss())
                .maxDailyTrades(properties.getMaxDailyTrades())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public RiskManager riskManager(RiskConfig riskConfig) {
        log.info("初始化风控管理器");
        return new RiskManager(riskConfig);
    }
}
