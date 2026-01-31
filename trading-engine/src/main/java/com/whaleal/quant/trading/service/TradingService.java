package com.whaleal.quant.trading.service;

import com.whaleal.quant.model.trading.Order;
import com.whaleal.quant.model.trading.Position;
import com.whaleal.quant.model.trading.TradeExecution;
import com.whaleal.quant.model.trading.TradeSignal;

import java.util.List;

/**
 * 交易服务接口
 *
 * <p>提供交易相关的核心功能
 *
 * @author trading
 * @version 1.0.0
 */
public interface TradingService {

    /**
     * 执行交易信号
     *
     * @param signal 交易信号
     * @return 交易执行结果
     */
    TradeExecution executeSignal(TradeSignal signal);

    /**
     * 下单
     *
     * @param order 订单
     * @return 交易执行结果
     */
    TradeExecution placeOrder(Order order);

    /**
     * 撤单
     *
     * @param orderId 订单ID
     * @return 交易执行结果
     */
    TradeExecution cancelOrder(String orderId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单
     */
    Order getOrder(String orderId);

    /**
     * 获取持仓列表
     *
     * @return 持仓列表
     */
    List<Position> getPositions();

    /**
     * 获取指定交易对的持仓
     *
     * @param symbol 交易对/股票代码
     * @return 持仓
     */
    Position getPosition(String symbol);
}
