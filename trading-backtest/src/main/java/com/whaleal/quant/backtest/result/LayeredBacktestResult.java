package com.whaleal.quant.backtest.result;

import com.whaleal.quant.backtest.engine.BacktestLayer.Layer;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class LayeredBacktestResult {
    
    private final Map<Layer, BacktestResult> layerResults;
    
    public LayeredBacktestResult(Map<Layer, BacktestResult> layerResults) {
        this.layerResults = new EnumMap<>(layerResults);
    }
    
    public BacktestResult getLayerResult(Layer layer) {
        return layerResults.get(layer);
    }
    
    public Map<Layer, BacktestResult> getLayerResults() {
        return layerResults;
    }
    
    public double getAverageReturn() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("totalReturn", 0.0))
                .average()
                .orElse(0.0);
    }
    
    public double getBestReturn() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("totalReturn", 0.0))
                .max()
                .orElse(0.0);
    }
    
    public double getWorstReturn() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("totalReturn", 0.0))
                .min()
                .orElse(0.0);
    }
    
    public double getAverageSharpeRatio() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("sharpeRatio", 0.0))
                .average()
                .orElse(0.0);
    }
    
    public double getAverageMaxDrawdown() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("maxDrawdown", 0.0))
                .average()
                .orElse(0.0);
    }
    
    public double getAverageWinRate() {
        return layerResults.values().stream()
                .mapToDouble(result -> result.getMetrics().getOrDefault("winRate", 0.0))
                .average()
                .orElse(0.0);
    }
    
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Layered Backtest Results Summary\n");
        summary.append("====================================\n");
        
        for (Layer layer : Layer.values()) {
            BacktestResult result = layerResults.get(layer);
            if (result != null) {
                summary.append(String.format("%s:\n", layer.getName()));
                summary.append(String.format("  Time Interval: %s\n", layer.getTimeInterval()));
                summary.append(String.format("  Total Return: %.4f\n", result.getMetrics().getOrDefault("totalReturn", 0.0)));
                summary.append(String.format("  Sharpe Ratio: %.4f\n", result.getMetrics().getOrDefault("sharpeRatio", 0.0)));
                summary.append(String.format("  Max Drawdown: %.4f\n", result.getMetrics().getOrDefault("maxDrawdown", 0.0)));
                summary.append(String.format("  Win Rate: %.4f\n", result.getMetrics().getOrDefault("winRate", 0.0)));
                summary.append("\n");
            }
        }
        
        summary.append("Overall Summary:\n");
        summary.append("====================================\n");
        summary.append(String.format("Average Return: %.4f\n", getAverageReturn()));
        summary.append(String.format("Best Return: %.4f\n", getBestReturn()));
        summary.append(String.format("Worst Return: %.4f\n", getWorstReturn()));
        summary.append(String.format("Average Sharpe Ratio: %.4f\n", getAverageSharpeRatio()));
        summary.append(String.format("Average Max Drawdown: %.4f\n", getAverageMaxDrawdown()));
        summary.append(String.format("Average Win Rate: %.4f\n", getAverageWinRate()));
        
        return summary.toString();
    }
}
