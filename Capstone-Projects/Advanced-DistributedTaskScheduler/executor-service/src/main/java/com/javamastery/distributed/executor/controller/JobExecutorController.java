package com.javamastery.distributed.executor.controller;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.dto.JobExecutionDto;
import com.javamastery.distributed.executor.service.JobExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for Job Execution
 * Provides API for job execution operations
 */
@RestController
@RequestMapping("/api/v1/executor")
@Tag(name = "Job Execution", description = "APIs for job execution operations")
@CrossOrigin(origins = "*")
public class JobExecutorController {
    
    private static final Logger logger = LoggerFactory.getLogger(JobExecutorController.class);
    
    private final JobExecutionService jobExecutionService;
    
    @Autowired
    public JobExecutorController(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }
    
    /**
     * Execute job
     */
    @PostMapping("/execute")
    @Operation(summary = "Execute job", description = "Executes a job asynchronously")
    public ResponseEntity<CompletableFuture<JobExecutionDto>> executeJob(
            @RequestBody JobDto jobDto) {
        logger.info("Request to execute job: {}", jobDto.getName());
        
        CompletableFuture<JobExecutionDto> execution = jobExecutionService.executeJob(jobDto);
        return ResponseEntity.ok(execution);
    }
    
    /**
     * Get node information
     */
    @GetMapping("/node/info")
    @Operation(summary = "Get node information", description = "Returns information about this executor node")
    public ResponseEntity<ExecutorNodeInfo> getNodeInfo() {
        ExecutorNodeInfo nodeInfo = new ExecutorNodeInfo(
            jobExecutionService.getNodeId(),
            jobExecutionService.getNodeStatus(),
            jobExecutionService.isHealthy()
        );
        return ResponseEntity.ok(nodeInfo);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks the health of the executor service")
    public ResponseEntity<String> healthCheck() {
        boolean healthy = jobExecutionService.isHealthy();
        if (healthy) {
            return ResponseEntity.ok("Executor service is healthy");
        } else {
            return ResponseEntity.status(503).body("Executor service is unhealthy");
        }
    }
    
    // Inner class for node information response
    public static class ExecutorNodeInfo {
        private String nodeId;
        private String status;
        private boolean healthy;
        
        public ExecutorNodeInfo(String nodeId, String status, boolean healthy) {
            this.nodeId = nodeId;
            this.status = status;
            this.healthy = healthy;
        }
        
        public String getNodeId() { return nodeId; }
        public String getStatus() { return status; }
        public boolean isHealthy() { return healthy; }
    }
}