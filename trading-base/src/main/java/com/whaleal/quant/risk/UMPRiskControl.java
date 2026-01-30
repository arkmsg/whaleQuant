package com.whaleal.quant.risk;

import com.whaleal.quant.model.Order;

public class UMPRiskControl implements RiskControlSystem {

    private double maxPositionRatio = 0.2;
    private double maxDrawdown = 0.1;
    private double marketVolatilityThreshold = 0.02;
    private double currentDrawdown = 0;
    private double currentMarketVolatility = 0;

    public UMPRiskControl() {
    }

    public UMPRiskControl(double maxPositionRatio, double maxDrawdown, double marketVolatilityThreshold) {
        this.maxPositionRatio = maxPositionRatio;
        this.maxDrawdown = maxDrawdown;
        this.marketVolatilityThreshold = marketVolatilityThreshold;
    }

    @Override
    public boolean checkRisk(Order order) {
        if (currentDrawdown > maxDrawdown) {
            return false;
        }

        if (currentMarketVolatility > marketVolatilityThreshold) {
            return false;
        }

        // 这里可以添加仓位风险评估逻辑
        // 例如：检查订单对整体仓位的影响

        return true;
    }

    @Override
    public RiskLevel assessRisk(Order order) {
        if (currentDrawdown > maxDrawdown * 0.8) {
            return RiskLevel.HIGH;
        } else if (currentMarketVolatility > marketVolatilityThreshold * 0.8) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }

    public void updateMarketVolatility(double volatility) {
        this.currentMarketVolatility = volatility;
    }

    public void updateDrawdown(double drawdown) {
        this.currentDrawdown = drawdown;
    }
}
