package com.whaleal.quant.ai.model;

import java.util.Map;

/**
 * AI模型工厂类
 * 用于创建不同类型的AI模型
 * 
 * @author whaleal
 * @version 1.0.0
 */
public class AIModelFactory {
    
    /**
     * 创建AI模型
     * 
     * @param modelType 模型类型
     * @param config 模型配置
     * @return AI模型实例
     */
    public static AIModel createModel(String modelType, Map<String, Object> config) {
        switch (modelType.toLowerCase()) {
            case "linear":
                return createLinearModel(config);
            case "tree":
                return createTreeModel(config);
            case "ensemble":
                return createEnsembleModel(config);
            case "deep":
                return createDeepModel(config);
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelType);
        }
    }
    
    /**
     * 创建线性模型
     * 
     * @param config 模型配置
     * @return 线性模型实例
     */
    private static AIModel createLinearModel(Map<String, Object> config) {
        return new LinearAIModel();
    }
    
    /**
     * 创建树模型
     * 
     * @param config 模型配置
     * @return 树模型实例
     */
    private static AIModel createTreeModel(Map<String, Object> config) {
        return new TreeAIModel();
    }
    
    /**
     * 创建集成学习模型
     * 
     * @param config 模型配置
     * @return 集成学习模型实例
     */
    private static AIModel createEnsembleModel(Map<String, Object> config) {
        return new EnsembleAIModel();
    }
    
    /**
     * 创建深度学习模型
     * 
     * @param config 模型配置
     * @return 深度学习模型实例
     */
    private static AIModel createDeepModel(Map<String, Object> config) {
        return new DeepAIModel();
    }
}
