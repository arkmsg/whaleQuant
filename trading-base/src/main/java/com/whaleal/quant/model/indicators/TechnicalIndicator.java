package com.whaleal.quant.model.indicators;

import java.util.List;
import com.whaleal.quant.model.Bar;

public interface TechnicalIndicator {
    double calculate(List<Bar> bars);
}
