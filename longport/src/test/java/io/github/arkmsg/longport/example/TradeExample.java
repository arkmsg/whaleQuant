package io.github.arkmsg.longport.example;

import io.github.arkmsg.third.longport.LongportSDK;
import io.github.arkmsg.third.longport.model.*;
import io.github.arkmsg.third.longport.service.TradeService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 交易功能测试示例
 *
 * <p>演示如何使用 Longport SDK 进行交易操作。
 *
 * @author Longport SDK Team
 */
public class TradeExample {

    // 是否执行真实交易（默认false，避免误操作）
    private static final boolean EXECUTE_REAL_TRADES = true;

    public static void main(String[] args) {

        // ========== 初始化SDK ==========
        try (LongportSDK sdk = LongportSDK.builder()
                .appKey("your_app_key")
                .appSecret("your_app_secret")
                .accessToken("your_access_token")
                .enableOvernight(true)
                .quietMode(true)
                .build()) {

            System.out.println("========== 长桥SDK 交易功能测试 ==========");
            System.out.println("⚠️  模式: " + (EXECUTE_REAL_TRADES ? "真实交易" : "查询模式"));
            System.out.println();

            TradeService trade = sdk.trade();

            // ========== 1. 账户信息查询 ==========
            testAccountBalance(trade);

            // ========== 2. 持仓查询 ==========
            testPositions(trade);

            // ========== 3. 订单查询 ==========
            testOrders(trade);

            if (EXECUTE_REAL_TRADES) {
                // ========== 4. 订单提交（真实执行） ==========
                testOrderSubmission(trade);

                // ========== 5. 订单修改（真实执行） ==========
                testOrderModification(trade);

                // ========== 6. 订单撤销（真实执行） ==========
                testOrderCancellation(trade);
            } else {
                System.out.println("ℹ️  真实交易已禁用，如需测试请修改 EXECUTE_REAL_TRADES = true");
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试账户余额查询
     */
    private static void testAccountBalance(TradeService trade) throws Exception {
        System.out.println("========== 1. 查询账户余额 ==========");

        AccountBalanceResp balance = trade.getAccountBalance();

        System.out.println("💰 账户信息:");
        System.out.println("  总现金: $" + balance.getTotalCash());
        System.out.println("  可用现金: $" + balance.getAvailableCash());
        System.out.println("  净资产: $" + balance.getNetAssets());

        // 测试辅助方法
        BigDecimal testAmount = new BigDecimal("10000");
        boolean sufficient = balance.hasSufficientCash(testAmount);
        System.out.println("  是否有足够资金($10,000): " + (sufficient ? "✅ 是" : "❌ 否"));

        System.out.println();
    }

    /**
     * 测试持仓查询
     */
    private static void testPositions(TradeService trade) throws Exception {
        System.out.println("========== 2. 查询持仓 ==========");

        List<PositionResp> positions = trade.getPositions();

        System.out.println("📊 持仓列表 (共 " + positions.size() + " 个):");

        if (positions.isEmpty()) {
            System.out.println("  暂无持仓");
        } else {
            System.out.println("  " + String.format("%-15s %-10s %-12s %-12s %-12s %-12s",
                    "股票代码", "数量", "可用", "成本价", "市值", "盈亏"));
            System.out.println("  " + "-".repeat(80));

            for (PositionResp pos : positions) {
                // 假设当前价格（实际应该从行情API获取）
                BigDecimal currentPrice = pos.getCostPrice().multiply(new BigDecimal("1.05")); // 模拟5%涨幅
                BigDecimal marketValue = pos.calculateMarketValue();
                BigDecimal profitLoss = currentPrice.subtract(pos.getCostPrice())
                        .multiply(pos.getQuantity());

                System.out.println("  " + String.format("%-15s %-10s %-12s $%-11s $%-11s %s$%.2f",
                        pos.getSymbol(),
                        pos.getQuantity(),
                        pos.getAvailableQuantity(),
                        pos.getCostPrice(),
                        marketValue,
                        profitLoss.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "",
                        profitLoss));
            }
        }

        System.out.println();
    }

    /**
     * 测试订单查询
     */
    private static void testOrders(TradeService trade) throws Exception {
        System.out.println("========== 3. 查询订单 ==========");

        // 3.1 今日订单
        System.out.println("📝 今日订单:");
        List<OrderResp> todayOrders = trade.getTodayOrders();

        if (todayOrders.isEmpty()) {
            System.out.println("  今日暂无订单");
        } else {
            printOrders(todayOrders, 5);
        }

        System.out.println();

        // 3.2 历史订单（最近7天）
        System.out.println("📝 历史订单（最近7天）:");
        OffsetDateTime endDate = OffsetDateTime.now();
        OffsetDateTime startDate = endDate.minusDays(7);

        List<OrderResp> historyOrders = trade.getHistoryOrders(startDate, endDate);

        if (historyOrders.isEmpty()) {
            System.out.println("  最近7天无订单记录");
        } else {
            printOrders(historyOrders, 10);
        }

        System.out.println();
    }

    /**
     * 测试订单提交（真实执行）
     */
    private static void testOrderSubmission(TradeService trade) throws Exception {
        System.out.println("========== 4. 订单提交（真实执行） ==========");

        try {
            // 示例1: 限价买入（使用较低价格，避免成交）
            System.out.println("📝 提交限价买入订单...");
            OrderResp order1 = trade.submitOrder()
                    .symbol("AAPL.US")
                    .buy()
                    .quantity(1)  // 1股，减少风险
                    .limitOrder()
                    .price(100.00)  // 低价，不会成交
                    .dayOrder()
                    .remark("SDK测试订单-限价买入")
                    .execute();

            System.out.println("  ✅ 订单提交成功:");
            System.out.println("     订单ID: " + order1.getOrderId());
            System.out.println("     股票: " + order1.getSymbol());
            System.out.println("     方向: " + order1.getSide());
            System.out.println("     数量: " + order1.getSubmittedQuantity());
            System.out.println("     价格: $" + order1.getSubmittedPrice());
            System.out.println("     状态: " + order1.getStatus());
            System.out.println();

        } catch (Exception e) {
            System.err.println("  ❌ 订单提交失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 测试订单修改（真实执行）
     */
    private static void testOrderModification(TradeService trade) throws Exception {
        System.out.println("========== 5. 订单修改（真实执行） ==========");

        List<OrderResp> orders = trade.getTodayOrders();
        List<OrderResp> pendingOrders = orders.stream()
                .filter(OrderResp::isPending)
                .toList();
        
        if (pendingOrders.isEmpty()) {
            System.out.println("⚠️  暂无可修改的待成交订单");
        } else {
            try {
                OrderResp order = pendingOrders.get(0);
                BigDecimal newPrice = new BigDecimal("105.00");  // 修改为新的价格
                
                System.out.println("📝 修改订单价格...");
                System.out.println("  订单ID: " + order.getOrderId());
                System.out.println("  股票: " + order.getSymbol());
                System.out.println("  原价格: $" + order.getSubmittedPrice());
                System.out.println("  新价格: $" + newPrice);
                
                trade.replaceOrder(order.getOrderId(), newPrice);
                
                System.out.println("  ✅ 订单修改成功");
                
            } catch (Exception e) {
                System.err.println("  ❌ 订单修改失败: " + e.getMessage());
                System.err.println("  提示: 某些订单可能不支持修改（如已部分成交或港股订单）");
            }
        }
        
        System.out.println();
    }

    /**
     * 测试订单撤销（真实执行）
     */
    private static void testOrderCancellation(TradeService trade) throws Exception {
        System.out.println("========== 6. 订单撤销（真实执行） ==========");

        List<OrderResp> orders = trade.getTodayOrders();
        List<OrderResp> pendingOrders = orders.stream()
                .filter(OrderResp::isPending)
                .toList();
        
        if (pendingOrders.isEmpty()) {
            System.out.println("⚠️  暂无待撤销的订单");
        } else {
            // 单个撤单
            if (pendingOrders.size() >= 1) {
                try {
                    OrderResp order = pendingOrders.get(0);
                    System.out.println("📝 撤销订单...");
                    System.out.println("  订单ID: " + order.getOrderId());
                    System.out.println("  股票: " + order.getSymbol());
                    
                    trade.cancelOrder(order.getOrderId());
                    
                    System.out.println("  ✅ 订单撤销成功");
                    System.out.println();
                    
                } catch (Exception e) {
                    System.err.println("  ❌ 单个撤单失败: " + e.getMessage());
                    System.out.println();
                }
            }

            // 批量撤单
            if (pendingOrders.size() > 1) {
                try {
                    List<String> orderIds = pendingOrders.stream()
                            .skip(1)  // 跳过第一个（已经撤销）
                            .limit(3)  // 最多撤3个
                            .map(OrderResp::getOrderId)
                            .toList();
                    
                    if (!orderIds.isEmpty()) {
                        System.out.println("📝 批量撤销订单...");
                        System.out.println("  订单数量: " + orderIds.size());
                        
                        List<CancelOrderResult> results = trade.cancelOrders(orderIds);
                        
                        System.out.println("  结果统计:");
                        long successCount = results.stream().filter(CancelOrderResult::isSuccess).count();
                        long failCount = results.size() - successCount;
                        
                        System.out.println("    ✅ 成功: " + successCount);
                        System.out.println("    ❌ 失败: " + failCount);
                        
                        // 显示失败详情
                        results.stream()
                                .filter(r -> !r.isSuccess())
                                .forEach(r -> System.out.println("       - " + r.getOrderId() + ": " + r.getErrorMessage()));
                    }
                    
                } catch (Exception e) {
                    System.err.println("  ❌ 批量撤单失败: " + e.getMessage());
                }
            }
        }
        
        System.out.println();
    }

    /**
     * 打印订单列表
     */
    private static void printOrders(List<OrderResp> orders, int limit) {
        System.out.println("  " + String.format("%-20s %-12s %-8s %-10s %-10s %-12s",
                "订单ID", "股票", "方向", "数量", "价格", "状态"));
        System.out.println("  " + "-".repeat(80));

        orders.stream()
                .limit(limit)
                .forEach(order -> {
                    String priceStr = order.getSubmittedPrice() != null
                            ? "$" + order.getSubmittedPrice()
                            : "市价";

                    String statusIcon = order.isFilled() ? "✅" :
                                       order.isCancelled() ? "❌" :
                                       "⏳";

                    System.out.println("  " + String.format("%-20s %-12s %-8s %-10s %-10s %s %-11s",
                            order.getOrderId().substring(0, Math.min(18, order.getOrderId().length())),
                            order.getSymbol(),
                            order.getSide(),
                            order.getSubmittedQuantity(),
                            priceStr,
                            statusIcon,
                            order.getStatus()));
                });

        if (orders.size() > limit) {
            System.out.println("  ... 还有 " + (orders.size() - limit) + " 条记录");
        }
    }
}

