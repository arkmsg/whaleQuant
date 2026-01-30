package com.whaleal.quant.backtest.engine;

import java.util.Arrays;
import java.util.List;

public class BacktestLayer {
    
    public enum Layer {
        HIGH_FREQUENCY("高频验证", "30m", Arrays.asList("opening", "closing", "extreme")),
        STANDARD("标准验证", "1h", Arrays.asList("normal", "regular")),
        LOW_FREQUENCY("低频验证", "4h", Arrays.asList("crypto", "afterhours", "stable"));
        
        private final String name;
        private final String timeInterval;
        private final List<String> scenarios;
        
        Layer(String name, String timeInterval, List<String> scenarios) {
            this.name = name;
            this.timeInterval = timeInterval;
            this.scenarios = scenarios;
        }
        
        public String getName() {
            return name;
        }
        
        public String getTimeInterval() {
            return timeInterval;
        }
        
        public List<String> getScenarios() {
            return scenarios;
        }
    }
}
