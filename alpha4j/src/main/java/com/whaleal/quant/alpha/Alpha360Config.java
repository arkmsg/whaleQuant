package com.whaleal.quant.alpha;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Alpha360因子配置类
 *
 * ⚠️ 重要：Alpha360 固定返回 360 个因子（60天 × 6特征）
 *
 * Alpha360包含360个原始价格和成交量的时间序列因子：
 * - 60天的CLOSE (CLOSE59, CLOSE58, ..., CLOSE1, CLOSE0)
 * - 60天的OPEN (OPEN59, OPEN58, ..., OPEN1, OPEN0)
 * - 60天的HIGH (HIGH59, HIGH58, ..., HIGH1, HIGH0)
 * - 60天的LOW (LOW59, LOW58, ..., LOW1, LOW0)
 * - 60天的VWAP (VWAP59, VWAP58, ..., VWAP1, VWAP0)
 * - 60天的VOLUME (VOLUME59, VOLUME58, ..., VOLUME1, VOLUME0)
 *
 * 归一化方式：
 * - 所有价格都归一化为相对当前收盘价的比例
 * - 所有成交量都归一化为相对当前成交量的比例
 *
 * 设计说明：
 * - 配置参数当前未被 Alpha360Calculator 使用
 * - Alpha360Calculator 硬编码了 60天窗口和6个特征
 * - 保留配置类是为了未来可能的扩展
 *
 * @author arkmsg
 */
@Data
@Builder
public class Alpha360Config {

    /**
     * 时间窗口长度（默认60天）
     *
     * ⚠️ 当前状态：未实现 - 仅用于文档和计算期望因子数
     * 📌 预留用途：未来可支持自定义窗口长度（如30天、120天）
     * 🔧 实现说明：需要修改 Alpha360Calculator 移除硬编码的 TIME_WINDOW
     *
     * @see Alpha360Calculator#TIME_WINDOW 当前硬编码为60
     */
    @Builder.Default
    private int lookbackDays = 60;

    /**
     * 包含的价格字段
     *
     * ⚠️ 当前状态：未实现 - 固定使用 CLOSE、OPEN、HIGH、LOW、VWAP
     * 📌 预留用途：未来可支持自定义价格字段组合（如只用CLOSE和VOLUME）
     * 🔧 实现说明：需要修改 Alpha360Calculator 动态循环 priceFields
     *
     * @see Alpha360Calculator#calculate 当前固定计算6个系列
     */
    @Builder.Default
    private List<String> priceFields = Arrays.asList("CLOSE", "OPEN", "HIGH", "LOW", "VWAP");

    /**
     * 是否包含成交量
     *
     * ⚠️ 当前状态：未实现 - 固定包含VOLUME系列
     * 📌 预留用途：未来可支持排除成交量因子（仅使用价格因子）
     * 🔧 实现说明：需要修改 Alpha360Calculator 添加条件判断
     *
     * @see Alpha360Calculator#calculate 当前固定计算VOLUME系列
     */
    @Builder.Default
    private boolean includeVolume = true;

    /**
     * 创建默认配置（固定360个因子）
     *
     * ⚠️ 注意：Alpha360 固定返回 360 个因子（60天 × 6特征）
     * 配置参数当前未使用，保留是为了未来扩展
     */
    public static Alpha360Config createDefault() {
        return Alpha360Config.builder()
            .lookbackDays(60)
            .priceFields(Arrays.asList("CLOSE", "OPEN", "HIGH", "LOW", "VWAP"))
            .includeVolume(true)
            .build();
    }

    /**
     * 计算总因子数量
     *
     * ⚠️ 注意：此方法返回配置期望的因子数量
     * 实际 Alpha360Calculator 固定返回 360 个因子
     */
    public int getTotalFactorCount() {
        int count = lookbackDays * priceFields.size();
        if (includeVolume) {
            count += lookbackDays;
        }
        return count;
    }
}

