package com.javamastery.performance.benchmarking.controller;

import com.javamastery.performance.benchmarking.dto.BenchmarkRequest;
import com.javamastery.performance.benchmarking.dto.BenchmarkResult;
import com.javamastery.performance.benchmarking.model.LoadTestScenario;
import com.javamastery.performance.benchmarking.service.JMeterService;
import com.javamastery.performance.benchmarking.database.DatabasePerformanceService;
import com.javamastery.performance.benchmarking.database.DatabaseTestConfig;
import com.javamastery.performance.benchmarking.database.DatabasePerformanceResult;
import com.javamastery.performance.benchmarking.microservice.MicroserviceStressTestService;
import com.javamastery.performance.benchmarking.microservice.MicroserviceTestConfig;
import com.javamastery.performance.benchmarking.microservice.MicroserviceTestResult;
import com.javamastery.performance.benchmarking.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/benchmark")
@CrossOrigin(origins = "*")
@Tag(name = "Performance Benchmarking", description = "Enhanced performance testing with database and microservice capabilities")
public class BenchmarkController {
    
    private static final Logger logger = LoggerFactory.getLogger(BenchmarkController.class);
    
    @Autowired
    private JMeterService jMeterService;
    
    @Autowired
    private DatabasePerformanceService databasePerformanceService;
    
    @Autowired
    private MicroserviceStressTestService microserviceStressTestService;
    
    @Autowired
    private ReportingService reportingService;
    
    @PostMapping("/run")
    @Operation(summary = "Run load test", description = "Execute a load test with JMeter")
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
    @Operation(summary = "Run synchronous load test", description = "Execute a load test and wait for results")
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
    
    // Database Performance Testing Endpoints
    
    @PostMapping("/database/run")
    @Operation(summary = "Run database performance test", description = "Execute comprehensive database performance testing")
    public ResponseEntity<CompletableFuture<DatabasePerformanceResult>> runDatabaseTest(@RequestBody DatabaseTestConfig config) {
        logger.info("Received database performance test request: {}", config.getTestName());
        
        try {
            CompletableFuture<DatabasePerformanceResult> result = databasePerformanceService.runDatabasePerformanceTest(config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to start database test: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/database/run-sync")
    @Operation(summary = "Run synchronous database test", description = "Execute database performance test and wait for results")
    public ResponseEntity<DatabasePerformanceResult> runDatabaseTestSync(@RequestBody DatabaseTestConfig config) {
        logger.info("Received synchronous database test request: {}", config.getTestName());
        
        try {
            DatabasePerformanceResult result = databasePerformanceService.runDatabasePerformanceTest(config).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to execute database test: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Microservice Testing Endpoints
    
    @PostMapping("/microservice/run")
    @Operation(summary = "Run microservice stress test", description = "Execute stress testing on microservices")
    public ResponseEntity<CompletableFuture<MicroserviceTestResult>> runMicroserviceTest(@RequestBody MicroserviceTestConfig config) {
        logger.info("Received microservice test request: {}", config.getTestName());
        
        try {
            CompletableFuture<MicroserviceTestResult> result = microserviceStressTestService.runMicroserviceStressTest(config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to start microservice test: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/microservice/run-sync")
    @Operation(summary = "Run synchronous microservice test", description = "Execute microservice stress test and wait for results")
    public ResponseEntity<MicroserviceTestResult> runMicroserviceTestSync(@RequestBody MicroserviceTestConfig config) {
        logger.info("Received synchronous microservice test request: {}", config.getTestName());
        
        try {
            MicroserviceTestResult result = microserviceStressTestService.runMicroserviceStressTest(config).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to execute microservice test: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Reporting Endpoints
    
    @PostMapping("/report/generate")
    @Operation(summary = "Generate comprehensive report", description = "Generate HTML report with all test results")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody ReportRequest reportRequest) {
        logger.info("Generating comprehensive performance report");
        
        try {
            // This would typically fetch results from a storage mechanism
            // For now, we'll use empty lists as placeholders
            List<BenchmarkResult> benchmarkResults = new ArrayList<>();
            List<DatabasePerformanceResult> databaseResults = new ArrayList<>();
            List<MicroserviceTestResult> microserviceResults = new ArrayList<>();
            
            String htmlReport = reportingService.generateHtmlReport(benchmarkResults, databaseResults, microserviceResults);
            String filePath = reportingService.saveReportToFile(htmlReport, reportRequest.getReportName());
            
            Map<String, Object> response = reportingService.generateJsonSummary(benchmarkResults, databaseResults, microserviceResults);
            response.put("reportFilePath", filePath);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to generate report: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/report/summary")
    @Operation(summary = "Get test summary", description = "Get JSON summary of all test results")
    public ResponseEntity<Map<String, Object>> getTestSummary() {
        logger.info("Retrieving test summary");
        
        try {
            // Placeholder for actual implementation
            List<BenchmarkResult> benchmarkResults = new ArrayList<>();
            List<DatabasePerformanceResult> databaseResults = new ArrayList<>();
            List<MicroserviceTestResult> microserviceResults = new ArrayList<>();
            
            Map<String, Object> summary = reportingService.generateJsonSummary(benchmarkResults, databaseResults, microserviceResults);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Failed to get test summary: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/scenarios")
    @Operation(summary = "Get available test scenarios", description = "Retrieve all predefined load test scenarios")
    public ResponseEntity<List<LoadTestScenario>> getAvailableScenarios() {
        logger.info("Retrieving available load test scenarios");
        List<LoadTestScenario> scenarios = jMeterService.getAvailableScenarios();
        return ResponseEntity.ok(scenarios);
    }
    
    @PostMapping("/quick-test")
    @Operation(summary = "Run quick test", description = "Execute a quick performance test with minimal configuration")
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
    @Operation(summary = "Run predefined scenario", description = "Execute a predefined load test scenario")
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
    @Operation(summary = "Health check", description = "Check if the benchmarking service is running")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Enhanced Benchmarking Tool is running with database and microservice testing capabilities");
    }
}

/**
 * Request object for report generation
 */
class ReportRequest {
    private String reportName = "Performance Test Report";
    
    public String getReportName() {
        return reportName;
    }
    
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}