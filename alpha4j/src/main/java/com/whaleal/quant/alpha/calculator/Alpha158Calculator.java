package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.*;
import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Alpha158因子计算器
 *
 * 整合K线因子、价格因子、成交量因子、滚动统计因子，生成完整的Alpha158特征向量
 *
 * 因子总数：158个
 * - K线因子：9个 (KMID, KLEN, KMID2, KUP, KUP2, KLOW, KLOW2, KSFT, KSFT2)
 * - 价格因子：20个 (OPEN, HIGH, LOW, VWAP，各5个窗口期：0, 5, 10, 20, 30, 60)
 * - 成交量因子：0个 (默认配置不启用)
 * - 滚动统计因子：145个 (29种算子 × 5个窗口期)
 *   - ROC5~60, MA5~60, STD5~60, BETA5~60, RSQR5~60
 *   - RESI5~60, MAX5~60, MIN5~60, QTLU5~60, QTLD5~60
 *   - RANK5~60, RSV5~60, IMAX5~60, IMIN5~60, IMXD5~60
 *   - CORR5~60, CORD5~60, CNTP5~60, CNTN5~60, CNTD5~60
 *   - SUMP5~60, SUMN5~60, SUMD5~60
 *   - VMA5~60, VSTD5~60, WVMA5~60, VSUMP5~60, VSUMN5~60, VSUMD5~60
 *
 * @author arkmsg
 */
@Slf4j
public class Alpha158Calculator {

    /**
     * 计算Alpha158因子（使用默认配置）
     *
     * @param data K线数据（建议至少70根，确保60窗口期有效）
     * @return Alpha因子结果
     */
    public static AlphaFactorResult calculate(List<Candlestick> data) {
        return calculate(data, Alpha158Config.createDefault());
    }

    /**
     * 计算Alpha158因子（使用自定义配置）
     *
     * @param data K线数据
     * @param config 配置
     * @return Alpha因子结果
     */
    public static AlphaFactorResult calculate(List<Candlestick> data, Alpha158Config config) {
        if (data == null || data.isEmpty()) {
            log.warn("K线数据为空，无法计算Alpha158因子");
            return null;
        }

        // 获取当前K线
        Candlestick current = data.get(data.size() - 1);
        String symbol = current.getSymbol();
        long timestamp = current.getTimestamp();

        // 创建结果对象
        AlphaFactorResult result = AlphaFactorResult.builder()
            .symbol(symbol)
            .timestamp(timestamp)
            .build();

        // 用于暂存价格历史窗口的变量
        List<Integer> priceHistoryWindows = null;

        // 1. 计算K线形态因子（9个）
        if (config.isEnableKbar()) {
            Map<String, Double> kbarFactors = KBarFactorCalculator.calculateAll(current);
            result.addFactorsFromMap(kbarFactors);
        }

        // 2. 计算价格因子（根据配置）
        // ⚠️ 重要：为了支持179配置（159+20），必须先处理window=0，历史窗口放在最后
        if (config.isEnablePrice()) {
            List<Integer> priceWindows = config.getPriceWindows();
            List<Integer> window0 = new ArrayList<>();
            List<Integer> historyWindows = new ArrayList<>();

            // 分离window=0和历史窗口
            for (Integer window : priceWindows) {
                if (window == 0) {
                    window0.add(window);
                } else {
                    historyWindows.add(window);
                }
            }

            // 先计算window=0的价格因子（OPEN0, HIGH0, LOW0, VWAP0）
            if (!window0.isEmpty()) {
                Map<String, Double> priceFactors0 = PriceFactorCalculator.calculateAll(
                data,
                    window0,
                config.getPriceFeatures()
            );
                result.addFactorsFromMap(priceFactors0);
            }

            // 历史窗口暂存，稍后在滚动统计因子之后添加
            if (!historyWindows.isEmpty()) {
                priceHistoryWindows = historyWindows;
            }
        }

        // 3. 计算成交量因子（根据配置）
        if (config.isEnableVolume()) {
            Map<String, Double> volumeFactors = VolumeFactorCalculator.calculateAll(
                data,
                config.getVolumeWindows()
            );
            result.addFactorsFromMap(volumeFactors);
        }

        // 4. 计算滚动统计因子（根据配置）
        // ⚠️ 重要：即使排除某些算子，也要保持因子顺序和数量不变，用NaN填充
        if (config.isEnableRolling()) {
            Map<String, Double> rollingFactors = RollingStatCalculator.calculateAll(
                data,
                config.getRollingWindows(),
                null  // 始终计算所有算子
            );

            // 对于被排除的算子，用NaN填充（保持顺序和维度不变）
            if (config.getRollingExclude() != null && !config.getRollingExclude().isEmpty()) {
                for (String excludedOp : config.getRollingExclude()) {
                    for (Integer window : config.getRollingWindows()) {
                        String factorName = excludedOp + window;
                        if (rollingFactors.containsKey(factorName)) {
                            rollingFactors.put(factorName, Double.NaN);  // 用NaN填充
                        }
                    }
                }
            }

            result.addFactorsFromMap(rollingFactors);
        }

        // 5. 计算价格历史窗口因子（如果有）
        // ⚠️ 重要：这些因子必须放在最后，确保179=159+20的设计
        if (priceHistoryWindows != null && !priceHistoryWindows.isEmpty()) {
            Map<String, Double> priceHistoryFactors = PriceFactorCalculator.calculateAll(
                data,
                priceHistoryWindows,
                config.getPriceFeatures()
            );
            result.addFactorsFromMap(priceHistoryFactors);
        }

        return result;
    }

    /**
     * 批量计算Alpha158因子（用于历史数据回测）
     *
     * @param data K线数据
     * @param config 配置
     * @return Alpha因子结果列表
     */
    public static AlphaDataset calculateBatch(List<Candlestick> data, Alpha158Config config) {
        if (data == null || data.isEmpty()) {
            log.warn("K线数据为空，无法批量计算Alpha158因子");
            return null;
        }

        // 创建数据集
        AlphaDataset dataset = new AlphaDataset(
            AlphaFeatureVector.AlphaType.ALPHA158,
            Alpha158FactorOrder.getFactorOrder(config)
        );

        // 需要的最小数据量
        int minDataSize = 70; // 60窗口期 + 10个缓冲

        if (data.size() < minDataSize) {
            log.warn("数据量不足，至少需要{}根K线，当前只有{}根", minDataSize, data.size());
            return dataset;
        }

        // 从第minDataSize根开始，逐个计算
        for (int i = minDataSize; i <= data.size(); i++) {
            List<Candlestick> window = data.subList(0, i);
            AlphaFactorResult factorResult = calculate(window, config);

            if (factorResult != null) {
                // 转换为特征向量并添加到数据集
                AlphaFeatureVector featureVector = factorResult.toFeatureVector(
                    AlphaFeatureVector.AlphaType.ALPHA158
                );
                dataset.addFeature(featureVector);
            }
        }

        return dataset;
    }

    /**
     * 计算单个K线的Alpha158因子（用于实时预测）
     *
     * @param historicalData 历史K线数据（至少60根）
     * @param newCandle 新K线
     * @param config 配置
     * @return Alpha因子结果
     */
    public static AlphaFactorResult calculateIncremental(
            List<Candlestick> historicalData,
            Candlestick newCandle,
            Alpha158Config config) {

        // 将新K线追加到历史数据
        List<Candlestick> allData = new java.util.ArrayList<>(historicalData);
        allData.add(newCandle);

        return calculate(allData, config);
    }
}

