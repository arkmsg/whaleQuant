package com.whaleal.quant.slippage.config;

import com.whaleal.quant.slippage.calculator.SlippageCalculator;
import com.whaleal.quant.slippage.calculator.CommissionCalculator;
import com.whaleal.quant.slippage.calculator.PercentageSlippageCalculator;
import com.whaleal.quant.slippage.calculator.PercentageCommissionCalculator;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 滑点配置
 * 用于管理滑点和手续费的配置参数
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class SlippageConfig {

    // 滑点配置
    private SlippageCalculator slippageCalculator;
    private BigDecimal slippagePercentage;
    private boolean enableSlippage;

    // 手续费配置
    private CommissionCalculator commissionCalculator;
    private BigDecimal commissionRate;
    private BigDecimal minimumCommission;
    private boolean enableCommission;

    // 税费配置
    private BigDecimal taxRate;
    private boolean enableTax;

    /**
     * 获取默认配置
     * @return 默认配置
     */
    public static SlippageConfig getDefault() {
        return SlippageConfig.builder()
                .slippageCalculator(new PercentageSlippageCalculator())
                .slippagePercentage(BigDecimal.valueOf(0.001))
                .enableSlippage(true)
                .commissionCalculator(new PercentageCommissionCalculator())
                .commissionRate(BigDecimal.valueOf(0.0003))
                .minimumCommission(BigDecimal.valueOf(0.1))
                .enableCommission(true)
                .taxRate(BigDecimal.valueOf(0.001))
                .enableTax(true)
                .build();
    }
}