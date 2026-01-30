package io.github.arkmsg.longport.example;

import com.longport.Market;
import com.longport.quote.*;
import io.github.arkmsg.third.longport.LongportSDK;
import io.github.arkmsg.third.longport.model.SecurityQuoteResp;
import io.github.arkmsg.third.longport.service.QuoteService;

import java.util.Arrays;
import java.util.List;

/**
 * 🎯 三大核心功能演示
 *
 * <h3>功能列表：</h3>
 * <ol>
 *   <li>✅ 获取股票市场列表</li>
 *   <li>✅ 获取盘口数据（五档行情）</li>
 *   <li>✅ 真实交易功能（见RealTradingExample.java）</li>
 * </ol>
 *
 * @author Longport SDK Team
 * @version 1.0.0
 */
public class FeatureDemo {

    public static void main(String[] args) {
        
        System.out.println("=" .repeat(100));
        System.out.println("🎯 长桥SDK - 三大核心功能演示");
        System.out.println("=" .repeat(100));
        System.out.println();

        try (LongportSDK sdk = LongportSDK.builder()
                .appKey("your_app_key")
                .appSecret("your_app_secret")
                .accessToken("your_access_token")
                .enableOvernight(true)
                .quietMode(true)
                .build()) {

            QuoteService quote = sdk.quote();

            // ============================================
            // 功能1: 获取股票市场列表
            // ============================================
            System.out.println("┌" + "─".repeat(98) + "┐");
            System.out.println("│ 功能1: 获取股票市场列表" + " ".repeat(75) + "│");
            System.out.println("└" + "─".repeat(98) + "┘");
            System.out.println();
            
            demoGetMarketList(sdk);
            
            // ============================================
            // 功能2: 获取盘口数据（五档行情）
            // ============================================
            System.out.println();
            System.out.println("┌" + "─".repeat(98) + "┐");
            System.out.println("│ 功能2: 获取盘口数据（五档行情）" + " ".repeat(67) + "│");
            System.out.println("└" + "─".repeat(98) + "┘");
            System.out.println();
            
            demoGetMarketDepth(quote);
            
            // ============================================
            // 功能3: 真实交易功能
            // ============================================
            System.out.println();
            System.out.println("┌" + "─".repeat(98) + "┐");
            System.out.println("│ 功能3: 真实交易功能（请查看 RealTradingExample.java）" + " ".repeat(47) + "│");
            System.out.println("└" + "─".repeat(98) + "┘");
            System.out.println();
            
            System.out.println("⚠️  真实交易功能请参考：RealTradingExample.java");
            System.out.println("    该示例包含完整的真实交易流程，包括：");
            System.out.println("    - 市价买入订单");
            System.out.println("    - 限价卖出订单");
            System.out.println("    - 修改订单价格");
            System.out.println("    - 撤销订单");
            System.out.println();

            System.out.println("=" .repeat(100));
            System.out.println("✅ 三大核心功能演示完成");
            System.out.println("=" .repeat(100));

        } catch (Exception e) {
            System.err.println("❌ 功能演示失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 功能1: 获取股票市场列表
     */
    private static void demoGetMarketList(LongportSDK sdk) throws Exception {
        System.out.println("📊 获取美股市场列表（部分示例）");
        System.out.println("-".repeat(100));
        
        // 使用长桥原生API获取市场列表
        QuoteContext quoteContext = sdk.getQuoteContext();
        
        try {
            // 获取美股列表
            System.out.println("🇺🇸 美股市场:");
            Security[] usSecurities = quoteContext.getSecurityList(Market.US, SecurityListCategory.Overnight).get();
            
            System.out.println("  总数量: " + usSecurities.length + " 只股票");
            System.out.println();
            System.out.println("  前20只股票示例:");
            System.out.printf("  %-15s %-40s %-20s%n", "股票代码", "股票名称", "中文名");
            System.out.println("  " + "-".repeat(80));
            
            Arrays.stream(usSecurities)
                .limit(20)
                .forEach(security -> {
                    System.out.printf("  %-15s %-40s %-20s%n",
                        security.getSymbol(),
                        truncate(security.getNameEn(), 38),
                        truncate(security.getNameCn(), 18));
                });
            
            System.out.println();
            
            // 获取港股列表
            System.out.println("🇭🇰 港股市场:");
            Security[] hkSecurities = quoteContext.getSecurityList(Market.HK, SecurityListCategory.Overnight).get();
            
            System.out.println("  总数量: " + hkSecurities.length + " 只股票");
            System.out.println();
            System.out.println("  前20只股票示例:");
            System.out.printf("  %-15s %-40s %-20s%n", "股票代码", "股票名称", "中文名");
            System.out.println("  " + "-".repeat(80));
            
            Arrays.stream(hkSecurities)
                .limit(20)
                .forEach(security -> {
                    System.out.printf("  %-15s %-40s %-20s%n",
                        security.getSymbol(),
                        truncate(security.getNameEn(), 38),
                        truncate(security.getNameCn(), 18));
                });
            
            System.out.println();
            
            // 搜索功能演示
            System.out.println("🔍 股票搜索功能:");
            System.out.println("-".repeat(100));
            
            String[] searchKeywords = {"Apple", "Tesla", "腾讯", "阿里"};
            QuoteService quote = sdk.quote();
            
            for (String keyword : searchKeywords) {
                List<Security> results = quote.search(keyword);
                System.out.println("  搜索「" + keyword + "」:");
                
                if (results.isEmpty()) {
                    System.out.println("    无结果");
                } else {
                    results.stream().limit(3).forEach(security -> {
                        System.out.printf("    %-15s %-30s %-20s%n",
                            security.getSymbol(),
                            truncate(security.getNameEn(), 28),
                            truncate(security.getNameCn(), 18));
                    });
                }
                System.out.println();
            }
            
            System.out.println("✅ 市场列表获取成功");
            
        } catch (Exception e) {
            System.err.println("❌ 获取市场列表失败: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 功能2: 获取盘口数据（五档行情）
     */
    private static void demoGetMarketDepth(QuoteService quote) throws Exception {
        System.out.println("📈 获取盘口数据（五档行情）");
        System.out.println("-".repeat(100));
        
        String[] symbols = {"AAPL.US", "TSLA.US", "00700.HK"};
        
        for (String symbol : symbols) {
            try {
                System.out.println("股票: " + symbol);
                System.out.println("-".repeat(100));
                
                // 获取实时报价
                SecurityQuoteResp quoteData = quote.getRealtimeQuote(symbol);
                System.out.println("  当前价格: $" + quoteData.getPrice());
                System.out.println("  昨收价:   $" + quoteData.getPrevClose());
                System.out.println("  涨跌幅:   " + String.format("%.2f%%", quoteData.getChangePercent()));
                System.out.println();
                
                // 获取盘口数据
                SecurityDepth depth = quote.getMarketDepth(symbol);
                
                // 显示卖盘（从高到低）
                System.out.println("  【卖盘】");
                Depth[] asks = depth.getAsks();
                for (int i = Math.min(5, asks.length) - 1; i >= 0; i--) {
                    System.out.printf("  卖%d  价格: $%-10s  数量: %-15s  订单数: %d%n",
                        (5 - i),
                        asks[i].getPrice(),
                        asks[i].getVolume(),
                        asks[i].getOrderNum());
                }
                
                System.out.println("  " + "-".repeat(80));
                System.out.printf("  当前价: $%-10s  (交易时段: %s)%n", 
                    quoteData.getPrice(), 
                    quoteData.getTimeSlot());
                System.out.println("  " + "-".repeat(80));
                
                // 显示买盘（从高到低）
                System.out.println("  【买盘】");
                Depth[] bids = depth.getBids();
                for (int i = 0; i < Math.min(5, bids.length); i++) {
                    System.out.printf("  买%d  价格: $%-10s  数量: %-15s  订单数: %d%n",
                        (i + 1),
                        bids[i].getPrice(),
                        bids[i].getVolume(),
                        bids[i].getOrderNum());
                }
                
                System.out.println();
                
                // 计算买卖盘力量对比
                long totalBidVolume = Arrays.stream(bids).limit(5).mapToLong(Depth::getVolume).sum();
                long totalAskVolume = Arrays.stream(asks).limit(5).mapToLong(Depth::getVolume).sum();
                double buyPressure = (double) totalBidVolume / (totalBidVolume + totalAskVolume) * 100;
                
                System.out.println("  【盘口分析】");
                System.out.printf("  总买盘量: %-15s  (%.1f%%)%n", totalBidVolume, buyPressure);
                System.out.printf("  总卖盘量: %-15s  (%.1f%%)%n", totalAskVolume, 100 - buyPressure);
                System.out.printf("  买卖力量: %s%n", 
                    buyPressure > 55 ? "买盘强势 🟢" : 
                    buyPressure < 45 ? "卖盘强势 🔴" : 
                    "多空平衡 🟡");
                
                System.out.println();
                System.out.println("✅ 盘口数据获取成功");
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("❌ 获取盘口数据失败 (" + symbol + "): " + e.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * 截断字符串
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 2) + "..";
    }
}

