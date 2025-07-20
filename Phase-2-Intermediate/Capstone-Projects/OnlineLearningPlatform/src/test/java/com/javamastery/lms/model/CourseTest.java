package com.javamastery.lms.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;

@DisplayName("Course Model Tests")
class CourseTest {
    
    private Course.Builder courseBuilder;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now().plusDays(7);
        endDate = LocalDateTime.now().plusDays(37);
        courseBuilder = new Course.Builder("COURSE-001", "Java Basics", "Dr. Smith")
                .description("Introduction to Java programming")
                .level(Course.CourseLevel.BEGINNER)
                .maxCapacity(30)
                .startDate(startDate)
                .endDate(endDate);
    }
    
    @Test
    @DisplayName("Should create course with valid parameters")
    void testCourseCreation() {
        Course course = courseBuilder.build();
        
        assertEquals("COURSE-001", course.courseId());
        assertEquals("Java Basics", course.title());
        assertEquals("Dr. Smith", course.instructor());
        assertEquals(Course.CourseLevel.BEGINNER, course.level());
        assertEquals(30, course.maxCapacity());
        assertEquals(startDate, course.startDate());
        assertEquals(endDate, course.endDate());
    }
    
    @Test
    @DisplayName("Should validate required fields")
    void testValidation() {
        assertThrows(NullPointerException.class, () ->
            new Course.Builder(null, "Title", "Instructor").build());
        
        assertThrows(NullPointerException.class, () ->
            new Course.Builder("ID", null, "Instructor").build());
        
        assertThrows(NullPointerException.class, () ->
            new Course.Builder("ID", "Title", null).build());
        
        assertThrows(IllegalArgumentException.class, () ->
            courseBuilder.maxCapacity(0).build());
        
        assertThrows(IllegalArgumentException.class, () ->
            courseBuilder.endDate(startDate.minusDays(1)).build());
    }
    
    @Test
    @DisplayName("Should handle course enrollment operations thread-safely")
    void testEnrollmentOperations() {
        Course course = courseBuilder.build();
        
        // Test initial state
        assertEquals(0, course.getCurrentEnrollmentCount());
        assertTrue(course.canEnroll());
        
        // Test enrollment
        assertTrue(course.enrollStudent("STUDENT-001"));
        assertEquals(1, course.getCurrentEnrollmentCount());
        assertTrue(course.getEnrolledStudents().contains("STUDENT-001"));
        
        // Test duplicate enrollment
        assertFalse(course.enrollStudent("STUDENT-001"));
        assertEquals(1, course.getCurrentEnrollmentCount());
        
        // Test unenrollment
        assertTrue(course.unenrollStudent("STUDENT-001"));
        assertEquals(0, course.getCurrentEnrollmentCount());
        assertFalse(course.getEnrolledStudents().contains("STUDENT-001"));
    }
    
    @Test
    @DisplayName("Should calculate derived properties correctly")
    void testDerivedProperties() {
        Course course = courseBuilder
                .addModule(new Course.Module("MOD-1", "Module 1", "Desc", 5, 10))
                .addModule(new Course.Module("MOD-2", "Module 2", "Desc", 3, 8))
                .build();
        
        assertEquals(8, course.getTotalLessons());
        assertEquals(30, course.getDurationInDays());
        assertFalse(course.isActive()); // Course starts in future
    }
    
    @Test
    @DisplayName("Should implement Comparable correctly")
    void testComparable() {
        Course course1 = courseBuilder.startDate(startDate).build();
        Course course2 = new Course.Builder("COURSE-002", "Advanced Java", "Dr. Johnson")
                .startDate(startDate.plusDays(1))
                .endDate(endDate.plusDays(1))
                .build();
        
        assertTrue(course1.compareTo(course2) < 0); // course1 starts earlier
        
        // Test secondary sort by title
        Course course3 = new Course.Builder("COURSE-003", "A-Course", "Dr. Wilson")
                .startDate(startDate) // Same start date
                .endDate(endDate)
                .build();
        
        assertTrue(course3.compareTo(course1) < 0); // "A-Course" comes before "Java Basics"
    }
    
    @Test
    @DisplayName("Should handle concurrent enrollment operations")
    void testConcurrentEnrollment() throws InterruptedException {
        Course course = courseBuilder.maxCapacity(2).build();
        
        Thread[] threads = new Thread[5];
        boolean[] results = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = course.enrollStudent("STUDENT-" + String.format("%03d", index + 1));
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Only 2 should succeed due to capacity limit
        long successCount = 0;
        for (boolean result : results) {
            if (result) successCount++;
        }
        
        assertEquals(2, successCount);
        assertEquals(2, course.getCurrentEnrollmentCount());
    }
    
    @Test
    @DisplayName("Should handle module operations correctly")
    void testModuleOperations() {
        Course.Module module = new Course.Module("MOD-1", "Test Module", "Description", 5, 10);
        
        assertEquals("MOD-1", module.moduleId());
        assertEquals("Test Module", module.title());
        assertEquals(5, module.getLessonCount());
        assertEquals(10, module.estimatedHours());
        
        // Test module validation
        assertThrows(NullPointerException.class, () ->
            new Course.Module(null, "Title", "Desc", 5, 10));
        
        assertThrows(IllegalArgumentException.class, () ->
            new Course.Module("ID", "Title", "Desc", -1, 10));
        
        assertThrows(IllegalArgumentException.class, () ->
            new Course.Module("ID", "Title", "Desc", 5, -1));
    }
}