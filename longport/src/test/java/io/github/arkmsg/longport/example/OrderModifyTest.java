package io.github.arkmsg.longport.example;

import io.github.arkmsg.third.longport.LongportSDK;
import io.github.arkmsg.third.longport.model.OrderResp;
import io.github.arkmsg.third.longport.service.TradeService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单修改功能专项测试
 *
 * @author Longport SDK Team
 */
public class OrderModifyTest {

    public static void main(String[] args) {

        try (LongportSDK sdk = LongportSDK.builder()
                .appKey("your_app_key")
                .appSecret("your_app_secret")
                .accessToken("your_access_token")
                .enableOvernight(true)
                .quietMode(true)
                .build()) {

            System.out.println("========== 订单修改功能专项测试 ==========\n");

            TradeService trade = sdk.trade();

            // 步骤1: 提交一个测试订单
            System.out.println("【步骤1】提交测试订单...");
            OrderResp newOrder = trade.submitOrder()
                    .symbol("AAPL.US")
                    .buy()
                    .quantity(1)
                    .limitOrder()
                    .price(100.00)  // 低价，不会成交
                    .dayOrder()
                    .remark("订单修改测试")
                    .execute();

            System.out.println("  ✅ 订单提交成功");
            System.out.println("     订单ID: " + newOrder.getOrderId());
            System.out.println("     股票: " + newOrder.getSymbol());
            System.out.println("     原价格: $" + newOrder.getSubmittedPrice());
            System.out.println("     状态: " + newOrder.getStatus());
            System.out.println();

            // 等待1秒，确保订单状态更新
            Thread.sleep(1000);

            // 步骤2: 查询订单，确认状态
            System.out.println("【步骤2】查询订单状态...");
            List<OrderResp> orders = trade.getTodayOrders();
            OrderResp targetOrder = orders.stream()
                    .filter(o -> o.getOrderId().equals(newOrder.getOrderId()))
                    .findFirst()
                    .orElse(null);

            if (targetOrder != null) {
                System.out.println("  ✅ 找到订单");
                System.out.println("     订单ID: " + targetOrder.getOrderId());
                System.out.println("     当前状态: " + targetOrder.getStatus());
                System.out.println("     是否待成交: " + (targetOrder.isPending() ? "是" : "否"));
                System.out.println();
            } else {
                System.out.println("  ❌ 未找到订单");
                return;
            }

            // 步骤3: 修改订单价格
            System.out.println("【步骤3】修改订单价格...");
            BigDecimal newPrice = new BigDecimal("105.00");
            System.out.println("  原价格: $" + targetOrder.getSubmittedPrice());
            System.out.println("  新价格: $" + newPrice);

            try {
                trade.replaceOrder(targetOrder.getOrderId(), newPrice);
                System.out.println("  ✅ 订单修改成功！");
                System.out.println();

                // 等待1秒
                Thread.sleep(1000);

                // 步骤4: 验证修改结果
                System.out.println("【步骤4】验证修改结果...");
                orders = trade.getTodayOrders();
                OrderResp modifiedOrder = orders.stream()
                        .filter(o -> o.getOrderId().equals(newOrder.getOrderId()))
                        .findFirst()
                        .orElse(null);

                if (modifiedOrder != null) {
                    System.out.println("  订单ID: " + modifiedOrder.getOrderId());
                    System.out.println("  修改后价格: $" + modifiedOrder.getSubmittedPrice());
                    System.out.println("  状态: " + modifiedOrder.getStatus());

                    if (modifiedOrder.getSubmittedPrice().compareTo(newPrice) == 0) {
                        System.out.println("  ✅ 价格修改验证成功！");
                    } else {
                        System.out.println("  ⚠️  价格显示异常（可能需要更长时间同步）");
                    }
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("  ❌ 订单修改失败");
                System.out.println("  错误信息: " + e.getMessage());
                System.out.println();

                // 打印详细错误信息
                System.out.println("  详细错误类型: " + e.getClass().getName());
                if (e.getCause() != null) {
                    System.out.println("  根本原因: " + e.getCause().getMessage());
                }
                System.out.println();
            }

            // 步骤5: 清理 - 撤销测试订单
            System.out.println("【步骤5】清理测试订单...");
            try {
                trade.cancelOrder(newOrder.getOrderId());
                System.out.println("  ✅ 订单已撤销");
            } catch (Exception e) {
                System.out.println("  ⚠️  撤单失败: " + e.getMessage());
            }

            System.out.println();
            System.out.println("========== 测试完成 ==========");

        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

