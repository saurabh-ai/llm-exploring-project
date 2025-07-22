package com.javamastery.distributed.monitoring.service;

import com.javamastery.distributed.common.dto.JobDto;
import com.javamastery.distributed.common.enums.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Dashboard Service for monitoring operations
 * Demonstrates Observer Pattern implementation
 */
@Service
public class DashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    
    // Mock data for demonstration - in real implementation, this would fetch from scheduler service
    private final List<JobDto> mockJobs = initializeMockData();
    
    /**
     * Get job statistics summary
     */
    public Map<String, Long> getJobStatistics() {
        logger.debug("Fetching job statistics");
        
        Map<String, Long> stats = new HashMap<>();
        
        // Count jobs by status
        for (JobStatus status : JobStatus.values()) {
            long count = mockJobs.stream()
                    .filter(job -> job.getStatus() == status)
                    .count();
            stats.put(status.name().toLowerCase(), count);
        }
        
        // Total jobs
        stats.put("total", (long) mockJobs.size());
        
        return stats;
    }
    
    /**
     * Get recent jobs
     */
    public List<JobDto> getRecentJobs(int limit) {
        logger.debug("Fetching recent jobs with limit: {}", limit);
        
        return mockJobs.stream()
                .sorted((j1, j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
    
    /**
     * Get all jobs
     */
    public List<JobDto> getAllJobs() {
        logger.debug("Fetching all jobs");
        return new ArrayList<>(mockJobs);
    }
    
    /**
     * Get jobs by status
     */
    public List<JobDto> getJobsByStatus(JobStatus status) {
        logger.debug("Fetching jobs with status: {}", status);
        
        return mockJobs.stream()
                .filter(job -> job.getStatus() == status)
                .toList();
    }
    
    /**
     * Check if system is healthy
     */
    public boolean isSystemHealthy() {
        // Simple health check - in real implementation, this would check all services
        return true;
    }
    
    /**
     * Get detailed system health information
     */
    public Map<String, Object> getSystemHealth() {
        logger.debug("Fetching system health information");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        
        Map<String, String> services = new HashMap<>();
        services.put("scheduler-service", "UP");
        services.put("executor-service", "UP");
        services.put("config-service", "UP");
        health.put("services", services);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalJobs", mockJobs.size());
        metrics.put("activeJobs", getJobsByStatus(JobStatus.RUNNING).size());
        metrics.put("completedJobs", getJobsByStatus(JobStatus.COMPLETED).size());
        metrics.put("failedJobs", getJobsByStatus(JobStatus.FAILED).size());
        health.put("metrics", metrics);
        
        return health;
    }
    
    /**
     * Initialize mock data for demonstration
     */
    private List<JobDto> initializeMockData() {
        List<JobDto> jobs = new ArrayList<>();
        
        // Create sample jobs with different statuses
        JobDto job1 = new JobDto("Daily Report Generation", com.javamastery.distributed.common.enums.JobPriority.HIGH, "com.example.ReportJob");
        job1.setId("job-001");
        job1.setStatus(JobStatus.COMPLETED);
        job1.setCreatedAt(LocalDateTime.now().minusHours(2));
        job1.setCronExpression("0 0 9 * * ?");
        jobs.add(job1);
        
        JobDto job2 = new JobDto("Email Notification", com.javamastery.distributed.common.enums.JobPriority.MEDIUM, "com.example.EmailJob");
        job2.setId("job-002");
        job2.setStatus(JobStatus.RUNNING);
        job2.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        job2.setIntervalMs(300000L); // 5 minutes
        jobs.add(job2);
        
        JobDto job3 = new JobDto("Data Import Process", com.javamastery.distributed.common.enums.JobPriority.LOW, "com.example.DataImportJob");
        job3.setId("job-003");
        job3.setStatus(JobStatus.SCHEDULED);
        job3.setCreatedAt(LocalDateTime.now().minusHours(1));
        job3.setCronExpression("0 */15 * * * ?");
        jobs.add(job3);
        
        JobDto job4 = new JobDto("Backup Task", com.javamastery.distributed.common.enums.JobPriority.MEDIUM, "com.example.BackupJob");
        job4.setId("job-004");
        job4.setStatus(JobStatus.FAILED);
        job4.setCreatedAt(LocalDateTime.now().minusHours(4));
        job4.setCronExpression("0 0 2 * * ?");
        jobs.add(job4);
        
        JobDto job5 = new JobDto("System Cleanup", com.javamastery.distributed.common.enums.JobPriority.LOW, "com.example.CleanupJob");
        job5.setId("job-005");
        job5.setStatus(JobStatus.PAUSED);
        job5.setCreatedAt(LocalDateTime.now().minusDays(1));
        job5.setCronExpression("0 0 1 * * SUN");
        jobs.add(job5);
        
        return jobs;
    }
}