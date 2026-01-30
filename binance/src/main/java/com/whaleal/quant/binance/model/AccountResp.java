package com.whaleal.quant.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户信息响应
 *
 * @author Binance SDK Team
 */
@Data
@Builder
public class AccountResp {

    /**
     * 手续费等级
     */
    @JsonProperty("makerCommission")
    private Integer makerCommission;

    @JsonProperty("takerCommission")
    private Integer takerCommission;

    @JsonProperty("buyerCommission")
    private Integer buyerCommission;

    @JsonProperty("sellerCommission")
    private Integer sellerCommission;

    /**
     * 是否可以交易
     */
    @JsonProperty("canTrade")
    private Boolean canTrade;

    /**
     * 是否可以提现
     */
    @JsonProperty("canWithdraw")
    private Boolean canWithdraw;

    /**
     * 是否可以充值
     */
    @JsonProperty("canDeposit")
    private Boolean canDeposit;

    /**
     * 账户更新时间
     */
    @JsonProperty("updateTime")
    private Long updateTime;

    /**
     * 账户类型
     */
    @JsonProperty("accountType")
    private String accountType;

    /**
     * 余额列表
     */
    private List<Balance> balances;

    /**
     * 权限列表
     */
    private List<String> permissions;

    /**
     * 余额信息
     */
    @Data
    @Builder
    public static class Balance {
        /**
         * 资产名称
         */
        private String asset;

        /**
         * 可用余额
         */
        private BigDecimal free;

        /**
         * 冻结余额
         */
        private BigDecimal locked;
    }
}

