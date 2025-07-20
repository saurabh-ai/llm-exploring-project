package com.javamastery.llm.client;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Mock implementation of LLMClient for testing and demonstration purposes.
 * This class simulates LLM behavior without making actual API calls.
 * 
 * Learning Objectives:
 * - Mock object pattern for testing
 * - CompletableFuture for asynchronous operations
 * - Simulated latency and error conditions
 * - Configuration handling and validation
 * 
 * Key Concepts Demonstrated:
 * - Test doubles and mock implementations
 * - Asynchronous programming patterns
 * - Random response generation
 * - Error simulation for testing
 * 
 * @author Java Mastery Student
 */
public class MockLLMClient implements LLMClient {
    
    private final String modelName;
    private final int minResponseTimeMs;
    private final int maxResponseTimeMs;
    private final double errorRate;
    private final Random random;
    
    private static final String[] MOCK_RESPONSES = {
        "This is a mock response from the LLM. The request has been processed successfully.",
        "I understand your question. Here's a detailed explanation of the concept you asked about.",
        "Based on the information provided, I can help you with this problem.",
        "That's an interesting question! Let me break it down into several key points.",
        "Here's a comprehensive answer to your query with examples and explanations.",
        "I'll provide you with a step-by-step solution to address your request.",
        "This topic involves several important considerations that I'll explain in detail.",
        "Great question! The answer involves understanding the relationship between these concepts."
    };
    
    /**
     * Constructs a MockLLMClient with default configuration.
     */
    public MockLLMClient() {
        this(Map.of());
    }
    
    /**
     * Constructs a MockLLMClient with specified configuration.
     * 
     * @param config configuration parameters
     */
    public MockLLMClient(Map<String, Object> config) {
        this.modelName = (String) config.getOrDefault("model", "mock-llm-v1.0");
        this.minResponseTimeMs = (Integer) config.getOrDefault("minResponseTime", 100);
        this.maxResponseTimeMs = (Integer) config.getOrDefault("maxResponseTime", 1000);
        this.errorRate = (Double) config.getOrDefault("errorRate", 0.05); // 5% error rate
        this.random = new Random();
        
        validateConfiguration();
    }
    
    @Override
    public LLMResponse sendPrompt(String prompt) throws LLMClientException {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new LLMClientException("Prompt cannot be null or empty", "Mock", 400);
        }
        
        // Simulate processing time
        simulateProcessingDelay();
        
        // Simulate errors
        if (shouldSimulateError()) {
            throw new LLMClientException("Simulated API error", "Mock", 500);
        }
        
        // Generate mock response
        return generateMockResponse(prompt, generateRequestId());
    }
    
    @Override
    public CompletableFuture<LLMResponse> sendPromptAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendPrompt(prompt);
            } catch (LLMClientException e) {
                // Convert checked exception to runtime exception for CompletableFuture
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public LLMResponse sendRequest(LLMRequest request) throws LLMClientException {
        if (request == null) {
            throw new LLMClientException("Request cannot be null", "Mock", 400);
        }
        
        // Simulate processing time
        simulateProcessingDelay();
        
        // Simulate errors
        if (shouldSimulateError()) {
            throw new LLMClientException("Simulated API error for request: " + request.getRequestId(), 
                                       "Mock", 500);
        }
        
        // Generate mock response based on request parameters
        return generateMockResponse(request);
    }
    
    @Override
    public CompletableFuture<LLMResponse> sendRequestAsync(LLMRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendRequest(request);
            } catch (LLMClientException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    @Override
    public String getProviderName() {
        return "Mock";
    }
    
    @Override
    public String getModelName() {
        return modelName;
    }
    
    @Override
    public boolean isReady() {
        return true; // Mock client is always ready
    }
    
    @Override
    public String getConfiguration() {
        return String.format("MockLLMClient{model='%s', responseTime=%d-%dms, errorRate=%.2f%%}",
                           modelName, minResponseTimeMs, maxResponseTimeMs, errorRate * 100);
    }
    
    /**
     * Validates the configuration parameters.
     */
    private void validateConfiguration() {
        if (minResponseTimeMs < 0 || maxResponseTimeMs < 0 || minResponseTimeMs > maxResponseTimeMs) {
            throw new IllegalArgumentException("Invalid response time configuration");
        }
        if (errorRate < 0.0 || errorRate > 1.0) {
            throw new IllegalArgumentException("Error rate must be between 0.0 and 1.0");
        }
    }
    
    /**
     * Simulates processing delay to mimic real API behavior.
     */
    private void simulateProcessingDelay() {
        int delay = random.nextInt(maxResponseTimeMs - minResponseTimeMs + 1) + minResponseTimeMs;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Determines if an error should be simulated based on the error rate.
     */
    private boolean shouldSimulateError() {
        return random.nextDouble() < errorRate;
    }
    
    /**
     * Generates a mock response for a simple prompt.
     */
    private LLMResponse generateMockResponse(String prompt, String requestId) {
        long responseTime = maxResponseTimeMs - minResponseTimeMs;
        
        // Select a random mock response
        String content = MOCK_RESPONSES[random.nextInt(MOCK_RESPONSES.length)];
        
        // Add some context based on prompt length
        if (prompt.length() > 100) {
            content += " Your question was quite detailed, so I've provided a comprehensive response.";
        }
        
        // Create mock usage statistics
        int promptTokens = Math.max(1, prompt.length() / 4); // Rough token estimation
        int completionTokens = Math.max(1, content.length() / 4);
        LLMResponse.Usage usage = new LLMResponse.Usage(
            promptTokens, 
            completionTokens, 
            promptTokens + completionTokens
        );
        
        return new LLMResponse(content, modelName, "stop", usage, requestId, responseTime);
    }
    
    /**
     * Generates a mock response for a structured request.
     */
    private LLMResponse generateMockResponse(LLMRequest request) {
        long responseTime = maxResponseTimeMs - minResponseTimeMs;
        
        // Generate response based on request parameters
        String content = MOCK_RESPONSES[random.nextInt(MOCK_RESPONSES.length)];
        
        // Adjust response based on temperature (higher temperature = more random)
        if (request.getTemperature() > 1.0) {
            content += " [Creative mode enabled due to high temperature setting]";
        }
        
        // Respect max tokens (roughly)
        int maxChars = request.getMaxTokens() * 4; // Rough character estimation
        if (content.length() > maxChars) {
            content = content.substring(0, maxChars - 3) + "...";
        }
        
        // Create mock usage statistics
        int promptTokens = Math.max(1, request.getPrompt().length() / 4);
        int completionTokens = Math.min(request.getMaxTokens(), content.length() / 4);
        LLMResponse.Usage usage = new LLMResponse.Usage(
            promptTokens, 
            completionTokens, 
            promptTokens + completionTokens
        );
        
        return new LLMResponse(content, modelName, "stop", usage, request.getRequestId(), responseTime);
    }
    
    /**
     * Generates a unique request ID for tracking.
     */
    private String generateRequestId() {
        return "mock_req_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
    }
}