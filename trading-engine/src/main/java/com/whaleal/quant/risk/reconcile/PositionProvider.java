package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.model.Position;

import java.util.List;

/**
 * 持仓提供者接口
 * 用于获取不同数据源的持仓
 *
 * @author whaleal
 * @version 1.0.0
 */
public interface PositionProvider {

    /**
     * 获取所有持仓
     * @return 持仓列表
     */
    List<Position> getPositions();

    /**
     * 获取指定交易对的持仓
     * @param symbol 交易对
     * @return 持仓
     */
    Position getPosition(String symbol);

    /**
     * 获取数据源名称
     * @return 数据源名称
     */
    String getSourceName();
}
