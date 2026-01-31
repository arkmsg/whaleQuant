package com.whaleal.quant.risk;

import com.whaleal.quant.model.trading.Order;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class TimeManagementRiskControl implements RiskControlSystem {

    private boolean allowPreMarketTrading = false;
    private boolean allowAfterHoursTrading = false;
    private LocalTime regularMarketOpen = LocalTime.of(9, 30);
    private LocalTime regularMarketClose = LocalTime.of(16, 0);

    public TimeManagementRiskControl() {
    }

    public TimeManagementRiskControl(boolean allowPreMarketTrading, boolean allowAfterHoursTrading) {
        this.allowPreMarketTrading = allowPreMarketTrading;
        this.allowAfterHoursTrading = allowAfterHoursTrading;
    }

    @Override
    public boolean checkRisk(Order order) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // 检查是否是周末
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // 检查交易时段
        if (currentTime.isBefore(regularMarketOpen)) {
            return allowPreMarketTrading;
        } else if (currentTime.isAfter(regularMarketClose)) {
            return allowAfterHoursTrading;
        } else {
            return true;
        }
    }

    @Override
    public RiskLevel assessRisk(Order order) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        if (currentTime.isBefore(regularMarketOpen) || currentTime.isAfter(regularMarketClose)) {
            return RiskLevel.MEDIUM;
        } else if (currentTime.isBefore(regularMarketOpen.plusMinutes(30)) || currentTime.isAfter(regularMarketClose.minusMinutes(30))) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }

    public void setMarketHours(LocalTime open, LocalTime close) {
        this.regularMarketOpen = open;
        this.regularMarketClose = close;
    }
}
