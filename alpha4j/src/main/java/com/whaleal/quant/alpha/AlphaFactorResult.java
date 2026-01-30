package com.whaleal.quant.alpha;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Alpha因子计算结果
 *
 * ⚠️ 重要：因子顺序对AI训练至关重要！
 * ⚠️ 重要变更：所有因子值使用double类型，提升性能10-100倍
 *
 * 使用LinkedHashMap和List双重保证顺序：
 * - LinkedHashMap：方便按名称查找因子值
 * - List<String>：严格保持因子顺序，用于生成特征向量
 *
 * Alpha158因子顺序（共158个）：
 * 1. K线形态因子（9个）：KMID, KLEN, KMID2, KUP, KUP2, KLOW, KLOW2, KSFT, KSFT2
 * 2. 价格因子（4个）：OPEN0, HIGH0, LOW0, VWAP0
 * 3. 滚动统计因子（145个）：按算子类型 × 窗口期 [5,10,20,30,60]
 *
 * Alpha360因子顺序（共360个）：
 * 1. CLOSE系列（60个）：CLOSE59, CLOSE58, ..., CLOSE1, CLOSE0
 * 2. OPEN系列（60个）：OPEN59, OPEN58, ..., OPEN1, OPEN0
 * 3. HIGH系列（60个）：HIGH59, HIGH58, ..., HIGH1, HIGH0
 * 4. LOW系列（60个）：LOW59, LOW58, ..., LOW1, LOW0
 * 5. VWAP系列（60个）：VWAP59, VWAP58, ..., VWAP1, VWAP0
 * 6. VOLUME系列（60个）：VOLUME59, VOLUME58, ..., VOLUME1, VOLUME0
 *
 * @author arkmsg
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlphaFactorResult {

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 因子名称到值的映射（使用double提升性能）
     */
    @Builder.Default
    private Map<String, Double> factors = new LinkedHashMap<>();

    /**
     * 因子名称顺序列表（严格保持顺序，用于生成特征向量）
     * ⚠️ 该顺序必须与Qlib完全一致！
     */
    @Builder.Default
    private List<String> factorOrder = new ArrayList<>();

    /**
     * 添加单个因子（保持顺序）
     */
    public void addFactor(String name, double value) {
        if (!factors.containsKey(name)) {
            factorOrder.add(name);
        }
        factors.put(name, value);
    }

    /**
     * 批量添加因子
     */
    public void addFactors(Map<String, Double> newFactors) {
        for (Map.Entry<String, Double> entry : newFactors.entrySet()) {
            addFactor(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 批量添加因子（从Map）
     */
    public void addFactorsFromMap(Map<String, Double> newFactors) {
        addFactors(newFactors);
    }

    /**
     * 按指定顺序添加因子
     */
    public void addFactorsInOrder(List<String> names, List<Double> values) {
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("因子名称和值的数量必须一致");
        }
        for (int i = 0; i < names.size(); i++) {
            addFactor(names.get(i), values.get(i));
        }
    }

    /**
     * 获取因子值
     */
    public Double getFactor(String name) {
        return factors.get(name);
    }

    /**
     * 获取因子数量
     */
    public int getFactorCount() {
        return factors.size();
    }

    /**
     * 转换为double数组（严格按factorOrder顺序，用于AI训练）
     *
     * ⚠️ 极其重要：该方法返回的数组顺序与Qlib完全一致！
     */
    public double[] toDoubleArray() {
        double[] result = new double[factorOrder.size()];
        for (int i = 0; i < factorOrder.size(); i++) {
            String name = factorOrder.get(i);
            Double value = factors.get(name);
            result[i] = (value != null) ? value : 0.0;
        }
        return result;
    }

    /**
     * 转换为AlphaFeatureVector（推荐使用）
     */
    public AlphaFeatureVector toFeatureVector(AlphaFeatureVector.AlphaType alphaType) {
        return AlphaFeatureVector.fromAlphaFactorResult(this, alphaType);
    }

    /**
     * 转换为float数组
     */
    public float[] toFloatArray() {
        float[] result = new float[factorOrder.size()];
        for (int i = 0; i < factorOrder.size(); i++) {
            String name = factorOrder.get(i);
            Double value = factors.get(name);
            result[i] = (value != null) ? value.floatValue() : 0.0f;
        }
        return result;
    }

    /**
     * 转换为特征向量Map
     */
    public Map<String, Double> toFeatureVector() {
        Map<String, Double> features = new LinkedHashMap<>();
        for (String name : factorOrder) {
            Double value = factors.get(name);
            features.put(name, value != null ? value : 0.0);
        }
        return features;
    }

    /**
     * 获取因子名称列表（按顺序）
     */
    public List<String> getFactorNames() {
        return new ArrayList<>(factorOrder);
    }

    /**
     * 验证因子顺序
     */
    public boolean validateOrder(List<String> expectedOrder) {
        if (factorOrder.size() != expectedOrder.size()) {
            return false;
        }
        for (int i = 0; i < factorOrder.size(); i++) {
            if (!factorOrder.get(i).equals(expectedOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlphaFactorResult{");
        sb.append("symbol='").append(symbol).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", factorCount=").append(factors.size());
        sb.append(", factors=[");

        int count = 0;
        for (Map.Entry<String, Double> entry : factors.entrySet()) {
            if (count > 0) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            count++;
            if (count >= 5) {
                sb.append(", ... (").append(factors.size() - 5).append(" more)");
                break;
            }
        }

        sb.append("]}");
        return sb.toString();
    }
}
