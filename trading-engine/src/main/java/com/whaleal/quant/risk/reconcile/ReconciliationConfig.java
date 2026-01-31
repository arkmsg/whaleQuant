package com.whaleal.quant.risk.reconcile;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * 对账配置
 * 用于配置对账服务的参数
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class ReconciliationConfig {
    /**
     * 持仓数量阈值
     */
    private final BigDecimal positionQuantityThreshold;

    /**
     * 持仓金额阈值
     */
    private final BigDecimal positionAmountThreshold;

    /**
     * 对账频率
     */
    private final Duration reconciliationFrequency;

    /**
     * 是否启用自动熔断
     */
    private final boolean enableAutoCircuitBreak;

    /**
     * 是否启用告警
     */
    private final boolean enableAlert;

    /**
     * 告警阈值
     */
    private final int alertThreshold;

    /**
     * 创建默认配置
     * @return 默认配置
     */
    public static ReconciliationConfig defaultConfig() {
        return ReconciliationConfig.builder()
                .positionQuantityThreshold(BigDecimal.valueOf(0.0001))
                .positionAmountThreshold(BigDecimal.valueOf(1))
                .reconciliationFrequency(Duration.ofMinutes(5))
                .enableAutoCircuitBreak(true)
                .enableAlert(true)
                .alertThreshold(3)
                .build();
    }
}
