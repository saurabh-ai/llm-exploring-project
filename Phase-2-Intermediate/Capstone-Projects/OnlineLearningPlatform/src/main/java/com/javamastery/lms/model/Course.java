package com.javamastery.lms.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Immutable record representing a course in the learning management system
 * Demonstrates records, collections, and concurrent data structures
 */
public record Course(
    String courseId,
    String title,
    String description,
    String instructor,
    CourseLevel level,
    int maxCapacity,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Set<String> prerequisites,
    List<Module> modules
) implements Comparable<Course> {
    
    // Thread-safe enrollment tracking
    private static final Map<String, AtomicInteger> enrollmentCounts = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> courseEnrollments = new ConcurrentHashMap<>();
    
    public Course {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(level, "Level cannot be null");
        
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive");
        }
        
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Initialize with immutable collections
        if (prerequisites == null) {
            prerequisites = Set.of();
        }
        if (modules == null) {
            modules = List.of();
        }
        
        // Initialize enrollment tracking for this course
        enrollmentCounts.putIfAbsent(courseId, new AtomicInteger(0));
        courseEnrollments.putIfAbsent(courseId, ConcurrentHashMap.newKeySet());
    }
    
    public enum CourseLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }
    
    // Derived properties using functional programming
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    public boolean canEnroll() {
        return getCurrentEnrollmentCount() < maxCapacity && isActive();
    }
    
    public long getDurationInDays() {
        return java.time.Duration.between(startDate, endDate).toDays();
    }
    
    public int getTotalLessons() {
        return modules.stream()
                .mapToInt(Module::getLessonCount)
                .sum();
    }
    
    public int getCurrentEnrollmentCount() {
        return enrollmentCounts.getOrDefault(courseId, new AtomicInteger(0)).get();
    }
    
    public Set<String> getEnrolledStudents() {
        return new HashSet<>(courseEnrollments.getOrDefault(courseId, Set.of()));
    }
    
    // Thread-safe enrollment operations
    public boolean enrollStudent(String studentId) {
        AtomicInteger count = enrollmentCounts.get(courseId);
        Set<String> enrollments = courseEnrollments.get(courseId);
        
        if (count.get() >= maxCapacity) {
            return false;
        }
        
        // Use atomic operations to ensure thread safety
        boolean added = enrollments.add(studentId);
        if (added) {
            count.incrementAndGet();
        }
        return added;
    }
    
    public boolean unenrollStudent(String studentId) {
        AtomicInteger count = enrollmentCounts.get(courseId);
        Set<String> enrollments = courseEnrollments.get(courseId);
        
        boolean removed = enrollments.remove(studentId);
        if (removed) {
            count.decrementAndGet();
        }
        return removed;
    }
    
    // Comparable implementation for natural ordering
    @Override
    public int compareTo(Course other) {
        // Order by start date, then by title
        int dateComparison = this.startDate.compareTo(other.startDate);
        return dateComparison != 0 ? dateComparison : this.title.compareTo(other.title);
    }
    
    /**
     * Nested record representing a course module
     */
    public record Module(
        String moduleId,
        String title,
        String description,
        int lessonCount,
        int estimatedHours
    ) {
        public Module {
            Objects.requireNonNull(moduleId, "Module ID cannot be null");
            Objects.requireNonNull(title, "Title cannot be null");
            
            if (lessonCount < 0) {
                throw new IllegalArgumentException("Lesson count cannot be negative");
            }
            if (estimatedHours < 0) {
                throw new IllegalArgumentException("Estimated hours cannot be negative");
            }
        }
        
        public int getLessonCount() {
            return lessonCount;
        }
    }
    
    // Builder pattern for easier course construction
    public static class Builder {
        private String courseId;
        private String title;
        private String description = "";
        private String instructor;
        private CourseLevel level = CourseLevel.BEGINNER;
        private int maxCapacity = 30;
        private LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        private LocalDateTime endDate = LocalDateTime.now().plusDays(37); // 30 days duration
        private Set<String> prerequisites = new HashSet<>();
        private List<Module> modules = new ArrayList<>();
        
        public Builder(String courseId, String title, String instructor) {
            this.courseId = courseId;
            this.title = title;
            this.instructor = instructor;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder level(CourseLevel level) {
            this.level = level;
            return this;
        }
        
        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }
        
        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public Builder addPrerequisite(String prerequisite) {
            this.prerequisites.add(prerequisite);
            return this;
        }
        
        public Builder addModule(Module module) {
            this.modules.add(module);
            return this;
        }
        
        public Course build() {
            return new Course(courseId, title, description, instructor, level, 
                            maxCapacity, startDate, endDate, 
                            Set.copyOf(prerequisites), List.copyOf(modules));
        }
    }
}