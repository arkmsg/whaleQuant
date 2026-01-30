package com.whaleal.quant.alpha;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Alpha因子数据集
 *
 * ⚠️⚠️⚠️ 用于批量管理特征向量，保证传递给AI模型的数据格式正确 ⚠️⚠️⚠️
 *
 * 使用场景：
 * 1. 模型训练：构建训练集/验证集/测试集
 * 2. 批量预测：批处理多只股票的特征
 * 3. 数据导出：导出为CSV、NumPy数组等格式
 *
 * @author arkmsg
 */
@Getter
public class AlphaDataset {

    /**
     * 特征向量列表
     */
    private final List<AlphaFeatureVector> features;

    /**
     * 因子类型
     */
    private final AlphaFeatureVector.AlphaType alphaType;

    /**
     * 期望的因子顺序（用于验证）
     */
    private final List<String> expectedFactorOrder;

    /**
     * 构造函数
     *
     * @param alphaType 因子类型
     * @param expectedFactorOrder 期望的因子顺序
     */
    public AlphaDataset(AlphaFeatureVector.AlphaType alphaType, List<String> expectedFactorOrder) {
        this.features = new ArrayList<>();
        this.alphaType = alphaType;
        this.expectedFactorOrder = List.copyOf(expectedFactorOrder);
    }

    /**
     * 创建Alpha158数据集
     */
    public static AlphaDataset createAlpha158() {
        List<String> order = Alpha158FactorOrder.getDefaultOrder();
        return new AlphaDataset(AlphaFeatureVector.AlphaType.ALPHA158, order);
    }

    /**
     * 创建Alpha360数据集
     */
    public static AlphaDataset createAlpha360() {
        List<String> order = Alpha360FactorOrder.getDefaultOrder();
        return new AlphaDataset(AlphaFeatureVector.AlphaType.ALPHA360, order);
    }

    /**
     * 添加特征向量
     *
     * ⚠️ 自动验证因子顺序，不一致时抛出异常
     *
     * @param feature 特征向量
     */
    public void addFeature(AlphaFeatureVector feature) {
        // 验证因子类型
        if (feature.getAlphaType() != alphaType) {
            throw new IllegalArgumentException(
                String.format("因子类型不匹配！期望%s，实际%s", alphaType, feature.getAlphaType())
            );
        }

        // 严格验证因子顺序
        feature.validateOrderStrict(expectedFactorOrder);

        features.add(feature);
    }

    /**
     * 批量添加特征向量
     *
     * @param featureList 特征向量列表
     */
    public void addFeatures(List<AlphaFeatureVector> featureList) {
        for (AlphaFeatureVector feature : featureList) {
            addFeature(feature);
        }
    }

    /**
     * 获取数据集大小
     *
     * @return 样本数量
     */
    public int size() {
        return features.size();
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return features.isEmpty();
    }

    /**
     * 获取特征维度
     *
     * @return 特征数量
     */
    public int getDimension() {
        return expectedFactorOrder.size();
    }

    /**
     * 转换为二维数组（用于模型训练）
     *
     * ⚠️ 返回的数组格式：[样本数量][特征维度]
     * ⚠️ 每一行都严格按照Qlib的因子顺序排列
     *
     * @return 特征矩阵
     */
    public double[][] toArray() {
        double[][] array = new double[features.size()][];
        for (int i = 0; i < features.size(); i++) {
            array[i] = features.get(i).toDoubleArray();
        }
        return array;
    }

    /**
     * 转换为二维float数组
     *
     * @return 特征矩阵（float类型）
     */
    public float[][] toFloatArray() {
        float[][] array = new float[features.size()][];
        for (int i = 0; i < features.size(); i++) {
            array[i] = features.get(i).toFloatArray();
        }
        return array;
    }

    /**
     * 转换为一维数组（展平）
     *
     * 用于某些需要展平数据的场景
     *
     * @return 展平后的数组
     */
    public double[] toFlatArray() {
        int totalSize = features.size() * getDimension();
        double[] flat = new double[totalSize];

        for (int i = 0; i < features.size(); i++) {
            double[] row = features.get(i).toDoubleArray();
            System.arraycopy(row, 0, flat, i * getDimension(), getDimension());
        }

        return flat;
    }

    /**
     * 获取所有股票代码
     *
     * @return 股票代码列表
     */
    public List<String> getSymbols() {
        return features.stream()
            .map(AlphaFeatureVector::getSymbol)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有时间戳
     *
     * @return 时间戳列表
     */
    public List<Long> getTimestamps() {
        return features.stream()
            .map(AlphaFeatureVector::getTimestamp)
            .collect(Collectors.toList());
    }

    /**
     * 检查是否包含非法值
     *
     * @return 是否包含非法值
     */
    public boolean hasInvalidValues() {
        return features.stream().anyMatch(AlphaFeatureVector::hasInvalidValues);
    }

    /**
     * 填充所有特征向量的缺失值
     *
     * @param fillValue 填充值
     * @return 新的数据集
     */
    public AlphaDataset fillInvalidValues(double fillValue) {
        AlphaDataset newDataset = new AlphaDataset(alphaType, expectedFactorOrder);
        for (AlphaFeatureVector feature : features) {
            newDataset.addFeature(feature.fillInvalidValues(fillValue));
        }
        return newDataset;
    }

    /**
     * 使用指定策略处理所有特征向量的NaN
     *
     * @param strategy NaN处理策略
     * @return 新的数据集
     */
    public AlphaDataset handleNaN(NaNHandlingStrategy strategy) {
        AlphaDataset newDataset = new AlphaDataset(alphaType, expectedFactorOrder);
        for (AlphaFeatureVector feature : features) {
            newDataset.addFeature(strategy.apply(feature));
        }
        return newDataset;
    }

    /**
     * 获取包含非法值的样本索引
     *
     * @return 样本索引列表
     */
    public List<Integer> getSamplesWithInvalidValues() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            if (features.get(i).hasInvalidValues()) {
                indices.add(i);
            }
        }
        return indices;
    }

    /**
     * 获取非法值的统计信息
     *
     * @return 统计信息字符串
     */
    public String getInvalidValuesStatistics() {
        int samplesWithNaN = 0;
        int totalNaN = 0;

        for (AlphaFeatureVector feature : features) {
            if (feature.hasInvalidValues()) {
                samplesWithNaN++;
                totalNaN += feature.getInvalidValueIndices().size();
            }
        }

        return String.format(
            "Samples with NaN: %d / %d (%.2f%%), Total NaN values: %d / %d (%.2f%%)",
            samplesWithNaN, features.size(),
            samplesWithNaN * 100.0 / Math.max(features.size(), 1),
            totalNaN, features.size() * getDimension(),
            totalNaN * 100.0 / Math.max(features.size() * getDimension(), 1)
        );
    }

    /**
     * 归一化所有特征向量
     *
     * @return 新的数据集
     */
    public AlphaDataset normalize() {
        AlphaDataset newDataset = new AlphaDataset(alphaType, expectedFactorOrder);
        for (AlphaFeatureVector feature : features) {
            newDataset.addFeature(feature.normalize());
        }
        return newDataset;
    }

    /**
     * 导出为CSV格式
     *
     * @param includeHeader 是否包含表头
     * @return CSV字符串
     */
    public String toCSV(boolean includeHeader) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < features.size(); i++) {
            if (i == 0) {
                sb.append(features.get(i).toCSV(includeHeader));
            } else {
                sb.append(features.get(i).toCSV(false));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * 打印数据集信息
     */
    public void printInfo() {
        System.out.println("=== Alpha Dataset Info ===");
        System.out.println("Type: " + alphaType);
        System.out.println("Size: " + size() + " samples");
        System.out.println("Dimension: " + getDimension() + " features");
        System.out.println("Has Invalid Values: " + hasInvalidValues());

        if (!features.isEmpty()) {
            System.out.println("\nFirst Feature:");
            System.out.println("  " + features.get(0));

            System.out.println("\nLast Feature:");
            System.out.println("  " + features.get(features.size() - 1));
        }

        System.out.println("\nFactor Order (first 10):");
        for (int i = 0; i < Math.min(10, expectedFactorOrder.size()); i++) {
            System.out.printf("  %3d. %s%n", i + 1, expectedFactorOrder.get(i));
        }
        if (expectedFactorOrder.size() > 10) {
            System.out.println("  ...");
        }
    }

    @Override
    public String toString() {
        return String.format(
            "AlphaDataset{type=%s, size=%d, dimension=%d, hasInvalidValues=%s}",
            alphaType, size(), getDimension(), hasInvalidValues()
        );
    }
}

