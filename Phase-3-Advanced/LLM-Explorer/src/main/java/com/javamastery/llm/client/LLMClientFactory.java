package com.javamastery.llm.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

/**
 * Factory class for creating LLM client instances.
 * Demonstrates the Factory pattern and dynamic provider registration.
 * 
 * Learning Objectives:
 * - Factory design pattern implementation
 * - Dynamic registration and discovery
 * - Singleton pattern with thread safety
 * - Optional usage for null safety
 * 
 * Key Concepts Demonstrated:
 * - Abstract factory pattern
 * - Provider registration mechanism
 * - Thread-safe singleton implementation
 * - Defensive programming with Optional
 * 
 * @author Java Mastery Student
 */
public class LLMClientFactory {
    
    private static volatile LLMClientFactory instance;
    private final Map<String, LLMClientProvider> providers = new HashMap<>();
    
    /**
     * Private constructor for singleton pattern.
     */
    private LLMClientFactory() {
        // Register default providers
        registerDefaultProviders();
    }
    
    /**
     * Returns the singleton instance of LLMClientFactory.
     * Implements double-checked locking for thread safety.
     * 
     * @return the factory instance
     */
    public static LLMClientFactory getInstance() {
        if (instance == null) {
            synchronized (LLMClientFactory.class) {
                if (instance == null) {
                    instance = new LLMClientFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * Creates an LLM client for the specified provider.
     * 
     * @param providerName the name of the provider (e.g., "openai", "claude", "mock")
     * @return Optional containing the LLM client if provider exists, empty otherwise
     */
    public Optional<LLMClient> createClient(String providerName) {
        return createClient(providerName, new HashMap<>());
    }
    
    /**
     * Creates an LLM client for the specified provider with configuration.
     * 
     * @param providerName the name of the provider
     * @param config configuration parameters for the client
     * @return Optional containing the LLM client if provider exists, empty otherwise
     */
    public Optional<LLMClient> createClient(String providerName, Map<String, Object> config) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return Optional.empty();
        }
        
        LLMClientProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            return Optional.empty();
        }
        
        try {
            LLMClient client = provider.createClient(config);
            return Optional.ofNullable(client);
        } catch (Exception e) {
            // Log error in real application
            System.err.println("Failed to create client for provider " + providerName + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Registers a new LLM client provider.
     * 
     * @param providerName the name of the provider
     * @param provider the provider implementation
     */
    public void registerProvider(String providerName, LLMClientProvider provider) {
        if (providerName == null || provider == null) {
            throw new IllegalArgumentException("Provider name and provider cannot be null");
        }
        providers.put(providerName.toLowerCase(), provider);
    }
    
    /**
     * Unregisters an LLM client provider.
     * 
     * @param providerName the name of the provider to remove
     * @return true if provider was removed, false if it didn't exist
     */
    public boolean unregisterProvider(String providerName) {
        if (providerName == null) {
            return false;
        }
        return providers.remove(providerName.toLowerCase()) != null;
    }
    
    /**
     * Returns the names of all registered providers.
     * 
     * @return set of provider names
     */
    public Set<String> getAvailableProviders() {
        return Set.copyOf(providers.keySet());
    }
    
    /**
     * Checks if a provider is registered.
     * 
     * @param providerName the name of the provider
     * @return true if the provider is registered, false otherwise
     */
    public boolean isProviderAvailable(String providerName) {
        return providerName != null && providers.containsKey(providerName.toLowerCase());
    }
    
    /**
     * Registers default providers that are always available.
     */
    private void registerDefaultProviders() {
        // Register mock provider for testing and demonstration
        registerProvider("mock", new MockLLMClientProvider());
        
        // In a real application, you would register actual providers here:
        // registerProvider("openai", new OpenAIClientProvider());
        // registerProvider("claude", new ClaudeClientProvider());
    }
    
    /**
     * Interface for LLM client providers.
     * Demonstrates the Abstract Factory pattern.
     */
    @FunctionalInterface
    public interface LLMClientProvider {
        /**
         * Creates a new LLM client instance.
         * 
         * @param config configuration parameters
         * @return the created LLM client
         * @throws Exception if client creation fails
         */
        LLMClient createClient(Map<String, Object> config) throws Exception;
    }
    
    /**
     * Mock provider for testing and demonstration purposes.
     * Inner class demonstrating provider implementation.
     */
    private static class MockLLMClientProvider implements LLMClientProvider {
        @Override
        public LLMClient createClient(Map<String, Object> config) {
            return new MockLLMClient(config);
        }
    }
}