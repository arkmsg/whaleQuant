package com.whaleal.quant.trading.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 深度行情模型
 *
 * <p>表示市场的买卖盘口信息
 *
 * @author trading
 * @version 1.0.0
 */
@Data
@Builder
public class Depth {

    /**
     * 交易对/股票代码
     */
    private String symbol;

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 买盘
     */
    private List<PriceLevel> bids;

    /**
     * 卖盘
     */
    private List<PriceLevel> asks;

    /**
     * 数据来源
     */
    private String source;

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
