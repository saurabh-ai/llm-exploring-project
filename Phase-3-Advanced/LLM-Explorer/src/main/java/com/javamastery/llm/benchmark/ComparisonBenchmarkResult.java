package com.javamastery.llm.benchmark;

import java.util.Comparator;
import java.util.Map;

/**
 * Immutable class representing comparison results between multiple LLM providers.
 * 
 * @author Java Mastery Student
 */
public class ComparisonBenchmarkResult {
    private final Map<String, BenchmarkResult> results;
    
    public ComparisonBenchmarkResult(Map<String, BenchmarkResult> results) {
        this.results = Map.copyOf(results);
    }
    
    public Map<String, BenchmarkResult> getResults() { return results; }
    
    public String getBestPerformer() {
        return results.entrySet().stream()
                     .max(Comparator.comparing(entry -> entry.getValue().getSuccessRate()))
                     .map(Map.Entry::getKey)
                     .orElse("None");
    }
    
    public String getFastestProvider() {
        return results.entrySet().stream()
                     .min(Comparator.comparing(entry -> entry.getValue().getAverageResponseTime()))
                     .map(Map.Entry::getKey)
                     .orElse("None");
    }
}