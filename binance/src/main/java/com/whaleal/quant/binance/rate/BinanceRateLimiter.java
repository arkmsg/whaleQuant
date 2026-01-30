package com.whaleal.quant.binance.rate;

import com.google.common.util.concurrent.RateLimiter;

public class BinanceRateLimiter {
    private final RateLimiter guavaRateLimiter;

    public BinanceRateLimiter(double permitsPerSecond) {
        this.guavaRateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public void acquire() {
        guavaRateLimiter.acquire();
    }

    public boolean tryAcquire() {
        return guavaRateLimiter.tryAcquire();
    }

    public boolean tryAcquire(int permits) {
        return guavaRateLimiter.tryAcquire(permits);
    }

    public double getRate() {
        return guavaRateLimiter.getRate();
    }

    public void setRate(double permitsPerSecond) {
        guavaRateLimiter.setRate(permitsPerSecond);
    }
}
