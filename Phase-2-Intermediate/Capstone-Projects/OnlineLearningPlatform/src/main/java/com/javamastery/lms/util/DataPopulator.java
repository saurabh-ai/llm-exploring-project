package com.javamastery.lms.util;

import com.javamastery.lms.analytics.LearningAnalyticsService;
import com.javamastery.lms.model.*;
import com.javamastery.lms.service.CourseCatalogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class to populate sample data for demonstration
 * Shows functional programming and builder pattern usage
 */
public class DataPopulator {
    
    private final CourseCatalogService catalogService;
    private final LearningAnalyticsService analyticsService;
    
    private static final String[] COURSE_TITLES = {
        "Introduction to Java Programming",
        "Advanced Spring Framework",
        "Microservices Architecture",
        "Database Design Fundamentals",
        "RESTful API Development",
        "Docker and Containerization",
        "React Frontend Development",
        "Machine Learning Basics",
        "Software Testing Strategies",
        "DevOps and CI/CD Pipelines"
    };
    
    private static final String[] INSTRUCTORS = {
        "Dr. Sarah Johnson", "Prof. Michael Chen", "Dr. Emily Davis",
        "Prof. David Wilson", "Dr. Lisa Anderson", "Prof. Robert Brown",
        "Dr. Jennifer Martinez", "Prof. Christopher Lee", "Dr. Amanda Taylor"
    };
    
    private static final String[] STUDENT_NAMES = {
        "Alice", "Bob", "Carol", "David", "Eva", "Frank", "Grace", "Henry",
        "Ivy", "Jack", "Karen", "Leo", "Maria", "Nathan", "Olivia", "Paul"
    };
    
    private static final String[] INTERESTS = {
        "Programming", "Web Development", "Data Science", "Mobile Apps",
        "Cloud Computing", "Artificial Intelligence", "Cybersecurity",
        "Game Development", "DevOps", "Machine Learning"
    };
    
    public DataPopulator(CourseCatalogService catalogService, 
                        LearningAnalyticsService analyticsService) {
        this.catalogService = catalogService;
        this.analyticsService = analyticsService;
    }
    
    /**
     * Populate sample data using functional programming patterns
     */
    public void populateSampleData() {
        populateCourses();
        populateStudents();
    }
    
    private void populateCourses() {
        for (int i = 0; i < COURSE_TITLES.length; i++) {
            Course course = new Course.Builder(
                "COURSE-" + String.format("%03d", i + 1),
                COURSE_TITLES[i],
                randomElement(INSTRUCTORS)
            )
            .description("Comprehensive course covering " + COURSE_TITLES[i].toLowerCase())
            .level(randomElement(Course.CourseLevel.values()))
            .maxCapacity(ThreadLocalRandom.current().nextInt(20, 51)) // 20-50 capacity
            .startDate(LocalDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(1, 30)))
            .endDate(LocalDateTime.now().plusDays(ThreadLocalRandom.current().nextInt(31, 90)))
            .addModule(createSampleModule("Module 1: Fundamentals", 1))
            .addModule(createSampleModule("Module 2: Intermediate Concepts", 2))
            .addModule(createSampleModule("Module 3: Advanced Topics", 3))
            .build();
            
            catalogService.addCourse(course);
        }
    }
    
    private Course.Module createSampleModule(String title, int moduleNum) {
        return new Course.Module(
            "MOD-" + moduleNum,
            title,
            "Detailed module covering " + title.toLowerCase(),
            ThreadLocalRandom.current().nextInt(5, 13), // 5-12 lessons
            ThreadLocalRandom.current().nextInt(8, 21)   // 8-20 hours
        );
    }
    
    private void populateStudents() {
        for (int i = 0; i < STUDENT_NAMES.length; i++) {
            String lastName = "Student" + (i + 1);
            Student student = new Student.Builder(
                "STUDENT-" + String.format("%03d", i + 1),
                STUDENT_NAMES[i],
                lastName,
                STUDENT_NAMES[i].toLowerCase() + "." + lastName.toLowerCase() + "@email.com"
            )
            .registrationDate(LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(1, 365)))
            .level(randomElement(Student.StudentLevel.values()))
            .interests(generateRandomInterests())
            .build();
            
            analyticsService.addStudent(student);
        }
    }
    
    private Set<String> generateRandomInterests() {
        int numInterests = ThreadLocalRandom.current().nextInt(2, 5); // 2-4 interests
        Set<String> interests = new java.util.HashSet<>();
        
        while (interests.size() < numInterests) {
            interests.add(randomElement(INTERESTS));
        }
        
        return interests;
    }
    
    private <T> T randomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}