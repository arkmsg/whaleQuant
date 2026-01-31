package com.whaleal.quant.risk;

import com.whaleal.quant.model.trading.Order;

public class DynamicSuppressionRiskControl implements RiskControlSystem {

    private double maxOrderAmount = 100000;
    private double priceDeviationThreshold = 0.05;
    private int maxDailyTrades = 50;
    private int currentDailyTrades = 0;

    public DynamicSuppressionRiskControl() {
    }

    public DynamicSuppressionRiskControl(double maxOrderAmount, double priceDeviationThreshold, int maxDailyTrades) {
        this.maxOrderAmount = maxOrderAmount;
        this.priceDeviationThreshold = priceDeviationThreshold;
        this.maxDailyTrades = maxDailyTrades;
    }

    @Override
    public boolean checkRisk(Order order) {
        if (currentDailyTrades >= maxDailyTrades) {
            return false;
        }

        double orderAmount = order.getQuantity().doubleValue() * order.getPrice().doubleValue();
        if (orderAmount > maxOrderAmount) {
            return false;
        }

        // 这里可以添加价格偏差检查逻辑
        // 例如：检查订单价格与当前市场价格的偏差

        currentDailyTrades++;
        return true;
    }

    @Override
    public RiskLevel assessRisk(Order order) {
        double orderAmount = order.getQuantity().doubleValue() * order.getPrice().doubleValue();

        if (orderAmount > maxOrderAmount * 0.8) {
            return RiskLevel.HIGH;
        } else if (orderAmount > maxOrderAmount * 0.5) {
            return RiskLevel.MEDIUM;
        } else if (currentDailyTrades > maxDailyTrades * 0.8) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }

    public void resetDailyTrades() {
        currentDailyTrades = 0;
    }
}
