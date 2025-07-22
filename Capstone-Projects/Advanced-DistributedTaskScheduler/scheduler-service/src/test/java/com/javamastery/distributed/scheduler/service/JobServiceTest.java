package com.javamastery.distributed.scheduler.service;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobPriority;
import com.javamastery.distributed.common.enums.JobStatus;
import com.javamastery.distributed.scheduler.entity.JobEntity;
import com.javamastery.distributed.scheduler.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JobService
 */
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void createJob_ShouldCreateJobSuccessfully() {
        // Arrange
        JobDto jobDto = new JobDto("Test Job", JobPriority.HIGH, "com.example.TestJob");
        JobEntity savedEntity = new JobEntity("Test Job", JobPriority.HIGH, "com.example.TestJob");
        savedEntity.setId("test-id");
        savedEntity.setStatus(JobStatus.CREATED);
        savedEntity.setCreatedAt(LocalDateTime.now());
        savedEntity.setUpdatedAt(LocalDateTime.now());

        when(jobRepository.save(any(JobEntity.class))).thenReturn(savedEntity);

        // Act
        JobDto result = jobService.createJob(jobDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Job", result.getName());
        assertEquals(JobPriority.HIGH, result.getPriority());
        assertEquals("com.example.TestJob", result.getJobClass());
        assertEquals(JobStatus.CREATED, result.getStatus());
        
        verify(jobRepository, times(1)).save(any(JobEntity.class));
    }

    @Test
    void getJobById_ShouldReturnJobWhenExists() {
        // Arrange
        String jobId = "test-id";
        JobEntity jobEntity = new JobEntity("Test Job", JobPriority.MEDIUM, "com.example.TestJob");
        jobEntity.setId(jobId);
        jobEntity.setStatus(JobStatus.CREATED);
        jobEntity.setCreatedAt(LocalDateTime.now());
        jobEntity.setUpdatedAt(LocalDateTime.now());

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobEntity));

        // Act
        JobDto result = jobService.getJobById(jobId);

        // Assert
        assertNotNull(result);
        assertEquals(jobId, result.getId());
        assertEquals("Test Job", result.getName());
        assertEquals(JobPriority.MEDIUM, result.getPriority());
        
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void scheduleJob_ShouldUpdateJobStatusToScheduled() {
        // Arrange
        String jobId = "test-id";
        JobEntity jobEntity = new JobEntity("Test Job", JobPriority.MEDIUM, "com.example.TestJob");
        jobEntity.setId(jobId);
        jobEntity.setStatus(JobStatus.CREATED);
        jobEntity.setIntervalMs(60000L); // 1 minute
        jobEntity.setCreatedAt(LocalDateTime.now());
        jobEntity.setUpdatedAt(LocalDateTime.now());

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(jobEntity));
        when(jobRepository.save(any(JobEntity.class))).thenReturn(jobEntity);

        // Act
        JobDto result = jobService.scheduleJob(jobId);

        // Assert
        assertNotNull(result);
        assertEquals(JobStatus.SCHEDULED, result.getStatus());
        assertNotNull(result.getNextExecutionTime());
        
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).save(any(JobEntity.class));
    }
}