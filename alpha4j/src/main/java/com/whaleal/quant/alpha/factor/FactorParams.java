package com.whaleal.quant.alpha.factor;

import java.util.HashMap;
import java.util.Map;

/**
 * 因子参数类
 * 用于存储和管理因子的参数
 *
 * @author whaleal
 * @version 1.0.0
 */
public class FactorParams {

    /**
     * 参数映射
     */
    private final Map<String, Object> params;

    /**
     * 构造方法
     */
    public FactorParams() {
        this.params = new HashMap<>();
    }

    /**
     * 构造方法
     * @param params 参数映射
     */
    public FactorParams(Map<String, Object> params) {
        this.params = new HashMap<>(params);
    }

    /**
     * 添加参数
     * @param key 参数键
     * @param value 参数值
     * @return 因子参数对象
     */
    public FactorParams addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 获取参数
     * @param key 参数键
     * @return 参数值
     */
    public Object getParam(String key) {
        return params.get(key);
    }

    /**
     * 获取整数参数
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 整数参数值
     */
    public int getIntParam(String key, int defaultValue) {
        Object value = params.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }

    /**
     * 获取双精度参数
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 双精度参数值
     */
    public double getDoubleParam(String key, double defaultValue) {
        Object value = params.get(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        return defaultValue;
    }

    /**
     * 获取布尔参数
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 布尔参数值
     */
    public boolean getBooleanParam(String key, boolean defaultValue) {
        Object value = params.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    /**
     * 获取字符串参数
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 字符串参数值
     */
    public String getStringParam(String key, String defaultValue) {
        Object value = params.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }

    /**
     * 获取所有参数
     * @return 参数映射
     */
    public Map<String, Object> getAllParams() {
        return new HashMap<>(params);
    }

    /**
     * 检查是否包含参数
     * @param key 参数键
     * @return 是否包含参数
     */
    public boolean containsParam(String key) {
        return params.containsKey(key);
    }

    /**
     * 移除参数
     * @param key 参数键
     * @return 移除的参数值
     */
    public Object removeParam(String key) {
        return params.remove(key);
    }

    /**
     * 清空参数
     */
    public void clearParams() {
        params.clear();
    }

    /**
     * 获取参数数量
     * @return 参数数量
     */
    public int size() {
        return params.size();
    }

    /**
     * 检查是否为空
     * @return 是否为空
     */
    public boolean isEmpty() {
        return params.isEmpty();
    }
}
