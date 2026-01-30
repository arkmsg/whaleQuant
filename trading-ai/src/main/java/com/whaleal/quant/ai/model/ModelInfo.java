package com.whaleal.quant.ai.model;

import lombok.Data;

import java.util.Map;

/**
 * 模型信息类
 * 表示AI模型的基本信息
 * 
 * @author whaleal
 * @version 1.0.0
 */
@Data
public class ModelInfo {
    
    private String modelName;
    private String modelVersion;
    private ModelType modelType;
    private Map<String, Object> parameters;
    private long createdAt;
    
    public ModelInfo(String modelName, String modelVersion, ModelType modelType, Map<String, Object> parameters) {
        this.modelName = modelName;
        this.modelVersion = modelVersion;
        this.modelType = modelType;
        this.parameters = parameters;
        this.createdAt = System.currentTimeMillis();
    }
    
    /**
     * 模型类型枚举
     */
    public enum ModelType {
        LINEAR_REGRESSION("线性回归"),
        LSTM("长短期记忆网络"),
        GRU("门控循环单元"),
        TRANSFORMER("Transformer"),
        XGBOOST("XGBoost"),
        LIGHTGBM("LightGBM"),
        CATBOOST("CatBoost"),
        PROPRIETARY("专有模型");
        
        private final String description;
        
        ModelType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 判断是否为深度学习模型
     * 
     * @return 是否为深度学习模型
     */
    public boolean isDeepLearningModel() {
        return modelType == ModelType.LSTM || 
               modelType == ModelType.GRU || 
               modelType == ModelType.TRANSFORMER;
    }
    
    /**
     * 判断是否为树模型
     * 
     * @return 是否为树模型
     */
    public boolean isTreeModel() {
        return modelType == ModelType.XGBOOST || 
               modelType == ModelType.LIGHTGBM || 
               modelType == ModelType.CATBOOST;
    }
    
    /**
     * 判断是否为线性模型
     * 
     * @return 是否为线性模型
     */
    public boolean isLinearModel() {
        return modelType == ModelType.LINEAR_REGRESSION;
    }
}