package com.javamastery.streams.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable data model representing a log entry
 * Used for demonstrating log processing and analysis with streams
 */
public record LogEntry(
    LocalDate date,
    String time,
    LogLevel level,
    String component,
    String message,
    String threadName,
    long responseTime
) {
    
    /**
     * Log levels in order of severity
     */
    public enum LogLevel {
        DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5);
        
        private final int severity;
        
        LogLevel(int severity) {
            this.severity = severity;
        }
        
        public int getSeverity() {
            return severity;
        }
        
        public boolean isError() {
            return this == ERROR || this == FATAL;
        }
        
        public boolean isWarningOrAbove() {
            return severity >= WARN.severity;
        }
    }
    
    /**
     * Compact constructor for validation
     */
    public LogEntry {
        Objects.requireNonNull(date, "Date cannot be null");
        Objects.requireNonNull(time, "Time cannot be null");
        Objects.requireNonNull(level, "Log level cannot be null");
        Objects.requireNonNull(component, "Component cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(threadName, "Thread name cannot be null");
        
        if (responseTime < 0) {
            throw new IllegalArgumentException("Response time cannot be negative");
        }
    }
    
    /**
     * Check if this is a slow operation (response time > 1000ms)
     */
    public boolean isSlowOperation() {
        return responseTime > 1000;
    }
    
    /**
     * Check if this is an error log entry
     */
    public boolean isError() {
        return level.isError();
    }
    
    /**
     * Get formatted log entry
     */
    public String formatted() {
        return String.format("[%s %s] [%s] [%s] [%s] %s (Response: %dms)", 
            date, time, level, threadName, component, message, responseTime);
    }
}