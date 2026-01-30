package com.whaleal.quant.core.trace;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 追踪上下文
 * 为每个交易信号分配唯一的 Trace ID，贯穿整个交易链路
 *
 * @author whaleal
 * @version 1.0.0
 */
public class TraceContext implements java.io.Serializable {

    /**
     * 唯一追踪 ID
     */
    private final String traceId;

    /**
     * 信号 ID
     */
    private final String signalId;

    /**
     * 创建时间
     */
    private final Instant createdAt;

    /**
     * 扩展参数
     */
    private final Map<String, Object> attributes;

    /**
     * 构造函数
     */
    public TraceContext(String traceId, String signalId, Instant createdAt, Map<String, Object> attributes) {
        this.traceId = traceId;
        this.signalId = signalId;
        this.createdAt = createdAt;
        this.attributes = attributes;
    }

    /**
     * 获取追踪 ID
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 获取信号 ID
     */
    public String getSignalId() {
        return signalId;
    }

    /**
     * 获取创建时间
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 获取扩展参数
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Builder类
     */
    public static class Builder {
        private String traceId;
        private String signalId;
        private Instant createdAt;
        private Map<String, Object> attributes;

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder signalId(String signalId) {
            this.signalId = signalId;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public TraceContext build() {
            return new TraceContext(traceId, signalId, createdAt, attributes);
        }
    }

    /**
     * 获取Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建新的追踪上下文
     * @return 追踪上下文
     */
    public static TraceContext create() {
        return TraceContext.builder()
                .traceId(generateTraceId())
                .signalId(generateSignalId())
                .createdAt(Instant.now())
                .attributes(new ConcurrentHashMap<>())
                .build();
    }

    /**
     * 创建新的追踪上下文
     * @param traceId 追踪 ID
     * @return 追踪上下文
     */
    public static TraceContext create(String traceId) {
        return TraceContext.builder()
                .traceId(traceId)
                .signalId(generateSignalId())
                .createdAt(Instant.now())
                .attributes(new ConcurrentHashMap<>())
                .build();
    }

    /**
     * 添加属性
     * @param key 键
     * @param value 值
     * @return 追踪上下文
     */
    public TraceContext addAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    /**
     * 获取属性
     * @param key 键
     * @return 值
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * 复制追踪上下文
     * @return 复制的追踪上下文
     */
    public TraceContext copy() {
        return TraceContext.builder()
                .traceId(traceId)
                .signalId(signalId)
                .createdAt(createdAt)
                .attributes(new ConcurrentHashMap<>(attributes))
                .build();
    }

    /**
     * 生成追踪 ID
     * @return 追踪 ID
     */
    private static String generateTraceId() {
        return "trace_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }

    /**
     * 生成信号 ID
     * @return 信号 ID
     */
    private static String generateSignalId() {
        return "signal_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }
}
