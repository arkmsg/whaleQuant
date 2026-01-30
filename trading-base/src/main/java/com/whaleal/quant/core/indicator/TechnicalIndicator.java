package com.whaleal.quant.core.indicator;

import java.util.List;
import com.whaleal.quant.core.model.Bar;

public interface TechnicalIndicator {
    double calculate(List<Bar> bars);
}
