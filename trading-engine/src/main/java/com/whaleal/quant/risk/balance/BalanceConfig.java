package com.whaleal.quant.risk.balance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资金配置类
 * 用于配置GlobalBalanceManager的参数
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class BalanceConfig {

    // 资金水位线阈值（百分比）
    private BigDecimal watermarkThreshold;

    // 单笔最大交易金额
    private BigDecimal maxSingleOrderAmount;

    // 单日最大交易金额
    private BigDecimal maxDailyTradingAmount;

    // 最大资金使用率（百分比）
    private BigDecimal maxFundUsageRatio;

    /**
     * 创建默认配置
     * @return 默认配置
     */
    public static BalanceConfig createDefault() {
        return BalanceConfig.builder()
                .watermarkThreshold(new BigDecimal("0.2")) // 20% 水位线
                .maxSingleOrderAmount(new BigDecimal("100000")) // 10万最大单笔
                .maxDailyTradingAmount(new BigDecimal("500000")) // 50万最大单日
                .maxFundUsageRatio(new BigDecimal("0.8")) // 80% 最大使用率
                .build();
    }
}
