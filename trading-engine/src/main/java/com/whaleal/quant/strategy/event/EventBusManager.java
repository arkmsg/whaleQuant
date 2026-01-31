package com.whaleal.quant.strategy.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 事件总线管理器
 *
 * <p>负责事件的发布和订阅管理
 *
 * @author stocks-strategy-sdk
 * @version 1.0.0
 */
@Slf4j
public class EventBusManager {

    private static EventBusManager instance;
    private EventBus eventBus;
    private ExecutorService executorService;

    private EventBusManager() {
        this.executorService = Executors.newFixedThreadPool(10);
        this.eventBus = new AsyncEventBus("strategy-event-bus", executorService);
    }

    public static synchronized EventBusManager getInstance() {
        if (instance == null) {
            instance = new EventBusManager();
        }
        return instance;
    }

    public void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    public void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    public void post(Object event) {
        eventBus.post(event);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
