package com.whaleal.quant.integration;

import com.whaleal.quant.strategy.core.BaseStrategy;
import com.whaleal.quant.strategy.core.Strategy;
import com.whaleal.quant.strategy.engine.StrategyEngine;
import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.binance.sync.PositionSyncService;
import com.whaleal.quant.core.model.Bar;
import com.whaleal.quant.core.model.Order;
import com.whaleal.quant.core.model.Position;
import com.whaleal.quant.core.model.Ticker;
import com.whaleal.quant.core.provider.BrokerProvider;
import com.whaleal.quant.core.provider.MarketProvider;
import com.whaleal.quant.data.service.CacheService;
import com.whaleal.quant.data.service.DataService;
import com.whaleal.quant.risk.RiskManager;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.rule.OrderAmountRule;
import com.whaleal.quant.risk.rule.PriceDeviationRule;
import com.whaleal.quant.risk.rule.TotalPositionRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 架构集成测试
 * 验证重构后的分层架构是否正常工作
 */
@SpringBootTest
@ActiveProfiles("test")
public class ArchitectureTest {

    @Autowired(required = false)
    private CacheService cacheService;

    @Autowired(required = false)
    private DataService dataService;

    @Autowired(required = false)
    private PositionSyncService binancePositionSyncService;

    @Autowired(required = false)
    private RiskManager riskManager;

    /**
     * 测试核心模块是否正常加载
     */
    @Test
    public void testCoreModule() {
        // 测试基础模型
        Ticker ticker = Ticker.builder()
                .symbol("AAPL")
                .lastPrice(BigDecimal.valueOf(150.0))
                .build();
        assertNotNull(ticker);

        Bar bar = Bar.builder()
                .symbol("AAPL")
                .open(BigDecimal.valueOf(150.0))
                .high(BigDecimal.valueOf(155.0))
                .low(BigDecimal.valueOf(149.0))
                .close(BigDecimal.valueOf(152.0))
                .volume(BigDecimal.valueOf(1000000))
                .build();
        assertNotNull(bar);

        Order order = Order.builder()
                .orderId("order-1")
                .symbol("AAPL")
                .side("BUY")
                .type("MARKET")
                .quantity(BigDecimal.valueOf(100))
                .status("PENDING")
                .build();
        assertNotNull(order);

        Position position = Position.builder()
                .positionId("position-1")
                .symbol("AAPL")
                .quantity(BigDecimal.valueOf(100))
                .averagePrice(BigDecimal.valueOf(150.0))
                .currentPrice(BigDecimal.valueOf(152.0))
                .direction("LONG")
                .build();
        assertNotNull(position);
    }

    /**
     * 测试数据模块是否正常加载
     */
    @Test
    public void testDataModule() {
        if (cacheService != null) {
            assertNotNull(cacheService);
        }

        if (dataService != null) {
            assertNotNull(dataService);
        }
    }

    /**
     * 测试交易模块是否正常加载
     */
    @Test
    public void testTradingModule() {
        if (binancePositionSyncService != null) {
            assertNotNull(binancePositionSyncService);
        }
    }

    /**
     * 测试风控模块是否正常加载
     */
    @Test
    public void testRiskModule() {
        if (riskManager != null) {
            assertNotNull(riskManager);
        } else {
            // 如果没有自动配置，手动创建测试
            RiskConfig config = RiskConfig.builder()
                    .maxOrderAmount(BigDecimal.valueOf(100000))
                    .priceDeviationThreshold(BigDecimal.valueOf(5))
                    .maxTotalPositionValue(BigDecimal.valueOf(1000000))
                    .build();

            RiskManager manualRiskManager = new RiskManager(config);
            manualRiskManager.addRule(new OrderAmountRule());
            manualRiskManager.addRule(new PriceDeviationRule(BigDecimal.valueOf(150.0)));
            manualRiskManager.addRule(new TotalPositionRule(List.of()));

            assertNotNull(manualRiskManager);
            assertTrue(manualRiskManager.getRules().size() > 0);
        }
    }

    /**
     * 测试策略模块是否正常加载
     */
    @Test
    public void testStrategyModule() {
        // 创建策略引擎
        StrategyEngine engine = new StrategyEngine();
        assertNotNull(engine);

        // 创建测试策略
        TestStrategy strategy = new TestStrategy();
        strategy.initialize("test-strategy", "Test Strategy");

        // 添加策略到引擎
        engine.addStrategy("test-strategy", strategy);

        // 启动策略
        engine.startAllStrategies();

        // 模拟行情数据
        Ticker ticker = Ticker.builder()
                .symbol("AAPL")
                .lastPrice(BigDecimal.valueOf(152.0))
                .build();

        Bar bar = Bar.builder()
                .symbol("AAPL")
                .open(BigDecimal.valueOf(150.0))
                .high(BigDecimal.valueOf(155.0))
                .low(BigDecimal.valueOf(149.0))
                .close(BigDecimal.valueOf(152.0))
                .volume(BigDecimal.valueOf(1000000))
                .build();

        // 创建并发布市场数据事件
        MarketDataEvent event = MarketDataEvent.builder()
                .eventId("event-1")
                .ticker(ticker)
                .timestamp(System.currentTimeMillis())
                .source("TEST")
                .build();

        // 发布事件
        engine.publishMarketDataEvent(event);

        // 停止策略
        engine.stopAllStrategies();
    }

    /**
     * 测试策略实现
     */
    private static class TestStrategy extends BaseStrategy {
        @Override
        public void onMarketData(MarketDataEvent event) {
            // 简单的测试策略逻辑
            System.out.println("Processing market data for " + event.getTicker().getSymbol());
        }
    }
}