package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 杠杆账户信息（全仓杠杆）
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class MarginAccountResp {

    /**
     * 是否可以交易
     */
    @JsonProperty("tradeEnabled")
    private Boolean tradeEnabled;

    /**
     * 是否可以转账
     */
    @JsonProperty("transferEnabled")
    private Boolean transferEnabled;

    /**
     * 是否可以借款
     */
    @JsonProperty("borrowEnabled")
    private Boolean borrowEnabled;

    /**
     * 账户总资产（BTC计价）
     */
    @JsonProperty("totalAssetOfBtc")
    private BigDecimal totalAssetOfBtc;

    /**
     * 账户总负债（BTC计价）
     */
    @JsonProperty("totalLiabilityOfBtc")
    private BigDecimal totalLiabilityOfBtc;

    /**
     * 账户净资产（BTC计价）
     */
    @JsonProperty("totalNetAssetOfBtc")
    private BigDecimal totalNetAssetOfBtc;

    /**
     * 保证金等级
     */
    @JsonProperty("marginLevel")
    private BigDecimal marginLevel;

    /**
     * 资产列表
     */
    @JsonProperty("userAssets")
    private List<MarginAsset> userAssets;

    /**
     * 杠杆资产
     */
    @Data
    @Builder
    public static class MarginAsset {
        /**
         * 资产名称
         */
        private String asset;

        /**
         * 可借数量
         */
        private BigDecimal borrowed;

        /**
         * 可用余额
         */
        private BigDecimal free;

        /**
         * 应付利息
         */
        private BigDecimal interest;

        /**
         * 冻结数量
         */
        private BigDecimal locked;

        /**
         * 净资产
         */
        @JsonProperty("netAsset")
        private BigDecimal netAsset;
    }
}

