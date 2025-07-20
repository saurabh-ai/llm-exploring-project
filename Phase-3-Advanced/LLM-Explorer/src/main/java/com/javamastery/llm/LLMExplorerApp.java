package com.javamastery.llm;

import com.javamastery.llm.analysis.AnalysisResult;
import com.javamastery.llm.analysis.ResponseAnalyzer;
import com.javamastery.llm.analysis.WordFrequencyEntry;
import com.javamastery.llm.benchmark.BenchmarkResult;
import com.javamastery.llm.benchmark.LoadTestResult;
import com.javamastery.llm.benchmark.LLMBenchmark;
import com.javamastery.llm.client.*;
import com.javamastery.llm.prompt.PromptTemplate;
import com.javamastery.llm.prompt.SimplePromptTemplate;
import com.javamastery.llm.prompt.TemplateMetadata;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Main application class for the LLM Explorer.
 * Demonstrates integration of all components and comprehensive usage of advanced Java concepts.
 * 
 * Learning Objectives:
 * - Application architecture and component integration
 * - CompletableFuture orchestration for complex workflows
 * - Resource management and proper cleanup
 * - Error handling in complex applications
 * 
 * Key Concepts Demonstrated:
 * - Facade pattern for simplified API access
 * - Asynchronous workflow coordination
 * - Factory pattern usage for object creation
 * - Stream API for data processing and presentation
 * 
 * @author Java Mastery Student
 */
public class LLMExplorerApp {
    
    private static final String[] SAMPLE_PROMPTS = {
        "Explain the concept of polymorphism in Java programming.",
        "What are the benefits of using design patterns in software development?",
        "Describe the difference between ArrayList and LinkedList in Java.",
        "How does the Java garbage collector work?",
        "What is the significance of the CompletableFuture class in Java?"
    };
    
    private final LLMClientFactory clientFactory;
    private final LLMBenchmark benchmark;
    
    public LLMExplorerApp() {
        this.clientFactory = LLMClientFactory.getInstance();
        this.benchmark = new LLMBenchmark(4); // 4 concurrent threads
    }
    
    public static void main(String[] args) {
        LLMExplorerApp app = new LLMExplorerApp();
        
        try {
            app.runComprehensiveDemo();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.shutdown();
        }
    }
    
    /**
     * Runs a comprehensive demonstration of all LLM Explorer features.
     * Demonstrates integration of all components in a realistic workflow.
     */
    public void runComprehensiveDemo() throws Exception {
        System.out.println("🚀 LLM Explorer - Advanced Java Concepts Demonstration");
        System.out.println("=" .repeat(60));
        
        // 1. Demonstrate Factory Pattern and Client Management
        demonstrateClientFactory();
        
        // 2. Demonstrate Prompt Template System
        demonstratePromptTemplates();
        
        // 3. Demonstrate Basic LLM Interactions
        demonstrateLLMInteractions();
        
        // 4. Demonstrate Response Analysis with Stream API
        demonstrateResponseAnalysis();
        
        // 5. Demonstrate Asynchronous Benchmarking
        demonstrateAsyncBenchmarking();
        
        // 6. Demonstrate Load Testing
        demonstrateLoadTesting();
        
        System.out.println("\n✅ All demonstrations completed successfully!");
    }
    
    /**
     * Demonstrates the Factory pattern for LLM client creation.
     */
    private void demonstrateClientFactory() {
        System.out.println("\n📋 1. Factory Pattern - LLM Client Creation");
        System.out.println("-".repeat(40));
        
        // Show available providers
        Set<String> providers = clientFactory.getAvailableProviders();
        System.out.printf("Available providers: %s%n", providers);
        
        // Create mock client with custom configuration
        Map<String, Object> config = Map.of(
            "model", "mock-gpt-4",
            "minResponseTime", 200,
            "maxResponseTime", 800,
            "errorRate", 0.01  // Reduced error rate for better demo
        );
        
        Optional<LLMClient> client = clientFactory.createClient("mock", config);
        if (client.isPresent()) {
            System.out.printf("✅ Created client: %s%n", client.get().getConfiguration());
        } else {
            System.out.println("❌ Failed to create client");
        }
    }
    
    /**
     * Demonstrates the prompt template system.
     */
    private void demonstratePromptTemplates() throws Exception {
        System.out.println("\n📝 2. Template Method Pattern - Prompt Management");
        System.out.println("-".repeat(40));
        
        // Create a template for code explanation prompts
        String templateContent = "As an expert {{language}} developer, please explain the following concept: {{concept}}. " +
                               "{{optional_context}} Please provide examples and best practices.";
        
        PromptTemplate template = new SimplePromptTemplate(
            "code-explanation",
            templateContent,
            new String[]{"optional_context"}, // optional parameters
            TemplateMetadata.builder("code-explanation")
                           .description("Template for explaining programming concepts")
                           .category("education")
                           .author("LLM Explorer")
                           .tags("programming", "education", "explanation")
                           .build()
        );
        
        // Demonstrate template usage
        Map<String, Object> params = Map.of(
            "language", "Java",
            "concept", "polymorphism",
            "optional_context", "Focus on runtime polymorphism."
        );
        
        String renderedPrompt = template.render(params);
        System.out.printf("📋 Template: %s%n", template.getName());
        System.out.printf("✨ Rendered: %s%n", renderedPrompt.substring(0, Math.min(100, renderedPrompt.length())) + "...");
    }
    
    /**
     * Demonstrates basic LLM client interactions.
     */
    private void demonstrateLLMInteractions() throws Exception {
        System.out.println("\n🤖 3. Strategy Pattern - LLM Client Interactions");
        System.out.println("-".repeat(40));
        
        Optional<LLMClient> clientOpt = clientFactory.createClient("mock");
        if (clientOpt.isEmpty()) {
            System.out.println("❌ No client available for demonstration");
            return;
        }
        
        LLMClient client = clientOpt.get();
        
        // Demonstrate synchronous request
        String prompt = "What is object-oriented programming?";
        System.out.printf("📤 Prompt: %s%n", prompt);
        
        LLMResponse response = client.sendPrompt(prompt);
        System.out.printf("📥 Response: %s%n", response.getContent().substring(0, Math.min(100, response.getContent().length())) + "...");
        System.out.printf("⏱️  Response time: %d ms%n", response.getResponseTimeMs());
        
        // Demonstrate structured request
        LLMRequest request = LLMRequest.builder("Explain Java streams")
                                     .maxTokens(500)
                                     .temperature(0.7)
                                     .model("mock-gpt-4")
                                     .build();
        
        LLMResponse structuredResponse = client.sendRequest(request);
        System.out.printf("📥 Structured response length: %d characters%n", structuredResponse.getContentLength());
    }
    
    /**
     * Demonstrates response analysis using Stream API.
     */
    private void demonstrateResponseAnalysis() throws Exception {
        System.out.println("\n📊 4. Stream API - Response Analysis");
        System.out.println("-".repeat(40));
        
        Optional<LLMClient> clientOpt = clientFactory.createClient("mock");
        if (clientOpt.isEmpty()) return;
        
        LLMClient client = clientOpt.get();
        
        // Generate multiple responses for analysis
        List<LLMResponse> responses = Arrays.stream(SAMPLE_PROMPTS)
                                          .map(prompt -> {
                                              try {
                                                  return client.sendPrompt(prompt);
                                              } catch (LLMClientException e) {
                                                  return null;
                                              }
                                          })
                                          .filter(Objects::nonNull)
                                          .collect(Collectors.toList());
        
        // Analyze responses using Stream API
        AnalysisResult analysis = ResponseAnalyzer.analyzeResponses(responses);
        System.out.printf("📈 Analysis Results:%n%s%n", analysis);
        
        // Demonstrate top words analysis
        var topWords = ResponseAnalyzer.getTopWords(responses, 5);
        System.out.printf("🏆 Top words: %s%n", 
                         topWords.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")));
    }
    
    /**
     * Demonstrates asynchronous benchmarking with CompletableFuture.
     */
    private void demonstrateAsyncBenchmarking() throws Exception {
        System.out.println("\n⚡ 5. CompletableFuture - Asynchronous Benchmarking");
        System.out.println("-".repeat(40));
        
        Optional<LLMClient> clientOpt = clientFactory.createClient("mock");
        if (clientOpt.isEmpty()) return;
        
        LLMClient client = clientOpt.get();
        List<String> testPrompts = Arrays.asList(SAMPLE_PROMPTS).subList(0, 3);
        
        // Run benchmark asynchronously
        System.out.println("🚀 Starting asynchronous benchmark...");
        CompletableFuture<BenchmarkResult> benchmarkFuture = benchmark.runBenchmark(client, testPrompts, 2);
        
        // Do other work while benchmark runs
        System.out.println("💼 Performing other work while benchmark runs...");
        Thread.sleep(1000); // Simulate other work
        
        // Get benchmark results
        BenchmarkResult result = benchmarkFuture.get();
        System.out.printf("📊 Benchmark completed: %s%n", result);
    }
    
    /**
     * Demonstrates load testing capabilities.
     */
    private void demonstrateLoadTesting() throws Exception {
        System.out.println("\n🔥 6. Concurrent Programming - Load Testing");
        System.out.println("-".repeat(40));
        
        Optional<LLMClient> clientOpt = clientFactory.createClient("mock");
        if (clientOpt.isEmpty()) return;
        
        LLMClient client = clientOpt.get();
        
        // Run load test with concurrent requests
        System.out.println("🧪 Starting load test with 10 concurrent requests...");
        CompletableFuture<LoadTestResult> loadTestFuture = benchmark.runLoadTest(
            client, "Explain design patterns", 10);
        
        LoadTestResult loadResult = loadTestFuture.get();
        System.out.printf("🎯 Load test completed: %s%n", loadResult);
    }
    
    /**
     * Performs cleanup and resource management.
     */
    public void shutdown() {
        System.out.println("\n🔄 Shutting down resources...");
        if (benchmark != null) {
            benchmark.shutdown();
        }
        System.out.println("✅ Shutdown completed.");
    }
}