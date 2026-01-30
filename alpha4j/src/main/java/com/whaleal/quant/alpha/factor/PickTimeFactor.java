package com.whaleal.quant.alpha.factor;

import com.whaleal.quant.model.Bar;
import com.whaleal.quant.model.Ticker;

import java.util.List;
import java.util.Map;

/**
 * 择时因子接口
 * 选择适合策略运行的市场时机（如牛市、熊市）
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface PickTimeFactor {

    /**
     * 获取因子名称
     * @return 因子名称
     */
    String getName();

    /**
     * 计算市场时机信号
     * @param marketSymbol 市场指数符号（如沪深300、纳斯达克等）
     * @param marketBars 市场指数K线数据列表
     * @param marketTicker 市场指数实时行情数据
     * @param symbolTickerMap 各交易对的实时行情数据映射
     * @return 市场时机信号强度，范围[-1, 1]，值越大表示市场时机越好
     */
    double calculateTimeSignal(String marketSymbol, List<Bar> marketBars, Ticker marketTicker, Map<String, Ticker> symbolTickerMap);

    /**
     * 判断当前市场时机是否适合策略运行
     * @param marketSymbol 市场指数符号（如沪深300、纳斯达克等）
     * @param marketBars 市场指数K线数据列表
     * @param marketTicker 市场指数实时行情数据
     * @param symbolTickerMap 各交易对的实时行情数据映射
     * @return 当前市场时机是否适合策略运行
     */
    default boolean isGoodTime(String marketSymbol, List<Bar> marketBars, Ticker marketTicker, Map<String, Ticker> symbolTickerMap) {
        return calculateTimeSignal(marketSymbol, marketBars, marketTicker, symbolTickerMap) > 0;
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
