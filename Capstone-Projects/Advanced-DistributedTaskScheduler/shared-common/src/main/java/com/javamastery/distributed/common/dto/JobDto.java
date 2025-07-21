package com.javamastery.distributed.common.dto;

import com.javamastery.distributed.common.enums.JobPriority;
import com.javamastery.distributed.common.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Job information
 */
public class JobDto {
    
    private String id;
    
    @NotBlank(message = "Job name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Job priority is required")
    private JobPriority priority = JobPriority.MEDIUM;
    
    private JobStatus status = JobStatus.CREATED;
    
    private String cronExpression;
    
    private Long intervalMs;
    
    private String jobClass;
    
    private Map<String, Object> parameters;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextExecutionTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastExecutionTime;
    
    private String executorNodeId;
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    // Default constructor
    public JobDto() {}
    
    // Constructor with required fields
    public JobDto(String name, JobPriority priority, String jobClass) {
        this.name = name;
        this.priority = priority;
        this.jobClass = jobClass;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
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
    
    @Override
    public String toString() {
        return "JobDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", cronExpression='" + cronExpression + '\'' +
                ", intervalMs=" + intervalMs +
                ", jobClass='" + jobClass + '\'' +
                ", createdAt=" + createdAt +
                ", nextExecutionTime=" + nextExecutionTime +
                '}';
    }
}