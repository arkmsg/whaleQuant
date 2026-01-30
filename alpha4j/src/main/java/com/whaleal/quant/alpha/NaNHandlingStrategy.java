package com.whaleal.quant.alpha;

/**
 * NaN处理策略枚举
 *
 * 用于指定如何处理特征向量中的NaN和无穷大值
 *
 * @author arkmsg
 */
public enum NaNHandlingStrategy {

    /**
     * 保留NaN（不处理）
     *
     * ⚠️ 注意：大多数ML库不支持NaN，直接传递可能导致错误
     * 使用场景：
     * - 某些ML框架内置NaN处理（如LightGBM）
     * - 需要手动处理NaN的场景
     */
    KEEP_NAN("保留NaN"),

    /**
     * 填充为0.0
     *
     * ⚠️ 最简单但可能不是最佳选择
     * 使用场景：
     * - 快速原型验证
     * - NaN比例很小（<1%）
     * - 因子已归一化，0代表中性值
     */
    FILL_ZERO("填充为0.0"),

    /**
     * 填充为均值
     *
     * ✅ 推荐：适用于大多数场景
     * 使用场景：
     * - NaN比例适中（1%-10%）
     * - 数据基本符合正态分布
     * - 希望保持数据分布
     */
    FILL_MEAN("填充为均值"),

    /**
     * 填充为中位数
     *
     * ✅ 推荐：适用于有异常值的场景
     * 使用场景：
     * - 数据包含异常值
     * - 数据分布不对称
     * - 对异常值敏感的模型
     */
    FILL_MEDIAN("填充为中位数"),

    /**
     * 前向填充（用前一个有效值填充）
     *
     * 使用场景：
     * - 时间序列数据
     * - 相邻值相关性强
     * - 缺失是随机的短期现象
     */
    FILL_FORWARD("前向填充"),

    /**
     * 后向填充（用后一个有效值填充）
     *
     * 使用场景：
     * - 时间序列数据
     * - 需要使用未来信息（回测场景慎用）
     */
    FILL_BACKWARD("后向填充"),

    /**
     * 抛出异常（严格模式）
     *
     * ⚠️ 发现NaN时直接抛出异常
     * 使用场景：
     * - 生产环境需要严格数据质量
     * - 不允许NaN数据进入模型
     * - 需要追踪数据质量问题
     */
    THROW_EXCEPTION("抛出异常");

    private final String description;

    NaNHandlingStrategy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 应用策略到特征向量
     *
     * @param feature 原始特征向量
     * @param initialValue 初始值（用于前向/后向填充）
     * @return 处理后的特征向量
     */
    public AlphaFeatureVector apply(AlphaFeatureVector feature, double initialValue) {
        if (!feature.hasInvalidValues()) {
            return feature; // 没有非法值，直接返回
        }

        switch (this) {
            case KEEP_NAN:
                return feature;

            case FILL_ZERO:
                return feature.fillInvalidValues(0.0);

            case FILL_MEAN:
                return feature.fillNaNWithMean();

            case FILL_MEDIAN:
                return feature.fillNaNWithMedian();

            case FILL_FORWARD:
                return feature.fillNaNForward(initialValue);

            case FILL_BACKWARD:
                return feature.fillNaNBackward(initialValue);

            case THROW_EXCEPTION:
                throw new IllegalStateException(
                    "特征向量包含非法值！\n" +
                    "Symbol: " + feature.getSymbol() + "\n" +
                    "Timestamp: " + feature.getTimestamp() + "\n" +
                    "Statistics: " + feature.getInvalidValuesStatistics() + "\n" +
                    "Details: " + feature.getInvalidValuesDetail()
                );

            default:
                return feature.fillInvalidValues(0.0);
        }
    }

    /**
     * 应用策略（使用默认初始值0.0）
     */
    public AlphaFeatureVector apply(AlphaFeatureVector feature) {
        return apply(feature, 0.0);
    }
}

