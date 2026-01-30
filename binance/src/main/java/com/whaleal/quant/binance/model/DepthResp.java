package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 深度信息（盘口）响应
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class DepthResp {

    /**
     * 最后更新ID
     */
    @JsonProperty("lastUpdateId")
    private Long lastUpdateId;

    /**
     * 买盘（价格从高到低）
     * 每个元素是 [价格, 数量]
     */
    private List<PriceLevel> bids;

    /**
     * 卖盘（价格从低到高）
     * 每个元素是 [价格, 数量]
     */
    private List<PriceLevel> asks;

    /**
     * 价格档位
     */
    @Data
    @Builder
    public static class PriceLevel {
        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private BigDecimal quantity;
    }


}

