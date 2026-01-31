package com.whaleal.quant.risk.autoconfigure;

import com.whaleal.quant.risk.balance.BalanceConfig;
import com.whaleal.quant.risk.balance.GlobalBalanceManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资金管理器自动配置
 * 将GlobalBalanceManager集成到Spring Boot应用中
 *
 * @author whaleal
 * @version 1.0.0
 */
@Configuration
public class BalanceManagerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BalanceConfig balanceConfig() {
        return BalanceConfig.createDefault();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalBalanceManager globalBalanceManager(BalanceConfig balanceConfig) {
        return new GlobalBalanceManager(balanceConfig);
    }
}
