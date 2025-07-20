package com.javamastery.lms.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record representing an enrollment in the learning management system
 * Demonstrates immutable data structures and functional validation
 */
public record Enrollment(
    String enrollmentId,
    String studentId,
    String courseId,
    LocalDateTime enrollmentDate,
    EnrollmentStatus status,
    LocalDateTime statusLastModified
) implements Comparable<Enrollment> {
    
    public Enrollment {
        Objects.requireNonNull(enrollmentId, "Enrollment ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(enrollmentDate, "Enrollment date cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(statusLastModified, "Status last modified cannot be null");
        
        if (statusLastModified.isBefore(enrollmentDate)) {
            throw new IllegalArgumentException("Status last modified cannot be before enrollment date");
        }
    }
    
    public enum EnrollmentStatus {
        ACTIVE, COMPLETED, DROPPED, SUSPENDED
    }
    
    // Factory methods for common operations
    public static Enrollment createNew(String enrollmentId, String studentId, String courseId) {
        LocalDateTime now = LocalDateTime.now();
        return new Enrollment(enrollmentId, studentId, courseId, now, EnrollmentStatus.ACTIVE, now);
    }
    
    // Functional methods for status changes (returning new instances)
    public Enrollment complete() {
        return new Enrollment(enrollmentId, studentId, courseId, enrollmentDate, 
                            EnrollmentStatus.COMPLETED, LocalDateTime.now());
    }
    
    public Enrollment drop() {
        return new Enrollment(enrollmentId, studentId, courseId, enrollmentDate, 
                            EnrollmentStatus.DROPPED, LocalDateTime.now());
    }
    
    public Enrollment suspend() {
        return new Enrollment(enrollmentId, studentId, courseId, enrollmentDate, 
                            EnrollmentStatus.SUSPENDED, LocalDateTime.now());
    }
    
    public Enrollment reactivate() {
        return new Enrollment(enrollmentId, studentId, courseId, enrollmentDate, 
                            EnrollmentStatus.ACTIVE, LocalDateTime.now());
    }
    
    // Query methods
    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }
    
    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }
    
    public long getDaysEnrolled() {
        return java.time.Duration.between(enrollmentDate, LocalDateTime.now()).toDays();
    }
    
    public long getDaysSinceStatusChange() {
        return java.time.Duration.between(statusLastModified, LocalDateTime.now()).toDays();
    }
    
    @Override
    public int compareTo(Enrollment other) {
        // Order by enrollment date (most recent first)
        return other.enrollmentDate.compareTo(this.enrollmentDate);
    }
}