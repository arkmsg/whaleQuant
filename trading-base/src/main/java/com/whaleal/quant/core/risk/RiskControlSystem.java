package com.whaleal.quant.core.risk;

import com.whaleal.quant.core.model.Order;

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
