package com.javamastery.performance.benchmarking.controller;

import com.javamastery.performance.benchmarking.dto.BenchmarkRequest;
import com.javamastery.performance.benchmarking.dto.BenchmarkResult;
import com.javamastery.performance.benchmarking.model.LoadTestScenario;
import com.javamastery.performance.benchmarking.service.JMeterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/benchmark")
@CrossOrigin(origins = "*")
public class BenchmarkController {
    
    private static final Logger logger = LoggerFactory.getLogger(BenchmarkController.class);
    
    @Autowired
    private JMeterService jMeterService;
    
    @PostMapping("/run")
    public ResponseEntity<CompletableFuture<BenchmarkResult>> runBenchmark(@RequestBody BenchmarkRequest request) {
        logger.info("Received benchmark request: {}", request.getTestName());
        
        try {
            CompletableFuture<BenchmarkResult> result = jMeterService.runBenchmark(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to start benchmark: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/run-sync")
    public ResponseEntity<BenchmarkResult> runBenchmarkSync(@RequestBody BenchmarkRequest request) {
        logger.info("Received synchronous benchmark request: {}", request.getTestName());
        
        try {
            BenchmarkResult result = jMeterService.runBenchmark(request).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to execute benchmark: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/scenarios")
    public ResponseEntity<List<LoadTestScenario>> getAvailableScenarios() {
        logger.info("Retrieving available load test scenarios");
        List<LoadTestScenario> scenarios = jMeterService.getAvailableScenarios();
        return ResponseEntity.ok(scenarios);
    }
    
    @PostMapping("/quick-test")
    public ResponseEntity<BenchmarkResult> runQuickTest(@RequestParam String host, 
                                                       @RequestParam(defaultValue = "/") String endpoint,
                                                       @RequestParam(defaultValue = "10") int threads) {
        logger.info("Running quick test on {}:{}", host, endpoint);
        
        BenchmarkRequest request = new BenchmarkRequest("Quick Test", host, endpoint);
        request.setThreads(threads);
        request.setIterations(50);
        request.setRampUpTime(10);
        request.setDurationSeconds(30);
        
        try {
            BenchmarkResult result = jMeterService.runBenchmark(request).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Quick test failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/scenario/{scenarioId}")
    public ResponseEntity<BenchmarkResult> runScenario(@PathVariable String scenarioId,
                                                      @RequestParam String host,
                                                      @RequestParam(defaultValue = "/") String endpoint) {
        logger.info("Running scenario {} on {}:{}", scenarioId, host, endpoint);
        
        List<LoadTestScenario> scenarios = jMeterService.getAvailableScenarios();
        LoadTestScenario scenario = scenarios.stream()
                .filter(s -> s.getId().equals(scenarioId))
                .findFirst()
                .orElse(null);
        
        if (scenario == null) {
            logger.error("Scenario not found: {}", scenarioId);
            return ResponseEntity.notFound().build();
        }
        
        BenchmarkRequest request = new BenchmarkRequest(scenario.getName(), host, endpoint);
        request.setThreads(scenario.getThreads());
        request.setIterations(scenario.getIterations());
        request.setRampUpTime(scenario.getRampUpTime());
        request.setDurationSeconds(scenario.getDurationSeconds());
        
        try {
            BenchmarkResult result = jMeterService.runBenchmark(request).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Scenario execution failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Benchmarking Tool is running");
    }
}