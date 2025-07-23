package com.javamastery.performance.benchmarking.database;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Result of database performance testing
 */
public class DatabasePerformanceResult {
    
    private String testName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private boolean success;
    private String errorMessage;
    
    private Map<String, Long> testResults;
    
    // Performance Statistics
    private long totalExecutionTime;
    private double averageOperationTime;
    private long maxOperationTime;
    private long minOperationTime;
    
    // Getters and Setters
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Long> getTestResults() {
        return testResults;
    }

    public void setTestResults(Map<String, Long> testResults) {
        this.testResults = testResults;
    }

    public long getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setTotalExecutionTime(long totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public double getAverageOperationTime() {
        return averageOperationTime;
    }

    public void setAverageOperationTime(double averageOperationTime) {
        this.averageOperationTime = averageOperationTime;
    }

    public long getMaxOperationTime() {
        return maxOperationTime;
    }

    public void setMaxOperationTime(long maxOperationTime) {
        this.maxOperationTime = maxOperationTime;
    }

    public long getMinOperationTime() {
        return minOperationTime;
    }

    public void setMinOperationTime(long minOperationTime) {
        this.minOperationTime = minOperationTime;
    }
}