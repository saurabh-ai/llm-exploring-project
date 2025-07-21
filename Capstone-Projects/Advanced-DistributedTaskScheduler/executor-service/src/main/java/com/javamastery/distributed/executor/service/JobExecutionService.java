package com.javamastery.distributed.executor.service;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.dto.JobExecutionDto;
import com.javamastery.distributed.common.enums.JobStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Job Execution Service
 * Demonstrates Strategy Pattern for different execution strategies
 */
@Service
public class JobExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);
    private final String nodeId;
    
    public JobExecutionService() {
        this.nodeId = "executor-node-" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("Job Execution Service initialized with node ID: {}", nodeId);
    }
    
    /**
     * Execute job asynchronously
     */
    @Async
    @CircuitBreaker(name = "job-execution", fallbackMethod = "executeJobFallback")
    public CompletableFuture<JobExecutionDto> executeJob(JobDto jobDto) {
        logger.info("Starting execution of job: {} on node: {}", jobDto.getName(), nodeId);
        
        JobExecutionDto execution = new JobExecutionDto(jobDto.getId(), jobDto.getName(), nodeId);
        execution.setExecutionId(UUID.randomUUID().toString());
        
        try {
            // Simulate job execution based on job class
            String result = executeJobByClass(jobDto);
            
            execution.markCompleted(result);
            logger.info("Job {} completed successfully on node: {}", jobDto.getName(), nodeId);
            
        } catch (Exception ex) {
            execution.markFailed(ex.getMessage(), getStackTrace(ex));
            logger.error("Job {} failed on node: {}: {}", jobDto.getName(), nodeId, ex.getMessage(), ex);
        }
        
        return CompletableFuture.completedFuture(execution);
    }
    
    /**
     * Execute job based on job class - demonstrates Strategy Pattern
     */
    private String executeJobByClass(JobDto jobDto) throws Exception {
        String jobClass = jobDto.getJobClass();
        
        logger.debug("Executing job class: {}", jobClass);
        
        // Simulate different job types
        switch (jobClass.toLowerCase()) {
            case "com.example.emailjob":
                return executeEmailJob(jobDto);
            case "com.example.reportjob":
                return executeReportJob(jobDto);
            case "com.example.dataimportjob":
                return executeDataImportJob(jobDto);
            default:
                return executeDefaultJob(jobDto);
        }
    }
    
    private String executeEmailJob(JobDto jobDto) throws Exception {
        logger.info("Executing email job: {}", jobDto.getName());
        Thread.sleep(2000); // Simulate email sending
        return "Email sent successfully to recipients";
    }
    
    private String executeReportJob(JobDto jobDto) throws Exception {
        logger.info("Executing report job: {}", jobDto.getName());
        Thread.sleep(5000); // Simulate report generation
        return "Report generated and saved to /reports/output.pdf";
    }
    
    private String executeDataImportJob(JobDto jobDto) throws Exception {
        logger.info("Executing data import job: {}", jobDto.getName());
        Thread.sleep(10000); // Simulate data import
        return "Data imported successfully: 1000 records processed";
    }
    
    private String executeDefaultJob(JobDto jobDto) throws Exception {
        logger.info("Executing default job: {}", jobDto.getName());
        Thread.sleep(3000); // Simulate generic task
        return "Job executed successfully";
    }
    
    /**
     * Health check for executor node
     */
    public boolean isHealthy() {
        return true; // Simple health check
    }
    
    /**
     * Get node information
     */
    public String getNodeId() {
        return nodeId;
    }
    
    public String getNodeStatus() {
        return "ACTIVE";
    }
    
    // Circuit breaker fallback method
    public CompletableFuture<JobExecutionDto> executeJobFallback(JobDto jobDto, Exception ex) {
        logger.error("Circuit breaker activated for job execution: {}", ex.getMessage());
        
        JobExecutionDto execution = new JobExecutionDto(jobDto.getId(), jobDto.getName(), nodeId);
        execution.setExecutionId(UUID.randomUUID().toString());
        execution.markFailed("Circuit breaker activated", ex.getMessage());
        
        return CompletableFuture.completedFuture(execution);
    }
    
    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getSimpleName()).append(": ").append(ex.getMessage()).append("\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 1000) { // Limit stack trace length
                sb.append("\t... (truncated)\n");
                break;
            }
        }
        return sb.toString();
    }
}