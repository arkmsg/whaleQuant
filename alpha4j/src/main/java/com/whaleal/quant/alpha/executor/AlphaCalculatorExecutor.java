package com.whaleal.quant.alpha.executor;

import com.whaleal.quant.alpha.AlphaFactorResult;
import com.whaleal.quant.alpha.calculator.Alpha101Calculator;
import com.whaleal.quant.alpha.calculator.Alpha158Calculator;
import com.whaleal.quant.alpha.calculator.Alpha360Calculator;
import com.whaleal.quant.alpha.model.Candlestick;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Alpha因子计算执行器
 * 提供线程隔离的因子计算能力，避免阻塞EventBus分发线程
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
public class AlphaCalculatorExecutor {

    private static final AlphaCalculatorExecutor INSTANCE = new AlphaCalculatorExecutor();

    private final ExecutorService executorService;

    private AlphaCalculatorExecutor() {
        // 创建线程池，核心线程数为CPU核心数，最大线程数为CPU核心数的2倍
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        int maxPoolSize = corePoolSize * 2;
        long keepAliveTime = 60L;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000);
        ThreadFactory threadFactory = new NamedThreadFactory("alpha-calculator-");
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                handler
        );

        log.info("AlphaCalculatorExecutor initialized with corePoolSize={}, maxPoolSize={}", corePoolSize, maxPoolSize);
    }

    public static AlphaCalculatorExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * 异步计算Alpha101因子
     * @param data K线数据
     * @return 计算结果的CompletableFuture
     */
    public CompletableFuture<AlphaFactorResult> calculateAlpha101Async(List<Candlestick> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Alpha101Calculator calculator = new Alpha101Calculator();
                return calculator.calculate(data);
            } catch (Exception e) {
                log.error("Error calculating Alpha101 factors", e);
                throw new CompletionException(e);
            }
        }, executorService);
    }

    /**
     * 异步计算Alpha158因子
     * @param data K线数据
     * @return 计算结果的CompletableFuture
     */
    public CompletableFuture<AlphaFactorResult> calculateAlpha158Async(List<Candlestick> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Alpha158Calculator calculator = new Alpha158Calculator();
                return calculator.calculate(data);
            } catch (Exception e) {
                log.error("Error calculating Alpha158 factors", e);
                throw new CompletionException(e);
            }
        }, executorService);
    }

    /**
     * 异步计算Alpha360因子
     * @param data K线数据
     * @return 计算结果的CompletableFuture
     */
    public CompletableFuture<AlphaFactorResult> calculateAlpha360Async(List<Candlestick> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Alpha360Calculator calculator = new Alpha360Calculator();
                return calculator.calculate(data);
            } catch (Exception e) {
                log.error("Error calculating Alpha360 factors", e);
                throw new CompletionException(e);
            }
        }, executorService);
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("AlphaCalculatorExecutor shutdown");
    }

    /**
     * 命名线程工厂
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger counter = new AtomicInteger(0);

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, prefix + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
