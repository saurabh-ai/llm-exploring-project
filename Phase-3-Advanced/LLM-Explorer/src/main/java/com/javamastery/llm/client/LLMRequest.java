package com.javamastery.llm.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a request to an LLM service.
 * Demonstrates proper data class design with builder pattern.
 * 
 * Learning Objectives:
 * - Immutable data class design
 * - Builder pattern implementation
 * - JSON serialization with Jackson
 * - Proper equals/hashCode implementation
 * 
 * Key Concepts Demonstrated:
 * - Value object pattern
 * - Fluent interface design
 * - Jackson annotations for JSON mapping
 * 
 * @author Java Mastery Student
 */
public class LLMRequest {
    
    @JsonProperty("prompt")
    private final String prompt;
    
    @JsonProperty("max_tokens")
    private final int maxTokens;
    
    @JsonProperty("temperature")
    private final double temperature;
    
    @JsonProperty("model")
    private final String model;
    
    @JsonProperty("system_message")
    private final String systemMessage;
    
    @JsonProperty("stream")
    private final boolean stream;
    
    private final LocalDateTime timestamp;
    private final String requestId;
    
    /**
     * Private constructor - use Builder to create instances.
     */
    private LLMRequest(Builder builder) {
        this.prompt = builder.prompt;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.model = builder.model;
        this.systemMessage = builder.systemMessage;
        this.stream = builder.stream;
        this.timestamp = LocalDateTime.now();
        this.requestId = generateRequestId();
    }
    
    // Getters
    public String getPrompt() { return prompt; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperature() { return temperature; }
    public String getModel() { return model; }
    public String getSystemMessage() { return systemMessage; }
    public boolean isStream() { return stream; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }
    
    /**
     * Creates a new Builder instance for constructing LLMRequest objects.
     * 
     * @param prompt the prompt text (required)
     * @return a new Builder instance
     */
    public static Builder builder(String prompt) {
        return new Builder(prompt);
    }
    
    /**
     * Builder class for creating LLMRequest instances.
     * Demonstrates the Builder pattern for complex object construction.
     */
    public static class Builder {
        private final String prompt;
        private int maxTokens = 1000;
        private double temperature = 0.7;
        private String model = "default";
        private String systemMessage = null;
        private boolean stream = false;
        
        private Builder(String prompt) {
            this.prompt = Objects.requireNonNull(prompt, "Prompt cannot be null");
        }
        
        public Builder maxTokens(int maxTokens) {
            if (maxTokens <= 0) {
                throw new IllegalArgumentException("Max tokens must be positive");
            }
            this.maxTokens = maxTokens;
            return this;
        }
        
        public Builder temperature(double temperature) {
            if (temperature < 0.0 || temperature > 2.0) {
                throw new IllegalArgumentException("Temperature must be between 0.0 and 2.0");
            }
            this.temperature = temperature;
            return this;
        }
        
        public Builder model(String model) {
            this.model = Objects.requireNonNull(model, "Model cannot be null");
            return this;
        }
        
        public Builder systemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
            return this;
        }
        
        public Builder stream(boolean stream) {
            this.stream = stream;
            return this;
        }
        
        public LLMRequest build() {
            return new LLMRequest(this);
        }
    }
    
    /**
     * Generates a unique request ID for tracking purposes.
     */
    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + 
               Integer.toHexString(System.identityHashCode(this));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LLMRequest that = (LLMRequest) obj;
        return maxTokens == that.maxTokens &&
               Double.compare(that.temperature, temperature) == 0 &&
               stream == that.stream &&
               Objects.equals(prompt, that.prompt) &&
               Objects.equals(model, that.model) &&
               Objects.equals(systemMessage, that.systemMessage);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(prompt, maxTokens, temperature, model, systemMessage, stream);
    }
    
    @Override
    public String toString() {
        return "LLMRequest{" +
               "requestId='" + requestId + '\'' +
               ", prompt='" + (prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt) + '\'' +
               ", maxTokens=" + maxTokens +
               ", temperature=" + temperature +
               ", model='" + model + '\'' +
               ", systemMessage=" + (systemMessage != null ? "present" : "null") +
               ", stream=" + stream +
               ", timestamp=" + timestamp +
               '}';
    }
}