package com.javamastery.distributed.common.dto;

import com.javamastery.distributed.common.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Job execution information
 */
public class JobExecutionDto {
    
    private String executionId;
    private String jobId;
    private String jobName;
    private JobStatus status;
    private String executorNodeId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private Long durationMs;
    private String result;
    private String errorMessage;
    private String stackTrace;
    private Map<String, Object> executionData;
    private Integer attemptNumber = 1;
    
    // Default constructor
    public JobExecutionDto() {}
    
    // Constructor with required fields
    public JobExecutionDto(String jobId, String jobName, String executorNodeId) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.executorNodeId = executorNodeId;
        this.status = JobStatus.RUNNING;
        this.startTime = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getJobName() {
        return jobName;
    }
    
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    
    public JobStatus getStatus() {
        return status;
    }
    
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    public String getExecutorNodeId() {
        return executorNodeId;
    }
    
    public void setExecutorNodeId(String executorNodeId) {
        this.executorNodeId = executorNodeId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Long getDurationMs() {
        return durationMs;
    }
    
    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
    
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
    
    public Map<String, Object> getExecutionData() {
        return executionData;
    }
    
    public void setExecutionData(Map<String, Object> executionData) {
        this.executionData = executionData;
    }
    
    public Integer getAttemptNumber() {
        return attemptNumber;
    }
    
    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
    
    public void markCompleted(String result) {
        this.status = JobStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.result = result;
        if (startTime != null && endTime != null) {
            this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }
    
    public void markFailed(String errorMessage, String stackTrace) {
        this.status = JobStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        if (startTime != null && endTime != null) {
            this.durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }
    
    @Override
    public String toString() {
        return "JobExecutionDto{" +
                "executionId='" + executionId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", status=" + status +
                ", executorNodeId='" + executorNodeId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", durationMs=" + durationMs +
                ", attemptNumber=" + attemptNumber +
                '}';
    }
}