package com.javamastery.distributed.scheduler.entity;

import com.javamastery.distributed.common.enums.JobPriority;
import com.javamastery.distributed.common.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Job entity for database persistence
 */
@Entity
@Table(name = "jobs")
public class JobEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank(message = "Job name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Job priority is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPriority priority = JobPriority.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.CREATED;
    
    @Column(name = "cron_expression")
    private String cronExpression;
    
    @Column(name = "interval_ms")
    private Long intervalMs;
    
    @Column(name = "job_class", nullable = false)
    private String jobClass;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_parameters", joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "parameter_key")
    @Column(name = "parameter_value")
    private Map<String, String> parameters;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "next_execution_time")
    private LocalDateTime nextExecutionTime;
    
    @Column(name = "last_execution_time")
    private LocalDateTime lastExecutionTime;
    
    @Column(name = "executor_node_id")
    private String executorNodeId;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds = 300;
    
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    // Default constructor
    public JobEntity() {}
    
    // Constructor with required fields
    public JobEntity(String name, JobPriority priority, String jobClass) {
        this.name = name;
        this.priority = priority;
        this.jobClass = jobClass;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public JobPriority getPriority() {
        return priority;
    }
    
    public void setPriority(JobPriority priority) {
        this.priority = priority;
    }
    
    public JobStatus getStatus() {
        return status;
    }
    
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    public String getCronExpression() {
        return cronExpression;
    }
    
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    
    public Long getIntervalMs() {
        return intervalMs;
    }
    
    public void setIntervalMs(Long intervalMs) {
        this.intervalMs = intervalMs;
    }
    
    public String getJobClass() {
        return jobClass;
    }
    
    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }
    
    public void setNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }
    
    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }
    
    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }
    
    public String getExecutorNodeId() {
        return executorNodeId;
    }
    
    public void setExecutorNodeId(String executorNodeId) {
        this.executorNodeId = executorNodeId;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "JobEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", cronExpression='" + cronExpression + '\'' +
                ", intervalMs=" + intervalMs +
                ", jobClass='" + jobClass + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}