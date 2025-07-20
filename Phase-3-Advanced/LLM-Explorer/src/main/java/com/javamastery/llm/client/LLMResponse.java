package com.javamastery.llm.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a response from an LLM service.
 * Demonstrates immutable data class design with comprehensive response information.
 * 
 * Learning Objectives:
 * - Immutable response object design
 * - JSON deserialization with Jackson
 * - Proper data validation and error handling
 * - Usage statistics and metadata handling
 * 
 * Key Concepts Demonstrated:
 * - Value object pattern
 * - Defensive copying for mutable collections
 * - Comprehensive toString implementation
 * 
 * @author Java Mastery Student
 */
public class LLMResponse {
    
    @JsonProperty("content")
    private final String content;
    
    @JsonProperty("model")
    private final String model;
    
    @JsonProperty("finish_reason")
    private final String finishReason;
    
    @JsonProperty("usage")
    private final Usage usage;
    
    private final LocalDateTime timestamp;
    private final String requestId;
    private final long responseTimeMs;
    private final boolean success;
    private final String errorMessage;
    
    /**
     * Constructor for successful responses.
     */
    public LLMResponse(String content, String model, String finishReason, 
                      Usage usage, String requestId, long responseTimeMs) {
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.model = Objects.requireNonNull(model, "Model cannot be null");
        this.finishReason = finishReason;
        this.usage = usage;
        this.requestId = requestId;
        this.responseTimeMs = responseTimeMs;
        this.timestamp = LocalDateTime.now();
        this.success = true;
        this.errorMessage = null;
    }
    
    /**
     * Constructor for error responses.
     */
    public LLMResponse(String errorMessage, String requestId, long responseTimeMs) {
        this.content = "";
        this.model = "";
        this.finishReason = "error";
        this.usage = null;
        this.requestId = requestId;
        this.responseTimeMs = responseTimeMs;
        this.timestamp = LocalDateTime.now();
        this.success = false;
        this.errorMessage = Objects.requireNonNull(errorMessage, "Error message cannot be null");
    }
    
    // Getters
    public String getContent() { return content; }
    public String getModel() { return model; }
    public String getFinishReason() { return finishReason; }
    public Usage getUsage() { return usage; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    
    /**
     * Returns the length of the response content.
     */
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }
    
    /**
     * Returns the number of words in the response content.
     * Demonstrates Stream API usage for text processing.
     */
    public long getWordCount() {
        if (content == null || content.trim().isEmpty()) {
            return 0;
        }
        return content.trim()
                     .split("\\s+")
                     .length;
    }
    
    /**
     * Represents usage statistics from the LLM API.
     * Nested static class demonstrating composition.
     */
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private final int promptTokens;
        
        @JsonProperty("completion_tokens")
        private final int completionTokens;
        
        @JsonProperty("total_tokens")
        private final int totalTokens;
        
        public Usage(int promptTokens, int completionTokens, int totalTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = totalTokens;
        }
        
        public int getPromptTokens() { return promptTokens; }
        public int getCompletionTokens() { return completionTokens; }
        public int getTotalTokens() { return totalTokens; }
        
        @Override
        public String toString() {
            return String.format("Usage{prompt=%d, completion=%d, total=%d}", 
                               promptTokens, completionTokens, totalTokens);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Usage usage = (Usage) obj;
            return promptTokens == usage.promptTokens &&
                   completionTokens == usage.completionTokens &&
                   totalTokens == usage.totalTokens;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(promptTokens, completionTokens, totalTokens);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LLMResponse that = (LLMResponse) obj;
        return responseTimeMs == that.responseTimeMs &&
               success == that.success &&
               Objects.equals(content, that.content) &&
               Objects.equals(model, that.model) &&
               Objects.equals(finishReason, that.finishReason) &&
               Objects.equals(usage, that.usage) &&
               Objects.equals(requestId, that.requestId) &&
               Objects.equals(errorMessage, that.errorMessage);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(content, model, finishReason, usage, 
                           requestId, responseTimeMs, success, errorMessage);
    }
    
    @Override
    public String toString() {
        if (!success) {
            return String.format("LLMResponse{ERROR: %s, requestId='%s', responseTime=%dms}", 
                               errorMessage, requestId, responseTimeMs);
        }
        
        return String.format("LLMResponse{model='%s', contentLength=%d, wordCount=%d, " +
                           "finishReason='%s', usage=%s, responseTime=%dms, requestId='%s'}", 
                           model, getContentLength(), getWordCount(), finishReason, 
                           usage, responseTimeMs, requestId);
    }
}