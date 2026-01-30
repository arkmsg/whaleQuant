package com.whaleal.quant.integration;

import com.whaleal.quant.backtest.engine.BacktestEngine;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.backtest.data.MockBacktestDataProvider;
import com.whaleal.quant.engine.risk.RiskManager;
import com.whaleal.quant.engine.risk.rule.StopLossTakeProfitRule;
import com.whaleal.quant.metrics.calculator.CompositeMetricCalculator;
import com.whaleal.quant.slippage.calculator.PercentageSlippageCalculator;
import com.whaleal.quant.strategy.core.BaseStrategy;
import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.core.model.Bar;
import com.whaleal.quant.core.model.Ticker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 回测集成测试
 * 验证回测系统的完整流程
 */
public class BacktestIntegrationTest {
    
    /**
     * 测试完整的回测流程
     */
    @Test
    public void testCompleteBacktestFlow() {
        // 1. 创建回测配置
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        BacktestConfig config = BacktestConfig.builder()
                .startDate(startDate)
                .endDate(endDate)
                .initialCapital(100000.0)
                .symbol("AAPL")
                .timeframe("1D")
                .build();
        
        // 2. 创建策略
        TestStrategy strategy = new TestStrategy();
        
        // 3. 创建风险管理器
        RiskManager riskManager = new RiskManager();
        riskManager.addRule(new StopLossTakeProfitRule(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10)));
        
        // 4. 创建滑点计算器
        PercentageSlippageCalculator slippageCalculator = new PercentageSlippageCalculator(0.001);
        
        // 5. 创建数据提供者
        MockBacktestDataProvider dataProvider = new MockBacktestDataProvider();
        
        // 6. 创建回测引擎
        BacktestEngine engine = new BacktestEngine();
        engine.setDataProvider(dataProvider);
        engine.setRiskManager(riskManager);
        engine.setSlippageCalculator(slippageCalculator);
        
        // 7. 执行回测
        BacktestResult result = engine.runBacktest(strategy, config);
        
        // 8. 验证回测结果
        assertNotNull(result);
        assertNotNull(result.getMetrics());
        assertTrue(result.getTradeCount() >= 0);
        
        // 9. 使用度量计算器计算指标
        CompositeMetricCalculator metricCalculator = new CompositeMetricCalculator();
        var metrics = metricCalculator.calculate(result);
        
        // 10. 验证度量计算结果
        assertNotNull(metrics);
        assertTrue(metrics.containsKey("totalReturn"));
        assertTrue(metrics.containsKey("sharpeRatio"));
        assertTrue(metrics.containsKey("winRate"));
        
        // 11. 生成回测报告
        String report = result.generateReport();
        assertNotNull(report);
        assertTrue(report.contains("回测报告"));
    }
    
    /**
     * 测试策略实现
     */
    private static class TestStrategy extends BaseStrategy {
        @Override
        public void onMarketData(MarketDataEvent event) {
            // 简单的测试策略逻辑：价格突破时买入
            Ticker ticker = event.getTicker();
            String symbol = ticker.getSymbol();
            BigDecimal price = ticker.getLastPrice();
            
            // 简单的买入逻辑
            if (price.compareTo(BigDecimal.valueOf(150)) > 0) {
                // 这里应该调用下单接口，回测引擎会捕获这些调用
                System.out.println("Buy signal for " + symbol + " at " + price);
            }
        }
    }
    
    /**
     * 测试多线程回测
     */
    @Test
    public void testParallelBacktest() {
        // 1. 创建回测配置
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        BacktestConfig config = BacktestConfig.builder()
                .startDate(startDate)
                .endDate(endDate)
                .initialCapital(100000.0)
                .symbol("AAPL")
                .timeframe("1D")
                .build();
        
        // 2. 创建策略
        TestStrategy strategy = new TestStrategy();
        
        // 3. 创建回测引擎
        BacktestEngine engine = new BacktestEngine();
        
        // 4. 执行并行回测
        List<BacktestResult> results = engine.runParallelBacktests(
                List.of(strategy),
                List.of(config),
                2 // 2个线程
        );
        
        // 5. 验证结果
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }
}
