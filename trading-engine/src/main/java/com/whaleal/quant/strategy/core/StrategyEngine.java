package com.whaleal.quant.strategy.core;

import com.whaleal.quant.alpha.factor.BuyFactor;
import com.whaleal.quant.alpha.factor.PickStockFactor;
import com.whaleal.quant.alpha.factor.PickTimeFactor;
import com.whaleal.quant.alpha.factor.SellFactor;
import com.whaleal.quant.model.Bar;
import com.whaleal.quant.model.Ticker;
import com.whaleal.quant.model.trading.Order;
import com.whaleal.quant.model.trading.Position;
import com.whaleal.quant.strategy.event.MarketDataEvent;
import com.whaleal.quant.trading.service.TradingService;

import java.util.*;

/**
 * 策略引擎
 * 整合买入因子、卖出因子、择时因子和择股因子，实现完整的策略逻辑
 *
 * @author whaleal
 * @version 1.0.0
 */
public class StrategyEngine {

    /**
     * 策略名称
     */
    private final String strategyName;

    /**
     * 买入因子列表
     */
    private final List<BuyFactor> buyFactors;

    /**
     * 卖出因子列表
     */
    private final List<SellFactor> sellFactors;

    /**
     * 择时因子
     */
    private final PickTimeFactor pickTimeFactor;

    /**
     * 择股因子
     */
    private final PickStockFactor pickStockFactor;

    /**
     * 交易服务
     */
    private final TradingService tradingService;

    /**
     * 股票池
     */
    private Set<String> stockPool;

    /**
     * 持仓映射
     */
    private final Map<String, Position> positions;

    /**
     * 订单映射
     */
    private final Map<String, Order> orders;

    /**
     * K线数据映射
     */
    private final Map<String, List<Bar>> symbolBarsMap;

    /**
     * 实时行情数据映射
     */
    private final Map<String, Ticker> symbolTickerMap;

    /**
     * 市场指数K线数据
     */
    private List<Bar> marketBars;

    /**
     * 市场指数实时行情数据
     */
    private Ticker marketTicker;

    /**
     * 构造方法
     * @param strategyName 策略名称
     * @param buyFactors 买入因子列表
     * @param sellFactors 卖出因子列表
     * @param pickTimeFactor 择时因子
     * @param pickStockFactor 择股因子
     * @param tradingService 交易服务
     */
    public StrategyEngine(String strategyName, List<BuyFactor> buyFactors, List<SellFactor> sellFactors, PickTimeFactor pickTimeFactor, PickStockFactor pickStockFactor, TradingService tradingService) {
        this.strategyName = strategyName;
        this.buyFactors = buyFactors;
        this.sellFactors = sellFactors;
        this.pickTimeFactor = pickTimeFactor;
        this.pickStockFactor = pickStockFactor;
        this.tradingService = tradingService;
        this.stockPool = new HashSet<>();
        this.positions = new HashMap<>();
        this.orders = new HashMap<>();
        this.symbolBarsMap = new HashMap<>();
        this.symbolTickerMap = new HashMap<>();
    }

    /**
     * 处理市场数据事件
     * @param event 市场数据事件
     */
    public void onMarketData(MarketDataEvent event) {
        String symbol = event.getSymbol();
        Ticker ticker = event.getTicker();
        List<Bar> bars = event.getBars();

        // 更新数据
        symbolTickerMap.put(symbol, ticker);
        symbolBarsMap.put(symbol, bars);

        // 检查市场时机
        if (!isGoodMarketTime()) {
            return;
        }

        // 检查卖出信号
        checkSellSignals(symbol, ticker, bars);

        // 检查买入信号
        checkBuySignals(symbol, ticker, bars);
    }

    /**
     * 检查市场时机
     * @return 是否是好的市场时机
     */
    private boolean isGoodMarketTime() {
        if (pickTimeFactor == null || marketBars == null || marketTicker == null) {
            return true;
        }
        return pickTimeFactor.isGoodTime("MARKET_INDEX", marketBars, marketTicker, symbolTickerMap);
    }

    /**
     * 检查卖出信号
     * @param symbol 交易对符号
     * @param ticker 实时行情数据
     * @param bars K线数据列表
     */
    private void checkSellSignals(String symbol, Ticker ticker, List<Bar> bars) {
        Position position = positions.get(symbol);
        Order order = orders.get(symbol);

        if (position == null) {
            return;
        }

        for (SellFactor sellFactor : sellFactors) {
            if (sellFactor.shouldSell(symbol, position, order, bars, ticker)) {
                // 执行卖出操作
                Order sellOrder = Order.builder()
                    .symbol(symbol)
                    .side("SELL")
                    .type("MARKET")
                    .quantity(position.getQuantity())
                    .price(ticker.getPrice())
                    .build();
                tradingService.placeOrder(sellOrder);
                positions.remove(symbol);
                orders.remove(symbol);
                break;
            }
        }
    }

    /**
     * 检查买入信号
     * @param symbol 交易对符号
     * @param ticker 实时行情数据
     * @param bars K线数据列表
     */
    private void checkBuySignals(String symbol, Ticker ticker, List<Bar> bars) {
        // 检查是否已经持仓
        if (positions.containsKey(symbol)) {
            return;
        }

        // 检查是否在股票池中
        if (!stockPool.contains(symbol)) {
            return;
        }

        // 计算买入信号强度
        double buySignalStrength = 0.0;
        for (BuyFactor buyFactor : buyFactors) {
            buySignalStrength += buyFactor.calculateBuySignal(symbol, bars, ticker);
        }
        buySignalStrength /= buyFactors.size();

        // 检查是否买入
        if (buySignalStrength > 0.5) {
            // 执行买入操作
            Order buyOrder = Order.builder()
                .symbol(symbol)
                .side("BUY")
                .type("MARKET")
                .quantity(java.math.BigDecimal.valueOf(100))
                .price(ticker.getPrice())
                .build();
            tradingService.placeOrder(buyOrder);
            orders.put(symbol, buyOrder);
            // 模拟持仓
            Position position = Position.builder()
                .symbol(symbol)
                .quantity(buyOrder.getQuantity())
                .build();
            positions.put(symbol, position);
        }
    }

    /**
     * 运行策略
     */
    public void run() {
        // 选择股票
        if (pickStockFactor != null) {
            stockPool = pickStockFactor.pickStocks(stockPool, symbolBarsMap, symbolTickerMap);
        }

        // 对每个股票执行策略
        for (String symbol : stockPool) {
            Ticker ticker = symbolTickerMap.get(symbol);
            List<Bar> bars = symbolBarsMap.get(symbol);
            if (ticker != null && bars != null) {
                MarketDataEvent event = MarketDataEvent.builder()
                    .symbol(symbol)
                    .ticker(ticker)
                    .bars(bars)
                    .eventId("market_data_" + System.currentTimeMillis())
                    .timestamp(System.currentTimeMillis())
                    .source("SYSTEM")
                    .build();
                onMarketData(event);
            }
        }
    }

    /**
     * 设置股票池
     * @param stockPool 股票池
     */
    public void setStockPool(Set<String> stockPool) {
        this.stockPool = stockPool;
    }

    /**
     * 设置市场指数数据
     * @param marketBars 市场指数K线数据
     * @param marketTicker 市场指数实时行情数据
     */
    public void setMarketData(List<Bar> marketBars, Ticker marketTicker) {
        this.marketBars = marketBars;
        this.marketTicker = marketTicker;
    }

    /**
     * 获取策略名称
     * @return 策略名称
     */
    public String getStrategyName() {
        return strategyName;
    }

    /**
     * 获取持仓映射
     * @return 持仓映射
     */
    public Map<String, Position> getPositions() {
        return positions;
    }

    /**
     * 获取订单映射
     * @return 订单映射
     */
    public Map<String, Order> getOrders() {
        return orders;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String strategyName;
        private List<BuyFactor> buyFactors;
        private List<SellFactor> sellFactors;
        private PickTimeFactor pickTimeFactor;
        private PickStockFactor pickStockFactor;
        private TradingService tradingService;
        private Set<String> stockPool;

        /**
         * 设置策略名称
         * @param strategyName 策略名称
         * @return Builder
         */
        public Builder strategyName(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        /**
         * 设置买入因子列表
         * @param buyFactors 买入因子列表
         * @return Builder
         */
        public Builder buyFactors(List<BuyFactor> buyFactors) {
            this.buyFactors = buyFactors;
            return this;
        }

        /**
         * 添加买入因子
         * @param buyFactor 买入因子
         * @return Builder
         */
        public Builder addBuyFactor(BuyFactor buyFactor) {
            if (this.buyFactors == null) {
                this.buyFactors = new ArrayList<>();
            }
            this.buyFactors.add(buyFactor);
            return this;
        }

        /**
         * 设置卖出因子列表
         * @param sellFactors 卖出因子列表
         * @return Builder
         */
        public Builder sellFactors(List<SellFactor> sellFactors) {
            this.sellFactors = sellFactors;
            return this;
        }

        /**
         * 添加卖出因子
         * @param sellFactor 卖出因子
         * @return Builder
         */
        public Builder addSellFactor(SellFactor sellFactor) {
            if (this.sellFactors == null) {
                this.sellFactors = new ArrayList<>();
            }
            this.sellFactors.add(sellFactor);
            return this;
        }

        /**
         * 设置择时因子
         * @param pickTimeFactor 择时因子
         * @return Builder
         */
        public Builder pickTimeFactor(PickTimeFactor pickTimeFactor) {
            this.pickTimeFactor = pickTimeFactor;
            return this;
        }

        /**
         * 设置择股因子
         * @param pickStockFactor 择股因子
         * @return Builder
         */
        public Builder pickStockFactor(PickStockFactor pickStockFactor) {
            this.pickStockFactor = pickStockFactor;
            return this;
        }

        /**
         * 设置交易服务
         * @param tradingService 交易服务
         * @return Builder
         */
        public Builder tradingService(TradingService tradingService) {
            this.tradingService = tradingService;
            return this;
        }

        /**
         * 设置股票池
         * @param stockPool 股票池
         * @return Builder
         */
        public Builder stockPool(Set<String> stockPool) {
            this.stockPool = stockPool;
            return this;
        }

        /**
         * 构建策略引擎
         * @return 策略引擎
         */
        public StrategyEngine build() {
            if (strategyName == null) {
                strategyName = "DefaultStrategy";
            }
            if (buyFactors == null) {
                buyFactors = new ArrayList<>();
            }
            if (sellFactors == null) {
                sellFactors = new ArrayList<>();
            }
            if (stockPool == null) {
                stockPool = new HashSet<>();
            }
            return new StrategyEngine(strategyName, buyFactors, sellFactors, pickTimeFactor, pickStockFactor, tradingService);
        }
    }

    /**
     * 创建Builder
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
