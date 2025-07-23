package com.javamastery.performance.benchmarking.microservice;

import java.util.List;
import java.util.Map;

/**
 * Configuration for microservice stress testing
 */
public class MicroserviceTestConfig {
    
    private String testName;
    private List<String> serviceUrls;
    private int requestsPerService = 100;
    private int concurrentThreads = 10;
    private String httpMethod = "GET";
    private String requestBody;
    private Map<String, String> headers;
    private int timeoutSeconds = 30;
    
    public MicroserviceTestConfig() {}
    
    public MicroserviceTestConfig(String testName, List<String> serviceUrls) {
        this.testName = testName;
        this.serviceUrls = serviceUrls;
    }

    // Getters and Setters
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public List<String> getServiceUrls() {
        return serviceUrls;
    }

    public void setServiceUrls(List<String> serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    public int getRequestsPerService() {
        return requestsPerService;
    }

    public void setRequestsPerService(int requestsPerService) {
        this.requestsPerService = requestsPerService;
    }

    public int getConcurrentThreads() {
        return concurrentThreads;
    }

    public void setConcurrentThreads(int concurrentThreads) {
        this.concurrentThreads = concurrentThreads;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}