package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.Alpha360Config;
import com.whaleal.quant.alpha.AlphaDataset;
import com.whaleal.quant.alpha.AlphaFactorResult;
import com.whaleal.quant.alpha.AlphaFeatureVector;
import com.whaleal.quant.alpha.model.Candlestick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Alpha360因子计算器单元测试
 */
class Alpha360CalculatorTest {

    private List<Candlestick> testData;

    @BeforeEach
    void setUp() {
        // 生成70根测试K线数据（超过60的最小需求）
        testData = generateTestData(70);
    }

    @Test
    void testCalculate_WithDefaultConfig() {
        AlphaFactorResult result = Alpha360Calculator.calculate(testData);

        assertNotNull(result);
        assertEquals("TEST", result.getSymbol());
        assertEquals(360, result.getFactorCount());

        System.out.println("因子总数: " + result.getFactorCount());
    }

    @Test
    void testCalculate_WithCustomConfig() {
        Alpha360Config config = Alpha360Config.createDefault();

        AlphaFactorResult result = Alpha360Calculator.calculate(testData, config);

        assertNotNull(result);
        assertEquals(360, result.getFactorCount());
    }

    @Test
    void testCalculate_InsufficientData() {
        List<Candlestick> shortData = generateTestData(30);

        AlphaFactorResult result = Alpha360Calculator.calculate(shortData);

        // 数据不足60根时应该返回null
        assertNull(result);
    }

    @Test
    void testFactorOrder_Alpha360() {
        AlphaFactorResult result = Alpha360Calculator.calculate(testData);
        assertNotNull(result);

        List<String> factorNames = result.getFactorNames();

        // 验证因子顺序（CLOSE先，然后OPEN，HIGH，LOW，VWAP，VOLUME）
        assertTrue(factorNames.get(0).startsWith("CLOSE"));
        assertTrue(factorNames.get(60).startsWith("OPEN"));
        assertTrue(factorNames.get(120).startsWith("HIGH"));
        assertTrue(factorNames.get(180).startsWith("LOW"));
        assertTrue(factorNames.get(240).startsWith("VWAP"));
        assertTrue(factorNames.get(300).startsWith("VOLUME"));

        // 验证时间窗口顺序（从day59到day0）
        assertEquals("CLOSE59", factorNames.get(0));
        assertEquals("CLOSE0", factorNames.get(59));
        assertEquals("VOLUME59", factorNames.get(300));
        assertEquals("VOLUME0", factorNames.get(359));
    }

    @Test
    void testToFeatureVector() {
        AlphaFactorResult result = Alpha360Calculator.calculate(testData);
        assertNotNull(result);

        AlphaFeatureVector vector = result.toFeatureVector(AlphaFeatureVector.AlphaType.ALPHA360);

        assertNotNull(vector);
        assertEquals(360, vector.getValues().length);
        assertEquals("TEST", vector.getSymbol());

        double[] values = vector.getValues();
        assertNotNull(values);
        assertEquals(360, values.length);
    }

    @Test
    void testCalculateBatch() {
        Alpha360Config config = Alpha360Config.createDefault();

        AlphaDataset dataset = Alpha360Calculator.calculateBatch(testData, config);

        assertNotNull(dataset);
        assertTrue(dataset.size() > 0);

        System.out.println("批量计算样本数: " + dataset.size());

        // 验证每个样本都是360维
        double[][] matrix = dataset.toArray();
        assertNotNull(matrix);
        for (double[] sample : matrix) {
            assertEquals(360, sample.length);
        }
    }

    // @Test
    // void testCalculateIncremental() {
    //     List<Candlestick> historical = testData.subList(0, 60);
    //     Candlestick newCandle = testData.get(60);
    //
    //     AlphaFactorResult result = Alpha360Calculator.calculateIncremental(
    //         historical,
    //         newCandle,
    //         Alpha360Config.createDefault()
    //     );
    //
    //     assertNotNull(result);
    //     assertEquals(360, result.getFactorCount());
    //     assertEquals(newCandle.getTimestamp(), result.getTimestamp());
    // }
    // 注：calculateIncremental方法已移除

    @Test
    void testTimeSeriesConsistency() {
        // 测试时间序列的一致性
        List<Candlestick> data61 = testData.subList(0, 61);
        List<Candlestick> data62 = testData.subList(0, 62);

        AlphaFactorResult result61 = Alpha360Calculator.calculate(data61);
        AlphaFactorResult result62 = Alpha360Calculator.calculate(data62);

        assertNotNull(result61);
        assertNotNull(result62);

        // 验证因子名称顺序一致
        assertEquals(result61.getFactorNames(), result62.getFactorNames());
    }

    /**
     * 生成测试数据
     */
    private List<Candlestick> generateTestData(int count) {
        List<Candlestick> data = new ArrayList<>();
        long baseTime = 1700000000L;
        double basePrice = 100.00;

        for (int i = 0; i < count; i++) {
            double change = Math.sin(i * 0.1) * 5;
            double close = basePrice + change;
            double open = close - (Math.random() * 2 - 1);
            double high = close + Math.random() * 3;
            double low = close - Math.random() * 3;
            long volume = (long) (1000000 + Math.random() * 500000);
            double amount = close * volume;

            Candlestick candle = Candlestick.builder()
                .symbol("TEST")
                .timestamp(baseTime + i * 86400)
                .open(open)
                .high(high)
                .low(low)
                .close(close)
                .volume(volume)
                .amount(amount)
                .build();

            data.add(candle);
        }

        return data;
    }
}

