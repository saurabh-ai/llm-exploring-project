package com.javamastery.llm.client;

/**
 * Custom exception class for LLM client operations.
 * Demonstrates proper exception hierarchy design and error handling.
 * 
 * Learning Objectives:
 * - Custom exception design
 * - Exception chaining and cause preservation
 * - Proper exception messaging
 * 
 * @author Java Mastery Student
 */
public class LLMClientException extends Exception {
    
    private final String providerName;
    private final int errorCode;
    
    /**
     * Constructs a new LLMClientException with the specified detail message.
     * 
     * @param message the detail message
     */
    public LLMClientException(String message) {
        super(message);
        this.providerName = "Unknown";
        this.errorCode = -1;
    }
    
    /**
     * Constructs a new LLMClientException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval)
     */
    public LLMClientException(String message, Throwable cause) {
        super(message, cause);
        this.providerName = "Unknown";
        this.errorCode = -1;
    }
    
    /**
     * Constructs a new LLMClientException with provider-specific information.
     * 
     * @param message the detail message
     * @param providerName the name of the LLM provider
     * @param errorCode the provider-specific error code
     */
    public LLMClientException(String message, String providerName, int errorCode) {
        super(message);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new LLMClientException with provider-specific information and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     * @param providerName the name of the LLM provider
     * @param errorCode the provider-specific error code
     */
    public LLMClientException(String message, Throwable cause, String providerName, int errorCode) {
        super(message, cause);
        this.providerName = providerName;
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the name of the LLM provider that generated this exception.
     * 
     * @return the provider name
     */
    public String getProviderName() {
        return providerName;
    }
    
    /**
     * Returns the provider-specific error code.
     * 
     * @return the error code, or -1 if not specified
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        if (providerName != null && !providerName.equals("Unknown")) {
            sb.append("[").append(providerName).append("]");
        }
        if (errorCode != -1) {
            sb.append("[").append(errorCode).append("]");
        }
        sb.append(": ").append(getMessage());
        return sb.toString();
    }
}