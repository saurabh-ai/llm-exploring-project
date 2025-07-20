package com.javamastery.lms.concurrent;

import com.javamastery.lms.model.*;
import com.javamastery.lms.service.CourseCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Thread-safe enrollment service demonstrating advanced multithreading concepts
 * Including ExecutorService, CompletableFuture, and concurrent collections
 */
public class EnrollmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);
    
    // Thread-safe enrollment storage
    private final Map<String, Enrollment> enrollments = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> studentEnrollments = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> courseEnrollments = new ConcurrentHashMap<>();
    
    // Atomic counters for ID generation
    private final AtomicLong enrollmentIdCounter = new AtomicLong(1);
    
    // Thread pool for async operations
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    
    // References to other services
    private final CourseCatalogService catalogService;
    
    public EnrollmentService(CourseCatalogService catalogService) {
        this.catalogService = catalogService;
        this.executorService = Executors.newFixedThreadPool(10, r -> {
            Thread thread = new Thread(r);
            thread.setName("EnrollmentService-Worker-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
        this.scheduledExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r);
            thread.setName("EnrollmentService-Scheduler-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
    }
    
    /**
     * Asynchronous enrollment method using CompletableFuture
     */
    public CompletableFuture<EnrollmentResult> enrollStudentAsync(String studentId, String courseId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return enrollStudent(studentId, courseId);
            } catch (Exception e) {
                logger.error("Error enrolling student {} in course {}", studentId, courseId, e);
                return EnrollmentResult.failure("Enrollment failed: " + e.getMessage());
            }
        }, executorService);
    }
    
    /**
     * Synchronous enrollment with thread-safe operations
     */
    public EnrollmentResult enrollStudent(String studentId, String courseId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        
        logger.info("Attempting to enroll student {} in course {}", studentId, courseId);
        
        // Check if already enrolled
        if (isStudentEnrolledInCourse(studentId, courseId)) {
            return EnrollmentResult.failure("Student is already enrolled in this course");
        }
        
        // Get course and validate capacity
        Optional<Course> courseOpt = catalogService.getCourse(courseId);
        if (courseOpt.isEmpty()) {
            return EnrollmentResult.failure("Course not found");
        }
        
        Course course = courseOpt.get();
        if (!course.canEnroll()) {
            return EnrollmentResult.failure("Course is full or not available for enrollment");
        }
        
        // Generate enrollment ID
        String enrollmentId = "ENR-" + enrollmentIdCounter.getAndIncrement();
        
        // Create enrollment
        Enrollment enrollment = Enrollment.createNew(enrollmentId, studentId, courseId);
        
        // Thread-safe updates using atomic operations
        enrollments.put(enrollmentId, enrollment);
        studentEnrollments.computeIfAbsent(studentId, k -> ConcurrentHashMap.newKeySet()).add(courseId);
        courseEnrollments.computeIfAbsent(courseId, k -> ConcurrentHashMap.newKeySet()).add(studentId);
        
        // Update course enrollment (this method is thread-safe)
        course.enrollStudent(studentId);
        
        logger.info("Successfully enrolled student {} in course {}", studentId, courseId);
        return EnrollmentResult.success(enrollment);
    }
    
    /**
     * Batch enrollment using parallel processing
     */
    public CompletableFuture<List<EnrollmentResult>> enrollStudentsBatch(List<String> studentIds, String courseId) {
        List<CompletableFuture<EnrollmentResult>> futures = studentIds.stream()
                .map(studentId -> enrollStudentAsync(studentId, courseId))
                .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }
    
    /**
     * Unenroll student with status updates
     */
    public CompletableFuture<Boolean> unenrollStudentAsync(String studentId, String courseId) {
        return CompletableFuture.supplyAsync(() -> {
            String enrollmentId = findEnrollmentId(studentId, courseId);
            if (enrollmentId == null) {
                logger.warn("No enrollment found for student {} in course {}", studentId, courseId);
                return false;
            }
            
            Enrollment current = enrollments.get(enrollmentId);
            if (current == null) {
                return false;
            }
            
            // Update enrollment status to DROPPED
            Enrollment updated = current.drop();
            enrollments.put(enrollmentId, updated);
            
            // Update collections
            Set<String> studentCourses = studentEnrollments.get(studentId);
            if (studentCourses != null) {
                studentCourses.remove(courseId);
            }
            
            Set<String> courseStudents = courseEnrollments.get(courseId);
            if (courseStudents != null) {
                courseStudents.remove(studentId);
            }
            
            // Update course enrollment count
            catalogService.getCourse(courseId).ifPresent(course -> course.unenrollStudent(studentId));
            
            logger.info("Student {} unenrolled from course {}", studentId, courseId);
            return true;
            
        }, executorService);
    }
    
    /**
     * Get enrollments for a student using streams
     */
    public List<Enrollment> getStudentEnrollments(String studentId) {
        Set<String> courseIds = studentEnrollments.getOrDefault(studentId, Set.of());
        
        return enrollments.values().stream()
                .filter(enrollment -> studentId.equals(enrollment.studentId()))
                .filter(enrollment -> courseIds.contains(enrollment.courseId()))
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Get enrollments for a course
     */
    public List<Enrollment> getCourseEnrollments(String courseId) {
        Set<String> studentIds = courseEnrollments.getOrDefault(courseId, Set.of());
        
        return enrollments.values().stream()
                .filter(enrollment -> courseId.equals(enrollment.courseId()))
                .filter(enrollment -> studentIds.contains(enrollment.studentId()))
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Generate enrollment analytics using parallel processing
     */
    public CompletableFuture<EnrollmentAnalytics> generateAnalyticsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            
            Map<Enrollment.EnrollmentStatus, Long> statusDistribution = enrollments.values()
                    .parallelStream()
                    .collect(Collectors.groupingBy(
                        Enrollment::status,
                        Collectors.counting()
                    ));
            
            Map<String, Long> coursePop = enrollments.values()
                    .parallelStream()
                    .filter(e -> e.status() == Enrollment.EnrollmentStatus.ACTIVE)
                    .collect(Collectors.groupingBy(
                        Enrollment::courseId,
                        Collectors.counting()
                    ));
            
            OptionalDouble avgEnrollmentAge = enrollments.values()
                    .parallelStream()
                    .mapToLong(Enrollment::getDaysEnrolled)
                    .average();
            
            return new EnrollmentAnalytics(
                enrollments.size(),
                statusDistribution,
                coursePop,
                avgEnrollmentAge.orElse(0.0)
            );
            
        }, executorService);
    }
    
    /**
     * Scheduled task for enrollment cleanup
     */
    public void startEnrollmentCleanupTask() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                cleanupExpiredEnrollments();
            } catch (Exception e) {
                logger.error("Error during enrollment cleanup", e);
            }
        }, 1, 24, TimeUnit.HOURS);
    }
    
    private void cleanupExpiredEnrollments() {
        logger.info("Starting enrollment cleanup task");
        
        List<String> expiredEnrollments = enrollments.values().stream()
                .filter(enrollment -> enrollment.status() == Enrollment.EnrollmentStatus.DROPPED)
                .filter(enrollment -> enrollment.getDaysSinceStatusChange() > 30)
                .map(Enrollment::enrollmentId)
                .collect(Collectors.toList());
        
        expiredEnrollments.forEach(enrollments::remove);
        
        logger.info("Cleaned up {} expired enrollments", expiredEnrollments.size());
    }
    
    /**
     * Concurrent wait list management
     */
    private final Map<String, Queue<String>> courseWaitLists = new ConcurrentHashMap<>();
    
    public CompletableFuture<Boolean> addToWaitList(String studentId, String courseId) {
        return CompletableFuture.supplyAsync(() -> {
            Queue<String> waitList = courseWaitLists.computeIfAbsent(courseId, 
                k -> new ConcurrentLinkedQueue<>());
            
            if (waitList.contains(studentId)) {
                return false; // Already on wait list
            }
            
            waitList.offer(studentId);
            logger.info("Added student {} to wait list for course {}", studentId, courseId);
            return true;
        }, executorService);
    }
    
    public CompletableFuture<Void> processWaitList(String courseId) {
        return CompletableFuture.runAsync(() -> {
            Queue<String> waitList = courseWaitLists.get(courseId);
            if (waitList == null || waitList.isEmpty()) {
                return;
            }
            
            Optional<Course> courseOpt = catalogService.getCourse(courseId);
            if (courseOpt.isEmpty() || !courseOpt.get().canEnroll()) {
                return;
            }
            
            String nextStudent = waitList.poll();
            if (nextStudent != null) {
                EnrollmentResult result = enrollStudent(nextStudent, courseId);
                if (result.success()) {
                    logger.info("Enrolled wait-listed student {} in course {}", nextStudent, courseId);
                } else {
                    // Put student back on wait list if enrollment failed
                    waitList.offer(nextStudent);
                }
            }
        }, executorService);
    }
    
    // Helper methods
    private boolean isStudentEnrolledInCourse(String studentId, String courseId) {
        return studentEnrollments.getOrDefault(studentId, Set.of()).contains(courseId);
    }
    
    private String findEnrollmentId(String studentId, String courseId) {
        return enrollments.entrySet().stream()
                .filter(entry -> studentId.equals(entry.getValue().studentId()) 
                               && courseId.equals(entry.getValue().courseId()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    
    // Shutdown hook for clean resource management
    public void shutdown() {
        logger.info("Shutting down enrollment service");
        executorService.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
        }
    }
    
    // Result classes
    public record EnrollmentResult(
        boolean success,
        String message,
        Enrollment enrollment
    ) {
        public static EnrollmentResult success(Enrollment enrollment) {
            return new EnrollmentResult(true, "Enrollment successful", enrollment);
        }
        
        public static EnrollmentResult failure(String message) {
            return new EnrollmentResult(false, message, null);
        }
    }
    
    public record EnrollmentAnalytics(
        int totalEnrollments,
        Map<Enrollment.EnrollmentStatus, Long> statusDistribution,
        Map<String, Long> coursePopularity,
        double averageEnrollmentDays
    ) {}
}