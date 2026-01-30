package com.whaleal.quant.alpha;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Alpha因子特征向量
 *
 * ⚠️⚠️⚠️ 极其重要：该类保证传递给AI模型的特征向量顺序与Qlib完全一致 ⚠️⚠️⚠️
 *
 * 使用场景：
 * 1. 模型训练：提取特征向量用于训练
 * 2. 模型预测：提取特征向量用于预测
 * 3. 特征工程：批量处理特征数据
 *
 * 核心保证：
 * - 因子顺序与Qlib Alpha158/Alpha360完全一致
 * - 提供多种格式输出（double[], float[], List<Double>）
 * - 内置验证机制，防止顺序错误
 * - 支持缺失值处理
 *
 * @author arkmsg
 */
@Getter
public class AlphaFeatureVector {

    /**
     * 股票代码
     */
    private final String symbol;

    /**
     * 时间戳
     */
    private final Long timestamp;

    /**
     * 因子名称列表（严格顺序）
     * ⚠️ 该顺序必须与Qlib完全一致
     */
    private final List<String> factorNames;

    /**
     * 因子值数组（与factorNames一一对应）
     * ⚠️ 该数组的顺序决定了传递给AI模型的特征顺序
     */
    private final double[] values;

    /**
     * 因子类型（Alpha158 或 Alpha360）
     */
    private final AlphaType alphaType;

    /**
     * 构造函数
     *
     * @param symbol 股票代码
     * @param timestamp 时间戳
     * @param factorNames 因子名称列表（严格顺序）
     * @param values 因子值数组（与factorNames对应）
     * @param alphaType 因子类型
     */
    public AlphaFeatureVector(String symbol, Long timestamp, List<String> factorNames,
                             double[] values, AlphaType alphaType) {
        // 参数验证
        if (factorNames == null || values == null) {
            throw new IllegalArgumentException("因子名称和值不能为null");
        }
        if (factorNames.size() != values.length) {
            throw new IllegalArgumentException(
                String.format("因子名称数量(%d)与值数量(%d)不一致", factorNames.size(), values.length)
            );
        }

        this.symbol = symbol;
        this.timestamp = timestamp;
        this.factorNames = List.copyOf(factorNames); // 不可变副本
        this.values = Arrays.copyOf(values, values.length); // 防御性复制
        this.alphaType = alphaType;
    }

    /**
     * 从AlphaFactorResult创建特征向量
     *
     * ⚠️ 该方法保证使用正确的因子顺序
     *
     * @param result Alpha因子计算结果
     * @param alphaType 因子类型
     * @return 特征向量
     */
    public static AlphaFeatureVector fromAlphaFactorResult(AlphaFactorResult result, AlphaType alphaType) {
        List<String> factorNames = result.getFactorNames();
        double[] values = result.toDoubleArray();

        return new AlphaFeatureVector(
            result.getSymbol(),
            result.getTimestamp(),
            factorNames,
            values,
            alphaType
        );
    }

    /**
     * 获取特征向量（double数组）
     *
     * ⚠️ 该方法返回的数组顺序与Qlib完全一致，可直接传递给AI模型
     *
     * @return 特征值数组
     */
    public double[] toDoubleArray() {
        return Arrays.copyOf(values, values.length);
    }

    /**
     * 获取特征向量（float数组）
     *
     * 某些ML框架（如TensorFlow）使用float32
     *
     * @return 特征值数组（float类型）
     */
    public float[] toFloatArray() {
        float[] floats = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            floats[i] = (float) values[i];
        }
        return floats;
    }

    /**
     * 获取特征向量（List<Double>）
     *
     * @return 特征值列表
     */
    public List<Double> toList() {
        return Arrays.stream(values).boxed().toList();
    }

    /**
     * 获取指定位置的因子值
     *
     * @param index 因子索引（从0开始）
     * @return 因子值
     */
    public double getValue(int index) {
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException(
                String.format("因子索引%d超出范围[0, %d)", index, values.length)
            );
        }
        return values[index];
    }

    /**
     * 获取指定名称的因子值
     *
     * @param factorName 因子名称
     * @return 因子值
     */
    public double getValue(String factorName) {
        int index = factorNames.indexOf(factorName);
        if (index < 0) {
            throw new IllegalArgumentException("因子不存在: " + factorName);
        }
        return values[index];
    }

    /**
     * 获取特征维度
     *
     * @return 特征数量
     */
    public int getDimension() {
        return values.length;
    }

    /**
     * 验证因子顺序是否与预期一致
     *
     * ⚠️ 训练和预测前务必调用此方法验证！
     *
     * @param expectedOrder 期望的因子顺序
     * @return 是否一致
     */
    public boolean validateOrder(List<String> expectedOrder) {
        if (factorNames.size() != expectedOrder.size()) {
            return false;
        }
        for (int i = 0; i < factorNames.size(); i++) {
            if (!factorNames.get(i).equals(expectedOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 严格验证因子顺序，不一致时抛出异常
     *
     * @param expectedOrder 期望的因子顺序
     * @throws IllegalStateException 如果顺序不一致
     */
    public void validateOrderStrict(List<String> expectedOrder) {
        if (!validateOrder(expectedOrder)) {
            // 找出第一个不一致的位置
            int errorIndex = -1;
            for (int i = 0; i < Math.min(factorNames.size(), expectedOrder.size()); i++) {
                if (!factorNames.get(i).equals(expectedOrder.get(i))) {
                    errorIndex = i;
                    break;
                }
            }

            String errorMsg;
            if (errorIndex >= 0) {
                errorMsg = String.format(
                    "因子顺序不一致！位置%d: 期望'%s'，实际'%s'",
                    errorIndex,
                    expectedOrder.get(errorIndex),
                    factorNames.get(errorIndex)
                );
            } else {
                errorMsg = String.format(
                    "因子数量不一致！期望%d个，实际%d个",
                    expectedOrder.size(),
                    factorNames.size()
                );
            }

            throw new IllegalStateException(errorMsg);
        }
    }

    /**
     * 检查是否包含NaN或无穷大
     *
     * @return 是否包含非法值
     */
    public boolean hasInvalidValues() {
        for (double value : values) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取非法值的位置
     *
     * @return 非法值的索引列表
     */
    public List<Integer> getInvalidValueIndices() {
        return java.util.stream.IntStream.range(0, values.length)
            .filter(i -> Double.isNaN(values[i]) || Double.isInfinite(values[i]))
            .boxed()
            .collect(Collectors.toList());
    }

    /**
     * 获取非法值的详细信息（包含因子名称）
     *
     * @return 因子名称到值的映射（只包含非法值）
     */
    public Map<String, Double> getInvalidValuesDetail() {
        Map<String, Double> invalidMap = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i++) {
            if (Double.isNaN(values[i]) || Double.isInfinite(values[i])) {
                invalidMap.put(factorNames.get(i), values[i]);
            }
        }
        return invalidMap;
    }

    /**
     * 获取非法值的统计信息
     *
     * @return 统计信息字符串
     */
    public String getInvalidValuesStatistics() {
        int nanCount = 0;
        int infCount = 0;

        for (double value : values) {
            if (Double.isNaN(value)) {
                nanCount++;
            } else if (Double.isInfinite(value)) {
                infCount++;
            }
        }

        return String.format("NaN: %d (%.2f%%), Infinity: %d (%.2f%%), Total: %d / %d",
            nanCount, nanCount * 100.0 / values.length,
            infCount, infCount * 100.0 / values.length,
            nanCount + infCount, values.length);
    }

    /**
     * 填充缺失值（NaN和无穷大）
     *
     * @param fillValue 填充值（默认0.0）
     * @return 新的特征向量
     */
    public AlphaFeatureVector fillInvalidValues(double fillValue) {
        double[] newValues = Arrays.copyOf(values, values.length);
        for (int i = 0; i < newValues.length; i++) {
            if (Double.isNaN(newValues[i]) || Double.isInfinite(newValues[i])) {
                newValues[i] = fillValue;
            }
        }
        return new AlphaFeatureVector(symbol, timestamp, factorNames, newValues, alphaType);
    }

    /**
     * 使用前向填充策略填充NaN
     *
     * 从前往后遍历，用前一个有效值填充NaN
     * 如果第一个值就是NaN，则使用指定的初始值
     *
     * @param initialValue 如果第一个值是NaN时使用的初始值
     * @return 新的特征向量
     */
    public AlphaFeatureVector fillNaNForward(double initialValue) {
        double[] newValues = Arrays.copyOf(values, values.length);
        double lastValid = initialValue;

        for (int i = 0; i < newValues.length; i++) {
            if (Double.isNaN(newValues[i]) || Double.isInfinite(newValues[i])) {
                newValues[i] = lastValid;
            } else {
                lastValid = newValues[i];
            }
        }

        return new AlphaFeatureVector(symbol, timestamp, factorNames, newValues, alphaType);
    }

    /**
     * 使用后向填充策略填充NaN
     *
     * 从后往前遍历，用后一个有效值填充NaN
     * 如果最后一个值就是NaN，则使用指定的初始值
     *
     * @param initialValue 如果最后一个值是NaN时使用的初始值
     * @return 新的特征向量
     */
    public AlphaFeatureVector fillNaNBackward(double initialValue) {
        double[] newValues = Arrays.copyOf(values, values.length);
        double lastValid = initialValue;

        for (int i = newValues.length - 1; i >= 0; i--) {
            if (Double.isNaN(newValues[i]) || Double.isInfinite(newValues[i])) {
                newValues[i] = lastValid;
            } else {
                lastValid = newValues[i];
            }
        }

        return new AlphaFeatureVector(symbol, timestamp, factorNames, newValues, alphaType);
    }

    /**
     * 使用均值填充NaN
     *
     * 计算所有非NaN值的均值，用均值填充NaN
     * 如果所有值都是NaN，则使用0.0
     *
     * @return 新的特征向量
     */
    public AlphaFeatureVector fillNaNWithMean() {
        double[] newValues = Arrays.copyOf(values, values.length);

        // 计算均值
        double sum = 0.0;
        int count = 0;
        for (double value : values) {
            if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                sum += value;
                count++;
            }
        }
        double mean = count > 0 ? sum / count : 0.0;

        // 填充NaN
        for (int i = 0; i < newValues.length; i++) {
            if (Double.isNaN(newValues[i]) || Double.isInfinite(newValues[i])) {
                newValues[i] = mean;
            }
        }

        return new AlphaFeatureVector(symbol, timestamp, factorNames, newValues, alphaType);
    }

    /**
     * 使用中位数填充NaN
     *
     * 计算所有非NaN值的中位数，用中位数填充NaN
     * 如果所有值都是NaN，则使用0.0
     *
     * @return 新的特征向量
     */
    public AlphaFeatureVector fillNaNWithMedian() {
        double[] newValues = Arrays.copyOf(values, values.length);

        // 收集有效值
        List<Double> validValues = new ArrayList<>();
        for (double value : values) {
            if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                validValues.add(value);
            }
        }

        // 计算中位数
        double median = 0.0;
        if (!validValues.isEmpty()) {
            Collections.sort(validValues);
            int size = validValues.size();
            if (size % 2 == 0) {
                median = (validValues.get(size / 2 - 1) + validValues.get(size / 2)) / 2.0;
            } else {
                median = validValues.get(size / 2);
            }
        }

        // 填充NaN
        for (int i = 0; i < newValues.length; i++) {
            if (Double.isNaN(newValues[i]) || Double.isInfinite(newValues[i])) {
                newValues[i] = median;
            }
        }

        return new AlphaFeatureVector(symbol, timestamp, factorNames, newValues, alphaType);
    }

    /**
     * 归一化（Z-Score）
     *
     * @return 归一化后的特征向量
     */
    public AlphaFeatureVector normalize() {
        // 计算均值
        double mean = Arrays.stream(values).average().orElse(0.0);

        // 计算标准差
        double variance = Arrays.stream(values)
            .map(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        double std = Math.sqrt(variance);

        // 归一化
        double[] normalized = new double[values.length];
        if (std > 1e-12) {
            for (int i = 0; i < values.length; i++) {
                normalized[i] = (values[i] - mean) / std;
            }
        } else {
            // 标准差太小，返回0
            Arrays.fill(normalized, 0.0);
        }

        return new AlphaFeatureVector(symbol, timestamp, factorNames, normalized, alphaType);
    }

    /**
     * 转换为CSV格式（用于数据导出）
     *
     * @param includeHeader 是否包含表头
     * @return CSV字符串
     */
    public String toCSV(boolean includeHeader) {
        StringBuilder sb = new StringBuilder();

        if (includeHeader) {
            sb.append("symbol,timestamp,");
            sb.append(String.join(",", factorNames));
            sb.append("\n");
        }

        sb.append(symbol).append(",");
        sb.append(timestamp).append(",");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
            "AlphaFeatureVector{symbol='%s', timestamp=%d, type=%s, dimension=%d, " +
            "firstValue=%.6f, lastValue=%.6f, hasInvalidValues=%s}",
            symbol, timestamp, alphaType, values.length,
            values.length > 0 ? values[0] : 0.0,
            values.length > 0 ? values[values.length - 1] : 0.0,
            hasInvalidValues()
        );
    }

    /**
     * Alpha类型枚举
     */
    public enum AlphaType {
        /** Alpha158（158个因子） */
        ALPHA158,

        /** Alpha360（360个因子） */
        ALPHA360,

        /** 自定义 */
        CUSTOM
    }
}

