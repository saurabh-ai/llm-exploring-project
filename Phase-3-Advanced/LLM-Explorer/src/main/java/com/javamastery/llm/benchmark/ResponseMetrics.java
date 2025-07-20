package com.javamastery.llm.benchmark;

import com.javamastery.llm.client.LLMResponse;

/**
 * Immutable class representing metrics for a single response measurement.
 * 
 * @author Java Mastery Student
 */
public class ResponseMetrics {
    private final int iteration;
    private final String prompt;
    private final LLMResponse response;
    private final double responseTimeMs;
    private final boolean successful;
    private final String errorMessage;
    
    public ResponseMetrics(int iteration, String prompt, LLMResponse response, 
                          double responseTimeMs, boolean successful, String errorMessage) {
        this.iteration = iteration;
        this.prompt = prompt;
        this.response = response;
        this.responseTimeMs = responseTimeMs;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }
    
    // Getters
    public int getIteration() { return iteration; }
    public String getPrompt() { return prompt; }
    public LLMResponse getResponse() { return response; }
    public double getResponseTimeMs() { return responseTimeMs; }
    public boolean isSuccessful() { return successful; }
    public String getErrorMessage() { return errorMessage; }
}