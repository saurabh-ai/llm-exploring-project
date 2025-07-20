package com.javamastery.llm.benchmark;

/**
 * Immutable class representing load test results.
 * 
 * @author Java Mastery Student
 */
public class LoadTestResult {
    private final String providerName;
    private final int concurrentRequests;
    private final int successfulRequests;
    private final long totalTimeMs;
    private final double successRate;
    private final double throughput;
    private final double averageResponseTime;
    
    public LoadTestResult(String providerName, int concurrentRequests, int successfulRequests,
                         long totalTimeMs, double successRate, double throughput, 
                         double averageResponseTime) {
        this.providerName = providerName;
        this.concurrentRequests = concurrentRequests;
        this.successfulRequests = successfulRequests;
        this.totalTimeMs = totalTimeMs;
        this.successRate = successRate;
        this.throughput = throughput;
        this.averageResponseTime = averageResponseTime;
    }
    
    // Getters
    public String getProviderName() { return providerName; }
    public int getConcurrentRequests() { return concurrentRequests; }
    public int getSuccessfulRequests() { return successfulRequests; }
    public long getTotalTimeMs() { return totalTimeMs; }
    public double getSuccessRate() { return successRate; }
    public double getThroughput() { return throughput; }
    public double getAverageResponseTime() { return averageResponseTime; }
    
    @Override
    public String toString() {
        return String.format("LoadTestResult{provider='%s', concurrent=%d, success=%d (%.2f%%), " +
                           "throughput=%.2f req/s, avgTime=%.2fms, totalTime=%dms}",
                           providerName, concurrentRequests, successfulRequests, successRate * 100,
                           throughput, averageResponseTime, totalTimeMs);
    }
}