package com.whaleal.quant.longport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤单结果
 *
 * @author Longport SDK Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderResult {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息（失败时）
     */
    private String errorMessage;
}

