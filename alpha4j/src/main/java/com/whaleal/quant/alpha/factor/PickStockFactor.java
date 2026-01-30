package com.whaleal.quant.alpha.factor;

import com.whaleal.quant.base.model.Bar;
import com.whaleal.quant.base.model.Ticker;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 择股因子接口
 * 从股票池中选择符合特定条件的股票
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface PickStockFactor {

    /**
     * 获取因子名称
     * @return 因子名称
     */
    String getName();

    /**
     * 从股票池中选择股票
     * @param stockPool 股票池
     * @param symbolBarsMap 各股票的K线数据映射
     * @param symbolTickerMap 各股票的实时行情数据映射
     * @return 选择的股票集合
     */
    Set<String> pickStocks(Set<String> stockPool, Map<String, List<Bar>> symbolBarsMap, Map<String, Ticker> symbolTickerMap);

    /**
     * 计算股票选择得分
     * @param symbol 股票符号
     * @param bars K线数据列表
     * @param ticker 实时行情数据
     * @return 股票选择得分，范围[-1, 1]，值越大表示股票越值得选择
     */
    double calculateStockScore(String symbol, List<Bar> bars, Ticker ticker);

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
