package com.whaleal.quant.alpha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Alpha158因子顺序定义
 *
 * ⚠️ 该类定义了Alpha158的158个因子的严格顺序，与Qlib完全一致
 *
 * 顺序说明：
 * 1. K线形态因子（9个）
 * 2. 价格因子（5个，窗口期=0）
 * 3. 成交量因子（1个，窗口期=0）- 可选
 * 4. 滚动统计因子（143个，5种窗口期 × 30种算子）
 *
 * @author arkmsg
 */
public class Alpha158FactorOrder {

    /**
     * K线形态因子名称（9个，固定顺序）
     */
    public static final List<String> KBAR_FACTORS = Collections.unmodifiableList(Arrays.asList(
        "KMID", "KLEN", "KMID2", "KUP", "KUP2", "KLOW", "KLOW2", "KSFT", "KSFT2"
    ));

    /**
     * 价格字段名称（用于生成价格因子）
     * ⚠️ 顺序与Qlib默认配置一致：["OPEN", "HIGH", "LOW", "VWAP"]
     * 注意：CLOSE不在默认配置中（因为默认windows=[0]，而CLOSE0=1.0没有信息量）
     */
    public static final List<String> PRICE_FIELDS = Collections.unmodifiableList(Arrays.asList(
        "OPEN", "HIGH", "LOW", "VWAP"
    ));

    /**
     * 滚动统计窗口期（5个，固定顺序）
     */
    public static final List<Integer> ROLLING_WINDOWS = Collections.unmodifiableList(Arrays.asList(
        5, 10, 20, 30, 60
    ));

    /**
     * 滚动统计算子名称（30个，固定顺序）
     *
     * ⚠️ 该顺序与Qlib的get_feature_config()中的if语句顺序完全一致
     */
    public static final List<String> ROLLING_OPERATORS = Collections.unmodifiableList(Arrays.asList(
        "ROC",    // Rate of change
        "MA",     // Moving average
        "STD",    // Standard deviation
        "BETA",   // Slope (trend)
        "RSQR",   // R-square
        "RESI",   // Residual
        "MAX",    // Maximum
        "MIN",    // Minimum (注意：Qlib中叫LOW，但实际是MIN)
        "QTLU",   // Quantile upper (0.8)
        "QTLD",   // Quantile lower (0.2)
        "RANK",   // Rank percentile
        "RSV",    // KDJ's RSV
        "IMAX",   // Index of maximum (Aroon Up)
        "IMIN",   // Index of minimum (Aroon Down)
        "IMXD",   // IMAX - IMIN
        "CORR",   // Correlation(close, log(volume))
        "CORD",   // Correlation(close_change, volume_change)
        "CNTP",   // Count positive (up days ratio)
        "CNTN",   // Count negative (down days ratio)
        "CNTD",   // CNTP - CNTN
        "SUMP",   // Sum positive (similar to RSI)
        "SUMN",   // Sum negative
        "SUMD",   // SUMP - SUMN
        "VMA",    // Volume moving average
        "VSTD",   // Volume standard deviation
        "WVMA",   // Weighted volume moving average
        "VSUMP",  // Volume sum positive
        "VSUMN",  // Volume sum negative
        "VSUMD"   // VSUMP - VSUMN
    ));

    /**
     * 获取完整的Alpha158因子顺序（158个因子名称）
     *
     * ⚠️ 严格按照Qlib源代码的顺序：
     * 1. K线形态因子（if "kbar" in config）
     * 2. 价格因子（if "price" in config）- 先遍历字段，再遍历窗口期
     * 3. 成交量因子（if "volume" in config）
     * 4. 滚动统计因子（if "rolling" in config）- 按算子的if语句顺序
     *
     * @param config Alpha158配置
     * @return 因子名称列表（严格按Qlib顺序）
     */
    public static List<String> getFactorOrder(Alpha158Config config) {
        List<String> order = new ArrayList<>();

        // 1. K线形态因子（9个）
        // Qlib: if "kbar" in config:
        if (config.isEnableKbar()) {
            order.addAll(KBAR_FACTORS);
        }

        // 2. 价格因子
        // Qlib: if "price" in config:
        //   for field in feature:
        //     names += [field.upper() + str(d) for d in windows]
        // ⚠️ Qlib是先遍历字段（OPEN, HIGH, LOW, VWAP），再遍历窗口期
        if (config.isEnablePrice()) {
            for (String field : config.getPriceFeatures()) {
                for (Integer window : config.getPriceWindows()) {
                    order.add(field + window);
                }
            }
        }

        // 3. 成交量因子（可选）
        // Qlib: if "volume" in config:
        //   names += ["VOLUME" + str(d) for d in windows]
        if (config.isEnableVolume()) {
            for (Integer window : config.getVolumeWindows()) {
                order.add("VOLUME" + window);
            }
        }

        // 4. 滚动统计因子
        // Qlib: if "rolling" in config:
        //   按if语句顺序（ROC, MA, STD, BETA, RSQR, ...）
        //   每个算子内部：names += [operator + str(d) for d in windows]
        //
        // ⚠️ 重要设计：即使算子被排除（exclude），也要保留在因子顺序中
        //   - 被排除的算子值为NaN
        //   - 因子数量和顺序始终固定
        //   - 训练和预测天然兼容
        if (config.isEnableRolling()) {
            for (String operator : ROLLING_OPERATORS) {
                // ⚠️ 不跳过被排除的算子！它们会被填充为NaN
                // 这样能保持因子顺序和维度的稳定性

                // 为每个窗口期生成因子
                // ⚠️ Qlib是先遍历窗口期：[5, 10, 20, 30, 60]
                for (Integer window : config.getRollingWindows()) {
                    order.add(operator + window);
                }
            }
        }

        return order;
    }

    /**
     * 获取默认的完整Alpha158因子顺序（158个）
     */
    public static List<String> getDefaultOrder() {
        return getFactorOrder(Alpha158Config.createDefault());
    }

    /**
     * 打印因子顺序（用于调试）
     */
    public static void printFactorOrder(Alpha158Config config) {
        List<String> order = getFactorOrder(config);
        System.out.println("=== Alpha158 Factor Order (Total: " + order.size() + ") ===");

        int index = 0;

        // K线形态因子
        if (config.isEnableKbar()) {
            System.out.println("\n[1] K-Bar Factors (9):");
            for (int i = 0; i < KBAR_FACTORS.size(); i++) {
                System.out.printf("  %3d. %s%n", ++index, order.get(index - 1));
            }
        }

        // 价格因子
        if (config.isEnablePrice()) {
            int priceCount = config.getPriceWindows().size() * config.getPriceFeatures().size();
            System.out.println("\n[2] Price Factors (" + priceCount + "):");
            for (int i = 0; i < priceCount; i++) {
                System.out.printf("  %3d. %s%n", ++index, order.get(index - 1));
            }
        }

        // 成交量因子
        if (config.isEnableVolume()) {
            int volumeCount = config.getVolumeWindows().size();
            System.out.println("\n[3] Volume Factors (" + volumeCount + "):");
            for (int i = 0; i < volumeCount; i++) {
                System.out.printf("  %3d. %s%n", ++index, order.get(index - 1));
            }
        }

        // 滚动统计因子
        if (config.isEnableRolling()) {
            System.out.println("\n[4] Rolling Statistical Factors:");
            for (String operator : ROLLING_OPERATORS) {
                // 即使被排除，也要显示（会标注为NaN填充）
                String excludeMarker = "";
                if (!config.useRollingOperator(operator)) {
                    excludeMarker = " [EXCLUDED, NaN-padded]";
                }
                System.out.println("  " + operator + excludeMarker + ":");
                for (Integer window : config.getRollingWindows()) {
                    System.out.printf("    %3d. %s%d%n", ++index, operator, window);
                }
            }
        }
    }
}

