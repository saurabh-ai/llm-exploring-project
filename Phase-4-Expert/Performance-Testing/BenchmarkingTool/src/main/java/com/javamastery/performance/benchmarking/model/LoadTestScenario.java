package com.javamastery.performance.benchmarking.model;

public class LoadTestScenario {
    
    private String id;
    private String name;
    private String description;
    private int threads;
    private int rampUpTime;
    private int iterations;
    private int durationSeconds;
    
    public LoadTestScenario() {}
    
    public LoadTestScenario(String id, String name, String description, 
                           int threads, int rampUpTime, int iterations, int durationSeconds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.threads = threads;
        this.rampUpTime = rampUpTime;
        this.iterations = iterations;
        this.durationSeconds = durationSeconds;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
}