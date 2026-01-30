package com.whaleal.quant.risk;

import com.whaleal.quant.model.Order;

public interface RiskControlSystem {
    boolean checkRisk(Order order);
    RiskLevel assessRisk(Order order);

    enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        EXTREME
    }
}
