# Stocks Data SDK

股票/加密货币数据存储与同步SDK，使用 MongoDB 作为存储引擎。

## 功能特性

### 1. K线数据存储

- 支持存储 >= 15分钟周期的 K 线数据
- 自动过滤不支持的短周期数据
- 基于 symbol + timestamp 唯一索引

### 2. 指标数据存储

- 支持存储所有技术指标计算结果
- 指标与 K 线数据关联存储

### 3. 交易数据存储

- **订单 (Order)**: 委托订单记录
- **成交 (Fill)**: 成交明细记录
- **持仓 (Position)**: 当前持仓信息
- **盈亏 (PnL)**: 盈亏统计信息
- **交易信号 (TradeSignal)**: AI/策略生成的交易信号
- **执行记录 (TradeExecution)**: HTTP请求/响应记录

### 4. 标的管理

- 监控标的列表管理 (Stock)
- 支持 CRUD 操作

## 依赖关系

```
stocks-data-sdk
├── stocks-sdk (统一模型: Trading models)
│   ├── stocks-indicator-sdk (核心模型: Candlestick)
│   │   └── trading-calendar-sdk (交易日历)
│   └── trading-calendar-sdk (Market 枚举)
└── spring-boot-starter-data-mongodb
```

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.whaleal.retail</groupId>
    <artifactId>stocks-data-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 MongoDB

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/stocks
```

### 3. 使用示例

#### K线存储

```java
@Autowired
private CandlestickService candlestickService;

// 保存 K 线（自动过滤 < 15分钟周期）
Candlestick candle = Candlestick.builder()
    .symbol("BTCUSDT")
    .timestamp(System.currentTimeMillis())
    .open(new BigDecimal("50000"))
    .high(new BigDecimal("51000"))
    .low(new BigDecimal("49000"))
    .close(new BigDecimal("50500"))
    .volume(new BigDecimal("1000"))
    .interval("1h")  // >= 15m 才会存储
    .build();
candlestickService.save(candle);

// 查询 K 线
List<Candlestick> candles = candlestickService.findBySymbolAndTimeRange(
    "BTCUSDT", startTime, endTime
);
```

#### 交易数据存储

```java
@Autowired
private TradingService tradingService;

// 保存订单
Order order = Order.builder()
    .orderId("123456")
    .symbol("BTCUSDT")
    .side("BUY")
    .type("LIMIT")
    .price(new BigDecimal("50000"))
    .quantity(new BigDecimal("0.1"))
    .status("PENDING")
    .build();
tradingService.saveOrder(order);

// 保存交易信号
TradeSignal signal = TradeSignal.builder()
    .symbol("BTCUSDT")
    .side("BUY")
    .source("AI")
    .confidence(new BigDecimal("0.85"))
    .suggestPrice(new BigDecimal("50000"))
    .status("PENDING")
    .build();
tradingService.saveSignal(signal);

// 查询待执行的信号
List<TradeSignal> pendingSignals = tradingService.findPendingSignals();
```

#### 标的管理

```java
@Autowired
private StockService stockService;

// 添加监控标的
Stock stock = Stock.builder()
    .symbol("BTCUSDT")
    .name("Bitcoin/USDT")
    .market("CRYPTO")
    .dataSource("BINANCE")
    .status("ACTIVE")
    .build();
stockService.save(stock);

// 查询所有监控标的
List<Stock> stocks = stockService.findAll();
```

## MongoDB 集合

| 集合名 | 用途 |
|--------|------|
| `candlestick` | K 线数据 |
| `indicator_data` | 指标数据 |
| `stock` | 监控标的 |
| `trading_order` | 订单记录 |
| `trading_fill` | 成交记录 |
| `trading_position` | 持仓信息 |
| `trading_pnl` | 盈亏统计 |
| `trading_signal` | 交易信号 |
| `trading_execution` | 执行记录 |

## 支持的 K 线周期

仅存储 >= 15分钟周期的数据：

- 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h
- 1d, 3d, 1w, 1M

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    stocks-data-sdk                          │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ CandlestickSvc  │  │  TradingDataSvc │                  │
│  │ (K线存储)       │  │  (交易数据存储) │                  │
│  └────────┬────────┘  └────────┬────────┘                  │
│           │                    │                            │
│           ▼                    ▼                            │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    MongoTemplate                        ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │    MongoDB      │
                    └─────────────────┘
```

## License

MIT License

