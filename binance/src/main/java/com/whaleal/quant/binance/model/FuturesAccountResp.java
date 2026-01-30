package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合约账户信息
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class FuturesAccountResp {

    /**
     * 总资产（USDT）
     */
    @JsonProperty("totalWalletBalance")
    private BigDecimal totalWalletBalance;

    /**
     * 可用余额
     */
    @JsonProperty("availableBalance")
    private BigDecimal availableBalance;

    /**
     * 总保证金
     */
    @JsonProperty("totalMarginBalance")
    private BigDecimal totalMarginBalance;

    /**
     * 总未实现盈亏
     */
    @JsonProperty("totalUnrealizedProfit")
    private BigDecimal totalUnrealizedProfit;

    /**
     * 总初始保证金
     */
    @JsonProperty("totalInitialMargin")
    private BigDecimal totalInitialMargin;

    /**
     * 总维持保证金
     */
    @JsonProperty("totalMaintMargin")
    private BigDecimal totalMaintMargin;

    /**
     * 最大可提现金额
     */
    @JsonProperty("maxWithdrawAmount")
    private BigDecimal maxWithdrawAmount;

    /**
     * 资产列表
     */
    private List<FuturesAsset> assets;

    /**
     * 持仓列表
     */
    private List<FuturesPositionResp> positions;

    /**
     * 合约资产
     */
    @Data
    @Builder
    public static class FuturesAsset {
        /**
         * 资产名称
         */
        private String asset;

        /**
         * 钱包余额
         */
        @JsonProperty("walletBalance")
        private BigDecimal walletBalance;

        /**
         * 未实现盈亏
         */
        @JsonProperty("unrealizedProfit")
        private BigDecimal unrealizedProfit;

        /**
         * 保证金余额
         */
        @JsonProperty("marginBalance")
        private BigDecimal marginBalance;

        /**
         * 维持保证金
         */
        @JsonProperty("maintMargin")
        private BigDecimal maintMargin;

        /**
         * 初始保证金
         */
        @JsonProperty("initialMargin")
        private BigDecimal initialMargin;

        /**
         * 可用余额
         */
        @JsonProperty("availableBalance")
        private BigDecimal availableBalance;
    }
}

