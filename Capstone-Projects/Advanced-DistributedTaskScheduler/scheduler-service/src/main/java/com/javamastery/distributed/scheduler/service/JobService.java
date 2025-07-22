package com.javamastery.distributed.scheduler.service;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobStatus;
import com.javamastery.distributed.common.exception.JobNotFoundException;
import com.javamastery.distributed.scheduler.entity.JobEntity;
import com.javamastery.distributed.scheduler.repository.JobRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Job Service for managing job lifecycle
 * Demonstrates Service Layer pattern and Circuit Breaker pattern
 */
@Service
@Transactional
public class JobService {
    
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    
    private final JobRepository jobRepository;
    
    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    
    /**
     * Create a new job
     */
    @CircuitBreaker(name = "job-service", fallbackMethod = "createJobFallback")
    public JobDto createJob(JobDto jobDto) {
        logger.info("Creating new job: {}", jobDto.getName());
        
        JobEntity jobEntity = convertToEntity(jobDto);
        jobEntity.setStatus(JobStatus.CREATED);
        jobEntity.setCreatedAt(LocalDateTime.now());
        jobEntity.setUpdatedAt(LocalDateTime.now());
        
        JobEntity savedEntity = jobRepository.save(jobEntity);
        logger.info("Job created successfully with ID: {}", savedEntity.getId());
        
        return convertToDto(savedEntity);
    }
    
    /**
     * Get job by ID
     */
    @Transactional(readOnly = true)
    public JobDto getJobById(String id) {
        logger.debug("Fetching job with ID: {}", id);
        
        Optional<JobEntity> jobEntity = jobRepository.findById(id);
        if (jobEntity.isEmpty()) {
            throw new JobNotFoundException(id);
        }
        
        return convertToDto(jobEntity.get());
    }
    
    /**
     * Get all jobs with pagination
     */
    @Transactional(readOnly = true)
    public Page<JobDto> getAllJobs(Pageable pageable) {
        logger.debug("Fetching all jobs with pagination");
        
        return jobRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    /**
     * Update job
     */
    public JobDto updateJob(String id, JobDto jobDto) {
        logger.info("Updating job with ID: {}", id);
        
        Optional<JobEntity> existingJob = jobRepository.findById(id);
        if (existingJob.isEmpty()) {
            throw new JobNotFoundException(id);
        }
        
        JobEntity jobEntity = existingJob.get();
        updateEntityFromDto(jobEntity, jobDto);
        jobEntity.setUpdatedAt(LocalDateTime.now());
        
        JobEntity savedEntity = jobRepository.save(jobEntity);
        logger.info("Job updated successfully: {}", savedEntity.getId());
        
        return convertToDto(savedEntity);
    }
    
    /**
     * Delete job
     */
    public void deleteJob(String id) {
        logger.info("Deleting job with ID: {}", id);
        
        if (!jobRepository.existsById(id)) {
            throw new JobNotFoundException(id);
        }
        
        jobRepository.deleteById(id);
        logger.info("Job deleted successfully: {}", id);
    }
    
    /**
     * Get jobs by status
     */
    @Transactional(readOnly = true)
    public List<JobDto> getJobsByStatus(JobStatus status) {
        logger.debug("Fetching jobs with status: {}", status);
        
        return jobRepository.findByStatus(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Schedule job for execution
     */
    public JobDto scheduleJob(String id) {
        logger.info("Scheduling job with ID: {}", id);
        
        Optional<JobEntity> jobEntity = jobRepository.findById(id);
        if (jobEntity.isEmpty()) {
            throw new JobNotFoundException(id);
        }
        
        JobEntity job = jobEntity.get();
        job.setStatus(JobStatus.SCHEDULED);
        job.setUpdatedAt(LocalDateTime.now());
        
        if (job.getCronExpression() == null && job.getIntervalMs() != null) {
            job.setNextExecutionTime(LocalDateTime.now().plusNanos(job.getIntervalMs() * 1_000_000));
        }
        
        JobEntity savedEntity = jobRepository.save(job);
        logger.info("Job scheduled successfully: {}", savedEntity.getId());
        
        return convertToDto(savedEntity);
    }
    
    /**
     * Pause job
     */
    public JobDto pauseJob(String id) {
        logger.info("Pausing job with ID: {}", id);
        
        Optional<JobEntity> jobEntity = jobRepository.findById(id);
        if (jobEntity.isEmpty()) {
            throw new JobNotFoundException(id);
        }
        
        JobEntity job = jobEntity.get();
        job.setStatus(JobStatus.PAUSED);
        job.setUpdatedAt(LocalDateTime.now());
        
        JobEntity savedEntity = jobRepository.save(job);
        logger.info("Job paused successfully: {}", savedEntity.getId());
        
        return convertToDto(savedEntity);
    }
    
    /**
     * Resume job
     */
    public JobDto resumeJob(String id) {
        logger.info("Resuming job with ID: {}", id);
        
        Optional<JobEntity> jobEntity = jobRepository.findById(id);
        if (jobEntity.isEmpty()) {
            throw new JobNotFoundException(id);
        }
        
        JobEntity job = jobEntity.get();
        job.setStatus(JobStatus.SCHEDULED);
        job.setUpdatedAt(LocalDateTime.now());
        
        JobEntity savedEntity = jobRepository.save(job);
        logger.info("Job resumed successfully: {}", savedEntity.getId());
        
        return convertToDto(savedEntity);
    }
    
    // Fallback methods for Circuit Breaker
    public JobDto createJobFallback(JobDto jobDto, Exception ex) {
        logger.error("Circuit breaker activated for createJob: {}", ex.getMessage());
        throw new RuntimeException("Job creation service is temporarily unavailable", ex);
    }
    
    // Conversion methods
    private JobDto convertToDto(JobEntity entity) {
        JobDto dto = new JobDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPriority(entity.getPriority());
        dto.setStatus(entity.getStatus());
        dto.setCronExpression(entity.getCronExpression());
        dto.setIntervalMs(entity.getIntervalMs());
        dto.setJobClass(entity.getJobClass());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setNextExecutionTime(entity.getNextExecutionTime());
        dto.setLastExecutionTime(entity.getLastExecutionTime());
        dto.setExecutorNodeId(entity.getExecutorNodeId());
        dto.setRetryCount(entity.getRetryCount());
        dto.setMaxRetries(entity.getMaxRetries());
        
        return dto;
    }
    
    private JobEntity convertToEntity(JobDto dto) {
        JobEntity entity = new JobEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPriority(dto.getPriority());
        entity.setCronExpression(dto.getCronExpression());
        entity.setIntervalMs(dto.getIntervalMs());
        entity.setJobClass(dto.getJobClass());
        entity.setRetryCount(dto.getRetryCount());
        entity.setMaxRetries(dto.getMaxRetries());
        
        return entity;
    }
    
    private void updateEntityFromDto(JobEntity entity, JobDto dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPriority(dto.getPriority());
        entity.setCronExpression(dto.getCronExpression());
        entity.setIntervalMs(dto.getIntervalMs());
        entity.setJobClass(dto.getJobClass());
        entity.setMaxRetries(dto.getMaxRetries());
    }
}