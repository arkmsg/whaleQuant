package com.whaleal.quant.optimization.result;

import com.whaleal.quant.optimization.engine.StrategyOptimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * 优化结果
 * 用于存储和展示策略优化的结果
 *
 * @author whaleal
 * @version 1.0.0
 */
public class OptimizationResult {

    private final List<StrategyOptimizer.OptimizationCandidate> candidates;

    /**
     * 构造方法
     * @param candidates 优化候选列表
     */
    public OptimizationResult(List<StrategyOptimizer.OptimizationCandidate> candidates) {
        this.candidates = new ArrayList<>(candidates);
    }

    /**
     * 获取最佳候选
     * @return 最佳候选
     */
    public StrategyOptimizer.OptimizationCandidate getBestCandidate() {
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0);
    }

    /**
     * 获取前N个最佳候选
     * @param n 数量
     * @return 前N个最佳候选
     */
    public List<StrategyOptimizer.OptimizationCandidate> getTopCandidates(int n) {
        int size = Math.min(n, candidates.size());
        return candidates.subList(0, size);
    }

    /**
     * 获取所有候选
     * @return 所有候选
     */
    public List<StrategyOptimizer.OptimizationCandidate> getAllCandidates() {
        return new ArrayList<>(candidates);
    }

    /**
     * 获取候选数量
     * @return 候选数量
     */
    public int getCandidateCount() {
        return candidates.size();
    }

    /**
     * 生成优化报告
     * @return 优化报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 策略优化报告 ===\n");
        report.append("总评估参数组合数: " + candidates.size() + "\n");
        
        if (!candidates.isEmpty()) {
            StrategyOptimizer.OptimizationCandidate best = getBestCandidate();
            report.append("\n最佳参数组合:\n");
            report.append(best.getParamSet().toString() + "\n");
            report.append("评分: " + best.getScore() + "\n");
            
            if (best.getResult() != null) {
                report.append("\n最佳策略回测结果:\n");
                report.append(best.getResult().generateReport());
            }
            
            // 显示前5个最佳候选
            if (candidates.size() > 1) {
                report.append("\n前5个最佳参数组合:\n");
                List<StrategyOptimizer.OptimizationCandidate> top5 = getTopCandidates(5);
                for (int i = 0; i < top5.size(); i++) {
                    StrategyOptimizer.OptimizationCandidate candidate = top5.get(i);
                    report.append((i + 1) + ". " + candidate.toString() + "\n");
                }
            }
        }
        
        report.append("================\n");
        return report.toString();
    }
}