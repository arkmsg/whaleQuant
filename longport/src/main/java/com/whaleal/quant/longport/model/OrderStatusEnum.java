package com.whaleal.quant.longport.model;

import com.longport.trade.OrderStatus;

/**
 * 订单状态枚举
 * 
 * @author arkmsg
 */
public enum OrderStatusEnum {
    
    /**
     * 未知
     */
    UNKNOWN("Unknown", "未知"),
    
    /**
     * 未报告
     */
    NOT_REPORTED("NotReported", "未报告"),
    
    /**
     * 已替换未报告
     */
    REPLACED_NOT_REPORTED("ReplacedNotReported", "已替换未报告"),
    
    /**
     * 保护未报告
     */
    PROTECTED_NOT_REPORTED("ProtectedNotReported", "保护未报告"),
    
    /**
     * 品种未报告
     */
    VARIETIES_NOT_REPORTED("VarietiesNotReported", "品种未报告"),
    
    /**
     * 已成交
     */
    FILLED("Filled", "已成交"),
    
    /**
     * 待确认新订单
     */
    WAIT_TO_NEW("WaitToNew", "待确认新订单"),
    
    /**
     * 新订单
     */
    NEW("New", "新订单"),
    
    /**
     * 待替换
     */
    WAIT_TO_REPLACE("WaitToReplace", "待替换"),
    
    /**
     * 替换中
     */
    PENDING_REPLACE("PendingReplace", "替换中"),
    
    /**
     * 已替换
     */
    REPLACED("Replaced", "已替换"),
    
    /**
     * 部分成交
     */
    PARTIAL_FILLED("PartialFilled", "部分成交"),
    
    /**
     * 待撤销
     */
    WAIT_TO_CANCEL("WaitToCancel", "待撤销"),
    
    /**
     * 撤销中
     */
    PENDING_CANCEL("PendingCancel", "撤销中"),
    
    /**
     * 已拒绝
     */
    REJECTED("Rejected", "已拒绝"),
    
    /**
     * 已撤销
     */
    CANCELED("Canceled", "已撤销"),
    
    /**
     * 已过期
     */
    EXPIRED("Expired", "已过期"),
    
    /**
     * 部分撤回
     */
    PARTIAL_WITHDRAWAL("PartialWithdrawal", "部分撤回");
    
    private final String key;
    private final String description;
    
    OrderStatusEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 从长桥SDK的OrderStatus转换
     */
    public static OrderStatusEnum fromLongport(OrderStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case NotReported:
                return NOT_REPORTED;
            case ReplacedNotReported:
                return REPLACED_NOT_REPORTED;
            case ProtectedNotReported:
                return PROTECTED_NOT_REPORTED;
            case VarietiesNotReported:
                return VARIETIES_NOT_REPORTED;
            case Filled:
                return FILLED;
            case WaitToNew:
                return WAIT_TO_NEW;
            case New:
                return NEW;
            case WaitToReplace:
                return WAIT_TO_REPLACE;
            case PendingReplace:
                return PENDING_REPLACE;
            case Replaced:
                return REPLACED;
            case PartialFilled:
                return PARTIAL_FILLED;
            case WaitToCancel:
                return WAIT_TO_CANCEL;
            case PendingCancel:
                return PENDING_CANCEL;
            case Rejected:
                return REJECTED;
            case Canceled:
                return CANCELED;
            case Expired:
                return EXPIRED;
            case PartialWithdrawal:
                return PARTIAL_WITHDRAWAL;
            case Unknown:
            default:
                return UNKNOWN;
        }
    }
    
    /**
     * 转换为长桥SDK的OrderStatus
     */
    public OrderStatus toLongport() {
        switch (this) {
            case NOT_REPORTED:
                return OrderStatus.NotReported;
            case REPLACED_NOT_REPORTED:
                return OrderStatus.ReplacedNotReported;
            case PROTECTED_NOT_REPORTED:
                return OrderStatus.ProtectedNotReported;
            case VARIETIES_NOT_REPORTED:
                return OrderStatus.VarietiesNotReported;
            case FILLED:
                return OrderStatus.Filled;
            case WAIT_TO_NEW:
                return OrderStatus.WaitToNew;
            case NEW:
                return OrderStatus.New;
            case WAIT_TO_REPLACE:
                return OrderStatus.WaitToReplace;
            case PENDING_REPLACE:
                return OrderStatus.PendingReplace;
            case REPLACED:
                return OrderStatus.Replaced;
            case PARTIAL_FILLED:
                return OrderStatus.PartialFilled;
            case WAIT_TO_CANCEL:
                return OrderStatus.WaitToCancel;
            case PENDING_CANCEL:
                return OrderStatus.PendingCancel;
            case REJECTED:
                return OrderStatus.Rejected;
            case CANCELED:
                return OrderStatus.Canceled;
            case EXPIRED:
                return OrderStatus.Expired;
            case PARTIAL_WITHDRAWAL:
                return OrderStatus.PartialWithdrawal;
            case UNKNOWN:
            default:
                return OrderStatus.Unknown;
        }
    }
    
    /**
     * 是否已成交
     */
    public boolean isFilled() {
        return this == FILLED;
    }
    
    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return this == CANCELED || this == EXPIRED;
    }
    
    /**
     * 是否待成交
     */
    public boolean isPending() {
        return this == NOT_REPORTED ||
               this == WAIT_TO_NEW ||
               this == NEW ||
               this == PARTIAL_FILLED ||
               this == WAIT_TO_REPLACE ||
               this == PENDING_REPLACE ||
               this == WAIT_TO_CANCEL ||
               this == PENDING_CANCEL;
    }
}

