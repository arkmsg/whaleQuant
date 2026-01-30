package com.whaleal.quant.binance.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * <p>统一定义所有可能的错误类型，便于错误分类和处理。
 *
 * @author Binance SDK Team
 * @version 1.0.0
 */
@Getter
public enum ErrorCode {

    // 认证错误 (1xxx)
    INVALID_API_KEY("1001", "API Key无效或已过期", false),
    INVALID_SIGNATURE("1002", "签名验证失败", false),
    INSUFFICIENT_PERMISSION("1003", "权限不足", false),
    IP_NOT_WHITELISTED("1004", "IP地址未加入白名单", false),

    // 参数错误 (2xxx)
    INVALID_PARAMETER("2001", "参数无效", false),
    MISSING_PARAMETER("2002", "缺少必要参数", false),
    INVALID_SYMBOL("2003", "交易对不存在或无效", false),
    INVALID_ORDER_TYPE("2004", "订单类型无效", false),
    INVALID_SIDE("2005", "交易方向无效", false),

    // 订单错误 (3xxx)
    ORDER_NOT_FOUND("3001", "订单不存在", false),
    ORDER_ALREADY_CANCELED("3002", "订单已被取消", false),
    ORDER_ALREADY_FILLED("3003", "订单已完全成交", false),
    INSUFFICIENT_BALANCE("3004", "余额不足", false),
    MIN_NOTIONAL_NOT_MET("3005", "订单金额低于最小值", false),
    PRICE_OUT_OF_RANGE("3006", "价格超出允许范围", false),
    QUANTITY_OUT_OF_RANGE("3007", "数量超出允许范围", false),

    // 合约错误 (4xxx)
    LEVERAGE_NOT_SET("4001", "未设置杠杆倍数", false),
    INVALID_LEVERAGE("4002", "杠杆倍数无效", false),
    POSITION_NOT_FOUND("4003", "持仓不存在", false),
    MARGIN_INSUFFICIENT("4004", "保证金不足", false),
    POSITION_SIDE_NOT_MATCH("4005", "持仓方向不匹配", false),

    // 杠杆交易错误 (5xxx)
    BORROW_LIMIT_EXCEEDED("5001", "借款额度已用尽", false),
    REPAY_AMOUNT_INVALID("5002", "还款金额无效", false),
    CROSS_MARGIN_ACCOUNT_ERROR("5003", "全仓杠杆账户错误", false),

    // 网络错误 (6xxx - 可重试)
    NETWORK_ERROR("6001", "网络连接失败", true),
    TIMEOUT("6002", "请求超时", true),
    SERVER_ERROR("6003", "服务器内部错误", true),
    SERVICE_UNAVAILABLE("6004", "服务暂时不可用", true),

    // 限流错误 (7xxx - 可重试)
    RATE_LIMIT_EXCEEDED("7001", "请求频率超限", true),
    ORDER_RATE_LIMIT("7002", "下单频率超限", true),

    // 系统错误 (9xxx)
    UNKNOWN_ERROR("9001", "未知错误", false),
    SERIALIZATION_ERROR("9002", "数据序列化失败", false),
    DESERIALIZATION_ERROR("9003", "数据反序列化失败", false);

    private final String code;
    private final String message;
    private final boolean retryable;

    ErrorCode(String code, String message, boolean retryable) {
        this.code = code;
        this.message = message;
        this.retryable = retryable;
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取错误信息
     * @return 错误信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 是否可重试
     * @return true表示可重试
     */
    public boolean isRetryable() {
        return retryable;
    }

    /**
     * 根据错误码查找枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }
}

