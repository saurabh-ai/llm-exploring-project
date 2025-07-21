package com.javamastery.distributed.common.enums;

/**
 * Job status enumeration for tracking job lifecycle
 */
public enum JobStatus {
    CREATED,
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    PAUSED
}