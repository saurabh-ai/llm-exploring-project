package com.javamastery.performance.benchmarking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class BenchmarkResult {
    
    private String testName;
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private double averageResponseTime; // milliseconds
    private double minResponseTime; // milliseconds
    private double maxResponseTime; // milliseconds
    private double throughput; // requests per second
    private double errorRate; // percentage as decimal (0.05 = 5%)
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executionTime;
    
    private int durationSeconds;
    private String errorMessage;
    
    public BenchmarkResult() {}
    
    // Getters and Setters
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public int getTotalRequests() {
        return totalRequests;
    }
    
    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
    
    public int getSuccessfulRequests() {
        return successfulRequests;
    }
    
    public void setSuccessfulRequests(int successfulRequests) {
        this.successfulRequests = successfulRequests;
    }
    
    public int getFailedRequests() {
        return failedRequests;
    }
    
    public void setFailedRequests(int failedRequests) {
        this.failedRequests = failedRequests;
    }
    
    public double getAverageResponseTime() {
        return averageResponseTime;
    }
    
    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }
    
    public double getMinResponseTime() {
        return minResponseTime;
    }
    
    public void setMinResponseTime(double minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
    
    public double getMaxResponseTime() {
        return maxResponseTime;
    }
    
    public void setMaxResponseTime(double maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }
    
    public double getThroughput() {
        return throughput;
    }
    
    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }
    
    public double getErrorRate() {
        return errorRate;
    }
    
    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }
    
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    // Convenience methods
    public double getSuccessRate() {
        if (totalRequests == 0) return 0.0;
        return (double) successfulRequests / totalRequests;
    }
    
    public String getFormattedErrorRate() {
        return String.format("%.2f%%", errorRate * 100);
    }
    
    public String getFormattedSuccessRate() {
        return String.format("%.2f%%", getSuccessRate() * 100);
    }
}