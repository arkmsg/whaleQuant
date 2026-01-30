package com.whaleal.quant.risk.reconcile;

import com.whaleal.quant.base.model.Order;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * 订单对账结果
 * 用于存储订单对账结果
 *
 * @author whaleal
 * @version 1.0.0
 */
@Data
@Builder
public class OrderReconciliationResult {
    /**
     * 对账 ID
     */
    private final String reconciliationId;

    /**
     * 对账时间
     */
    private final Instant reconciliationTime;

    /**
     * 订单列表
     */
    private List<Order> orders;

    /**
     * 获取订单数量
     * @return 订单数量
     */
    public int getOrderCount() {
        return orders != null ? orders.size() : 0;
    }
}
