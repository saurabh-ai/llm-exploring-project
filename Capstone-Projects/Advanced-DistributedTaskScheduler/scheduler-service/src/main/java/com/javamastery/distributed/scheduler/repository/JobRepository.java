package com.javamastery.distributed.scheduler.repository;

import com.javamastery.distributed.scheduler.entity.JobEntity;
import com.javamastery.distributed.common.enums.JobStatus;
import com.javamastery.distributed.common.enums.JobPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Job entities
 * Demonstrates Repository Pattern for data access abstraction
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, String> {
    
    /**
     * Find all jobs by status
     */
    List<JobEntity> findByStatus(JobStatus status);
    
    /**
     * Find all jobs by priority ordered by creation date
     */
    List<JobEntity> findByPriorityOrderByCreatedAtAsc(JobPriority priority);
    
    /**
     * Find all enabled jobs ready for execution
     */
    @Query("SELECT j FROM JobEntity j WHERE j.enabled = true AND j.status IN :statuses AND " +
           "(j.nextExecutionTime IS NULL OR j.nextExecutionTime <= :currentTime)")
    List<JobEntity> findJobsReadyForExecution(@Param("statuses") List<JobStatus> statuses, 
                                              @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find jobs by name (partial match, case-insensitive)
     */
    List<JobEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Count jobs by status
     */
    long countByStatus(JobStatus status);
    
    /**
     * Find jobs that need retry (failed jobs with retry count less than max retries)
     */
    @Query("SELECT j FROM JobEntity j WHERE j.status = 'FAILED' AND j.enabled = true AND j.retryCount < j.maxRetries")
    List<JobEntity> findJobsForRetry();
    
    /**
     * Find jobs currently assigned to an executor
     */
    List<JobEntity> findByExecutorNodeId(String executorNodeId);
    
    /**
     * Find jobs by status and enabled flag
     */
    List<JobEntity> findByStatusAndEnabled(JobStatus status, Boolean enabled);
    
    /**
     * Custom query to find top priority jobs for execution
     */
    @Query("SELECT j FROM JobEntity j WHERE j.enabled = true AND j.status IN :statuses " +
           "ORDER BY j.priority DESC, j.createdAt ASC")
    List<JobEntity> findTopPriorityJobsForExecution(@Param("statuses") List<JobStatus> statuses);
}