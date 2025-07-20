package com.javamastery.llm.client;

import java.util.concurrent.CompletableFuture;

/**
 * Core interface for LLM (Large Language Model) clients.
 * This interface demonstrates the Strategy pattern, allowing different LLM
 * implementations to be used interchangeably.
 * 
 * Learning Objectives:
 * - Interface segregation principle
 * - Strategy design pattern
 * - CompletableFuture for asynchronous operations
 * - Generic programming with bounded wildcards
 * 
 * Key Concepts Demonstrated:
 * - Abstract factory pattern foundation
 * - Asynchronous programming with CompletableFuture
 * - Clean interface design for pluggable architectures
 * 
 * @author Java Mastery Student
 */
public interface LLMClient {
    
    /**
     * Sends a prompt to the LLM and returns a synchronous response.
     * 
     * @param prompt the input prompt to send to the LLM
     * @return the LLM's response
     * @throws LLMClientException if the request fails
     */
    LLMResponse sendPrompt(String prompt) throws LLMClientException;
    
    /**
     * Sends a prompt to the LLM and returns an asynchronous response.
     * This method demonstrates CompletableFuture usage for non-blocking operations.
     * 
     * @param prompt the input prompt to send to the LLM
     * @return a CompletableFuture containing the LLM's response
     */
    CompletableFuture<LLMResponse> sendPromptAsync(String prompt);
    
    /**
     * Sends a structured prompt request to the LLM.
     * 
     * @param request the structured prompt request
     * @return the LLM's response
     * @throws LLMClientException if the request fails
     */
    LLMResponse sendRequest(LLMRequest request) throws LLMClientException;
    
    /**
     * Sends a structured prompt request to the LLM asynchronously.
     * 
     * @param request the structured prompt request
     * @return a CompletableFuture containing the LLM's response
     */
    CompletableFuture<LLMResponse> sendRequestAsync(LLMRequest request);
    
    /**
     * Returns the name/identifier of this LLM provider.
     * 
     * @return the provider name (e.g., "OpenAI", "Claude", "Mock")
     */
    String getProviderName();
    
    /**
     * Returns the model name or version being used.
     * 
     * @return the model name/version
     */
    String getModelName();
    
    /**
     * Checks if the client is properly configured and ready to use.
     * 
     * @return true if the client is ready, false otherwise
     */
    boolean isReady();
    
    /**
     * Returns configuration information about this client.
     * 
     * @return configuration details as a string
     */
    String getConfiguration();
}