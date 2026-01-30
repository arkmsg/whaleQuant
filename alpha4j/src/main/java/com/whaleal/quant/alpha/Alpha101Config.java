package com.whaleal.quant.alpha;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Alpha101因子配置类
 *
 * WorldQuant Alpha101包含101个经过验证的Alpha因子
 * 每个因子都是一个独立的交易信号
 *
 * @author arkmsg
 */
@Data
@Builder
public class Alpha101Config {

    /**
     * 包含的Alpha因子编号列表
     *
     * ✅ 已实现：完全生效
     * null表示全部101个因子（包括未实现的，未实现的返回Double.NaN）
     * 非null时只计算指定的因子
     *
     * ⚠️ 注意：未实现的21个因子会返回 Double.NaN，不是 0.0
     *
     * 使用示例：
     * - null: 计算全部101个因子（80个有值，21个NaN）
     * - Arrays.asList(1, 5, 10): 只计算Alpha#1, #5, #10
     */
    private List<Integer> includeAlphas;

    /**
     * 排除的Alpha因子编号列表
     *
     * ✅ 已实现：完全生效
     * 指定要排除的因子编号，这些因子不会被计算，也不会出现在结果中
     *
     * ⚠️ 重要：排除后的行为
     * - 被排除的因子不会被计算
     * - 不会出现在 factors Map 中
     * - 不会出现在 factorOrder 列表中
     * - getFactorCount() 只返回实际计算的因子数量
     * - toDoubleArray() 只包含实际计算的因子
     *
     * 使用示例：
     * - Arrays.asList(48, 56, 58, 59): 排除WorldQuant缺失的因子（结果：97个因子）
     * - Arrays.asList(63, 67, 69, 70, 76, 79, 80, 82, 87, 89, 90, 91, 93, 97, 100): 排除需要行业中性化的因子
     *
     * 💡 建议：排除所有未实现的21个因子以获得80个有效因子
     *
     * 完整的未实现因子列表：
     * Arrays.asList(48, 56, 58, 59, 63, 67, 69, 70, 76, 79, 80, 82,
     *               87, 89, 90, 91, 93, 97, 100)
     * 排除后结果：80个有效因子，无NaN值
     */
    @Builder.Default
    private List<Integer> excludeAlphas = new ArrayList<>();

    /**
     * 是否启用高级因子（某些因子计算复杂度较高）
     *
     * ⚠️ 当前状态：未实现 - 参数已定义但未使用
     * 📌 预留用途：未来可用于控制是否计算复杂度高的因子（如嵌套多层的因子）
     * 🔧 实现说明：需要在各Group中添加复杂度判断逻辑
     *
     * 说明：当前所有因子（除未实现的21个）都会计算，此参数不影响结果
     */
    @Builder.Default
    private boolean enableAdvancedAlphas = true;

    /**
     * ADV20的窗口期（平均成交量）
     *
     * ✅ 已实现：完全生效
     * 用于计算20日平均成交量（ADV20），多个因子依赖此值
     *
     * 默认值：20
     * 建议范围：10-30天
     *
     * 说明：ADV20 = SMA(Volume, adv20Window)
     */
    @Builder.Default
    private int adv20Window = 20;

    /**
     * 创建默认配置（全部101个因子）
     */
    public static Alpha101Config createDefault() {
        return Alpha101Config.builder()
            .includeAlphas(null) // null表示全部
            .excludeAlphas(new ArrayList<>())
            .enableAdvancedAlphas(true)
            .adv20Window(20)
            .build();
    }

    /**
     * 创建快速配置（排除计算复杂的因子）
     */
    public static Alpha101Config createFast() {
        // 排除一些计算复杂度高的因子
        List<Integer> exclude = Arrays.asList(
            7, 17, 19, 21, 23, 24, // 包含复杂条件判断
            48, 49, 50, 51, 52, 53, // 计算复杂
            84, 85, 86, 87, 88, 89, // 高级因子
            98, 99, 100, 101 // 最复杂的因子
        );
        return Alpha101Config.builder()
            .includeAlphas(null)
            .excludeAlphas(exclude)
            .enableAdvancedAlphas(false)
            .adv20Window(20)
            .build();
    }

    /**
     * 创建自定义配置
     */
    public static Alpha101Config create(List<Integer> includeAlphas) {
        return Alpha101Config.builder()
            .includeAlphas(includeAlphas)
            .excludeAlphas(new ArrayList<>())
            .enableAdvancedAlphas(true)
            .adv20Window(20)
            .build();
    }

    /**
     * 判断是否使用某个Alpha因子
     */
    public boolean useAlpha(int alphaNumber) {
        if (alphaNumber < 1 || alphaNumber > 101) {
            return false;
        }

        if (excludeAlphas != null && excludeAlphas.contains(alphaNumber)) {
            return false;
        }

        if (includeAlphas == null) {
            return true;
        }

        return includeAlphas.contains(alphaNumber);
    }

    /**
     * 获取期望的因子数量
     *
     * ✅ 正确处理 includeAlphas 和 excludeAlphas 的组合
     */
    public int getExpectedFactorCount() {
        int count = 0;
        for (int i = 1; i <= 101; i++) {
            if (useAlpha(i)) {
                count++;
            }
        }
        return count;
    }
}

