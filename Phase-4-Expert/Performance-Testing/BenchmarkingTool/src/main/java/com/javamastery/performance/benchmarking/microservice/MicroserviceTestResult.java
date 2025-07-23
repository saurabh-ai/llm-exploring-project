package com.javamastery.performance.benchmarking.microservice;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of microservice stress testing
 */
public class MicroserviceTestResult {
    
    private String testName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private boolean success;
    private String errorMessage;
    
    private List<ServiceTestResult> serviceResults;
    
    // Overall Statistics
    private int totalRequests;
    private int totalSuccessfulRequests;
    private int totalFailedRequests;
    private double overallSuccessRate;
    private double averageResponseTime;
    private long maxResponseTime;
    private long minResponseTime;

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

    public List<ServiceTestResult> getServiceResults() {
        return serviceResults;
    }

    public void setServiceResults(List<ServiceTestResult> serviceResults) {
        this.serviceResults = serviceResults;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getTotalSuccessfulRequests() {
        return totalSuccessfulRequests;
    }

    public void setTotalSuccessfulRequests(int totalSuccessfulRequests) {
        this.totalSuccessfulRequests = totalSuccessfulRequests;
    }

    public int getTotalFailedRequests() {
        return totalFailedRequests;
    }

    public void setTotalFailedRequests(int totalFailedRequests) {
        this.totalFailedRequests = totalFailedRequests;
    }

    public double getOverallSuccessRate() {
        return overallSuccessRate;
    }

    public void setOverallSuccessRate(double overallSuccessRate) {
        this.overallSuccessRate = overallSuccessRate;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public long getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public long getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}

/**
 * Individual service test result
 */
class ServiceTestResult {
    private String serviceUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalRequests;
    private int successfulRequests;
    private int failedRequests;
    private double successRate;
    private double averageResponseTime;
    private long maxResponseTime;
    private long minResponseTime;

    // Getters and Setters
    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
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

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public long getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public long getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}

/**
 * Individual test request result
 */
class TestRequest {
    private String url;
    private long responseTime;
    private int statusCode;
    private String responseBody;
    private String errorMessage;
    private boolean success;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

/**
 * Circuit breaker test result
 */
class CircuitBreakerTestResult {
    private String serviceUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> responseCodes;
    private boolean circuitBreakerTriggered;
    private int consecutiveFailures;

    // Getters and Setters
    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
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

    public List<Integer> getResponseCodes() {
        return responseCodes;
    }

    public void setResponseCodes(List<Integer> responseCodes) {
        this.responseCodes = responseCodes;
    }

    public boolean isCircuitBreakerTriggered() {
        return circuitBreakerTriggered;
    }

    public void setCircuitBreakerTriggered(boolean circuitBreakerTriggered) {
        this.circuitBreakerTriggered = circuitBreakerTriggered;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(int consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }
}