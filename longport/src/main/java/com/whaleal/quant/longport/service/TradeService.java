package com.whaleal.quant.longport.service;

import com.longport.trade.*;
import com.whaleal.quant.longport.builder.OrderBuilder;
import com.whaleal.quant.longport.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易服务
 * 
 * <p>提供股票交易相关功能，包括下单、撤单、查询持仓、查询订单等。
 * 
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>提交订单（Builder模式）</li>
 *   <li>撤单</li>
 *   <li>查询持仓</li>
 *   <li>查询订单</li>
 *   <li>查询账户余额</li>
 * </ul>
 * 
 * @author Longport SDK Team
 */
@Slf4j
public class TradeService {
    
    private final TradeContext tradeContext;
    
    public TradeService(TradeContext tradeContext) {
        this.tradeContext = tradeContext;
    }
    
    /**
     * 创建订单Builder
     * 
     * <p>使用示例：
     * <pre>{@code
     * OrderResp order = tradeService.submitOrder()
     *     .symbol("AAPL.US")
     *     .side(OrderSide.BUY)
     *     .quantity(100)
     *     .orderType(OrderType.LIMIT)
     *     .price(150.00)
     *     .execute();
     * }</pre>
     * 
     * @return 订单Builder
     */
    public OrderBuilder submitOrder() {
        return new OrderBuilder(tradeContext);
    }
    
    /**
     * 撤单
     * 
     * @param orderId 订单ID
     * @throws Exception 如果撤单失败
     */
    public void cancelOrder(String orderId) throws Exception {
        tradeContext.cancelOrder(orderId).get();
        log.debug("撤单成功: {}", orderId);
    }
    
    /**
     * 批量撤单
     * 
     * @param orderIds 订单ID列表
     * @return 撤单结果列表（成功/失败）
     */
    public List<CancelOrderResult> cancelOrders(List<String> orderIds) {
        List<CancelOrderResult> results = new ArrayList<>();
        
        for (String orderId : orderIds) {
            try {
                cancelOrder(orderId);
                results.add(new CancelOrderResult(orderId, true, null));
            } catch (Exception e) {
                log.warn("撤单失败: {} - {}", orderId, e.getMessage());
                results.add(new CancelOrderResult(orderId, false, e.getMessage()));
            }
        }
        
        return results;
    }
    
    /**
     * 查询持仓
     * 
     * @return 持仓列表
     * @throws Exception 如果查询失败
     */
    public List<PositionResp> getPositions() throws Exception {
        GetStockPositionsOptions options = new GetStockPositionsOptions();
        StockPositionsResponse response = tradeContext.getStockPositions(options).get();
        
        List<PositionResp> positions = new ArrayList<>();
        
        if (response.getChannels() != null) {
            for (StockPositionChannel channel : response.getChannels()) {
                if (channel.getPositions() != null) {
                    for (StockPosition position : channel.getPositions()) {
                        if (position.getQuantity() != null && 
                            position.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                            
                            positions.add(PositionResp.builder()
                                    .symbol(position.getSymbol())
                                    .quantity(position.getQuantity())
                                    .availableQuantity(position.getAvailableQuantity())
                                    .costPrice(position.getCostPrice())
                                    .market(position.getMarket() != null ? position.getMarket().name() : "")
                                    .build());
                        }
                    }
                }
            }
        }
        
        log.debug("查询到 {} 个持仓", positions.size());
        return positions;
    }
    
    /**
     * 查询今日订单
     * 
     * @return 订单列表
     * @throws Exception 如果查询失败
     */
    public List<OrderResp> getTodayOrders() throws Exception {
        GetTodayOrdersOptions options = new GetTodayOrdersOptions();
        Order[] orders = tradeContext.getTodayOrders(options).get();
        
        if (orders == null || orders.length == 0) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(orders)
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询历史订单
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     * @throws Exception 如果查询失败
     */
    public List<OrderResp> getHistoryOrders(java.time.OffsetDateTime startDate, java.time.OffsetDateTime endDate) throws Exception {
        GetHistoryOrdersOptions options = new GetHistoryOrdersOptions();
        options.setStartAt(startDate);
        options.setEndAt(endDate);
        
        Order[] orders = tradeContext.getHistoryOrders(options).get();
        
        if (orders == null || orders.length == 0) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(orders)
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 查询账户余额
     * 
     * @return 账户余额信息
     * @throws Exception 如果查询失败
     */
    public AccountBalanceResp getAccountBalance() throws Exception {
        AccountBalance[] balances = tradeContext.getAccountBalance().get();
        
        if (balances == null || balances.length == 0) {
            throw new IllegalStateException("未获取到账户信息");
        }
        
        AccountBalance balance = balances[0];
        
        return AccountBalanceResp.builder()
                .totalCash(balance.getTotalCash())
                .maxFinanceAmount(balance.getMaxFinanceAmount())
                .remainingFinanceAmount(balance.getRemainingFinanceAmount())
                .riskLevel(balance.getRiskLevel())
                .marginCall(balance.getMarginCall())
                .netAssets(balance.getNetAssets())
                .initMargin(balance.getInitMargin())
                .maintenanceMargin(balance.getMaintenanceMargin())
                .build();
    }
    
    /**
     * 修改订单价格
     * 
     * @param orderId 订单ID
     * @param price 新价格
     * @throws Exception 如果修改失败
     */
    public void replaceOrder(String orderId, BigDecimal price) throws Exception {
        ReplaceOrderOptions options = new ReplaceOrderOptions(orderId, price);
        tradeContext.replaceOrder(options).get();
        log.debug("订单修改成功: {} | 新价格: {}", orderId, price);
    }
    
    /**
     * 获取原始TradeContext（高级用户使用）
     * 
     * @return TradeContext实例
     */
    public TradeContext getRawContext() {
        return tradeContext;
    }
    
    /**
     * 转换Order为OrderResp
     */
    private OrderResp convertToOrderResponse(Order order) {
        return OrderResp.builder()
                .orderId(order.getOrderId())
                .symbol(order.getSymbol())
                .side(OrderSideEnum.fromLongport(order.getSide()))
                .orderType(OrderTypeEnum.fromLongport(order.getOrderType()))
                .submittedPrice(order.getPrice())
                .submittedQuantity(order.getQuantity())
                .executedPrice(order.getExecutedPrice())
                .executedQuantity(order.getExecutedQuantity())
                .status(OrderStatusEnum.fromLongport(order.getStatus()))
                .submittedAt(order.getSubmittedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

