package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.model.Candlestick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RollingStatCalculator测试类
 * 测试所有29个滚动统计算子的正确性
 */
@DisplayName("RollingStatCalculator 测试")
class RollingStatCalculatorTest {

    private List<Candlestick> testData;
    private static final double DELTA = 0.0001; // 浮点数比较精度
    private static final int TEST_WINDOW = 5;

    @BeforeEach
    void setUp() {
        testData = generateTestData(100);
    }

    /**
     * 生成测试数据
     */
    private List<Candlestick> generateTestData(int count) {
        List<Candlestick> data = new ArrayList<>();
        long baseTime = System.currentTimeMillis() / 1000; // Unix时间戳（秒）

        for (int i = 0; i < count; i++) {
            double base = 100.0 + i * 0.5;
            data.add(Candlestick.builder()
                    .symbol("TEST")
                    .timestamp(baseTime + i * 60) // 每分钟递增
                    .open(base)
                    .high(base + 2.0)
                    .low(base - 1.0)
                    .close(base + 0.5)
                    .volume((long)(1000000.0 + i * 10000.0))
                    .build());
        }

        return data;
    }

    // ==================== 基础数据验证 ====================

    @Test
    @DisplayName("测试数据生成正确性")
    void testDataGeneration() {
        assertNotNull(testData);
        assertEquals(100, testData.size());

        Candlestick first = testData.get(0);
        assertEquals(100.0, first.getOpen());
        assertEquals(102.0, first.getHigh());
        assertEquals(99.0, first.getLow());
        assertEquals(100.5, first.getClose());
        assertEquals(1000000.0, first.getVolume());
    }

    // ==================== ROC (价格变化率) ====================

    @Test
    @DisplayName("ROC - 价格变化率")
    void testCalculateROC() {
        double result = RollingStatCalculator.calculateROC(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // ROC = (close[-1] - close[-window-1]) / close[-1]
        // 应该是正值（价格上涨趋势）
        assertTrue(result > 0, "ROC should be positive for uptrend");
    }

    @Test
    @DisplayName("ROC - 数据不足")
    void testCalculateROC_InsufficientData() {
        List<Candlestick> smallData = testData.subList(0, 3);
        double result = RollingStatCalculator.calculateROC(smallData, TEST_WINDOW, smallData.get(smallData.size() - 1).getClose());

        assertTrue(Double.isNaN(result), "ROC should return NaN when data insufficient");
    }

    // ==================== MA (移动平均) ====================

    @Test
    @DisplayName("MA - 移动平均")
    void testCalculateMA() {
        double result = RollingStatCalculator.calculateMA(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // MA应该接近1.0（归一化后）
        assertTrue(result > 0.9 && result < 1.1, "MA should be close to 1.0 when normalized");
    }

    // ==================== STD (标准差) ====================

    @Test
    @DisplayName("STD - 标准差")
    void testCalculateSTD() {
        double result = RollingStatCalculator.calculateSTD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result >= 0, "STD should be non-negative");
    }

    // ==================== BETA (Beta系数) ====================

    @Test
    @DisplayName("BETA - Beta系数")
    void testCalculateBETA() {
        double result = RollingStatCalculator.calculateBETA(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 对于上涨趋势，BETA应该是正值
        assertTrue(result > 0, "BETA should be positive for uptrend");
    }

    // ==================== RSQR (R²) ====================

    @Test
    @DisplayName("RSQR - R平方")
    void testCalculateRSQR() {
        double result = RollingStatCalculator.calculateRSQR(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // R²应该在[0, 1]范围内
        assertTrue(result >= 0 && result <= 1, "RSQR should be in [0, 1]");
    }

    // ==================== RESI (回归残差) ====================

    @Test
    @DisplayName("RESI - 回归残差")
    void testCalculateRESI() {
        double result = RollingStatCalculator.calculateRESI(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
    }

    // ==================== MAX/MIN (最大最小值) ====================

    @Test
    @DisplayName("MAX - 最大值（使用high）")
    void testCalculateMAX() {
        double result = RollingStatCalculator.calculateMAX(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // MAX应该大于1（因为high > close）
        assertTrue(result > 1.0, "MAX should be > 1.0 since high > close");
    }

    @Test
    @DisplayName("MIN - 最小值（使用low）")
    void testCalculateMIN() {
        double result = RollingStatCalculator.calculateMIN(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // MIN应该小于1（因为low < close）
        assertTrue(result < 1.0, "MIN should be < 1.0 since low < close");
    }

    // ==================== QTLU/QTLD (分位数) ====================

    @Test
    @DisplayName("QTLU - 上分位数（80%）")
    void testCalculateQTLU() {
        double result = RollingStatCalculator.calculateQTLU(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result > 0, "QTLU should be positive");
    }

    @Test
    @DisplayName("QTLD - 下分位数（20%）")
    void testCalculateQTLD() {
        double result = RollingStatCalculator.calculateQTLD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result > 0, "QTLD should be positive");
    }

    @Test
    @DisplayName("QTLU应该大于QTLD")
    void testQTLU_GreaterThan_QTLD() {
        double qtlu = RollingStatCalculator.calculateQTLU(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());
        double qtld = RollingStatCalculator.calculateQTLD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertTrue(qtlu >= qtld, "QTLU should be >= QTLD");
    }

    // ==================== RANK (排名) ====================

    @Test
    @DisplayName("RANK - 排名百分比")
    void testCalculateRANK() {
        double result = RollingStatCalculator.calculateRANK(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // RANK应该在[0, 1]范围内
        assertTrue(result >= 0 && result <= 1, "RANK should be in [0, 1]");
    }

    // ==================== RSV (随机指标) ====================

    @Test
    @DisplayName("RSV - 随机指标（使用high/low）")
    void testCalculateRSV() {
        double result = RollingStatCalculator.calculateRSV(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // RSV应该在合理范围内
        assertTrue(result >= -1 && result <= 2, "RSV should be in reasonable range");
    }

    // ==================== IMAX/IMIN (极值索引) ====================

    @Test
    @DisplayName("IMAX - 最大值索引（使用high）")
    void testCalculateIMAX() {
        double result = RollingStatCalculator.calculateIMAX(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // IMAX应该在[0, window-1]范围内
        assertTrue(result >= 0 && result < TEST_WINDOW, "IMAX should be in [0, window)");
    }

    @Test
    @DisplayName("IMIN - 最小值索引（使用low）")
    void testCalculateIMIN() {
        double result = RollingStatCalculator.calculateIMIN(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // IMIN应该在[0, window-1]范围内
        assertTrue(result >= 0 && result < TEST_WINDOW, "IMIN should be in [0, window)");
    }

    @Test
    @DisplayName("IMXD - 极值索引差（使用high/low）")
    void testCalculateIMXD() {
        double result = RollingStatCalculator.calculateIMXD(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // IMXD = IMAX - IMIN，应该在[-(window-1), window-1]范围内
        assertTrue(result >= -(TEST_WINDOW - 1) && result <= (TEST_WINDOW - 1),
                   "IMXD should be in [-(window-1), window-1]");
    }

    // ==================== CORR/CORD (相关性) ====================

    @Test
    @DisplayName("CORR - 价格成交量相关性")
    void testCalculateCORR() {
        double result = RollingStatCalculator.calculateCORR(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 相关系数应该在[-1, 1]范围内
        assertTrue(result >= -1 && result <= 1, "CORR should be in [-1, 1]");
    }

    @Test
    @DisplayName("CORD - 收益率相关性")
    void testCalculateCORD() {
        double result = RollingStatCalculator.calculateCORD(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 相关系数应该在[-1, 1]范围内
        assertTrue(result >= -1 && result <= 1, "CORD should be in [-1, 1]");
    }

    // ==================== CNT* (涨跌统计) ====================

    @Test
    @DisplayName("CNTP - 上涨天数")
    void testCalculateCNTP() {
        double result = RollingStatCalculator.calculateCNTP(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 上涨天数应该在[0, window]范围内
        assertTrue(result >= 0 && result <= TEST_WINDOW, "CNTP should be in [0, window]");
    }

    @Test
    @DisplayName("CNTN - 下跌天数")
    void testCalculateCNTN() {
        double result = RollingStatCalculator.calculateCNTN(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 下跌天数应该在[0, window]范围内
        assertTrue(result >= 0 && result <= TEST_WINDOW, "CNTN should be in [0, window]");
    }

    @Test
    @DisplayName("CNTD - 涨跌天数差")
    void testCalculateCNTD() {
        double result = RollingStatCalculator.calculateCNTD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // CNTD = CNTP - CNTN，应该在[-window, window]范围内
        assertTrue(result >= -TEST_WINDOW && result <= TEST_WINDOW, "CNTD should be in [-window, window]");
    }

    @Test
    @DisplayName("CNTP + CNTN 应该等于 window")
    void testCNTP_Plus_CNTN_Equals_Window() {
        double cntp = RollingStatCalculator.calculateCNTP(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());
        double cntn = RollingStatCalculator.calculateCNTN(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        // CNTP + CNTN应该约等于window（可能有持平的情况）
        assertTrue(cntp + cntn <= TEST_WINDOW, "CNTP + CNTN should be <= window");
    }

    // ==================== SUM* (涨跌幅度) ====================

    @Test
    @DisplayName("SUMP - 上涨幅度和")
    void testCalculateSUMP() {
        double result = RollingStatCalculator.calculateSUMP(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 对于上涨趋势，SUMP应该是正值
        assertTrue(result >= 0, "SUMP should be >= 0");
    }

    @Test
    @DisplayName("SUMN - 下跌幅度和")
    void testCalculateSUMN() {
        double result = RollingStatCalculator.calculateSUMN(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // SUMN应该是非负值（因为是abs值）
        assertTrue(result >= 0, "SUMN should be >= 0");
    }

    @Test
    @DisplayName("SUMD - 涨跌幅度差")
    void testCalculateSUMD() {
        double result = RollingStatCalculator.calculateSUMD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // 对于上涨趋势，SUMD应该是正值
        assertTrue(result > 0, "SUMD should be > 0 for uptrend");
    }

    // ==================== V* (成交量相关) ====================

    @Test
    @DisplayName("VMA - 成交量均值")
    void testCalculateVMA() {
        double result = RollingStatCalculator.calculateVMA(testData, TEST_WINDOW, testData.get(testData.size() - 1).getVolume());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));

        // VMA应该接近1.0（归一化后）
        assertTrue(result > 0.8 && result < 1.2, "VMA should be close to 1.0 when normalized");
    }

    @Test
    @DisplayName("VSTD - 成交量标准差")
    void testCalculateVSTD() {
        double result = RollingStatCalculator.calculateVSTD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getVolume());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result >= 0, "VSTD should be non-negative");
    }

    @Test
    @DisplayName("WVMA - 成交量加权波动率")
    void testCalculateWVMA() {
        double result = RollingStatCalculator.calculateWVMA(testData, TEST_WINDOW);

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result >= 0, "WVMA should be non-negative");
    }

    @Test
    @DisplayName("VSUMP - 上涨成交量和")
    void testCalculateVSUMP() {
        double result = RollingStatCalculator.calculateVSUMP(testData, TEST_WINDOW, testData.get(testData.size() - 1).getVolume());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result >= 0, "VSUMP should be >= 0");
    }

    @Test
    @DisplayName("VSUMN - 下跌成交量和")
    void testCalculateVSUMN() {
        double result = RollingStatCalculator.calculateVSUMN(testData, TEST_WINDOW, testData.get(testData.size() - 1).getVolume());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
        assertTrue(result >= 0, "VSUMN should be >= 0");
    }

    @Test
    @DisplayName("VSUMD - 成交量涨跌差")
    void testCalculateVSUMD() {
        double result = RollingStatCalculator.calculateVSUMD(testData, TEST_WINDOW, testData.get(testData.size() - 1).getVolume());

        assertFalse(Double.isNaN(result));
        assertFalse(Double.isInfinite(result));
    }

    // ==================== calculateAll 批量计算测试 ====================

    @Test
    @DisplayName("calculateAll - 批量计算所有因子")
    void testCalculateAll() {
        List<Integer> windows = List.of(5, 10, 20);
        List<String> operators = List.of("ROC", "MA", "STD", "MAX", "MIN", "CORR", "CORD");

        Map<String, Double> factors = RollingStatCalculator.calculateAll(testData, windows, operators);

        assertNotNull(factors);
        assertFalse(factors.isEmpty());

        // 验证生成的因子数量：7个算子 × 3个窗口 = 21个因子
        assertEquals(21, factors.size(), "Should generate 21 factors (7 operators × 3 windows)");

        // 验证所有因子都有值
        for (Map.Entry<String, Double> entry : factors.entrySet()) {
            assertNotNull(entry.getValue(), "Factor " + entry.getKey() + " should not be null");
            assertFalse(Double.isNaN(entry.getValue()), "Factor " + entry.getKey() + " should not be NaN");
        }

        // 验证因子命名正确
        assertTrue(factors.containsKey("ROC5"));
        assertTrue(factors.containsKey("MA10"));
        assertTrue(factors.containsKey("MAX20"));
    }

    @Test
    @DisplayName("calculateAll - 所有29个算子")
    void testCalculateAll_AllOperators() {
        List<Integer> windows = List.of(5);
        List<String> operators = List.of(
            "ROC", "MA", "STD", "BETA", "RSQR", "RESI",
            "MAX", "MIN", "QTLU", "QTLD", "RANK", "RSV",
            "IMAX", "IMIN", "IMXD", "CORR", "CORD",
            "CNTP", "CNTN", "CNTD", "SUMP", "SUMN", "SUMD",
            "VMA", "VSTD", "WVMA", "VSUMP", "VSUMN", "VSUMD"
        );

        Map<String, Double> factors = RollingStatCalculator.calculateAll(testData, windows, operators);

        assertNotNull(factors);
        assertEquals(29, factors.size(), "Should generate 29 factors");

        // 验证所有因子都有值且不是NaN
        for (Map.Entry<String, Double> entry : factors.entrySet()) {
            assertNotNull(entry.getValue(), "Factor " + entry.getKey() + " should not be null");
            assertFalse(Double.isNaN(entry.getValue()),
                       "Factor " + entry.getKey() + " should not be NaN");
        }
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("空数据列表")
    void testEmptyData() {
        List<Candlestick> emptyData = new ArrayList<>();

        double result = RollingStatCalculator.calculateROC(emptyData, TEST_WINDOW, 100.0);
        assertTrue(Double.isNaN(result), "Should return NaN for empty data");
    }

    @Test
    @DisplayName("窗口大于数据长度")
    void testWindowLargerThanData() {
        List<Candlestick> smallData = testData.subList(0, 3);

        double result = RollingStatCalculator.calculateMA(smallData, 10, smallData.get(smallData.size() - 1).getClose());
        assertTrue(Double.isNaN(result), "Should return NaN when window > data size");
    }

    @Test
    @DisplayName("窗口为1")
    void testWindowOne() {
        double result = RollingStatCalculator.calculateMA(testData, 1, testData.get(testData.size() - 1).getClose());

        assertFalse(Double.isNaN(result));
        // 窗口为1时，MA应该等于1.0（close/close）
        assertEquals(1.0, result, DELTA, "MA with window=1 should be 1.0");
    }

    // ==================== 数据源验证测试 ====================

    @Test
    @DisplayName("验证MAX使用high数据源")
    void testMAX_UsesHigh() {
        // 创建特殊数据：high明显高于close
        List<Candlestick> specialData = new ArrayList<>();
        long baseTime = System.currentTimeMillis() / 1000;
        for (int i = 0; i < 10; i++) {
            specialData.add(Candlestick.builder()
                    .symbol("TEST")
                    .timestamp(baseTime + i * 60)
                    .open(100.0)
                    .high(150.0)  // high明显高于close
                    .low(90.0)
                    .close(100.0)
                    .volume(1000000L)
                    .build());
        }

        double max = RollingStatCalculator.calculateMAX(specialData, 5, 100.0);

        // MAX应该约等于1.5（150/100），说明使用的是high
        assertTrue(max > 1.4 && max < 1.6, "MAX should use high, expected ~1.5, got " + max);
    }

    @Test
    @DisplayName("验证MIN使用low数据源")
    void testMIN_UsesLow() {
        // 创建特殊数据：low明显低于close
        List<Candlestick> specialData = new ArrayList<>();
        long baseTime = System.currentTimeMillis() / 1000;
        for (int i = 0; i < 10; i++) {
            specialData.add(Candlestick.builder()
                    .symbol("TEST")
                    .timestamp(baseTime + i * 60)
                    .open(100.0)
                    .high(110.0)
                    .low(50.0)  // low明显低于close
                    .close(100.0)
                    .volume(1000000L)
                    .build());
        }

        double min = RollingStatCalculator.calculateMIN(specialData, 5, 100.0);

        // MIN应该约等于0.5（50/100），说明使用的是low
        assertTrue(min > 0.4 && min < 0.6, "MIN should use low, expected ~0.5, got " + min);
    }

    @Test
    @DisplayName("验证CORR使用volume数据源")
    void testCORR_UsesVolume() {
        // CORR应该计算close与log(volume+1)的相关性
        double result = RollingStatCalculator.calculateCORR(testData, 20);

        assertFalse(Double.isNaN(result));
        // 由于我们的测试数据volume递增，close也递增，所以相关性应该是正的
        assertTrue(result > 0, "CORR should be positive for correlated data");
    }

    // ==================== 性能测试 ====================

    @Test
    @DisplayName("大数据集性能测试")
    void testPerformance_LargeDataset() {
        List<Candlestick> largeData = generateTestData(10000);

        long startTime = System.currentTimeMillis();

        Map<String, Double> factors = RollingStatCalculator.calculateAll(
            largeData,
            List.of(5, 10, 20, 30, 60),
            List.of("ROC", "MA", "STD", "MAX", "MIN", "CORR", "CORD")
        );

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(factors);
        assertEquals(35, factors.size()); // 7 operators × 5 windows = 35 factors

        System.out.println("Large dataset (10000 records) calculation time: " + duration + "ms");

        // 性能要求：10000条数据，35个因子，应该在1秒内完成
        assertTrue(duration < 1000, "Calculation should complete within 1 second, took " + duration + "ms");
    }
}

