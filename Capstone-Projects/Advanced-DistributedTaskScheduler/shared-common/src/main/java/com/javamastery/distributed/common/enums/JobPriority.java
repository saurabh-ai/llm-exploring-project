package com.javamastery.distributed.common.enums;

/**
 * Job priority levels for queue management
 */
public enum JobPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);
    
    private final int value;
    
    JobPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}