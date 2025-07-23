package com.javamastery.performance.benchmarking.microservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for microservice performance testing and stress testing
 */
@Service
public class MicroserviceStressTestService {
    
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceStressTestService.class);
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public MicroserviceStressTestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Run comprehensive microservice stress test
     */
    public CompletableFuture<MicroserviceTestResult> runMicroserviceStressTest(MicroserviceTestConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Starting microservice stress test: {}", config.getTestName());
            
            MicroserviceTestResult result = new MicroserviceTestResult();
            result.setTestName(config.getTestName());
            result.setStartTime(LocalDateTime.now());
            
            try {
                List<ServiceTestResult> serviceResults = new ArrayList<>();
                
                // Test each service endpoint
                for (String serviceUrl : config.getServiceUrls()) {
                    ServiceTestResult serviceResult = testService(serviceUrl, config);
                    serviceResults.add(serviceResult);
                }
                
                result.setServiceResults(serviceResults);
                result.setSuccess(true);
                
                // Calculate overall statistics
                calculateOverallStatistics(result, serviceResults);
                
            } catch (Exception e) {
                logger.error("Microservice stress test failed: {}", e.getMessage(), e);
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            } finally {
                result.setEndTime(LocalDateTime.now());
            }
            
            return result;
        });
    }
    
    private ServiceTestResult testService(String serviceUrl, MicroserviceTestConfig config) {
        logger.info("Testing service: {}", serviceUrl);
        
        ServiceTestResult result = new ServiceTestResult();
        result.setServiceUrl(serviceUrl);
        result.setStartTime(LocalDateTime.now());
        
        List<Long> responseTimes = new ArrayList<>();
        List<Integer> statusCodes = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        
        ExecutorService executor = Executors.newFixedThreadPool(config.getConcurrentThreads());
        List<CompletableFuture<TestRequest>> futures = new ArrayList<>();
        
        // Execute concurrent requests
        for (int i = 0; i < config.getRequestsPerService(); i++) {
            futures.add(CompletableFuture.supplyAsync(() -> makeRequest(serviceUrl, config), executor));
        }
        
        // Collect results
        for (CompletableFuture<TestRequest> future : futures) {
            try {
                TestRequest testRequest = future.get();
                responseTimes.add(testRequest.getResponseTime());
                statusCodes.add(testRequest.getStatusCode());
                
                if (testRequest.isSuccess()) {
                    successCount++;
                } else {
                    errorCount++;
                }
            } catch (Exception e) {
                logger.error("Request failed: {}", e.getMessage());
                errorCount++;
            }
        }
        
        executor.shutdown();
        
        // Calculate statistics
        result.setTotalRequests(config.getRequestsPerService());
        result.setSuccessfulRequests(successCount);
        result.setFailedRequests(errorCount);
        result.setSuccessRate((double) successCount / config.getRequestsPerService() * 100);
        
        if (!responseTimes.isEmpty()) {
            result.setAverageResponseTime(responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0));
            result.setMinResponseTime(responseTimes.stream().mapToLong(Long::longValue).min().orElse(0L));
            result.setMaxResponseTime(responseTimes.stream().mapToLong(Long::longValue).max().orElse(0L));
        }
        
        result.setEndTime(LocalDateTime.now());
        
        logger.info("Service test completed: {} - Success Rate: {:.2f}%", 
                   serviceUrl, result.getSuccessRate());
        
        return result;
    }
    
    private TestRequest makeRequest(String serviceUrl, MicroserviceTestConfig config) {
        TestRequest testRequest = new TestRequest();
        testRequest.setUrl(serviceUrl);
        
        long startTime = System.currentTimeMillis();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Add custom headers if configured
            if (config.getHeaders() != null) {
                config.getHeaders().forEach(headers::add);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(config.getRequestBody(), headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                serviceUrl,
                HttpMethod.valueOf(config.getHttpMethod()),
                entity,
                String.class
            );
            
            testRequest.setStatusCode(response.getStatusCode().value());
            testRequest.setResponseBody(response.getBody());
            testRequest.setSuccess(response.getStatusCode().is2xxSuccessful());
            
        } catch (Exception e) {
            logger.debug("Request to {} failed: {}", serviceUrl, e.getMessage());
            testRequest.setStatusCode(500);
            testRequest.setErrorMessage(e.getMessage());
            testRequest.setSuccess(false);
        } finally {
            testRequest.setResponseTime(System.currentTimeMillis() - startTime);
        }
        
        return testRequest;
    }
    
    private void calculateOverallStatistics(MicroserviceTestResult result, List<ServiceTestResult> serviceResults) {
        if (serviceResults.isEmpty()) {
            return;
        }
        
        int totalRequests = serviceResults.stream().mapToInt(ServiceTestResult::getTotalRequests).sum();
        int totalSuccessful = serviceResults.stream().mapToInt(ServiceTestResult::getSuccessfulRequests).sum();
        int totalFailed = serviceResults.stream().mapToInt(ServiceTestResult::getFailedRequests).sum();
        
        result.setTotalRequests(totalRequests);
        result.setTotalSuccessfulRequests(totalSuccessful);
        result.setTotalFailedRequests(totalFailed);
        result.setOverallSuccessRate((double) totalSuccessful / totalRequests * 100);
        
        double avgResponseTime = serviceResults.stream()
                .mapToDouble(ServiceTestResult::getAverageResponseTime)
                .average()
                .orElse(0.0);
        result.setAverageResponseTime(avgResponseTime);
        
        long maxResponseTime = serviceResults.stream()
                .mapToLong(ServiceTestResult::getMaxResponseTime)
                .max()
                .orElse(0L);
        result.setMaxResponseTime(maxResponseTime);
        
        long minResponseTime = serviceResults.stream()
                .mapToLong(ServiceTestResult::getMinResponseTime)
                .min()
                .orElse(0L);
        result.setMinResponseTime(minResponseTime);
        
        logger.info("Overall microservice test statistics: {}% success rate, avg response: {}ms",
                   String.format("%.2f", result.getOverallSuccessRate()), 
                   String.format("%.2f", result.getAverageResponseTime()));
    }
    
    /**
     * Test circuit breaker behavior by gradually increasing load
     */
    public CompletableFuture<CircuitBreakerTestResult> testCircuitBreaker(String serviceUrl, int maxRequests) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Testing circuit breaker behavior for: {}", serviceUrl);
            
            CircuitBreakerTestResult result = new CircuitBreakerTestResult();
            result.setServiceUrl(serviceUrl);
            result.setStartTime(LocalDateTime.now());
            
            List<Integer> responseCodes = new ArrayList<>();
            boolean circuitBreakerTriggered = false;
            int consecutiveFailures = 0;
            
            for (int i = 0; i < maxRequests; i++) {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl, String.class);
                    responseCodes.add(response.getStatusCode().value());
                    
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        consecutiveFailures++;
                        if (consecutiveFailures >= 5) {
                            circuitBreakerTriggered = true;
                            break;
                        }
                    } else {
                        consecutiveFailures = 0;
                    }
                    
                } catch (Exception e) {
                    responseCodes.add(500);
                    consecutiveFailures++;
                    if (consecutiveFailures >= 5) {
                        circuitBreakerTriggered = true;
                        break;
                    }
                }
                
                // Brief pause between requests
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            result.setResponseCodes(responseCodes);
            result.setCircuitBreakerTriggered(circuitBreakerTriggered);
            result.setConsecutiveFailures(consecutiveFailures);
            result.setEndTime(LocalDateTime.now());
            
            logger.info("Circuit breaker test completed. Triggered: {}, Consecutive failures: {}", 
                       circuitBreakerTriggered, consecutiveFailures);
            
            return result;
        });
    }
}