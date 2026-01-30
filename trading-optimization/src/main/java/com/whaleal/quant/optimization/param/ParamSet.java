package com.whaleal.quant.optimization.param;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数集
 * 用于表示一组参数值
 *
 * @author whaleal
 * @version 1.0.0
 */
public class ParamSet {

    private Map<String, Object> params;

    /**
     * 构造方法
     */
    public ParamSet() {
        this.params = new HashMap<>();
    }

    /**
     * 构造方法
     * @param other 其他参数集
     */
    public ParamSet(ParamSet other) {
        this.params = new HashMap<>(other.params);
    }

    /**
     * 添加参数
     * @param name 参数名称
     * @param value 参数值
     * @return 参数集
     */
    public ParamSet addParam(String name, Object value) {
        params.put(name, value);
        return this;
    }

    /**
     * 获取参数
     * @param name 参数名称
     * @param <T> 参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParam(String name) {
        return (T) params.get(name);
    }

    /**
     * 获取整数参数
     * @param name 参数名称
     * @param defaultValue 默认值
     * @return 整数参数值
     */
    public int getIntParam(String name, int defaultValue) {
        Object value = params.get(name);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }

    /**
     * 获取双精度参数
     * @param name 参数名称
     * @param defaultValue 默认值
     * @return 双精度参数值
     */
    public double getDoubleParam(String name, double defaultValue) {
        Object value = params.get(name);
        if (value instanceof Double) {
            return (Double) value;
        }
        return defaultValue;
    }

    /**
     * 获取布尔参数
     * @param name 参数名称
     * @param defaultValue 默认值
     * @return 布尔参数值
     */
    public boolean getBooleanParam(String name, boolean defaultValue) {
        Object value = params.get(name);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    /**
     * 检查是否包含参数
     * @param name 参数名称
     * @return 是否包含参数
     */
    public boolean containsParam(String name) {
        return params.containsKey(name);
    }

    /**
     * 获取参数数量
     * @return 参数数量
     */
    public int size() {
        return params.size();
    }

    /**
     * 获取所有参数
     * @return 参数映射
     */
    public Map<String, Object> getAllParams() {
        return new HashMap<>(params);
    }

    @Override
    public String toString() {
        return "ParamSet{" +
                "params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamSet paramSet = (ParamSet) o;
        return params.equals(paramSet.params);
    }

    @Override
    public int hashCode() {
        return params.hashCode();
    }
}