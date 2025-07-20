package com.javamastery.llm.config;

/**
 * Configuration class demonstrating custom annotation usage.
 * Shows practical application of reflection and annotations.
 * 
 * Learning Objectives:
 * - Annotation-based configuration
 * - Reflection for field processing
 * - Type-safe configuration management
 * 
 * @author Java Mastery Student
 */
public class LLMExplorerConfig {
    
    @ConfigProperty(value = "llm.default.provider", defaultValue = "mock", 
                   description = "Default LLM provider to use")
    private String defaultProvider;
    
    @ConfigProperty(value = "llm.default.model", defaultValue = "mock-gpt-3.5", 
                   description = "Default model name")
    private String defaultModel;
    
    @ConfigProperty(value = "llm.max.tokens", defaultValue = "1000", 
                   description = "Maximum tokens per request")
    private int maxTokens;
    
    @ConfigProperty(value = "llm.temperature", defaultValue = "0.7", 
                   description = "Default temperature setting")
    private double temperature;
    
    @ConfigProperty(value = "benchmark.max.concurrency", defaultValue = "4", 
                   description = "Maximum concurrent benchmark threads")
    private int maxConcurrency;
    
    @ConfigProperty(value = "benchmark.timeout.seconds", defaultValue = "30", 
                   description = "Benchmark timeout in seconds")
    private long timeoutSeconds;
    
    @ConfigProperty(value = "analysis.word.min.length", defaultValue = "3", 
                   description = "Minimum word length for analysis")
    private int minWordLength;
    
    // Getters and setters
    public String getDefaultProvider() { return defaultProvider; }
    public void setDefaultProvider(String defaultProvider) { this.defaultProvider = defaultProvider; }
    
    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String defaultModel) { this.defaultModel = defaultModel; }
    
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getMaxConcurrency() { return maxConcurrency; }
    public void setMaxConcurrency(int maxConcurrency) { this.maxConcurrency = maxConcurrency; }
    
    public long getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(long timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    
    public int getMinWordLength() { return minWordLength; }
    public void setMinWordLength(int minWordLength) { this.minWordLength = minWordLength; }
}