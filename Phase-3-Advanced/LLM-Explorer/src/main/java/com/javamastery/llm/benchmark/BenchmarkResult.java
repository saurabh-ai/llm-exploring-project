package com.javamastery.llm.benchmark;

/**
 * Immutable class representing benchmark results for a single LLM provider.
 * 
 * @author Java Mastery Student
 */
public class BenchmarkResult {
    private final String providerName;
    private final int successfulRequests;
    private final int totalRequests;
    private final double successRate;
    private final double averageResponseTime;
    private final double p95ResponseTime;
    private final double p99ResponseTime;
    
    public BenchmarkResult(String providerName, int successfulRequests, int totalRequests,
                          double successRate, double averageResponseTime,
                          double p95ResponseTime, double p99ResponseTime) {
        this.providerName = providerName;
        this.successfulRequests = successfulRequests;
        this.totalRequests = totalRequests;
        this.successRate = successRate;
        this.averageResponseTime = averageResponseTime;
        this.p95ResponseTime = p95ResponseTime;
        this.p99ResponseTime = p99ResponseTime;
    }
    
    // Getters
    public String getProviderName() { return providerName; }
    public int getSuccessfulRequests() { return successfulRequests; }
    public int getTotalRequests() { return totalRequests; }
    public double getSuccessRate() { return successRate; }
    public double getAverageResponseTime() { return averageResponseTime; }
    public double getP95ResponseTime() { return p95ResponseTime; }
    public double getP99ResponseTime() { return p99ResponseTime; }
    
    @Override
    public String toString() {
        return String.format("BenchmarkResult{provider='%s', success=%d/%d (%.2f%%), " +
                           "avgTime=%.2fms, p95=%.2fms, p99=%.2fms}",
                           providerName, successfulRequests, totalRequests, successRate * 100,
                           averageResponseTime, p95ResponseTime, p99ResponseTime);
    }
}