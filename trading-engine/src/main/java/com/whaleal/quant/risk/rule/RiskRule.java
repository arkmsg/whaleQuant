package com.whaleal.quant.risk.rule;

import com.whaleal.quant.model.Order;
import com.whaleal.quant.model.Position;
import com.whaleal.quant.risk.config.RiskConfig;
import com.whaleal.quant.risk.exception.RiskException;

public interface RiskRule {
    /**
     * 检查订单是否符合风控规则
     * @param order 订单对象
     * @param config 风控配置
     * @throws RiskException 风控检查失败时抛出异常
     */
    void checkOrder(Order order, RiskConfig config) throws RiskException;

    /**
     * 检查持仓是否符合风控规则
     * @param position 持仓对象
     * @param config 风控配置
     * @throws RiskException 风控检查失败时抛出异常
     */
    void checkPosition(Position position, RiskConfig config) throws RiskException;

    /**
     * 获取规则名称
     * @return 规则名称
     */
    String getName();
}
