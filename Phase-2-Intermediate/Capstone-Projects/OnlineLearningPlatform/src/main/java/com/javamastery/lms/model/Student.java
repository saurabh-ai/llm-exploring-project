package com.javamastery.lms.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable record representing a student in the learning management system
 * Demonstrates thread-safe operations and functional programming patterns
 */
public record Student(
    String studentId,
    String firstName,
    String lastName,
    String email,
    LocalDateTime registrationDate,
    StudentLevel level,
    Set<String> interests
) implements Comparable<Student> {
    
    // Thread-safe progress tracking
    private static final Map<String, Map<String, Progress>> studentProgress = new ConcurrentHashMap<>();
    
    public Student {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(registrationDate, "Registration date cannot be null");
        Objects.requireNonNull(level, "Level cannot be null");
        
        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Initialize with immutable collections
        if (interests == null) {
            interests = Set.of();
        }
        
        // Initialize progress tracking
        studentProgress.putIfAbsent(studentId, new ConcurrentHashMap<>());
    }
    
    public enum StudentLevel {
        NEWCOMER, REGULAR, ADVANCED, EXPERT
    }
    
    public enum ProgressStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, DROPPED
    }
    
    // Derived properties
    public String fullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isActive() {
        return registrationDate.isBefore(LocalDateTime.now());
    }
    
    public long daysSinceRegistration() {
        return java.time.Duration.between(registrationDate, LocalDateTime.now()).toDays();
    }
    
    // Progress management methods (thread-safe)
    public void updateProgress(String courseId, int completedLessons, int totalLessons, ProgressStatus status) {
        Map<String, Progress> progress = studentProgress.get(studentId);
        progress.put(courseId, new Progress(courseId, completedLessons, totalLessons, status, LocalDateTime.now()));
    }
    
    public Optional<Progress> getProgress(String courseId) {
        Map<String, Progress> progress = studentProgress.get(studentId);
        return Optional.ofNullable(progress.get(courseId));
    }
    
    public Map<String, Progress> getAllProgress() {
        return new HashMap<>(studentProgress.getOrDefault(studentId, Map.of()));
    }
    
    public List<String> getCompletedCourses() {
        return studentProgress.getOrDefault(studentId, Map.of())
                .values()
                .stream()
                .filter(progress -> progress.status() == ProgressStatus.COMPLETED)
                .map(Progress::courseId)
                .toList();
    }
    
    public List<String> getInProgressCourses() {
        return studentProgress.getOrDefault(studentId, Map.of())
                .values()
                .stream()
                .filter(progress -> progress.status() == ProgressStatus.IN_PROGRESS)
                .map(Progress::courseId)
                .toList();
    }
    
    // Comparable implementation
    @Override
    public int compareTo(Student other) {
        // Order by registration date, then by last name
        int dateComparison = this.registrationDate.compareTo(other.registrationDate);
        return dateComparison != 0 ? dateComparison : this.lastName.compareTo(other.lastName);
    }
    
    /**
     * Record representing student progress in a course
     */
    public record Progress(
        String courseId,
        int completedLessons,
        int totalLessons,
        ProgressStatus status,
        LocalDateTime lastUpdated
    ) {
        public Progress {
            Objects.requireNonNull(courseId, "Course ID cannot be null");
            Objects.requireNonNull(status, "Status cannot be null");
            Objects.requireNonNull(lastUpdated, "Last updated cannot be null");
            
            if (completedLessons < 0) {
                throw new IllegalArgumentException("Completed lessons cannot be negative");
            }
            if (totalLessons < 0) {
                throw new IllegalArgumentException("Total lessons cannot be negative");
            }
            if (completedLessons > totalLessons) {
                throw new IllegalArgumentException("Completed lessons cannot exceed total lessons");
            }
        }
        
        public double getCompletionPercentage() {
            return totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0.0;
        }
        
        public boolean isCompleted() {
            return status == ProgressStatus.COMPLETED;
        }
        
        public boolean isInProgress() {
            return status == ProgressStatus.IN_PROGRESS;
        }
    }
    
    // Builder pattern
    public static class Builder {
        private String studentId;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDateTime registrationDate = LocalDateTime.now();
        private StudentLevel level = StudentLevel.NEWCOMER;
        private Set<String> interests = new HashSet<>();
        
        public Builder(String studentId, String firstName, String lastName, String email) {
            this.studentId = studentId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
        
        public Builder registrationDate(LocalDateTime registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }
        
        public Builder level(StudentLevel level) {
            this.level = level;
            return this;
        }
        
        public Builder addInterest(String interest) {
            this.interests.add(interest);
            return this;
        }
        
        public Builder interests(Set<String> interests) {
            this.interests = new HashSet<>(interests);
            return this;
        }
        
        public Student build() {
            return new Student(studentId, firstName, lastName, email, 
                             registrationDate, level, Set.copyOf(interests));
        }
    }
}