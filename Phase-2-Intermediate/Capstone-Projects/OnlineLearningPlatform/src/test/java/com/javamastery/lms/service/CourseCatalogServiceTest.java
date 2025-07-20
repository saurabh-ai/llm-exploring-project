package com.javamastery.lms.service;

import com.javamastery.lms.model.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DisplayName("Course Catalog Service Tests")
class CourseCatalogServiceTest {
    
    private CourseCatalogService catalogService;
    private Course testCourse;
    
    @BeforeEach
    void setUp() {
        catalogService = new CourseCatalogService();
        testCourse = new Course.Builder("COURSE-001", "Java Basics", "Dr. Smith")
                .description("Introduction to Java")
                .level(Course.CourseLevel.BEGINNER)
                .maxCapacity(30)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(31))
                .build();
    }
    
    @Test
    @DisplayName("Should add and retrieve courses correctly")
    void testAddAndRetrieveCourse() {
        assertTrue(catalogService.addCourse(testCourse));
        
        Optional<Course> retrieved = catalogService.getCourse("COURSE-001");
        assertTrue(retrieved.isPresent());
        assertEquals(testCourse.title(), retrieved.get().title());
        
        // Test duplicate addition
        assertFalse(catalogService.addCourse(testCourse));
    }
    
    @Test
    @DisplayName("Should filter courses by various criteria")
    void testCourseFiltering() {
        // Add multiple courses
        catalogService.addCourse(testCourse);
        catalogService.addCourse(createCourse("COURSE-002", "Advanced Java", Course.CourseLevel.ADVANCED));
        catalogService.addCourse(createCourse("COURSE-003", "Spring Framework", Course.CourseLevel.INTERMEDIATE));
        
        // Test filtering by level
        List<Course> beginnerCourses = catalogService.getCoursesByLevel(Course.CourseLevel.BEGINNER);
        assertEquals(1, beginnerCourses.size());
        assertEquals("Java Basics", beginnerCourses.get(0).title());
        
        // Test filtering by instructor
        List<Course> smithCourses = catalogService.getCoursesByInstructor("Dr. Smith");
        assertEquals(3, smithCourses.size()); // All courses have Dr. Smith as instructor
        
        // Test available courses
        List<Course> availableCourses = catalogService.getAvailableCourses();
        assertFalse(availableCourses.isEmpty());
    }
    
    @Test
    @DisplayName("Should perform advanced search with criteria")
    void testAdvancedSearch() {
        catalogService.addCourse(testCourse);
        catalogService.addCourse(createCourse("COURSE-002", "Advanced Java", Course.CourseLevel.ADVANCED));
        
        var criteria = CourseCatalogService.CourseSearchCriteria.builder()
                .level(Course.CourseLevel.BEGINNER)
                .instructor("Dr. Smith")
                .availableOnly()
                .maxResults(10)
                .build();
        
        List<Course> results = catalogService.findCourses(criteria);
        assertEquals(1, results.size());
        assertEquals("Java Basics", results.get(0).title());
    }
    
    @Test
    @DisplayName("Should generate statistics correctly")
    void testStatisticsGeneration() {
        catalogService.addCourse(testCourse);
        catalogService.addCourse(createCourse("COURSE-002", "Advanced Java", Course.CourseLevel.ADVANCED));
        
        var stats = catalogService.getStatistics();
        
        assertEquals(2, stats.totalCourses());
        assertEquals(2, stats.activeCourses());
        assertTrue(stats.averageCapacity() > 0);
        assertEquals(1, stats.uniqueInstructors()); // All courses by Dr. Smith
    }
    
    @Test
    @DisplayName("Should handle concurrent operations safely")
    void testConcurrentOperations() throws InterruptedException {
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                Course course = createCourse("COURSE-" + String.format("%03d", index), 
                                           "Course " + index, Course.CourseLevel.BEGINNER);
                catalogService.addCourse(course);
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        assertEquals(10, catalogService.getAllCourses().size());
    }
    
    private Course createCourse(String id, String title, Course.CourseLevel level) {
        return new Course.Builder(id, title, "Dr. Smith")
                .level(level)
                .maxCapacity(25)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(31))
                .build();
    }
}