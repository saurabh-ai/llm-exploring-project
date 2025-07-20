package com.javamastery.lms.service;

import com.javamastery.lms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Thread-safe course catalog service demonstrating comprehensive Collections framework usage
 * and functional programming patterns with lambda expressions
 */
public class CourseCatalogService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseCatalogService.class);
    
    // Thread-safe collections for concurrent access
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final List<Course> courseList = new CopyOnWriteArrayList<>();
    private final Set<String> activeInstructors = ConcurrentHashMap.newKeySet();
    
    // Read-write lock for complex operations
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Functional interfaces for course operations
    private final Predicate<Course> isActiveCourse = Course::isActive;
    private final Function<Course, String> courseToInstructor = Course::instructor;
    private final Comparator<Course> byStartDate = Comparator.comparing(Course::startDate);
    private final Comparator<Course> byCapacity = Comparator.comparing(Course::maxCapacity);
    
    /**
     * Add a new course to the catalog (thread-safe)
     */
    public boolean addCourse(Course course) {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(course, "Course cannot be null");
            
            if (courses.containsKey(course.courseId())) {
                logger.warn("Course with ID {} already exists", course.courseId());
                return false;
            }
            
            courses.put(course.courseId(), course);
            courseList.add(course);
            activeInstructors.add(course.instructor());
            
            logger.info("Added course: {} by {}", course.title(), course.instructor());
            return true;
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get course by ID with Optional return type
     */
    public Optional<Course> getCourse(String courseId) {
        return Optional.ofNullable(courses.get(courseId));
    }
    
    /**
     * Get all courses as immutable list
     */
    public List<Course> getAllCourses() {
        return List.copyOf(courseList);
    }
    
    /**
     * Stream-based course filtering operations
     */
    public List<Course> getActiveCourses() {
        return courseList.stream()
                .filter(isActiveCourse)
                .sorted(byStartDate)
                .collect(Collectors.toList());
    }
    
    public List<Course> getCoursesByLevel(Course.CourseLevel level) {
        return courseList.stream()
                .filter(course -> course.level() == level)
                .sorted(byStartDate)
                .collect(Collectors.toList());
    }
    
    public List<Course> getCoursesByInstructor(String instructor) {
        return courseList.stream()
                .filter(course -> instructor.equals(course.instructor()))
                .sorted(byStartDate)
                .collect(Collectors.toList());
    }
    
    public List<Course> getAvailableCourses() {
        return courseList.stream()
                .filter(Course::canEnroll)
                .sorted(byCapacity.reversed()) // Most capacity first
                .collect(Collectors.toList());
    }
    
    /**
     * Advanced filtering with compound predicates
     */
    public List<Course> findCourses(CourseSearchCriteria criteria) {
        Predicate<Course> combinedFilter = course -> true; // Start with always true
        
        if (criteria.level() != null) {
            combinedFilter = combinedFilter.and(course -> course.level() == criteria.level());
        }
        
        if (criteria.instructor() != null && !criteria.instructor().isBlank()) {
            combinedFilter = combinedFilter.and(course -> 
                course.instructor().toLowerCase().contains(criteria.instructor().toLowerCase()));
        }
        
        if (criteria.availableOnly()) {
            combinedFilter = combinedFilter.and(Course::canEnroll);
        }
        
        if (criteria.activeOnly()) {
            combinedFilter = combinedFilter.and(Course::isActive);
        }
        
        if (criteria.minCapacity() > 0) {
            combinedFilter = combinedFilter.and(course -> course.maxCapacity() >= criteria.minCapacity());
        }
        
        return courseList.stream()
                .filter(combinedFilter)
                .sorted(getSortComparator(criteria.sortBy()))
                .limit(criteria.maxResults())
                .collect(Collectors.toList());
    }
    
    /**
     * Grouping operations demonstrating advanced stream usage
     */
    public Map<String, List<Course>> getCoursesByInstructor() {
        return courseList.stream()
                .collect(Collectors.groupingBy(Course::instructor));
    }
    
    public Map<Course.CourseLevel, List<Course>> getCoursesByLevel() {
        return courseList.stream()
                .collect(Collectors.groupingBy(Course::level));
    }
    
    public Map<String, Long> getInstructorCourseCounts() {
        return courseList.stream()
                .collect(Collectors.groupingBy(
                    Course::instructor,
                    Collectors.counting()
                ));
    }
    
    public Map<Course.CourseLevel, Double> getAverageCapacityByLevel() {
        return courseList.stream()
                .collect(Collectors.groupingBy(
                    Course::level,
                    Collectors.averagingInt(Course::maxCapacity)
                ));
    }
    
    /**
     * Complex aggregation operations
     */
    public CourseCatalogStatistics getStatistics() {
        lock.readLock().lock();
        try {
            int totalCourses = courseList.size();
            int activeCourses = (int) courseList.stream().filter(Course::isActive).count();
            int availableCourses = (int) courseList.stream().filter(Course::canEnroll).count();
            
            OptionalDouble avgCapacity = courseList.stream()
                    .mapToInt(Course::maxCapacity)
                    .average();
            
            Optional<Course> mostPopular = courseList.stream()
                    .max(Comparator.comparing(Course::getCurrentEnrollmentCount));
            
            Set<String> uniqueInstructors = courseList.stream()
                    .map(Course::instructor)
                    .collect(Collectors.toSet());
            
            Map<Course.CourseLevel, Long> levelDistribution = courseList.stream()
                    .collect(Collectors.groupingBy(Course::level, Collectors.counting()));
            
            return new CourseCatalogStatistics(
                totalCourses,
                activeCourses,
                availableCourses,
                avgCapacity.orElse(0.0),
                mostPopular.map(Course::title).orElse("None"),
                uniqueInstructors.size(),
                levelDistribution
            );
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Parallel processing for expensive operations
     */
    public List<CourseReport> generateCourseReports() {
        return courseList.parallelStream()
                .map(this::generateCourseReport)
                .collect(Collectors.toList());
    }
    
    private CourseReport generateCourseReport(Course course) {
        // Simulate expensive report generation
        int enrollmentCount = course.getCurrentEnrollmentCount();
        double fillRate = (double) enrollmentCount / course.maxCapacity() * 100;
        
        return new CourseReport(
            course.courseId(),
            course.title(),
            course.instructor(),
            enrollmentCount,
            course.maxCapacity(),
            fillRate,
            course.isActive()
        );
    }
    
    /**
     * Generic search method demonstrating type parameters
     */
    public <T> List<Course> searchBy(Function<Course, T> extractor, T value) {
        return courseList.stream()
                .filter(course -> Objects.equals(extractor.apply(course), value))
                .collect(Collectors.toList());
    }
    
    /**
     * Higher-order function for custom filtering
     */
    public List<Course> filterCourses(Predicate<Course> customFilter) {
        return courseList.stream()
                .filter(customFilter)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private Comparator<Course> getSortComparator(CourseSortBy sortBy) {
        return switch (sortBy) {
            case TITLE -> Comparator.comparing(Course::title);
            case START_DATE -> Comparator.comparing(Course::startDate);
            case INSTRUCTOR -> Comparator.comparing(Course::instructor);
            case CAPACITY -> Comparator.comparing(Course::maxCapacity).reversed();
            case LEVEL -> Comparator.comparing(Course::level);
        };
    }
    
    // Inner classes for search criteria and results
    public record CourseSearchCriteria(
        Course.CourseLevel level,
        String instructor,
        boolean availableOnly,
        boolean activeOnly,
        int minCapacity,
        CourseSortBy sortBy,
        int maxResults
    ) {
        public CourseSearchCriteria {
            if (maxResults <= 0) {
                maxResults = Integer.MAX_VALUE;
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Course.CourseLevel level;
            private String instructor;
            private boolean availableOnly = false;
            private boolean activeOnly = false;
            private int minCapacity = 0;
            private CourseSortBy sortBy = CourseSortBy.START_DATE;
            private int maxResults = Integer.MAX_VALUE;
            
            public Builder level(Course.CourseLevel level) {
                this.level = level;
                return this;
            }
            
            public Builder instructor(String instructor) {
                this.instructor = instructor;
                return this;
            }
            
            public Builder availableOnly() {
                this.availableOnly = true;
                return this;
            }
            
            public Builder activeOnly() {
                this.activeOnly = true;
                return this;
            }
            
            public Builder minCapacity(int minCapacity) {
                this.minCapacity = minCapacity;
                return this;
            }
            
            public Builder sortBy(CourseSortBy sortBy) {
                this.sortBy = sortBy;
                return this;
            }
            
            public Builder maxResults(int maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public CourseSearchCriteria build() {
                return new CourseSearchCriteria(level, instructor, availableOnly, 
                                              activeOnly, minCapacity, sortBy, maxResults);
            }
        }
    }
    
    public enum CourseSortBy {
        TITLE, START_DATE, INSTRUCTOR, CAPACITY, LEVEL
    }
    
    public record CourseCatalogStatistics(
        int totalCourses,
        int activeCourses,
        int availableCourses,
        double averageCapacity,
        String mostPopularCourse,
        int uniqueInstructors,
        Map<Course.CourseLevel, Long> levelDistribution
    ) {}
    
    public record CourseReport(
        String courseId,
        String title,
        String instructor,
        int currentEnrollment,
        int maxCapacity,
        double fillRate,
        boolean isActive
    ) {}
}