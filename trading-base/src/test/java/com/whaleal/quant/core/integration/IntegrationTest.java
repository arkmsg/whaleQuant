package com.whaleal.quant.core.integration;

import com.whaleal.quant.core.indicator.MACDIndicator;
import com.whaleal.quant.core.indicator.RSIIndicator;
import com.whaleal.quant.core.indicator.BOLLIndicator;
import com.whaleal.quant.core.model.Bar;
import com.whaleal.quant.core.model.Order;
import com.whaleal.quant.core.risk.DynamicSuppressionRiskControl;
import com.whaleal.quant.core.risk.UMPRiskControl;
import com.whaleal.quant.core.risk.TimeManagementRiskControl;
import com.whaleal.quant.core.market.MarketTimeManager;
import com.whaleal.quant.ai.model.AIDecisionEngine;
import com.whaleal.quant.ai.model.ConfidenceScoring;
import com.whaleal.quant.ai.model.AIModelFactory;
import com.whaleal.quant.ai.model.MarketSentiment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class IntegrationTest {
    
    @Test
    public void testTechnicalIndicators() {
        // 创建测试K线数据
        List<Bar> bars = createTestBars();
        
        // 测试MACD指标
        MACDIndicator macd = new MACDIndicator();
        double macdValue = macd.calculate(bars);
        System.out.println("MACD value: " + macdValue);
        
        // 测试RSI指标
        RSIIndicator rsi = new RSIIndicator();
        double rsiValue = rsi.calculate(bars);
        System.out.println("RSI value: " + rsiValue);
        
        // 测试布林带指标
        BOLLIndicator boll = new BOLLIndicator();
        double bollValue = boll.calculate(bars);
        System.out.println("BOLL value: " + bollValue);
    }
    
    @Test
    public void testRiskControlSystem() {
        // 创建测试订单
        Order order = createTestOrder();
        
        // 测试动态压制风控
        DynamicSuppressionRiskControl dynamicRiskControl = new DynamicSuppressionRiskControl();
        boolean dynamicRiskPass = dynamicRiskControl.checkRisk(order);
        System.out.println("Dynamic risk control pass: " + dynamicRiskPass);
        
        // 测试UMP风控
        UMPRiskControl umpRiskControl = new UMPRiskControl();
        boolean umpRiskPass = umpRiskControl.checkRisk(order);
        System.out.println("UMP risk control pass: " + umpRiskPass);
        
        // 测试时间管理风控
        TimeManagementRiskControl timeRiskControl = new TimeManagementRiskControl();
        boolean timeRiskPass = timeRiskControl.checkRisk(order);
        System.out.println("Time risk control pass: " + timeRiskPass);
    }
    
    @Test
    public void testMarketTimeManager() {
        MarketTimeManager timeManager = new MarketTimeManager();
        
        // 测试美股市场时间
        boolean usMarketOpen = timeManager.isMarketOpen(MarketTimeManager.Market.US);
        MarketTimeManager.TradingSession usSession = timeManager.getTradingSession(MarketTimeManager.Market.US);
        System.out.println("US market open: " + usMarketOpen);
        System.out.println("US trading session: " + usSession);
        
        // 测试港股市场时间
        boolean hkMarketOpen = timeManager.isMarketOpen(MarketTimeManager.Market.HK);
        MarketTimeManager.TradingSession hkSession = timeManager.getTradingSession(MarketTimeManager.Market.HK);
        System.out.println("HK market open: " + hkMarketOpen);
        System.out.println("HK trading session: " + hkSession);
    }
    
    @Test
    public void testAIDecisionEngine() {
        // 创建AI模型
        var aiModel = AIModelFactory.createModel(com.whaleal.quant.ai.model.ModelInfo.ModelType.TREE);
        AIDecisionEngine decisionEngine = new AIDecisionEngine(aiModel);
        
        // 创建市场情绪
        MarketSentiment sentiment = new MarketSentiment(MarketSentiment.SentimentType.BULLISH, 0.8);
        
        // 生成交易信号
        var signal = decisionEngine.generateSignal("AAPL", sentiment, 0.75);
        System.out.println("Generated signal: " + signal);
    }
    
    private List<Bar> createTestBars() {
        List<Bar> bars = new ArrayList<>();
        Instant now = Instant.now();
        
        // 创建20根测试K线
        for (int i = 0; i < 20; i++) {
            Bar bar = new Bar();
            bar.setSymbol("AAPL");
            bar.setOpen(BigDecimal.valueOf(100 + i));
            bar.setHigh(BigDecimal.valueOf(102 + i));
            bar.setLow(BigDecimal.valueOf(99 + i));
            bar.setClose(BigDecimal.valueOf(101 + i));
            bar.setVolume(BigDecimal.valueOf(1000000 + i * 10000));
            bar.setTimestamp(now.minusSeconds(i * 3600));
            bars.add(bar);
        }
        
        return bars;
    }
    
    private Order createTestOrder() {
        Order order = new Order();
        order.setOrderId("test_order_1");
        order.setSymbol("AAPL");
        order.setType(Order.OrderType.LIMIT);
        order.setSide(Order.OrderSide.BUY);
        order.setStatus(Order.OrderStatus.NEW);
        order.setQuantity(BigDecimal.valueOf(100));
        order.setPrice(BigDecimal.valueOf(150));
        order.setExecutedQuantity(BigDecimal.ZERO);
        order.setAvgPrice(BigDecimal.ZERO);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        return order;
    }
}
