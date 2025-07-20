package com.javamastery.lms.analytics;

import com.javamastery.lms.model.*;
import com.javamastery.lms.service.CourseCatalogService;
import com.javamastery.lms.concurrent.EnrollmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Advanced analytics service demonstrating comprehensive stream processing,
 * lambda expressions, and functional programming patterns
 */
public class LearningAnalyticsService {
    
    private final CourseCatalogService catalogService;
    private final EnrollmentService enrollmentService;
    
    // Map of student data for analytics
    private final Map<String, Student> students = new HashMap<>();
    
    public LearningAnalyticsService(CourseCatalogService catalogService, 
                                  EnrollmentService enrollmentService) {
        this.catalogService = catalogService;
        this.enrollmentService = enrollmentService;
    }
    
    public void addStudent(Student student) {
        students.put(student.studentId(), student);
    }
    
    /**
     * Generate comprehensive platform analytics using advanced stream operations
     */
    public CompletableFuture<PlatformAnalytics> generatePlatformAnalytics() {
        return CompletableFuture.supplyAsync(() -> {
            
            // Course analytics using custom collectors
            CourseAnalytics courseAnalytics = generateCourseAnalytics();
            
            // Student analytics with functional composition
            StudentAnalytics studentAnalytics = generateStudentAnalytics();
            
            // Enrollment trends using time-based grouping
            EnrollmentTrends enrollmentTrends = generateEnrollmentTrends();
            
            // Performance metrics using statistical operations
            PerformanceMetrics performance = generatePerformanceMetrics();
            
            return new PlatformAnalytics(
                LocalDateTime.now(),
                courseAnalytics,
                studentAnalytics,
                enrollmentTrends,
                performance
            );
        });
    }
    
    /**
     * Course analytics using advanced stream operations and custom collectors
     */
    private CourseAnalytics generateCourseAnalytics() {
        List<Course> courses = catalogService.getAllCourses();
        
        // Total and active course counts
        long totalCourses = courses.size();
        long activeCourses = courses.stream().filter(Course::isActive).count();
        
        // Course distribution by level using grouping collector
        Map<Course.CourseLevel, Long> levelDistribution = courses.stream()
                .collect(Collectors.groupingBy(Course::level, Collectors.counting()));
        
        // Capacity utilization statistics using custom collector
        CapacityStatistics capacityStats = courses.stream()
                .collect(toCapacityStatistics());
        
        // Top instructors by course count using advanced grouping
        List<InstructorStats> topInstructors = courses.stream()
                .collect(Collectors.groupingBy(
                    Course::instructor,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        courseList -> new InstructorStats(
                            courseList.get(0).instructor(),
                            courseList.size(),
                            courseList.stream().mapToInt(Course::getCurrentEnrollmentCount).sum(),
                            courseList.stream().mapToInt(Course::maxCapacity).sum()
                        )
                    )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(InstructorStats::courseCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        // Course popularity ranking
        List<CoursePopularity> popularity = courses.stream()
                .map(course -> new CoursePopularity(
                    course.courseId(),
                    course.title(),
                    course.getCurrentEnrollmentCount(),
                    course.maxCapacity(),
                    (double) course.getCurrentEnrollmentCount() / course.maxCapacity() * 100
                ))
                .sorted(Comparator.comparing(CoursePopularity::enrollmentRate).reversed())
                .collect(Collectors.toList());
        
        return new CourseAnalytics(
            totalCourses,
            activeCourses,
            levelDistribution,
            capacityStats,
            topInstructors,
            popularity
        );
    }
    
    /**
     * Student analytics demonstrating functional programming patterns
     */
    private StudentAnalytics generateStudentAnalytics() {
        Collection<Student> allStudents = students.values();
        
        // Basic statistics
        long totalStudents = allStudents.size();
        long activeStudents = allStudents.stream()
                .filter(Student::isActive)
                .count();
        
        // Student level distribution
        Map<Student.StudentLevel, Long> levelDistribution = allStudents.stream()
                .collect(Collectors.groupingBy(Student::level, Collectors.counting()));
        
        // Interest analysis using flatMap and frequency counting
        Map<String, Long> topInterests = allStudents.stream()
                .flatMap(student -> student.interests().stream())
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        
        // Registration trends by month
        Map<String, Long> registrationTrends = allStudents.stream()
                .collect(Collectors.groupingBy(
                    student -> student.registrationDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    Collectors.counting()
                ));
        
        // Student engagement metrics using progress analysis
        StudentEngagementMetrics engagement = calculateEngagementMetrics();
        
        return new StudentAnalytics(
            totalStudents,
            activeStudents,
            levelDistribution,
            topInterests,
            registrationTrends,
            engagement
        );
    }
    
    /**
     * Enrollment trends using time-based stream processing
     */
    private EnrollmentTrends generateEnrollmentTrends() {
        // This would typically get data from enrollmentService
        // For demo purposes, creating sample trend data
        
        Map<String, Long> monthlyEnrollments = new HashMap<>();
        Map<String, Double> conversionRates = new HashMap<>();
        Map<Course.CourseLevel, Long> enrollmentsByLevel = new HashMap<>();
        
        // Simulate enrollment trend calculation
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 12; i++) {
            LocalDateTime month = now.minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyEnrollments.put(monthKey, (long) (Math.random() * 100 + 50));
            conversionRates.put(monthKey, Math.random() * 20 + 70); // 70-90% conversion rate
        }
        
        // Enrollment distribution by course level
        Arrays.stream(Course.CourseLevel.values())
              .forEach(level -> enrollmentsByLevel.put(level, (long) (Math.random() * 200 + 50)));
        
        return new EnrollmentTrends(
            monthlyEnrollments,
            conversionRates,
            enrollmentsByLevel
        );
    }
    
    /**
     * Performance metrics using statistical stream operations
     */
    private PerformanceMetrics generatePerformanceMetrics() {
        List<Course> courses = catalogService.getAllCourses();
        
        // Completion rate analysis
        double overallCompletionRate = courses.stream()
                .mapToDouble(course -> calculateCompletionRate(course.courseId()))
                .average()
                .orElse(0.0);
        
        // Course effectiveness ratings
        Map<String, Double> courseEffectiveness = courses.stream()
                .collect(Collectors.toMap(
                    Course::courseId,
                    course -> calculateCourseEffectiveness(course.courseId())
                ));
        
        // Student progress distribution
        ProgressDistribution progressDist = calculateProgressDistribution();
        
        // Learning path optimization suggestions
        List<OptimizationSuggestion> suggestions = generateOptimizationSuggestions();
        
        return new PerformanceMetrics(
            overallCompletionRate,
            courseEffectiveness,
            progressDist,
            suggestions
        );
    }
    
    // Custom collectors and helper methods
    
    /**
     * Custom collector for capacity statistics
     */
    private Collector<Course, ?, CapacityStatistics> toCapacityStatistics() {
        return Collector.of(
            CapacityAccumulator::new,
            CapacityAccumulator::accept,
            CapacityAccumulator::combine,
            CapacityAccumulator::finish
        );
    }
    
    /**
     * Engagement metrics calculation using functional composition
     */
    private StudentEngagementMetrics calculateEngagementMetrics() {
        double avgCoursesPerStudent = students.values().stream()
                .mapToInt(student -> student.getInProgressCourses().size() + student.getCompletedCourses().size())
                .average()
                .orElse(0.0);
        
        double completionRate = students.values().stream()
                .flatMap(student -> student.getAllProgress().values().stream())
                .collect(Collectors.partitioningBy(progress -> 
                    progress.status() == Student.ProgressStatus.COMPLETED))
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey()) // true = completed
                .map(entry -> entry.getValue().size())
                .mapToDouble(Integer::doubleValue)
                .findFirst()
                .orElse(0.0);
        
        Map<Student.StudentLevel, Double> engagementByLevel = students.values().stream()
                .collect(Collectors.groupingBy(
                    Student::level,
                    Collectors.averagingInt(student -> 
                        student.getInProgressCourses().size() + student.getCompletedCourses().size())
                ));
        
        return new StudentEngagementMetrics(
            avgCoursesPerStudent,
            completionRate,
            engagementByLevel
        );
    }
    
    // Helper methods for metric calculations
    private double calculateCompletionRate(String courseId) {
        // Simulate completion rate calculation
        return Math.random() * 30 + 70; // 70-100% completion rate
    }
    
    private double calculateCourseEffectiveness(String courseId) {
        // Simulate effectiveness calculation based on multiple factors
        return Math.random() * 20 + 80; // 80-100% effectiveness
    }
    
    private ProgressDistribution calculateProgressDistribution() {
        // Simulate progress distribution calculation
        Map<Student.ProgressStatus, Long> distribution = new HashMap<>();
        distribution.put(Student.ProgressStatus.NOT_STARTED, 150L);
        distribution.put(Student.ProgressStatus.IN_PROGRESS, 300L);
        distribution.put(Student.ProgressStatus.COMPLETED, 200L);
        distribution.put(Student.ProgressStatus.DROPPED, 50L);
        
        return new ProgressDistribution(distribution);
    }
    
    private List<OptimizationSuggestion> generateOptimizationSuggestions() {
        return List.of(
            new OptimizationSuggestion("Course Capacity", 
                "Consider increasing capacity for high-demand courses", "HIGH"),
            new OptimizationSuggestion("Learning Path", 
                "Add prerequisite relationships between related courses", "MEDIUM"),
            new OptimizationSuggestion("Student Engagement", 
                "Implement gamification elements to improve completion rates", "MEDIUM")
        );
    }
    
    // Inner classes and records for analytics data structures
    
    private static class CapacityAccumulator {
        private int totalCapacity = 0;
        private int totalEnrolled = 0;
        private int courseCount = 0;
        
        void accept(Course course) {
            totalCapacity += course.maxCapacity();
            totalEnrolled += course.getCurrentEnrollmentCount();
            courseCount++;
        }
        
        CapacityAccumulator combine(CapacityAccumulator other) {
            CapacityAccumulator combined = new CapacityAccumulator();
            combined.totalCapacity = this.totalCapacity + other.totalCapacity;
            combined.totalEnrolled = this.totalEnrolled + other.totalEnrolled;
            combined.courseCount = this.courseCount + other.courseCount;
            return combined;
        }
        
        CapacityStatistics finish() {
            double utilizationRate = courseCount > 0 ? 
                (double) totalEnrolled / totalCapacity * 100 : 0.0;
            double avgCapacity = courseCount > 0 ? 
                (double) totalCapacity / courseCount : 0.0;
            
            return new CapacityStatistics(
                totalCapacity,
                totalEnrolled,
                utilizationRate,
                avgCapacity
            );
        }
    }
    
    // Analytics result records
    
    public record PlatformAnalytics(
        LocalDateTime generatedAt,
        CourseAnalytics courseAnalytics,
        StudentAnalytics studentAnalytics,
        EnrollmentTrends enrollmentTrends,
        PerformanceMetrics performanceMetrics
    ) {}
    
    public record CourseAnalytics(
        long totalCourses,
        long activeCourses,
        Map<Course.CourseLevel, Long> levelDistribution,
        CapacityStatistics capacityStats,
        List<InstructorStats> topInstructors,
        List<CoursePopularity> coursePopularity
    ) {}
    
    public record StudentAnalytics(
        long totalStudents,
        long activeStudents,
        Map<Student.StudentLevel, Long> levelDistribution,
        Map<String, Long> topInterests,
        Map<String, Long> registrationTrends,
        StudentEngagementMetrics engagement
    ) {}
    
    public record EnrollmentTrends(
        Map<String, Long> monthlyEnrollments,
        Map<String, Double> conversionRates,
        Map<Course.CourseLevel, Long> enrollmentsByLevel
    ) {}
    
    public record PerformanceMetrics(
        double overallCompletionRate,
        Map<String, Double> courseEffectiveness,
        ProgressDistribution progressDistribution,
        List<OptimizationSuggestion> suggestions
    ) {}
    
    public record CapacityStatistics(
        int totalCapacity,
        int totalEnrolled,
        double utilizationRate,
        double averageCapacity
    ) {}
    
    public record InstructorStats(
        String instructor,
        int courseCount,
        int totalEnrollments,
        int totalCapacity
    ) {}
    
    public record CoursePopularity(
        String courseId,
        String title,
        int enrollmentCount,
        int capacity,
        double enrollmentRate
    ) {}
    
    public record StudentEngagementMetrics(
        double averageCoursesPerStudent,
        double completionRate,
        Map<Student.StudentLevel, Double> engagementByLevel
    ) {}
    
    public record ProgressDistribution(
        Map<Student.ProgressStatus, Long> statusDistribution
    ) {}
    
    public record OptimizationSuggestion(
        String category,
        String suggestion,
        String priority
    ) {}
}