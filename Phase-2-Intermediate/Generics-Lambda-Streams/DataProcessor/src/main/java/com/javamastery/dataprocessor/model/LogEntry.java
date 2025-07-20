package com.javamastery.dataprocessor.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record representing a system log entry
 * Demonstrates enum usage and timestamp handling
 */
public record LogEntry(
    String id,
    LocalDateTime timestamp,
    LogLevel level,
    String component,
    String message,
    String threadName,
    long responseTime,
    String userId,
    String sessionId,
    String ipAddress
) {
    
    public LogEntry {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(level, "Log level cannot be null");
        Objects.requireNonNull(component, "Component cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        
        if (responseTime < 0) {
            throw new IllegalArgumentException("Response time cannot be negative");
        }
    }
    
    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    // Functional predicates for filtering
    public boolean isError() {
        return level == LogLevel.ERROR || level == LogLevel.FATAL;
    }
    
    public boolean isSlowRequest() {
        return responseTime > 5000; // 5 seconds
    }
    
    public boolean isFromUser(String targetUserId) {
        return Objects.equals(userId, targetUserId);
    }
    
    public boolean containsKeyword(String keyword) {
        return message.toLowerCase().contains(keyword.toLowerCase());
    }
    
    public String getSeverity() {
        return switch (level) {
            case TRACE, DEBUG -> "LOW";
            case INFO -> "NORMAL";
            case WARN -> "MEDIUM";
            case ERROR, FATAL -> "HIGH";
        };
    }
}