package io.github.arkmsg.longport.example;

import io.github.arkmsg.third.longport.LongportSDK;
import io.github.arkmsg.third.longport.model.*;
import io.github.arkmsg.third.longport.service.QuoteService;
import io.github.arkmsg.third.longport.service.TradeService;
import com.longport.quote.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SDK快速开始示例
 *
 * <p>演示如何使用Longport SDK进行行情查询和交易操作。
 *
 * @author Longport SDK Team
 */
public class QuickStartExample {

    public static void main(String[] args) {

        // ========== 1. 初始化SDK ==========
        try (LongportSDK sdk = LongportSDK.builder()
                .appKey("your_app_key")
                .appSecret("your_app_secret")
                .accessToken("your_access_token")
                .enableOvernight(true)  // 启用夜盘行情
                .quietMode(true)        // 静默模式
                .build()) {

            System.out.println("========== Longport SDK 初始化成功 ==========\n");

            // ========== 2. 行情查询示例 ==========
            quoteExamples(sdk);

            // ========== 3. 交易操作示例 ==========
            tradeExamples(sdk);

        } catch (Exception e) {
            System.err.println("SDK错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 行情查询示例
     */
    private static void quoteExamples(LongportSDK sdk) throws Exception {
        System.out.println("========== 行情查询示例 ==========\n");

        QuoteService quote = sdk.quote();

        // 2.1 搜索股票
        System.out.println("【搜索股票】");
        List<Security> searchResults = quote.search("苹果");
        searchResults.stream().limit(3).forEach(result ->
            System.out.println("  " + result.getSymbol() + " - " + result.getNameCn())
        );
        System.out.println();

        // 2.2 获取实时报价（自动识别时段）
        System.out.println("【获取实时报价】");
        SecurityQuoteResp appleQuote = quote.getRealtimeQuote("AAPL.US");
        
        // 显示当前美东时间
        var currentEtTime = java.time.ZonedDateTime.now(ZoneId.of("America/New_York"));
        var timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        System.out.println("  当前时间: " + currentEtTime.format(timeFormatter));
        
        System.out.println("  股票: " + appleQuote.getSymbol());
        System.out.println("  价格: $" + appleQuote.getPrice());
        System.out.println("  涨跌: " + (appleQuote.getChange().doubleValue() >= 0 ? "+" : "") +
                           appleQuote.getChange() + " (" + appleQuote.getChangePercent() + "%)");
        System.out.println("  时段: " + appleQuote.getTimeSlot());
        System.out.println("  成交量: " + appleQuote.getVolume());
        System.out.println();

        // 2.3 批量获取报价
        System.out.println("【批量获取报价】");
        List<SecurityQuoteResp> batchQuotes = quote.getBatchQuotes(
            List.of("AAPL.US", "TSLA.US", "MSFT.US")
        );
        batchQuotes.forEach(q ->
            System.out.println("  " + q.getSymbol() + ": $" + q.getPrice())
        );
        System.out.println();

        // 2.4 获取K线数据
        System.out.println("【获取K线数据】");
        
        // 先打印当前时间
        var nowEt = java.time.ZonedDateTime.now(ZoneId.of("America/New_York"));
        var fullTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        System.out.println("  🕐 查询时间: " + nowEt.format(fullTimeFormatter));
        System.out.println();
        
        List<Candlestick> klines = quote.getKlines()
            .symbol("AAPL.US")
            .oneMinute()      // 1分钟K线
            .count(20)        // 最近20根
            .noAdjust()       // 不复权
            .fetch();

        System.out.println("  📊 获取到 " + klines.size() + " 根K线:");
        System.out.println("  " + "=".repeat(100));
        System.out.printf("  %-3s %-20s %-10s %-10s %-10s %-10s %-12s %-10s%n", 
                         "#", "K线时间(EST)", "开盘", "最高", "最低", "收盘", "成交量", "时段");
        System.out.println("  " + "=".repeat(100));
        
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (int i = 0; i < klines.size(); i++) {
            Candlestick k = klines.get(i);
            var etTime = k.getTimestamp().atZoneSameInstant(ZoneId.of("America/New_York"));
            String label = (i == klines.size() - 1) ? "→" : " ";
            
            // 获取交易时段
            String session = "";
            try {
                if (k.getClass().getMethod("getTradeSession") != null) {
                    Object ts = k.getClass().getMethod("getTradeSession").invoke(k);
                    session = ts != null ? ts.toString() : "N/A";
                }
            } catch (Exception e) {
                session = "N/A";
            }
            
            System.out.printf("  %s%-2d %-20s %-10s %-10s %-10s %-10s %-12d %-10s%n",
                label,
                (i + 1),
                etTime.format(formatter),
                k.getOpen(),
                k.getHigh(),
                k.getLow(),
                k.getClose(),
                k.getVolume(),
                session);
        }
        System.out.println("  " + "=".repeat(100));
        
        // 显示最新K线和当前时间的差异
        if (!klines.isEmpty()) {
            Candlestick latest = klines.get(klines.size() - 1);
            var latestTime = latest.getTimestamp().atZoneSameInstant(ZoneId.of("America/New_York"));
            long minutesDiff = java.time.Duration.between(latestTime, nowEt).toMinutes();
            System.out.println("  ⏱️  最新K线时间: " + latestTime.format(formatter));
            System.out.println("  ⏱️  查询时间:   " + nowEt.format(formatter));
            System.out.println("  ⏱️  时间差:     " + minutesDiff + " 分钟");
        }
        System.out.println();

        // 2.5 获取盘口数据
        System.out.println("【获取盘口数据】");
        var depth = quote.getMarketDepth("AAPL.US");
        System.out.println("  买一: " + depth.getAsks()[0].getPrice() + " x " + depth.getAsks()[0].getVolume());
        System.out.println("  卖一: " + depth.getBids()[0].getPrice() + " x " + depth.getBids()[0].getVolume());
        System.out.println();
    }

    /**
     * 交易操作示例
     */
    private static void tradeExamples(LongportSDK sdk) throws Exception {
        System.out.println("========== 交易操作示例 ==========\n");

        TradeService trade = sdk.trade();

        // 3.1 查询账户余额
        System.out.println("【查询账户余额】");
        AccountBalanceResp balance = trade.getAccountBalance();
        System.out.println("  现金总额: $" + balance.getTotalCash());
        System.out.println("  可用现金: $" + balance.getAvailableCash());
        System.out.println("  净资产: $" + balance.getNetAssets());
        System.out.println();

        // 3.2 查询持仓
        System.out.println("【查询持仓】");
        List<PositionResp> positions = trade.getPositions();
        System.out.println("  持仓数量: " + positions.size());
        positions.forEach(pos -> {
            System.out.println("  " + pos.getSymbol() +
                             ": " + pos.getQuantity() + "股" +
                             " @ $" + pos.getCostPrice());
        });
        System.out.println();

        // 3.3 查询今日订单
        System.out.println("【查询今日订单】");
        List<OrderResp> orders = trade.getTodayOrders();
        System.out.println("  订单数量: " + orders.size());
        orders.stream().limit(5).forEach(order -> {
            System.out.println("  " + order.getOrderId() +
                             " | " + order.getSide() +
                             " " + order.getSymbol() +
                             " | " + order.getStatus());
        });
        System.out.println();

        // 3.4 提交限价单（示例，不实际执行）
        System.out.println("【提交订单示例】（此处仅演示，不实际执行）");
        /*
        OrderResp order = trade.submitOrder()
            .symbol("AAPL.US")
            .buy()              // 买入
            .quantity(100)      // 100股
            .limitOrder()       // 限价单
            .price(150.00)      // 价格$150
            .dayOrder()         // 当日有效
            .remark("测试订单")
            .execute();

        System.out.println("  订单ID: " + order.getOrderId());
        System.out.println("  状态: " + order.getStatus());
        */

        // 3.5 撤单示例
        System.out.println("【撤单示例】（此处仅演示，不实际执行）");
        /*
        if (!orders.isEmpty()) {
            String orderId = orders.get(0).getOrderId();
            trade.cancelOrder(orderId);
            System.out.println("  已撤销订单: " + orderId);
        }
        */

        System.out.println();
    }
}

