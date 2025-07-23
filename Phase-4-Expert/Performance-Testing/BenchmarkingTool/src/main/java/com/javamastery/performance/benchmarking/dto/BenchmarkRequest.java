package com.javamastery.performance.benchmarking.dto;

import java.util.Map;

public class BenchmarkRequest {
    
    private String testName;
    private String host;
    private int port = 80;
    private String protocol = "http";
    private String endpoint = "/";
    private String httpMethod = "GET";
    private int threads = 10;
    private int rampUpTime = 30; // seconds
    private int iterations = 100;
    private int durationSeconds = 60;
    private int connectionTimeout = 5000; // milliseconds
    private int responseTimeout = 10000; // milliseconds
    private Map<String, String> headers;
    private String requestBody;
    
    public BenchmarkRequest() {}
    
    public BenchmarkRequest(String testName, String host, String endpoint) {
        this.testName = testName;
        this.host = host;
        this.endpoint = endpoint;
    }
    
    // Getters and Setters
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public int getThreads() {
        return threads;
    }
    
    public void setThreads(int threads) {
        this.threads = threads;
    }
    
    public int getRampUpTime() {
        return rampUpTime;
    }
    
    public void setRampUpTime(int rampUpTime) {
        this.rampUpTime = rampUpTime;
    }
    
    public int getIterations() {
        return iterations;
    }
    
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getResponseTimeout() {
        return responseTimeout;
    }
    
    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public String getRequestBody() {
        return requestBody;
    }
    
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}