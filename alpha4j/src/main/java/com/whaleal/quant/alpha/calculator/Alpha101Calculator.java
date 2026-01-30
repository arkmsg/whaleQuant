package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.Alpha101Config;
import com.whaleal.quant.alpha.Alpha101FactorOrder;
import com.whaleal.quant.alpha.AlphaFactorResult;
import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.whaleal.quant.alpha.calculator.Alpha101Operators.*;

/**
 * Alpha101因子计算器
 *
 * 实现WorldQuant Alpha101的全部101个因子
 * 使用模块化Group架构，基于alphas/alphas101.py Python实现
 *
 * 架构说明:
 * - Alpha#1-20:   Alpha101Group1 (已实现 - 基础因子组) ✅
 * - Alpha#21-40:  Alpha101Group2 (已实现 - 相关性和价量因子组) ✅
 * - Alpha#41-60:  Alpha101Group3 (部分实现 - 价量因子和复杂统计因子组) ⚠️
 * - Alpha#61-80:  Alpha101Group4 (部分实现 - 高级价量因子组) ⚠️
 * - Alpha#81-101: Alpha101Group5 (部分实现 - 综合因子组) ⚠️
 *
 * 实现状态:
 * - ✅ 已实现: 80/101 个因子 (79.2%)
 * - ❌ 未实现: 21/101 个因子 (20.8%)
 *
 * ⚠️ 未实现因子详情:
 *
 * 1️⃣ 需要行业中性化（IndNeutralize）的因子（19个）：
 *    - Group3: #58, #59（同时缺少公式）
 *    - Group4: #63, #67, #69, #70, #76, #79, #80
 *    - Group5: #82, #87, #89, #90, #91, #93, #97, #100
 *
 * 2️⃣ WorldQuant公开实现中缺失的因子（4个）：
 *    - Group3: #48, #56, #58, #59（后两个同时需要行业中性化）
 *
 * 📌 预留扩展:
 *    行业中性化（IndNeutralize）需要：
 *    - 行业分类数据（申万/中信/GICS等）
 *    - 批量股票数据（横截面计算）
 *    - 修改接口支持 Map<String, List<Candlestick>> 多股票计算
 *
 * 🔧 实现说明:
 *    详见 ALPHA101_UNIMPLEMENTED_FACTORS.md 文档
 *    每个未实现因子在对应的Group类中都有详细的注释说明
 *
 * @author arkmsg
 * @see Alpha101Group3 包含4个未实现因子的详细说明
 * @see Alpha101Group4 包含7个未实现因子的详细说明
 * @see Alpha101Group5 包含8个未实现因子的详细说明
 */
@Slf4j
public class Alpha101Calculator {

    private final Alpha101Config config;
    private static final double EPSILON = 1e-12;

    public Alpha101Calculator() {
        this.config = Alpha101Config.createDefault();
    }

    public Alpha101Calculator(Alpha101Config config) {
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.config = config;
    }

    /**
     * 计算所有Alpha101因子
     *
     * @param data K线数据，按时间升序排列，建议至少250根K线以支持所有Alpha
     * @return Alpha因子结果
     */
    public AlphaFactorResult calculate(List<Candlestick> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        // ⚠️ 数据长度警告
        if (data.size() < 60) {
            throw new IllegalArgumentException("Data size must be at least 60 for basic calculations");
        }

        if (data.size() < 250) {
            log.warn("Data size {} < 250, some Alpha factors (e.g. Alpha#19) may not be accurate", data.size());
        }

        Candlestick current = data.get(data.size() - 1);
        Map<String, Double> factors = new LinkedHashMap<>();
        List<String> factorOrder = Alpha101FactorOrder.getFactorOrder(config);

        // 提取基础数据
        List<Double> close = extractClose(data);
        List<Double> open = extractOpen(data);
        List<Double> high = extractHigh(data);
        List<Double> low = extractLow(data);
        List<Double> volume = extractVolume(data);
        List<Double> vwap = extractVwap(data);
        List<Double> returns = calculateReturns(data);

        // 计算adv20（20日平均成交量）
        List<Double> adv20 = sma(volume, config.getAdv20Window());

        // 计算每个Alpha因子
        for (int i = 1; i <= 101; i++) {
            if (!config.useAlpha(i)) {
                continue;
            }

            try {
                Double alphaValue = calculateAlpha(i, close, open, high, low, volume, vwap, returns, adv20);
                if (alphaValue != null) {
                    // ⚠️ 注意：未实现的因子返回 NaN，已实现但计算错误的返回 Infinity
                    // 保留 NaN 用于标识未实现的因子
                    if (Double.isInfinite(alphaValue)) {
                        // Infinity 视为计算错误，填充为 0.0
                        factors.put("alpha" + String.format("%03d", i), 0.0);
                    } else {
                        // 保留 NaN 和正常值
                        factors.put("alpha" + String.format("%03d", i), alphaValue);
                    }
                } else {
                    factors.put("alpha" + String.format("%03d", i), 0.0);
                }
            } catch (Exception e) {
                log.warn("Failed to calculate alpha{}: {}", i, e.getMessage());
                factors.put("alpha" + String.format("%03d", i), 0.0);
            }
        }

        return AlphaFactorResult.builder()
            .symbol(current.getSymbol())
            .timestamp(current.getTimestamp())
            .factors(factors)
            .factorOrder(factorOrder)  // ✅ 传入过滤后的因子顺序
            .build();
    }

    /**
     * 计算单个Alpha因子 - 使用Group模块化架构
     */
    private Double calculateAlpha(int alphaNumber, List<Double> close, List<Double> open,
                                  List<Double> high, List<Double> low, List<Double> volume,
                                  List<Double> vwap, List<Double> returns, List<Double> adv20) {

        // 根据Alpha编号路由到对应的Group
        if (alphaNumber >= 1 && alphaNumber <= 20) {
            return Alpha101Group1.calculate(alphaNumber, close, open, high, low, volume, vwap, returns, adv20);
        } else if (alphaNumber >= 21 && alphaNumber <= 40) {
            return Alpha101Group2.calculate(alphaNumber, close, open, high, low, volume, vwap, returns, adv20);
        } else if (alphaNumber >= 41 && alphaNumber <= 60) {
            return Alpha101Group3.calculate(alphaNumber, close, open, high, low, volume, vwap, returns, adv20);
        } else if (alphaNumber >= 61 && alphaNumber <= 80) {
            return Alpha101Group4.calculate(alphaNumber, close, open, high, low, volume, vwap, returns, adv20);
        } else if (alphaNumber >= 81 && alphaNumber <= 101) {
            return Alpha101Group5.calculate(alphaNumber, close, open, high, low, volume, vwap, returns, adv20);
        }

        log.warn("Alpha#{} is out of range [1, 101]", alphaNumber);
        return Double.NaN;  // ✅ 超出范围返回 NaN，保持一致性
    }
}
