package com.whaleal.quant.longport.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 账户余额响应
 *
 * @author Longport SDK Team
 */
@Data
@Builder
public class AccountBalanceResp {

    /**
     * 现金总额
     */
    private BigDecimal totalCash;

    /**
     * 最大融资金额
     */
    private BigDecimal maxFinanceAmount;

    /**
     * 剩余融资金额
     */
    private BigDecimal remainingFinanceAmount;

    /**
     * 风险等级
     */
    private Integer riskLevel;

    /**
     * 保证金催缴
     */
    private BigDecimal marginCall;

    /**
     * 净资产
     */
    private BigDecimal netAssets;

    /**
     * 初始保证金
     */
    private BigDecimal initMargin;

    /**
     * 维持保证金
     */
    private BigDecimal maintenanceMargin;

    /**
     * 可用现金
     */
    public BigDecimal getAvailableCash() {
        if (totalCash != null && initMargin != null) {
            return totalCash.subtract(initMargin);
        }
        return totalCash;
    }

    /**
     * 是否有足够现金
     */
    public boolean hasSufficientCash(BigDecimal requiredAmount) {
        BigDecimal available = getAvailableCash();
        return available != null && available.compareTo(requiredAmount) >= 0;
    }
}

