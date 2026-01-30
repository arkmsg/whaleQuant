package io.github.arkmsg.longport.example;

import com.longport.trade.OrderSide;
import com.longport.trade.OrderType;
import io.github.arkmsg.third.longport.LongportSDK;
import io.github.arkmsg.third.longport.model.*;
import io.github.arkmsg.third.longport.service.QuoteService;
import io.github.arkmsg.third.longport.service.TradeService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

/**
 * 🔥 真实交易示例
 *
 * <h3>⚠️ 重要警告：</h3>
 * <ul>
 *   <li>本示例会执行<b>真实交易</b>，请谨慎使用</li>
 *   <li>建议先在<b>模拟账户</b>测试</li>
 *   <li>真实交易会产生<b>资金变动</b></li>
 *   <li>请确保您<b>完全理解</b>每个操作的含义</li>
 * </ul>
 *
 * <h3>功能列表：</h3>
 * <ol>
 *   <li>查询账户余额</li>
 *   <li>查询持仓信息</li>
 *   <li>查询实时报价</li>
 *   <li>提交市价买入订单</li>
 *   <li>提交限价卖出订单</li>
 *   <li>修改订单价格</li>
 *   <li>撤销订单</li>
 *   <li>查询今日订单</li>
 * </ol>
 *
 * @author Longport SDK Team
 * @version 1.0.0
 */
public class RealTradingExample {

    // ⚠️ 真实交易开关（请谨慎开启）
    private static final boolean ENABLE_REAL_TRADING = false;  // 默认关闭，防止误操作

    // 交易配置
    private static final String DEFAULT_SYMBOL = "AAPL.US";
    private static final int DEFAULT_QUANTITY = 1;  // 默认1股，降低风险

    public static void main(String[] args) {
        
        System.out.println("=" .repeat(80));
        System.out.println("🔥 长桥SDK - 真实交易示例");
        System.out.println("=" .repeat(80));
        System.out.println();

        if (!ENABLE_REAL_TRADING) {
            System.out.println("⚠️  真实交易功能已禁用");
            System.out.println("⚠️  如需启用，请修改 ENABLE_REAL_TRADING = true");
            System.out.println("⚠️  建议先在模拟账户测试！");
            System.out.println();
            return;
        }

        // 二次确认
        if (!confirmRealTrading()) {
            System.out.println("❌ 用户取消交易");
            return;
        }

        // ========== 初始化SDK ==========
        try (LongportSDK sdk = LongportSDK.builder()
                .appKey("your_app_key")
                .appSecret("your_app_secret")
                .accessToken("your_access_token")
                .enableOvernight(true)
                .quietMode(true)
                .build()) {

            System.out.println("✅ SDK初始化成功");
            System.out.println();

            QuoteService quote = sdk.quote();
            TradeService trade = sdk.trade();

            // ========== 交易流程演示 ==========
            
            // 1. 查询账户信息
            displayAccountInfo(trade);
            
            // 2. 查询实时报价
            SecurityQuoteResp currentQuote = displayRealtimeQuote(quote, DEFAULT_SYMBOL);
            
            // 3. 查询当前持仓
            List<PositionResp> positions = displayPositions(trade);
            
            // 4. 查询当前订单
            List<OrderResp> todayOrders = displayTodayOrders(trade);

            // ========== 真实交易示例 ==========
            
            // 示例1: 市价买入
            System.out.println("=" .repeat(80));
            System.out.println("📝 示例1: 市价买入");
            System.out.println("=" .repeat(80));
            executMarketBuyOrder(trade, DEFAULT_SYMBOL, DEFAULT_QUANTITY);
            System.out.println();
            
            Thread.sleep(2000); // 等待2秒

            // 示例2: 限价卖出
            System.out.println("=" .repeat(80));
            System.out.println("📝 示例2: 限价卖出");
            System.out.println("=" .repeat(80));
            BigDecimal sellPrice = currentQuote.getPrice().multiply(new BigDecimal("1.05")); // 高于当前价5%
            executeLimitSellOrder(trade, DEFAULT_SYMBOL, DEFAULT_QUANTITY, sellPrice);
            System.out.println();
            
            Thread.sleep(2000);

            // 示例3: 修改订单
            System.out.println("=" .repeat(80));
            System.out.println("📝 示例3: 修改订单价格");
            System.out.println("=" .repeat(80));
            modifyPendingOrders(trade);
            System.out.println();
            
            Thread.sleep(2000);

            // 示例4: 撤销订单
            System.out.println("=" .repeat(80));
            System.out.println("📝 示例4: 撤销待成交订单");
            System.out.println("=" .repeat(80));
            cancelPendingOrders(trade);
            System.out.println();

            // 最终状态
            System.out.println("=" .repeat(80));
            System.out.println("📊 最终账户状态");
            System.out.println("=" .repeat(80));
            displayAccountInfo(trade);
            displayPositions(trade);
            displayTodayOrders(trade);

            System.out.println("=" .repeat(80));
            System.out.println("✅ 真实交易示例执行完成");
            System.out.println("=" .repeat(80));

        } catch (Exception e) {
            System.err.println("❌ 交易失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 二次确认真实交易
     */
    private static boolean confirmRealTrading() {
        System.out.println("⚠️⚠️⚠️ 真实交易确认 ⚠️⚠️⚠️");
        System.out.println();
        System.out.println("您即将执行真实交易操作，这将产生实际的资金变动！");
        System.out.println();
        System.out.println("请确认以下事项：");
        System.out.println("  1. 我已经充分理解交易风险");
        System.out.println("  2. 我知道这是真实账户交易");
        System.out.println("  3. 我愿意承担可能的损失");
        System.out.println();
        System.out.print("是否继续？(输入 YES 确认): ");
        
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        System.out.println();
        
        return "YES".equals(input);
    }

    /**
     * 显示账户信息
     */
    private static void displayAccountInfo(TradeService trade) throws Exception {
        System.out.println("💰 账户余额信息");
        System.out.println("-" .repeat(80));
        
        AccountBalanceResp balance = trade.getAccountBalance();
        
        System.out.println("  总现金:       $" + formatMoney(balance.getTotalCash()));
        System.out.println("  可用现金:     $" + formatMoney(balance.getAvailableCash()));
        System.out.println("  净资产:       $" + formatMoney(balance.getNetAssets()));
        System.out.println("  初始保证金:   $" + formatMoney(balance.getInitMargin()));
        System.out.println("  维持保证金:   $" + formatMoney(balance.getMaintenanceMargin()));
        System.out.println();
    }

    /**
     * 显示实时报价
     */
    private static SecurityQuoteResp displayRealtimeQuote(QuoteService quote, String symbol) throws Exception {
        System.out.println("📈 实时报价信息 - " + symbol);
        System.out.println("-" .repeat(80));
        
        SecurityQuoteResp quoteData = quote.getRealtimeQuote(symbol);
        
        System.out.println("  当前价格:     $" + quoteData.getPrice());
        System.out.println("  昨收价:       $" + quoteData.getPrevClose());
        System.out.println("  今开价:       $" + quoteData.getOpen());
        System.out.println("  最高价:       $" + quoteData.getHigh());
        System.out.println("  最低价:       $" + quoteData.getLow());
        System.out.println("  涨跌额:       $" + quoteData.getChange());
        System.out.println("  涨跌幅:       " + quoteData.getChangePercent().setScale(2, RoundingMode.HALF_UP) + "%");
        System.out.println("  成交量:       " + quoteData.getVolume());
        System.out.println("  交易时段:     " + quoteData.getTimeSlot());
        System.out.println();
        
        return quoteData;
    }

    /**
     * 显示持仓信息
     */
    private static List<PositionResp> displayPositions(TradeService trade) throws Exception {
        System.out.println("📊 持仓信息");
        System.out.println("-" .repeat(80));
        
        List<PositionResp> positions = trade.getPositions();
        
        if (positions.isEmpty()) {
            System.out.println("  暂无持仓");
        } else {
            System.out.printf("  %-15s %-10s %-12s %-12s %-12s%n", 
                "股票代码", "持仓数量", "可用数量", "成本价", "市场");
            System.out.println("  " + "-".repeat(65));
            
            for (PositionResp pos : positions) {
                System.out.printf("  %-15s %-10s %-12s $%-11s %-12s%n",
                    pos.getSymbol(),
                    pos.getQuantity(),
                    pos.getAvailableQuantity(),
                    pos.getCostPrice(),
                    pos.getMarket());
            }
        }
        System.out.println();
        
        return positions;
    }

    /**
     * 显示今日订单
     */
    private static List<OrderResp> displayTodayOrders(TradeService trade) throws Exception {
        System.out.println("📝 今日订单");
        System.out.println("-" .repeat(80));
        
        List<OrderResp> orders = trade.getTodayOrders();
        
        if (orders.isEmpty()) {
            System.out.println("  今日暂无订单");
        } else {
            System.out.printf("  %-20s %-12s %-6s %-10s %-10s %-15s%n", 
                "订单ID", "股票", "方向", "数量", "价格", "状态");
            System.out.println("  " + "-".repeat(75));
            
            for (OrderResp order : orders) {
                String orderId = order.getOrderId().length() > 18 
                    ? order.getOrderId().substring(0, 18) + "..."
                    : order.getOrderId();
                    
                String priceStr = order.getSubmittedPrice() != null 
                    ? "$" + order.getSubmittedPrice() 
                    : "市价";
                    
                String statusIcon = order.isFilled() ? "✅" :
                                   order.isCancelled() ? "❌" :
                                   order.isPending() ? "⏳" : "❓";
                
                System.out.printf("  %-20s %-12s %-6s %-10s %-10s %s %-14s%n",
                    orderId,
                    order.getSymbol(),
                    order.getSide(),
                    order.getSubmittedQuantity(),
                    priceStr,
                    statusIcon,
                    order.getStatus());
            }
        }
        System.out.println();
        
        return orders;
    }

    /**
     * 执行市价买入订单
     */
    private static void executMarketBuyOrder(TradeService trade, String symbol, int quantity) {
        try {
            System.out.println("🛒 提交市价买入订单...");
            System.out.println("  股票: " + symbol);
            System.out.println("  数量: " + quantity + " 股");
            System.out.println();
            
            OrderResp order = trade.submitOrder()
                .symbol(symbol)
                .buy()                  // 买入
                .quantity(quantity)
                .marketOrder()          // 市价单
                .dayOrder()             // 当日有效
                .remark("SDK真实交易测试-市价买入")
                .execute();
            
            System.out.println("✅ 订单提交成功！");
            System.out.println("  订单ID: " + order.getOrderId());
            System.out.println("  状态: " + order.getStatus());
            
        } catch (Exception e) {
            System.err.println("❌ 市价买入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行限价卖出订单
     */
    private static void executeLimitSellOrder(TradeService trade, String symbol, int quantity, BigDecimal price) {
        try {
            System.out.println("🏷️ 提交限价卖出订单...");
            System.out.println("  股票: " + symbol);
            System.out.println("  数量: " + quantity + " 股");
            System.out.println("  价格: $" + price);
            System.out.println();
            
            OrderResp order = trade.submitOrder()
                .symbol(symbol)
                .sell()                 // 卖出
                .quantity(quantity)
                .limitOrder()           // 限价单
                .price(price)
                .dayOrder()             // 当日有效
                .remark("SDK真实交易测试-限价卖出")
                .execute();
            
            System.out.println("✅ 订单提交成功！");
            System.out.println("  订单ID: " + order.getOrderId());
            System.out.println("  委托价格: $" + order.getSubmittedPrice());
            System.out.println("  状态: " + order.getStatus());
            
        } catch (Exception e) {
            System.err.println("❌ 限价卖出失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 修改待成交订单
     */
    private static void modifyPendingOrders(TradeService trade) {
        try {
            List<OrderResp> orders = trade.getTodayOrders();
            List<OrderResp> pendingOrders = orders.stream()
                .filter(OrderResp::isPending)
                .toList();
            
            if (pendingOrders.isEmpty()) {
                System.out.println("⚠️  暂无待修改的订单");
                return;
            }
            
            OrderResp order = pendingOrders.get(0);
            BigDecimal newPrice = order.getSubmittedPrice().multiply(new BigDecimal("0.98")); // 降低2%
            
            System.out.println("📝 修改订单价格...");
            System.out.println("  订单ID: " + order.getOrderId());
            System.out.println("  原价格: $" + order.getSubmittedPrice());
            System.out.println("  新价格: $" + newPrice);
            System.out.println();
            
            trade.replaceOrder(order.getOrderId(), newPrice);
            
            System.out.println("✅ 订单修改成功！");
            
        } catch (Exception e) {
            System.err.println("❌ 订单修改失败: " + e.getMessage());
            System.err.println("  提示: 某些订单类型可能不支持修改");
        }
    }

    /**
     * 撤销待成交订单
     */
    private static void cancelPendingOrders(TradeService trade) {
        try {
            List<OrderResp> orders = trade.getTodayOrders();
            List<OrderResp> pendingOrders = orders.stream()
                .filter(OrderResp::isPending)
                .toList();
            
            if (pendingOrders.isEmpty()) {
                System.out.println("⚠️  暂无待撤销的订单");
                return;
            }
            
            System.out.println("🗑️ 撤销待成交订单...");
            System.out.println("  待撤销订单数: " + pendingOrders.size());
            System.out.println();
            
            for (OrderResp order : pendingOrders) {
                try {
                    System.out.println("  撤销订单: " + order.getOrderId());
                    trade.cancelOrder(order.getOrderId());
                    System.out.println("    ✅ 撤销成功");
                } catch (Exception e) {
                    System.err.println("    ❌ 撤销失败: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 批量撤单失败: " + e.getMessage());
        }
    }

    /**
     * 格式化金额
     */
    private static String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
}


