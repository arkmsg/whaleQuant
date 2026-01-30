package com.whaleal.quant.binance.retry;

import com.whaleal.quant.binance.exception.BinanceException;
import com.whaleal.quant.binance.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 重试策略
 *
 * <p>为可重试的错误提供自动重试机制，提高系统稳定性。
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * String result = RetryPolicy.execute(
 *     () -> sdk.market().getTicker("BTCUSDT"),
 *     3,    // 最多重试3次
 *     1000  // 重试间隔1秒
 * );
 * }</pre>
 *
 * @author Binance SDK Team
 * @version 1.0.0
 */
public class RetryPolicy {

    private static final Logger log = LoggerFactory.getLogger(RetryPolicy.class);

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY_MS = 1000;

    /**
     * 执行操作，失败时自动重试（使用默认配置）
     *
     * @param supplier 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public static <T> T execute(Supplier<T> supplier) {
        return execute(supplier, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY_MS);
    }

    /**
     * 执行操作，失败时自动重试
     *
     * @param supplier 要执行的操作
     * @param maxRetries 最大重试次数
     * @param retryDelayMs 重试间隔（毫秒）
     * @param <T> 返回类型
     * @return 操作结果
     * @throws BinanceException 如果所有重试都失败
     */
    public static <T> T execute(Supplier<T> supplier, int maxRetries, long retryDelayMs) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                return supplier.get();
            } catch (BinanceException e) {
                lastException = e;

                // 如果不可重试，直接抛出
                if (!e.isRetryable()) {
                    throw e;
                }

                // 达到最大重试次数
                if (attempt >= maxRetries) {
                    log.error("操作失败，已达到最大重试次数 {}: {}", maxRetries, e.getFullMessage());
                    throw e;
                }

                attempt++;
                log.warn("操作失败（可重试），第{}次重试: {}", attempt, e.getFullMessage());

                // 等待后重试
                sleep(retryDelayMs * attempt); // 指数退避

            } catch (Exception e) {
                lastException = e;
                log.error("操作失败（不可重试）: {}", e.getMessage(), e);
                throw new BinanceException(
                    ErrorCode.UNKNOWN_ERROR,
                    "操作执行失败: " + e.getMessage(),
                    e
                );
            }
        }

        // 理论上不会到这里
        throw new BinanceException(
            ErrorCode.UNKNOWN_ERROR,
            "重试失败",
            lastException
        );
    }

    /**
     * 执行无返回值的操作，失败时自动重试
     */
    public static void executeVoid(Runnable runnable) {
        execute(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 执行无返回值的操作，失败时自动重试
     */
    public static void executeVoid(Runnable runnable, int maxRetries, long retryDelayMs) {
        execute(() -> {
            runnable.run();
            return null;
        }, maxRetries, retryDelayMs);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("重试等待被中断");
        }
    }
}

