package com.javamastery.llm.benchmark;

import com.javamastery.llm.client.LLMClient;
import com.javamastery.llm.client.LLMRequest;
import com.javamastery.llm.client.LLMResponse;
import com.javamastery.llm.client.LLMClientException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Performance benchmarking tool for LLM clients using CompletableFuture.
 * Demonstrates advanced asynchronous programming and performance measurement.
 * 
 * Learning Objectives:
 * - CompletableFuture for asynchronous operations
 * - Thread pool management with ExecutorService
 * - Concurrent performance measurement
 * - Statistical analysis of performance metrics
 * 
 * Key Concepts Demonstrated:
 * - Asynchronous programming patterns
 * - Parallel execution and coordination
 * - Performance metrics collection
 * - Resource management with try-with-resources
 * 
 * @author Java Mastery Student
 */
public class LLMBenchmark {
    
    private final ExecutorService executorService;
    private final int maxConcurrency;
    
    public LLMBenchmark(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
        this.executorService = Executors.newFixedThreadPool(maxConcurrency);
    }
    
    /**
     * Runs a comprehensive benchmark on a single LLM client.
     * Demonstrates CompletableFuture usage for concurrent testing.
     * 
     * @param client the LLM client to benchmark
     * @param testPrompts list of prompts to test with
     * @param iterations number of iterations per prompt
     * @return benchmark results
     */
    public CompletableFuture<BenchmarkResult> runBenchmark(LLMClient client, 
                                                          List<String> testPrompts, 
                                                          int iterations) {
        return CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture<ResponseMetrics>> futures = new ArrayList<>();
            
            // Create async tasks for each prompt-iteration combination
            for (String prompt : testPrompts) {
                for (int i = 0; i < iterations; i++) {
                    CompletableFuture<ResponseMetrics> future = measureResponse(client, prompt, i);
                    futures.add(future);
                }
            }
            
            // Wait for all futures to complete and collect results
            List<ResponseMetrics> allMetrics = futures.stream()
                                                     .map(CompletableFuture::join)
                                                     .filter(Objects::nonNull)
                                                     .collect(Collectors.toList());
            
            return analyzeBenchmarkResults(client.getProviderName(), allMetrics);
        }, executorService);
    }
    
    /**
     * Compares performance between multiple LLM clients.
     * Demonstrates parallel execution and comparison of results.
     * 
     * @param clients map of client names to LLM clients
     * @param testPrompts prompts to test
     * @param iterations iterations per prompt
     * @return future containing comparison results
     */
    public CompletableFuture<ComparisonBenchmarkResult> compareBenchmark(
            Map<String, LLMClient> clients, List<String> testPrompts, int iterations) {
        
        // Run benchmarks for all clients in parallel
        Map<String, CompletableFuture<BenchmarkResult>> benchmarkFutures = clients.entrySet()
                                                                                  .stream()
                                                                                  .collect(Collectors.toMap(
                                                                                      Map.Entry::getKey,
                                                                                      entry -> runBenchmark(entry.getValue(), testPrompts, iterations)
                                                                                  ));
        
        // Wait for all benchmarks to complete
        return CompletableFuture.allOf(benchmarkFutures.values().toArray(new CompletableFuture[0]))
                               .thenApply(v -> {
                                   Map<String, BenchmarkResult> results = benchmarkFutures.entrySet()
                                                                                         .stream()
                                                                                         .collect(Collectors.toMap(
                                                                                             Map.Entry::getKey,
                                                                                             entry -> entry.getValue().join()
                                                                                         ));
                                   return new ComparisonBenchmarkResult(results);
                               });
    }
    
    /**
     * Runs a load test to measure performance under concurrent load.
     * Demonstrates high-concurrency testing with CompletableFuture.
     * 
     * @param client the client to test
     * @param prompt the prompt to use
     * @param concurrentRequests number of concurrent requests
     * @return future containing load test results
     */
    public CompletableFuture<LoadTestResult> runLoadTest(LLMClient client, String prompt, 
                                                        int concurrentRequests) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            
            // Create concurrent requests
            List<CompletableFuture<ResponseMetrics>> futures = IntStream.range(0, concurrentRequests)
                                                                       .mapToObj(i -> measureResponse(client, prompt, i))
                                                                       .collect(Collectors.toList());
            
            // Wait for all requests to complete
            List<ResponseMetrics> metrics = futures.stream()
                                                  .map(CompletableFuture::join)
                                                  .filter(Objects::nonNull)
                                                  .collect(Collectors.toList());
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            return analyzeLoadTestResults(client.getProviderName(), metrics, totalTime, concurrentRequests);
        }, executorService);
    }
    
    /**
     * Measures the performance of a single request.
     * Demonstrates error handling in asynchronous operations.
     */
    private CompletableFuture<ResponseMetrics> measureResponse(LLMClient client, String prompt, int iteration) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();
            
            try {
                LLMResponse response = client.sendPrompt(prompt);
                long endTime = System.nanoTime();
                long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                
                return new ResponseMetrics(iteration, prompt, response, responseTime, true, null);
            } catch (LLMClientException e) {
                long endTime = System.nanoTime();
                long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                
                return new ResponseMetrics(iteration, prompt, null, responseTime, false, e.getMessage());
            }
        }, executorService);
    }
    
    /**
     * Analyzes benchmark results and creates summary statistics.
     * Demonstrates Stream API usage for statistical analysis.
     */
    private BenchmarkResult analyzeBenchmarkResults(String providerName, List<ResponseMetrics> metrics) {
        List<ResponseMetrics> successful = metrics.stream()
                                                 .filter(ResponseMetrics::isSuccessful)
                                                 .collect(Collectors.toList());
        
        if (successful.isEmpty()) {
            return new BenchmarkResult(providerName, 0, metrics.size(), 0.0, 0.0, 0.0, 0.0);
        }
        
        double successRate = (double) successful.size() / metrics.size();
        double avgResponseTime = successful.stream()
                                         .mapToDouble(ResponseMetrics::getResponseTimeMs)
                                         .average()
                                         .orElse(0.0);
        
        List<Double> responseTimes = successful.stream()
                                             .map(ResponseMetrics::getResponseTimeMs)
                                             .sorted()
                                             .collect(Collectors.toList());
        
        double p50 = calculatePercentile(responseTimes, 0.5);
        double p95 = calculatePercentile(responseTimes, 0.95);
        double p99 = calculatePercentile(responseTimes, 0.99);
        
        return new BenchmarkResult(providerName, successful.size(), metrics.size(), 
                                 successRate, avgResponseTime, p95, p99);
    }
    
    /**
     * Analyzes load test results.
     */
    private LoadTestResult analyzeLoadTestResults(String providerName, List<ResponseMetrics> metrics, 
                                                 long totalTimeMs, int concurrentRequests) {
        int successful = (int) metrics.stream().filter(ResponseMetrics::isSuccessful).count();
        double successRate = (double) successful / metrics.size();
        double throughput = (double) successful / totalTimeMs * 1000; // requests per second
        
        double avgResponseTime = metrics.stream()
                                       .filter(ResponseMetrics::isSuccessful)
                                       .mapToDouble(ResponseMetrics::getResponseTimeMs)
                                       .average()
                                       .orElse(0.0);
        
        return new LoadTestResult(providerName, concurrentRequests, successful, 
                                totalTimeMs, successRate, throughput, avgResponseTime);
    }
    
    /**
     * Calculates a percentile value from a sorted list of numbers.
     */
    private double calculatePercentile(List<Double> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }
        
        int index = (int) Math.ceil(percentile * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(index, sortedValues.size() - 1));
        
        return sortedValues.get(index);
    }
    
    /**
     * Shuts down the executor service.
     * Should be called when benchmarking is complete.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}