package com.whaleal.quant.alpha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Alpha360因子顺序定义
 *
 * ⚠️ 该类定义了Alpha360的360个因子的严格顺序，与Qlib完全一致
 *
 * Qlib Alpha360顺序（共360个）：
 * 1. CLOSE系列（60个）：CLOSE59 → CLOSE58 → ... → CLOSE1 → CLOSE0
 * 2. OPEN系列（60个）：OPEN59 → OPEN58 → ... → OPEN1 → OPEN0
 * 3. HIGH系列（60个）：HIGH59 → HIGH58 → ... → HIGH1 → HIGH0
 * 4. LOW系列（60个）：LOW59 → LOW58 → ... → LOW1 → LOW0
 * 5. VWAP系列（60个）：VWAP59 → VWAP58 → ... → VWAP1 → VWAP0
 * 6. VOLUME系列（60个）：VOLUME59 → VOLUME58 → ... → VOLUME1 → VOLUME0
 *
 * 所有价格归一化为相对当前收盘价的比例
 * 所有成交量归一化为相对当前成交量的比例
 *
 * @author arkmsg
 */
public class Alpha360FactorOrder {

    /**
     * 价格字段顺序（6个，固定顺序）
     *
     * ⚠️ 该顺序与Qlib的Alpha360DL.get_feature_config()完全一致
     */
    public static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList(
        "CLOSE",   // 收盘价
        "OPEN",    // 开盘价
        "HIGH",    // 最高价
        "LOW",     // 最低价
        "VWAP",    // 成交量加权平均价
        "VOLUME"   // 成交量
    ));

    /**
     * 获取完整的Alpha360因子顺序
     *
     * @param config Alpha360配置
     * @return 因子名称列表（严格按Qlib顺序）
     */
    public static List<String> getFactorOrder(Alpha360Config config) {
        List<String> order = new ArrayList<>();
        int lookback = config.getLookbackDays();

        // 遍历每个字段
        for (String field : FIELD_ORDER) {
            // 跳过VOLUME字段（如果未启用）
            if (field.equals("VOLUME") && !config.isIncludeVolume()) {
                continue;
            }

            // 跳过不在配置中的价格字段
            if (!field.equals("VOLUME") && !config.getPriceFields().contains(field)) {
                continue;
            }

            // 为每个字段生成时间序列因子
            // 顺序：Field(N-1), Field(N-2), ..., Field1, Field0
            // 即从最远到最近
            for (int i = lookback - 1; i >= 0; i--) {
                order.add(field + i);
            }
        }

        return order;
    }

    /**
     * 获取默认的Alpha360因子顺序（固定360个）
     *
     * ⚠️ 注意：Alpha360 固定返回 360 个因子
     */
    public static List<String> getDefaultOrder() {
        return getFactorOrder(Alpha360Config.createDefault());
    }

    /**
     * 打印因子顺序（用于调试）
     */
    public static void printFactorOrder(Alpha360Config config) {
        List<String> order = getFactorOrder(config);
        System.out.println("=== Alpha360 Factor Order (Total: " + order.size() + ") ===");
        System.out.println("Lookback Days: " + config.getLookbackDays());
        System.out.println("Price Fields: " + config.getPriceFields());
        System.out.println("Include Volume: " + config.isIncludeVolume());

        int index = 0;

        // 按字段分组显示
        for (String field : FIELD_ORDER) {
            // 跳过未启用的字段
            if (field.equals("VOLUME") && !config.isIncludeVolume()) {
                continue;
            }
            if (!field.equals("VOLUME") && !config.getPriceFields().contains(field)) {
                continue;
            }

            System.out.println("\n[" + field + "] Time Series (" + config.getLookbackDays() + " factors):");

            // 只显示前3个和后3个
            int count = 0;
            for (int i = config.getLookbackDays() - 1; i >= 0; i--) {
                index++;
                if (count < 3 || i < 3) {
                    System.out.printf("  %3d. %s%d%n", index, field, i);
                }
                if (count == 3 && i >= 3) {
                    System.out.printf("  ... (%d factors omitted)%n", config.getLookbackDays() - 6);
                }
                count++;
            }
        }
    }

    /**
     * 验证因子数量是否正确
     */
    public static boolean validateFactorCount(Alpha360Config config, int actualCount) {
        int expectedCount = config.getTotalFactorCount();
        if (actualCount != expectedCount) {
            System.err.printf("因子数量不匹配！期望: %d, 实际: %d%n", expectedCount, actualCount);
            return false;
        }
        return true;
    }
}

