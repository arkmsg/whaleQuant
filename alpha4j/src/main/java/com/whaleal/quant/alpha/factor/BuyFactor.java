package com.whaleal.quant.alpha.factor;

import com.whaleal.quant.base.model.Bar;
import com.whaleal.quant.base.model.Ticker;

import java.util.List;

/**
 * 买入因子接口
 * 决定何时买入股票或资产
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface BuyFactor {

    /**
     * 获取因子名称
     * @return 因子名称
     */
    String getName();

    /**
     * 计算买入信号
     * @param symbol 交易对符号
     * @param bars K线数据列表
     * @param ticker 实时行情数据
     * @return 买入信号强度，范围[-1, 1]，值越大表示买入信号越强
     */
    double calculateBuySignal(String symbol, List<Bar> bars, Ticker ticker);

    /**
     * 判断是否买入
     * @param symbol 交易对符号
     * @param bars K线数据列表
     * @param ticker 实时行情数据
     * @return 是否买入
     */
    default boolean shouldBuy(String symbol, List<Bar> bars, Ticker ticker) {
        return calculateBuySignal(symbol, bars, ticker) > 0.5;
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
