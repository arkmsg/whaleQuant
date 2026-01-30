package com.whaleal.quant.alpha.factor;

import com.whaleal.quant.model.Bar;
import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.model.Ticker;

import java.util.List;

/**
 * 卖出因子接口
 * 决定何时卖出股票或资产，包括止盈、止损、时间退出等
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface SellFactor {

    /**
     * 获取因子名称
     * @return 因子名称
     */
    String getName();

    /**
     * 计算卖出信号
     * @param symbol 交易对符号
     * @param position 持仓信息
     * @param order 订单信息
     * @param bars K线数据列表
     * @param ticker 实时行情数据
     * @return 卖出信号强度，范围[-1, 1]，值越大表示卖出信号越强
     */
    double calculateSellSignal(String symbol, Position position, Order order, List<Bar> bars, Ticker ticker);

    /**
     * 判断是否卖出
     * @param symbol 交易对符号
     * @param position 持仓信息
     * @param order 订单信息
     * @param bars K线数据列表
     * @param ticker 实时行情数据
     * @return 是否卖出
     */
    default boolean shouldSell(String symbol, Position position, Order order, List<Bar> bars, Ticker ticker) {
        return calculateSellSignal(symbol, position, order, bars, ticker) > 0.5;
    }

    /**
     * 获取因子参数
     * @return 因子参数
     */
    default FactorParams getParams() {
        return new FactorParams();
    }

    /**
     * 设置因子参数
     * @param params 因子参数
     */
    default void setParams(FactorParams params) {
        // 默认实现，不做任何操作
    }
}
