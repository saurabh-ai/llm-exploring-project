package com.javamastery.distributed.common.exception;

/**
 * Custom exception for job-related errors
 */
public class JobException extends RuntimeException {
    
    private final String jobId;
    
    public JobException(String message) {
        super(message);
        this.jobId = null;
    }
    
    public JobException(String message, String jobId) {
        super(message);
        this.jobId = jobId;
    }
    
    public JobException(String message, Throwable cause) {
        super(message, cause);
        this.jobId = null;
    }
    
    public JobException(String message, String jobId, Throwable cause) {
        super(message, cause);
        this.jobId = jobId;
    }
    
    public String getJobId() {
        return jobId;
    }
}