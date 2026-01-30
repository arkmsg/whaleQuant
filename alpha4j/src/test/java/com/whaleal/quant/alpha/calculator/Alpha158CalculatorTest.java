package com.whaleal.quant.alpha.calculator;

import com.whaleal.quant.alpha.Alpha158Config;
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
 * Alpha158因子计算器单元测试
 */
class Alpha158CalculatorTest {

    private List<Candlestick> testData;

    @BeforeEach
    void setUp() {
        // 生成70根测试K线数据
        testData = generateTestData(70);
    }

    @Test
    void testCalculate_WithDefaultConfig() {
        AlphaFactorResult result = Alpha158Calculator.calculate(testData);

        assertNotNull(result);
        assertEquals("TEST", result.getSymbol());
        assertTrue(result.getFactorCount() > 0);

        System.out.println("因子总数: " + result.getFactorCount());
        System.out.println("因子顺序: " + result.getFactorNames());
    }

    @Test
    void testCalculate_WithCustomConfig() {
        Alpha158Config config = Alpha158Config.builder()
            .enableKbar(true)
            .enablePrice(true)
            .priceWindows(List.of(0, 5, 10))
            .priceFeatures(List.of("OPEN", "HIGH", "LOW", "VWAP"))
            .enableVolume(false)
            .enableRolling(true)
            .rollingWindows(List.of(5, 10, 20))
            .rollingInclude(List.of("ROC", "MA", "STD", "BETA", "RSQR"))
            .build();

        AlphaFactorResult result = Alpha158Calculator.calculate(testData, config);

        assertNotNull(result);
        assertTrue(result.getFactorCount() > 0);

        // 验证K线因子存在
        assertTrue(result.getFactors().containsKey("KMID"));
        assertTrue(result.getFactors().containsKey("KLEN"));
    }

    @Test
    void testCalculate_InsufficientData() {
        List<Candlestick> shortData = generateTestData(10);

        AlphaFactorResult result = Alpha158Calculator.calculate(shortData);

        // 数据不足时应该返回null或空结果
        assertNotNull(result);
    }

    @Test
    void testToFeatureVector() {
        AlphaFactorResult result = Alpha158Calculator.calculate(testData);
        assertNotNull(result);

        AlphaFeatureVector vector = result.toFeatureVector(AlphaFeatureVector.AlphaType.ALPHA158);

        assertNotNull(vector);
        assertEquals(result.getFactorCount(), vector.getValues().length);
        assertEquals("TEST", vector.getSymbol());

        // 验证特征向量的值数组
        double[] values = vector.getValues();
        assertNotNull(values);
        assertEquals(result.getFactorCount(), values.length);
    }

    @Test
    void testCalculateBatch() {
        Alpha158Config config = Alpha158Config.createDefault();

        AlphaDataset dataset = Alpha158Calculator.calculateBatch(testData, config);

        assertNotNull(dataset);
        assertTrue(dataset.size() > 0);

        System.out.println("批量计算样本数: " + dataset.size());

        // 验证数据集转换为矩阵
        double[][] matrix = dataset.toArray();
        assertNotNull(matrix);
        assertEquals(dataset.size(), matrix.length);
    }

    @Test
    void testCalculateIncremental() {
        List<Candlestick> historical = testData.subList(0, 60);
        Candlestick newCandle = testData.get(60);

        AlphaFactorResult result = Alpha158Calculator.calculateIncremental(
            historical,
            newCandle,
            Alpha158Config.createDefault()
        );

        assertNotNull(result);
        assertEquals(newCandle.getTimestamp(), result.getTimestamp());
    }

    @Test
    void testFactorOrder_Consistency() {
        AlphaFactorResult result1 = Alpha158Calculator.calculate(testData);
        AlphaFactorResult result2 = Alpha158Calculator.calculate(testData);

        assertNotNull(result1);
        assertNotNull(result2);

        // 验证因子顺序一致性
        assertEquals(result1.getFactorNames(), result2.getFactorNames());
    }

    @Test
    void testNaNHandling() {
        // 生成包含异常值的数据
        List<Candlestick> dataWithNaN = generateTestData(70);

        AlphaFactorResult result = Alpha158Calculator.calculate(dataWithNaN);
        assertNotNull(result);

        AlphaFeatureVector vector = result.toFeatureVector(AlphaFeatureVector.AlphaType.ALPHA158);

        // 检查是否有NaN值
        boolean hasInvalid = vector.hasInvalidValues();

        if (hasInvalid) {
            System.out.println("检测到无效值: " + vector.getInvalidValuesDetail());

            // 测试填充策略
            AlphaFeatureVector filled = vector.fillInvalidValues(0.0);
            assertFalse(filled.hasInvalidValues());
        }
    }

    /**
     * 生成测试数据
     */
    private List<Candlestick> generateTestData(int count) {
        List<Candlestick> data = new ArrayList<>();
        long baseTime = 1700000000L; // 2023-11-15
        double basePrice = 100.00;

        for (int i = 0; i < count; i++) {
            // 模拟价格波动
            double change = Math.sin(i * 0.1) * 5; // ±5的正弦波动
            double close = basePrice + change;
            double open = close - (Math.random() * 2 - 1);
            double high = close + Math.random() * 3;
            double low = close - Math.random() * 3;
            long volume = (long) (1000000 + Math.random() * 500000);
            double amount = close * volume;

            Candlestick candle = Candlestick.builder()
                .symbol("TEST")
                .timestamp(baseTime + i * 86400) // 每天
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

