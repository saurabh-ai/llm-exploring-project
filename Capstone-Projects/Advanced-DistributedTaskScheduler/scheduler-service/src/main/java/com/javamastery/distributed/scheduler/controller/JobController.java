package com.javamastery.distributed.scheduler.controller;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobStatus;
import com.javamastery.distributed.scheduler.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Job Management
 * Provides comprehensive API for job operations
 */
@RestController
@RequestMapping("/api/v1/jobs")
@Tag(name = "Job Management", description = "APIs for managing scheduled jobs")
@CrossOrigin(origins = "*")
public class JobController {
    
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    
    private final JobService jobService;
    
    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    
    /**
     * Create a new job
     */
    @PostMapping
    @Operation(summary = "Create a new job", description = "Creates a new job with the provided configuration")
    public ResponseEntity<JobDto> createJob(
            @Valid @RequestBody JobDto jobDto) {
        logger.info("Request to create job: {}", jobDto.getName());
        
        JobDto createdJob = jobService.createJob(jobDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }
    
    /**
     * Get job by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID", description = "Retrieves a job by its unique identifier")
    public ResponseEntity<JobDto> getJobById(
            @Parameter(description = "Job unique identifier") @PathVariable String id) {
        logger.debug("Request to get job with ID: {}", id);
        
        JobDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
    
    /**
     * Get all jobs with pagination
     */
    @GetMapping
    @Operation(summary = "Get all jobs", description = "Retrieves all jobs with pagination support")
    public ResponseEntity<Page<JobDto>> getAllJobs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Request to get all jobs - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDir);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<JobDto> jobs = jobService.getAllJobs(pageable);
        return ResponseEntity.ok(jobs);
    }
    
    /**
     * Update job
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update job", description = "Updates an existing job with new configuration")
    public ResponseEntity<JobDto> updateJob(
            @Parameter(description = "Job unique identifier") @PathVariable String id,
            @Valid @RequestBody JobDto jobDto) {
        logger.info("Request to update job with ID: {}", id);
        
        JobDto updatedJob = jobService.updateJob(id, jobDto);
        return ResponseEntity.ok(updatedJob);
    }
    
    /**
     * Delete job
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job", description = "Deletes a job by its unique identifier")
    public ResponseEntity<Void> deleteJob(
            @Parameter(description = "Job unique identifier") @PathVariable String id) {
        logger.info("Request to delete job with ID: {}", id);
        
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get jobs by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get jobs by status", description = "Retrieves all jobs with the specified status")
    public ResponseEntity<List<JobDto>> getJobsByStatus(
            @Parameter(description = "Job status") @PathVariable JobStatus status) {
        logger.debug("Request to get jobs with status: {}", status);
        
        List<JobDto> jobs = jobService.getJobsByStatus(status);
        return ResponseEntity.ok(jobs);
    }
    
    /**
     * Schedule job
     */
    @PostMapping("/{id}/schedule")
    @Operation(summary = "Schedule job", description = "Schedules a job for execution")
    public ResponseEntity<JobDto> scheduleJob(
            @Parameter(description = "Job unique identifier") @PathVariable String id) {
        logger.info("Request to schedule job with ID: {}", id);
        
        JobDto scheduledJob = jobService.scheduleJob(id);
        return ResponseEntity.ok(scheduledJob);
    }
    
    /**
     * Pause job
     */
    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause job", description = "Pauses a scheduled job")
    public ResponseEntity<JobDto> pauseJob(
            @Parameter(description = "Job unique identifier") @PathVariable String id) {
        logger.info("Request to pause job with ID: {}", id);
        
        JobDto pausedJob = jobService.pauseJob(id);
        return ResponseEntity.ok(pausedJob);
    }
    
    /**
     * Resume job
     */
    @PostMapping("/{id}/resume")
    @Operation(summary = "Resume job", description = "Resumes a paused job")
    public ResponseEntity<JobDto> resumeJob(
            @Parameter(description = "Job unique identifier") @PathVariable String id) {
        logger.info("Request to resume job with ID: {}", id);
        
        JobDto resumedJob = jobService.resumeJob(id);
        return ResponseEntity.ok(resumedJob);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks the health of the job service")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Job service is healthy");
    }
}