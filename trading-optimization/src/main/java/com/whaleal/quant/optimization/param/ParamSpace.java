package com.whaleal.quant.optimization.param;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 参数空间
 * 用于定义策略参数的搜索空间
 *
 * @author whaleal
 * @version 1.0.0
 */
public class ParamSpace {

    private List<ParamDefinition<?>> paramDefinitions;

    /**
     * 构造方法
     */
    public ParamSpace() {
        this.paramDefinitions = new ArrayList<>();
    }

    /**
     * 添加整数参数定义
     * @param name 参数名称
     * @param min 最小值
     * @param max 最大值
     * @param step 步长
     * @return 参数空间
     */
    public ParamSpace addIntParam(String name, int min, int max, int step) {
        paramDefinitions.add(new IntParamDefinition(name, min, max, step));
        return this;
    }

    /**
     * 添加双精度参数定义
     * @param name 参数名称
     * @param min 最小值
     * @param max 最大值
     * @param step 步长
     * @return 参数空间
     */
    public ParamSpace addDoubleParam(String name, double min, double max, double step) {
        paramDefinitions.add(new DoubleParamDefinition(name, min, max, step));
        return this;
    }

    /**
     * 添加布尔参数定义
     * @param name 参数名称
     * @return 参数空间
     */
    public ParamSpace addBooleanParam(String name) {
        paramDefinitions.add(new BooleanParamDefinition(name));
        return this;
    }

    /**
     * 添加枚举参数定义
     * @param name 参数名称
     * @param values 枚举值
     * @param <T> 枚举类型
     * @return 参数空间
     */
    @SafeVarargs
    public final <T> ParamSpace addEnumParam(String name, T... values) {
        paramDefinitions.add(new EnumParamDefinition<>(name, values));
        return this;
    }

    /**
     * 添加自定义参数定义
     * @param name 参数名称
     * @param values 自定义值列表
     * @param <T> 参数类型
     * @return 参数空间
     */
    @SafeVarargs
    public final <T> ParamSpace addCustomParam(String name, T... values) {
        paramDefinitions.add(new CustomParamDefinition<>(name, values));
        return this;
    }

    /**
     * 获取参数定义列表
     * @return 参数定义列表
     */
    public List<ParamDefinition<?>> getParamDefinitions() {
        return paramDefinitions;
    }

    /**
     * 生成所有参数组合
     * @return 参数组合列表
     */
    public List<ParamSet> generateParamSets() {
        List<ParamSet> paramSets = new ArrayList<>();
        generateParamSetsRecursive(paramSets, new ParamSet(), 0);
        return paramSets;
    }

    /**
     * 递归生成参数组合
     * @param paramSets 参数组合列表
     * @param currentSet 当前参数集
     * @param index 当前参数索引
     */
    private void generateParamSetsRecursive(List<ParamSet> paramSets, ParamSet currentSet, int index) {
        if (index >= paramDefinitions.size()) {
            paramSets.add(currentSet);
            return;
        }

        ParamDefinition<?> paramDef = paramDefinitions.get(index);
        List<?> values = paramDef.getValues();

        for (Object value : values) {
            ParamSet newSet = new ParamSet(currentSet);
            newSet.addParam(paramDef.getName(), value);
            generateParamSetsRecursive(paramSets, newSet, index + 1);
        }
    }

    /**
     * 参数定义接口
     * @param <T> 参数类型
     */
    public interface ParamDefinition<T> {
        /**
         * 获取参数名称
         * @return 参数名称
         */
        String getName();

        /**
         * 获取参数值列表
         * @return 参数值列表
         */
        List<T> getValues();

        /**
         * 获取参数类型
         * @return 参数类型
         */
        Class<T> getType();
    }

    /**
     * 整数参数定义
     */
    public static class IntParamDefinition implements ParamDefinition<Integer> {
        private String name;
        private int min;
        private int max;
        private int step;

        public IntParamDefinition(String name, int min, int max, int step) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Integer> getValues() {
            List<Integer> values = new ArrayList<>();
            for (int i = min; i <= max; i += step) {
                values.add(i);
            }
            return values;
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }
    }

    /**
     * 双精度参数定义
     */
    public static class DoubleParamDefinition implements ParamDefinition<Double> {
        private String name;
        private double min;
        private double max;
        private double step;

        public DoubleParamDefinition(String name, double min, double max, double step) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Double> getValues() {
            List<Double> values = new ArrayList<>();
            for (double i = min; i <= max; i += step) {
                values.add(i);
            }
            return values;
        }

        @Override
        public Class<Double> getType() {
            return Double.class;
        }
    }

    /**
     * 布尔参数定义
     */
    public static class BooleanParamDefinition implements ParamDefinition<Boolean> {
        private String name;

        public BooleanParamDefinition(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Boolean> getValues() {
            List<Boolean> values = new ArrayList<>();
            values.add(true);
            values.add(false);
            return values;
        }

        @Override
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    }

    /**
     * 枚举参数定义
     * @param <T> 枚举类型
     */
    public static class EnumParamDefinition<T> implements ParamDefinition<T> {
        private String name;
        private T[] values;

        @SafeVarargs
        public EnumParamDefinition(String name, T... values) {
            this.name = name;
            this.values = values;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<T> getValues() {
            List<T> valueList = new ArrayList<>();
            for (T value : values) {
                valueList.add(value);
            }
            return valueList;
        }

        @Override
        public Class<T> getType() {
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) (values.length > 0 ? values[0].getClass() : Object.class);
            return type;
        }
    }

    /**
     * 自定义参数定义
     * @param <T> 参数类型
     */
    public static class CustomParamDefinition<T> implements ParamDefinition<T> {
        private String name;
        private T[] values;

        @SafeVarargs
        public CustomParamDefinition(String name, T... values) {
            this.name = name;
            this.values = values;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<T> getValues() {
            List<T> valueList = new ArrayList<>();
            for (T value : values) {
                valueList.add(value);
            }
            return valueList;
        }

        @Override
        public Class<T> getType() {
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) (values.length > 0 ? values[0].getClass() : Object.class);
            return type;
        }
    }
}