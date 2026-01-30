package com.whaleal.quant.longport.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * 订单响应
 *
 * @author Longport SDK Team
 */
@Data
@Builder
public class OrderResp {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 买卖方向
     */
    private OrderSideEnum side;

    /**
     * 订单类型
     */
    private OrderTypeEnum orderType;

    /**
     * 委托价格
     */
    private BigDecimal submittedPrice;

    /**
     * 委托数量
     */
    private BigDecimal submittedQuantity;

    /**
     * 成交价格
     */
    private BigDecimal executedPrice;

    /**
     * 成交数量
     */
    private BigDecimal executedQuantity;

    /**
     * 订单状态
     */
    private OrderStatusEnum status;

    /**
     * 提交时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime submittedAt;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    /**
     * 是否已成交
     */
    public boolean isFilled() {
        return status != null && status.isFilled();
    }

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return status != null && status.isCancelled();
    }

    /**
     * 是否待成交
     */
    public boolean isPending() {
        return status != null && status.isPending();
    }
}

