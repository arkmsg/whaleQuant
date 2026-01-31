# WhaleQuant SDK

## 项目简介

WhaleQuant SDK 是一个功能完整的量化交易平台开发工具包，提供了从数据获取、因子计算、策略执行到风险管理的全流程解决方案。

- **轻量级设计**：基础设施层（trading-base）移除了Spring依赖，保持轻量级
- **模块化架构**：采用分层架构，各模块职责明确，易于扩展
- **多交易所支持**：集成了长桥证券（港股、美股、A股）和币安（加密货币）
- **完整的交易功能**：包含策略管理、风险管理和交易执行
- **AI集成**：支持AI模型的管理和预测
- **技术指标分析**：集成MACD、RSI、布林带等多种技术指标
- **三层风控系统**：动态压制、UMP风控、时间管理
- **分层回测验证**：高频、标准、低频三层回测
- **市场时间管理**：跨时区交易时段支持
- **WebSocket实时推送**：实时行情和交易信号推送

## 项目结构

项目采用模块化架构，遵循金字塔规则，确保依赖关系清晰，避免循环依赖。

### 模块划分

| 模块 | 层级 | 职责 | 依赖 |
| --- | --- | --- | --- |
| **trading-base** | L0 | 基础设施层，包含核心模型、枚举、指标和风险管理 | 无 |
| **trading-data** | L1 | 数据层，负责数据存储和分发 | trading-base |
| **longport** | L1 | 长桥证券SDK，负责港股、美股和A股的行情和交易 | trading-base |
| **binance** | L1 | 币安交易所SDK，负责加密货币的行情和交易 | trading-base |
| **alpha4j** | L2 | 因子计算层，负责Alpha因子的计算 | trading-base, trading-data |
| **trading-ai** | L2 | AI层，负责AI模型的管理和预测 | trading-base, trading-data |
| **trading-backtest** | L2 | 回测引擎层，包含分层回测验证 | trading-base, trading-data |
| **trading-engine** | L3 | 交易引擎层，包含风险管理、策略和交易执行 | trading-base, trading-data, alpha4j |
| **trading-slippage** | L3 | 滑点计算层，负责交易滑点的计算 | trading-base |
| **trading-metrics** | L3 | 指标计算层，负责交易指标的计算 | trading-base |
| **integration-test** | L4 | 集成测试 | 所有模块 |

### 核心功能

#### 1. 基础设施层 (trading-base)
- **核心模型**：Position、Bar、Ticker、Order、Trade
- **技术指标**：MACD、RSI、布林带等
- **风险控制**：三层风控系统
- **市场时间管理**：跨时区交易时段支持
- **WebSocket**：实时推送功能

#### 2. 数据层 (trading-data)
- 数据存储和分发
- 市场数据管理
- 历史数据查询

#### 3. 因子计算层 (alpha4j)
- Alpha101因子计算
- Alpha158因子计算
- Alpha360因子计算
- 价格因子计算
- 成交量因子计算
- K线因子计算

#### 4. AI层 (trading-ai)
- 线性AI模型
- 树模型
- 深度学习模型
- 集成模型
- 市场情绪分析
- 置信度评分机制

#### 5. 交易引擎层 (trading-engine)
- 风险管理：订单金额规则、价格偏差规则、总持仓规则
- 策略管理：策略引擎、事件总线
- 交易执行：交易服务、订单构建器
- 对账服务：持仓对账、交易对账

#### 6. 交易所SDK
- **longport**：长桥证券SDK，支持港股、美股、A股
- **binance**：币安交易所SDK，支持加密货币

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+

### 安装依赖

```bash
mvn clean install -DskipTests
```

### 基本使用

#### 1. 创建交易策略

```java
import com.whaleal.quant.strategy.core.BaseStrategy;
import com.whaleal.quant.strategy.event.MarketDataEvent;

public class MyStrategy extends BaseStrategy {

    @Override
    public void onMarketData(MarketDataEvent event) {
        // 处理市场数据
        String symbol = event.getSymbol();
        double price = event.getPrice();
        
        // 实现交易逻辑
        if (shouldBuy(symbol, price)) {
            buy(symbol, 100, price);
        } else if (shouldSell(symbol, price)) {
            sell(symbol, 100, price);
        }
    }

    private boolean shouldBuy(String symbol, double price) {
        // 买入逻辑
        return false;
    }

    private boolean shouldSell(String symbol, double price) {
        // 卖出逻辑
        return false;
    }
}
```

#### 2. 使用技术指标

```java
import com.whaleal.quant.model.indicators.MACDIndicator;
import com.whaleal.quant.model.indicators.RSIIndicator;
import com.whaleal.quant.model.indicators.BOLLIndicator;
import com.whaleal.quant.model.Bar;

import java.util.List;

public class TechnicalAnalysisExample {

    public void analyzeTechnicalIndicators(List<Bar> bars) {
        // 计算MACD指标
        MACDIndicator macd = new MACDIndicator();
        double macdValue = macd.calculate(bars);
        System.out.println("MACD value: " + macdValue);
        
        // 计算RSI指标
        RSIIndicator rsi = new RSIIndicator();
        double rsiValue = rsi.calculate(bars);
        System.out.println("RSI value: " + rsiValue);
        
        // 计算布林带指标
        BOLLIndicator boll = new BOLLIndicator();
        double bollValue = boll.calculate(bars);
        System.out.println("BOLL value: " + bollValue);
    }
}
```

#### 3. 使用风险管理

```java
import com.whaleal.quant.risk.DynamicSuppressionRiskControl;
import com.whaleal.quant.risk.UMPRiskControl;
import com.whaleal.quant.risk.TimeManagementRiskControl;
import com.whaleal.quant.model.trading.Order;

public class RiskManagementExample {

    public void checkOrderRisk(Order order) {
        // 动态压制风控
        DynamicSuppressionRiskControl dynamicRiskControl = new DynamicSuppressionRiskControl();
        boolean dynamicRiskPass = dynamicRiskControl.checkRisk(order);
        System.out.println("Dynamic risk control pass: " + dynamicRiskPass);
        
        // UMP风控
        UMPRiskControl umpRiskControl = new UMPRiskControl();
        boolean umpRiskPass = umpRiskControl.checkRisk(order);
        System.out.println("UMP risk control pass: " + umpRiskPass);
        
        // 时间管理风控
        TimeManagementRiskControl timeRiskControl = new TimeManagementRiskControl();
        boolean timeRiskPass = timeRiskControl.checkRisk(order);
        System.out.println("Time risk control pass: " + timeRiskPass);
    }
}
```

#### 4. 使用AI决策引擎

```java
import com.whaleal.quant.ai.model.AIDecisionEngine;
import com.whaleal.quant.ai.model.AIModelFactory;
import com.whaleal.quant.ai.model.MarketSentiment;
import com.whaleal.quant.ai.model.TradingSignal;

public class AIDecisionExample {

    public void generateTradingSignal() {
        // 创建AI模型
        var aiModel = AIModelFactory.createModel(com.whaleal.quant.ai.model.ModelInfo.ModelType.TREE);
        AIDecisionEngine decisionEngine = new AIDecisionEngine(aiModel);
        
        // 创建市场情绪
        MarketSentiment sentiment = new MarketSentiment("AAPL", MarketSentiment.SentimentType.BULLISH, 0.8);
        
        // 生成交易信号
        TradingSignal signal = decisionEngine.generateSignal("AAPL", sentiment, 0.75);
        System.out.println("Generated signal: " + signal);
    }
}
```

#### 5. 使用分层回测

```java
import com.whaleal.quant.backtest.engine.LayeredBacktestEngine;
import com.whaleal.quant.backtest.model.BacktestConfig;
import com.whaleal.quant.backtest.result.BacktestResult;
import com.whaleal.quant.model.Bar;

import java.util.List;

public class BacktestExample {

    public void runLayeredBacktest(List<Bar> bars) {
        // 创建模拟数据提供者
        var dataProvider = new com.whaleal.quant.backtest.data.MockBacktestDataProvider();
        
        // 创建策略引擎
        var strategyEngine = com.whaleal.quant.strategy.core.StrategyEngine.builder()
                .strategyName("TestStrategy")
                .build();
        
        // 创建分层回测引擎
        LayeredBacktestEngine engine = new LayeredBacktestEngine(dataProvider, strategyEngine);
        BacktestConfig config = new BacktestConfig();
        
        // 运行分层回测
        var result = engine.runLayeredBacktest(bars, config);
        
        System.out.println("High frequency result: " + result.getHighFrequencyResult());
        System.out.println("Standard result: " + result.getStandardResult());
        System.out.println("Low frequency result: " + result.getLowFrequencyResult());
        System.out.println("Overall score: " + result.getOverallScore());
    }
}
```

#### 6. 使用市场时间管理

```java
import com.whaleal.quant.market.MarketTimeManager;

public class MarketTimeExample {

    public void checkMarketStatus() {
        MarketTimeManager timeManager = new MarketTimeManager();
        
        // 检查美股市场状态
        boolean usMarketOpen = timeManager.isMarketOpen(MarketTimeManager.Market.US);
        MarketTimeManager.TradingSession usSession = timeManager.getTradingSession(MarketTimeManager.Market.US);
        System.out.println("US market open: " + usMarketOpen);
        System.out.println("US trading session: " + usSession);
        
        // 检查港股市场状态
        boolean hkMarketOpen = timeManager.isMarketOpen(MarketTimeManager.Market.HK);
        MarketTimeManager.TradingSession hkSession = timeManager.getTradingSession(MarketTimeManager.Market.HK);
        System.out.println("HK market open: " + hkMarketOpen);
        System.out.println("HK trading session: " + hkSession);
    }
}
```

#### 7. 使用WebSocket实时推送

```java
import com.whaleal.quant.websocket.WebSocketServer;

public class WebSocketExample {

    public void broadcastMarketData(String marketData) {
        // 广播市场数据
        WebSocketServer.broadcast(marketData);
        System.out.println("Market data broadcasted to all clients");
        
        // 获取活跃连接数
        int activeConnections = WebSocketServer.getActiveConnections();
        System.out.println("Active WebSocket connections: " + activeConnections);
    }
}
```

## 配置说明

### trading-base 配置

trading-base 是轻量级模块，不需要特殊配置，可直接使用。

### 交易所配置

#### 长桥证券配置

```yaml
longport:
  app-key: your_app_key
  app-secret: your_app_secret
  access-token: your_access_token
  environment: production
```

#### 币安配置

```yaml
binance:
  api-key: your_api_key
  api-secret: your_api_secret
  environment: production
```

### 风险管理配置

```yaml
risk:
  order-amount-limit: 100000
  price-deviation-threshold: 0.05
  total-position-limit: 500000
  max-daily-trades: 50
  max-drawdown: 0.1
  market-volatility-threshold: 0.02
```

## 开发指南

### 新增因子

1. 在 alpha4j 模块中创建新的因子计算器类
2. 实现相应的计算方法
3. 在策略中使用因子结果

### 新增策略

1. 继承 BaseStrategy 类
2. 实现 onMarketData 方法
3. 在策略中实现交易逻辑

### 新增交易所支持

1. 创建新的交易所SDK模块
2. 实现行情和交易接口
3. 集成到交易引擎

### 新增技术指标

1. 在 trading-base 模块的 indicator 包中创建新的指标类
2. 实现 TechnicalIndicator 接口
3. 在策略中使用新指标

## 测试

项目包含集成测试模块，用于测试各模块的功能。

```bash
mvn test -f integration-test/pom.xml
```

## 部署

### 本地部署

```bash
# 编译打包
mvn clean package -DskipTests

# 运行
java -jar quant-service/quant-platform/quant-platform-backend/target/quant-platform-backend-1.0.0.jar
```

### 容器部署

项目支持容器化部署，可使用Docker构建镜像。

## 版本历史

- **1.0.0**：初始版本，包含完整的量化交易功能
- **1.1.0**：集成技术指标分析、三层风控系统、分层回测验证
- **1.2.0**：集成AI决策引擎、市场时间管理、WebSocket实时推送

## 贡献指南

欢迎贡献代码和提出问题！

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目基于 [MIT License](LICENSE) 开源协议。

## 联系方式

- **项目地址**：https://github.com/arkmsg/whaleQuant
- **问题反馈**：https://github.com/arkmsg/whaleQuant/issues

---

**Whaleal Quant Team**

*量化交易，智能未来*
