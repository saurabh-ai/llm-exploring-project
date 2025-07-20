package com.javamastery.llm.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Unit tests for LLMClientFactory.
 * Demonstrates unit testing with JUnit 5 and factory pattern testing.
 * 
 * Learning Objectives:
 * - JUnit 5 testing framework usage
 * - Factory pattern testing strategies
 * - Optional handling and null safety testing
 * - Concurrent testing patterns
 * 
 * @author Java Mastery Student
 */
public class LLMClientFactoryTest {
    
    private LLMClientFactory factory;
    
    @BeforeEach
    void setUp() {
        factory = LLMClientFactory.getInstance();
    }
    
    @Test
    void testSingletonBehavior() {
        LLMClientFactory factory1 = LLMClientFactory.getInstance();
        LLMClientFactory factory2 = LLMClientFactory.getInstance();
        
        assertSame(factory1, factory2, "Factory should follow singleton pattern");
    }
    
    @Test
    void testAvailableProviders() {
        var providers = factory.getAvailableProviders();
        
        assertFalse(providers.isEmpty(), "Should have at least one provider");
        assertTrue(providers.contains("mock"), "Should contain mock provider");
    }
    
    @Test
    void testCreateMockClient() {
        var clientOpt = factory.createClient("mock");
        
        assertTrue(clientOpt.isPresent(), "Mock client should be available");
        
        LLMClient client = clientOpt.get();
        assertEquals("Mock", client.getProviderName());
        assertTrue(client.isReady());
    }
    
    @Test
    void testCreateClientWithConfiguration() {
        Map<String, Object> config = Map.of(
            "model", "test-model",
            "errorRate", 0.0 // No errors for testing
        );
        
        var clientOpt = factory.createClient("mock", config);
        
        assertTrue(clientOpt.isPresent());
        LLMClient client = clientOpt.get();
        assertTrue(client.getConfiguration().contains("test-model"));
    }
    
    @Test
    void testCreateNonExistentProvider() {
        var clientOpt = factory.createClient("nonexistent");
        
        assertTrue(clientOpt.isEmpty(), "Should return empty for non-existent provider");
    }
    
    @Test
    void testNullProviderName() {
        var clientOpt = factory.createClient(null);
        
        assertTrue(clientOpt.isEmpty(), "Should return empty for null provider name");
    }
    
    @Test
    void testProviderRegistration() {
        // Create a mock provider
        LLMClientFactory.LLMClientProvider testProvider = 
            config -> new MockLLMClient(config);
        
        factory.registerProvider("test", testProvider);
        
        assertTrue(factory.isProviderAvailable("test"));
        var providers = factory.getAvailableProviders();
        assertTrue(providers.contains("test"));
        
        // Clean up
        factory.unregisterProvider("test");
        assertFalse(factory.isProviderAvailable("test"));
    }
}