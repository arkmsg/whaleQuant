# Longport SDK

基于长桥证券官方API封装的Java SDK，提供港股、美股、A股行情数据和交易功能。

## 📦 Maven坐标

```xml
<dependency>
    <groupId>com.whaleal.retail</groupId>
    <artifactId>longport-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

**包名**: `com.whaleal.ark.cloud.stocks.longport`

## 核心功能

- 📊 **行情查询** - 获取股票市场列表、实时报价、盘口数据
- 💰 **交易管理** - 市价/限价订单、订单修改、批量撤单
- 📈 **账户查询** - 余额查询、持仓查询、历史订单

## 快速开始

### 1. 安装依赖

```xml
<dependency>
    <groupId>com.whaleal.retail</groupId>
    <artifactId>longport-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 初始化SDK

```java
try (LongportSDK sdk = LongportSDK.builder()
        .appKey("your_app_key")
        .appSecret("your_app_secret")
        .accessToken("your_access_token")
        .build()) {
    
    // 使用SDK
    QuoteService quote = sdk.quote();
    TradeService trade = sdk.trade();
}
```

### 3. 基本使用

**获取盘口数据**
```java
SecurityDepth depth = quote.getMarketDepth("AAPL.US");
System.out.println("买一: " + depth.getBids()[0].getPrice());
```

**股票搜索**
```java
List<Security> results = quote.search("Apple");
```

**提交订单**
```java
OrderResponse order = trade.submitOrder()
    .symbol("AAPL.US")
    .buy()
    .quantity(100)
    .limitOrder()
    .price(150.00)
    .execute();
```

**订单修改**
```java
trade.replaceOrder(orderId, new BigDecimal("151.00"));
```

**订单撤销**
```java
trade.cancelOrder(orderId);
```

**查询账户**
```java
AccountBalanceResponse balance = trade.getAccountBalance();
List<PositionResponse> positions = trade.getPositions();
List<OrderResponse> orders = trade.getTodayOrders();
```

## 运行示例

```bash
# 行情和盘口数据演示
mvn clean test -Dtest=FeatureDemo

# 快速开始示例
mvn clean test -Dtest=QuickStartExample

# 交易功能测试（查询模式）
mvn clean test -Dtest=TradeExample
```

## 发布到 GitHub Packages

### 配置 Maven settings.xml

在 `~/.m2/settings.xml` 中添加：

```xml
<servers>
  <server>
    <id>githubarkmsg</id>
    <username>你的GitHub用户名</username>
    <password>你的GitHub_Token</password>
  </server>
</servers>
```

### 发布命令

```bash
mvn clean test
mvn deploy
```

### 在其他项目中使用

在 `~/.m2/settings.xml` 添加仓库配置：

```xml
<profiles>
  <profile>
    <id>github</id>
    <repositories>
      <repository>
        <id>githubarkmsg</id>
        <url>https://maven.pkg.github.com/arkmsg/longport-sdk-java</url>
      </repository>
    </repositories>
  </profile>
</profiles>

<activeProfiles>
  <activeProfile>github</activeProfile>
</activeProfiles>
```

## 许可证

MIT License