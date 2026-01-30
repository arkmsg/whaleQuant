package com.whaleal.quant.alpha;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

    /**
     * Alpha158因子配置类
     *
     * ⚠️ 重要设计：始终保持159个因子，顺序固定
     * - 9个K线形态因子 (KMID, KLEN, KMID2, KUP, KUP2, KLOW, KLOW2, KSFT, KSFT2)
     * - 4个价格因子 (OPEN0, HIGH0, LOW0, VWAP0)
     * - 1个成交量因子 (VOLUME0)
     * - 145个滚动统计因子 (29种算子 × 5窗口)
     *
     * 排除机制：
     * - 被排除的算子用NaN填充（例如：RANK5=NaN, RANK10=NaN等）
     * - 因子数量和顺序始终不变（159个）
     * - 训练和预测天然兼容
     *
     * 参考标准：Qlib Alpha158（排除冗余算子，用NaN填充）
     *
     * @author arkmsg
     */
@Data
@Builder
public class Alpha158Config {

    /**
     * 是否启用K线形态因子（9个）
     */
    @Builder.Default
    private boolean enableKbar = true;

    /**
     * 是否启用价格因子
     */
    @Builder.Default
    private boolean enablePrice = true;

    /**
     * 价格因子的窗口期（默认只有0，即当天）
     */
    @Builder.Default
    private List<Integer> priceWindows = Arrays.asList(0);

    /**
     * 价格因子的字段（OPEN, HIGH, LOW, CLOSE, VWAP）
     */
    @Builder.Default
    private List<String> priceFeatures = Arrays.asList("OPEN", "HIGH", "LOW", "VWAP");

    /**
     * 是否启用成交量因子
     */
    @Builder.Default
    private boolean enableVolume = false;

    /**
     * 成交量因子的窗口期
     */
    @Builder.Default
    private List<Integer> volumeWindows = Arrays.asList(0);

    /**
     * 是否启用滚动统计因子
     */
    @Builder.Default
    private boolean enableRolling = true;

    /**
     * 滚动统计的窗口期（默认5,10,20,30,60天）
     */
    @Builder.Default
    private List<Integer> rollingWindows = Arrays.asList(5, 10, 20, 30, 60);

    /**
     * 包含的滚动统计算子（null表示全部）
     */
    private List<String> rollingInclude;

    /**
     * 排除的滚动统计算子
     *
     * ⚠️ 重要设计：排除的算子会用NaN填充，保持因子顺序和数量不变
     *
     * 默认排除5个冗余算子（与Qlib的Alpha158对齐）：
     * - RANK: 排名因子，信息可通过分位数获得
     * - IMXD: 极值索引差，可通过IMAX-IMIN计算
     * - CORD: 收益率相关性，与CORR高度相关
     * - CNTD: 涨跌天数差，可通过CNTP-CNTN计算
     * - VSUMD: 成交量差值和，可通过VSUMP-VSUMN计算
     *
     * 排除后的效果：
     * - 因子总数：始终159个（29算子×5窗口 + K线9 + 价格4 + 成交量1）
     * - 被排除因子：填充为NaN（例如：RANK5=NaN, RANK10=NaN, ...）
     * - 因子顺序：完全固定，不受排除影响
     */
    @Builder.Default
    private List<String> rollingExclude = Arrays.asList("RANK", "IMXD", "CORD", "CNTD", "VSUMD"); // 默认排除5个冗余算子

    /**
     * 创建默认配置（Qlib标准，排除5个冗余算子）
     *
     * 因子构成：
     * - K线形态：9个
     * - 价格因子：4个 (OPEN0, HIGH0, LOW0, VWAP0)
     * - 成交量因子：1个 (VOLUME0)
     * - 滚动统计：29×5 = 145个（其中5个算子×5窗口=25个用NaN填充）
     *
     * ⚠️ 重要：因子总数始终为159个，被排除的25个因子用NaN填充
     *
     * 总计：9 + 4 + 1 + 145 = 159个因子（其中25个为NaN填充）✅
     */
    public static Alpha158Config createDefault() {
        return Alpha158Config.builder()
            .enableKbar(true)
            .enablePrice(true)
            .priceWindows(Arrays.asList(0))
            .priceFeatures(Arrays.asList("OPEN", "HIGH", "LOW", "VWAP"))
            .enableVolume(true)  // ✅ 启用成交量因子
            .volumeWindows(Arrays.asList(0))
            .enableRolling(true)
            .rollingWindows(Arrays.asList(5, 10, 20, 30, 60))
            .rollingInclude(null)
            .rollingExclude(Arrays.asList("RANK", "IMXD", "CORD", "CNTD", "VSUMD")) // 排除5个冗余算子
            .build();
    }

    /**
     * 创建完整配置（所有因子，无排除）
     *
     * 包含所有29个算子的完整实现
     * 结果：9 + 4 + 1 + 29×5 = 9 + 4 + 1 + 145 = 159个因子
     */
    public static Alpha158Config createFull() {
        return Alpha158Config.builder()
            .enableKbar(true)
            .enablePrice(true)
            .priceWindows(Arrays.asList(0))
            .priceFeatures(Arrays.asList("OPEN", "HIGH", "LOW", "VWAP"))
            .enableVolume(true)  // ✅ 启用成交量
            .volumeWindows(Arrays.asList(0))
            .enableRolling(true)
            .rollingWindows(Arrays.asList(5, 10, 20, 30, 60))
            .rollingInclude(null)
            .rollingExclude(Arrays.asList()) // 不排除任何算子
            .build();
    }

    /**
     * 创建扩展配置（包含价格历史窗口因子）
     *
     * ⚠️ 重要设计：179 = 159 + 20
     *
     * 因子构成（严格顺序）：
     * 【前159个 - 与标准配置相同】
     * - K线形态：9个
     * - 价格因子：4个 (OPEN0, HIGH0, LOW0, VWAP0)
     * - 成交量因子：1个 (VOLUME0)
     * - 滚动统计：145个（29×5，排除算子用NaN填充）
     *
     * 【后20个 - 价格历史窗口】
     * - OPEN5, OPEN10, OPEN20, OPEN30, OPEN60     (5个)
     * - HIGH5, HIGH10, HIGH20, HIGH30, HIGH60     (5个)
     * - LOW5, LOW10, LOW20, LOW30, LOW60          (5个)
     * - VWAP5, VWAP10, VWAP20, VWAP30, VWAP60     (5个)
     *
     * 总计：159 + 20 = 179个因子 ✅
     *
     * 优势：
     * - ✅ 向后兼容159配置（前159个完全相同）
     * - ✅ 不影响原有模型训练
     * - ✅ 可选添加更丰富的价格历史信息
     * - ✅ 因子顺序固定，排除用NaN填充
     */
    public static Alpha158Config createExtended() {
        return Alpha158Config.builder()
            .enableKbar(true)
            .enablePrice(true)
            .priceWindows(Arrays.asList(0, 5, 10, 20, 30, 60))  // ← 扩展到6个窗口
            .priceFeatures(Arrays.asList("OPEN", "HIGH", "LOW", "VWAP"))
            .enableVolume(true)
            .volumeWindows(Arrays.asList(0))  // ← 成交量保持只用当天
            .enableRolling(true)
            .rollingWindows(Arrays.asList(5, 10, 20, 30, 60))
            .rollingInclude(null)
            .rollingExclude(Arrays.asList("RANK", "IMXD", "CORD", "CNTD", "VSUMD")) // 排除5个冗余算子
            .build();
    }

    /**
     * 创建精简配置（只包含核心因子，用于快速计算）
     */
    public static Alpha158Config createLite() {
        return Alpha158Config.builder()
            .enableKbar(true)
            .enablePrice(true)
            .priceWindows(Arrays.asList(0))
            .priceFeatures(Arrays.asList("OPEN", "HIGH", "LOW", "VWAP"))
            .enableVolume(false)
            .enableRolling(true)
            .rollingWindows(Arrays.asList(5, 10, 20, 60)) // 减少窗口
            .rollingInclude(Arrays.asList("ROC", "MA", "STD", "MAX", "MIN", "RSV")) // 只包含核心算子
            .build();
    }

    /**
     * 判断是否使用某个滚动统计算子
     */
    public boolean useRollingOperator(String operator) {
        if (rollingExclude != null && rollingExclude.contains(operator)) {
            return false;
        }
        if (rollingInclude == null) {
            return true;
        }
        return rollingInclude.contains(operator);
    }
}

