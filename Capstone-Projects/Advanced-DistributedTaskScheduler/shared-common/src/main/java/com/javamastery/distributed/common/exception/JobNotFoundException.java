package com.javamastery.distributed.common.exception;

/**
 * Exception thrown when a job is not found
 */
public class JobNotFoundException extends JobException {
    
    public JobNotFoundException(String jobId) {
        super("Job not found with ID: " + jobId, jobId);
    }
    
    public JobNotFoundException(String message, String jobId) {
        super(message, jobId);
    }
}