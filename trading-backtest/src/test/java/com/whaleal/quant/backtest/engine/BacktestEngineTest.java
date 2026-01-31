package com.whaleal.quant.backtest.engine;

import com.whaleal.quant.alpha.factor.BuyFactor;
import com.whaleal.quant.alpha.factor.SellFactor;
import com.whaleal.quant.backtest.data.MockBacktestDataProvider;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.strategy.core.StrategyEngine;
import com.whaleal.quant.trading.service.TradingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 回测引擎测试
 * 用于测试回测系统的基本功能
 *
 * @author whaleal
 * @version 1.0.0
 */
public class BacktestEngineTest {

    public static void main(String[] args) {
        // 创建回测配置
        BacktestConfig config = new BacktestConfig()
                .setStartDate(LocalDateTime.now().minusMonths(3))
                .setEndDate(LocalDateTime.now())
                .setInitialCapital(100000.0)
                .setCommissionRate(0.0003)
                .setSlippageRate(0.001);

        // 添加测试股票
        Set<String> symbols = new HashSet<>();
        symbols.add("AAPL");
        symbols.add("MSFT");
        symbols.add("GOOGL");
        config.setSymbols(symbols);

        // 创建模拟数据提供者
        MockBacktestDataProvider dataProvider = new MockBacktestDataProvider();

        // 创建简单的交易服务
        TradingService tradingService = new TradingService() {
            @Override
            public Object buy(String symbol, double quantity, double price) {
                System.out.println("Buy: " + symbol + " " + quantity + " @ " + price);
                return null;
            }

            @Override
            public Object sell(String symbol, double quantity, double price) {
                System.out.println("Sell: " + symbol + " " + quantity + " @ " + price);
                return null;
            }

            @Override
            public Object getPosition(String symbol) {
                return null;
            }
        };

        // 创建简单的买入因子
        BuyFactor buyFactor = new BuyFactor() {
            @Override
            public String getName() {
                return "SimpleBuyFactor";
            }

            @Override
            public double calculateBuySignal(String symbol, java.util.List<com.whaleal.quant.model.Bar> bars, com.whaleal.quant.model.Ticker ticker) {
                // 简单的随机买入信号
                return Math.random();
            }
        };

        // 创建简单的卖出因子
        SellFactor sellFactor = new SellFactor() {
            @Override
            public String getName() {
                return "SimpleSellFactor";
            }

            @Override
            public double calculateSellSignal(String symbol, com.whaleal.quant.model.trading.Position position, com.whaleal.quant.model.trading.Order order, java.util.List<com.whaleal.quant.model.Bar> bars, com.whaleal.quant.model.Ticker ticker) {
                // 简单的随机卖出信号
                return Math.random();
            }
        };

        // 创建策略引擎
        StrategyEngine strategyEngine = StrategyEngine.builder()
                .strategyName("TestStrategy")
                .buyFactors(Collections.singletonList(buyFactor))
                .sellFactors(Collections.singletonList(sellFactor))
                .tradingService(tradingService)
                .stockPool(symbols)
                .build();

        // 创建回测引擎
        BacktestEngine backtestEngine = BacktestEngine.builder()
                .config(config)
                .dataProvider(dataProvider)
                .strategyEngine(strategyEngine)
                .build();

        // 添加回测监听器
        backtestEngine.addListener(new BacktestListener() {
            @Override
            public void onBacktestStart(BacktestEngine engine) {
                System.out.println("Backtest started");
            }

            @Override
            public void onDataLoading(BacktestEngine engine) {
                System.out.println("Loading data...");
            }

            @Override
            public void onBacktestComplete(BacktestEngine engine, BacktestResult result) {
                System.out.println("Backtest completed");
                System.out.println(result.generateReport());
            }

            @Override
            public void onBacktestError(BacktestEngine engine, Exception e) {
                System.out.println("Backtest error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 运行回测
        try {
            BacktestResult result = backtestEngine.run();
            System.out.println("Test completed successfully!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
