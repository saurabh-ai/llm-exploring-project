package com.javamastery.grades.service;

import com.javamastery.grades.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for the StudentManager service demonstrating Collections Framework operations.
 */
public class StudentManagerTest {

    private StudentManager studentManager;
    private Subject mathSubject;
    private Subject physicsSubject;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        mathSubject = new Subject("MATH101", "Calculus I", 4);
        physicsSubject = new Subject("PHYS101", "Physics I", 4);
    }

    @Test
    void testAddStudent() {
        Student student = new Student("ST123456", "John Doe", "Computer Science", 2023);
        
        assertTrue(studentManager.addStudent(student));
        assertEquals(1, studentManager.getTotalStudentCount());
        assertEquals(student, studentManager.getStudent("ST123456"));
        
        // Test duplicate prevention
        assertFalse(studentManager.addStudent(student));
        assertEquals(1, studentManager.getTotalStudentCount());
    }

    @Test
    void testRemoveStudent() {
        Student student = new Student("ST123456", "John Doe", "Computer Science", 2023);
        studentManager.addStudent(student);
        
        assertTrue(studentManager.removeStudent("ST123456"));
        assertEquals(0, studentManager.getTotalStudentCount());
        assertNull(studentManager.getStudent("ST123456"));
        
        // Test removing non-existent student
        assertFalse(studentManager.removeStudent("ST999999"));
    }

    @Test
    void testUpdateStudent() {
        Student student = new Student("ST123456", "John Doe", "Computer Science", 2023);
        studentManager.addStudent(student);
        
        assertTrue(studentManager.updateStudent("ST123456", "John Smith", "Mathematics", 2022));
        
        Student updated = studentManager.getStudent("ST123456");
        assertEquals("John Smith", updated.getName());
        assertEquals("Mathematics", updated.getMajor());
        assertEquals(2022, updated.getEnrollmentYear());
        
        // Test updating non-existent student
        assertFalse(studentManager.updateStudent("ST999999", "Test", null, null));
    }

    @Test
    void testStudentRankings() {
        Student student1 = new Student("ST001", "Alice", "CS", 2023);
        Student student2 = new Student("ST002", "Bob", "Math", 2023);
        Student student3 = new Student("ST003", "Charlie", "Physics", 2023);

        studentManager.addStudent(student1);
        studentManager.addStudent(student2);
        studentManager.addStudent(student3);

        // Add different grades to create rankings
        studentManager.addGradeToStudent("ST001", new Grade<>(95.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        studentManager.addGradeToStudent("ST002", new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        studentManager.addGradeToStudent("ST003", new Grade<>(75.0, 100.0, mathSubject, GradeType.EXAM, "Test"));

        List<Student> rankings = studentManager.getStudentRankings();
        assertEquals(3, rankings.size());
        
        // Should be sorted by GPA (highest first)
        assertEquals("Alice", rankings.get(0).getName());
        assertEquals("Bob", rankings.get(1).getName());
        assertEquals("Charlie", rankings.get(2).getName());
    }

    @Test
    void testTopStudents() {
        // Add multiple students
        for (int i = 1; i <= 5; i++) {
            Student student = new Student("ST00" + i, "Student" + i, "CS", 2023);
            studentManager.addStudent(student);
            
            // Give different grades
            double score = 70 + (i * 5); // Scores from 75 to 95
            studentManager.addGradeToStudent("ST00" + i, 
                new Grade<>(score, 100.0, mathSubject, GradeType.EXAM, "Test"));
        }

        List<Student> top3 = studentManager.getTopStudents(3);
        assertEquals(3, top3.size());
        assertEquals("Student5", top3.get(0).getName()); // Highest GPA
    }

    @Test
    void testStudentsNeedingSupport() {
        Student goodStudent = new Student("ST001", "Good Student", "CS", 2023);
        Student strugglingStudent = new Student("ST002", "Struggling Student", "Math", 2023);

        studentManager.addStudent(goodStudent);
        studentManager.addStudent(strugglingStudent);

        // Good student gets high grade
        studentManager.addGradeToStudent("ST001", 
            new Grade<>(90.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        
        // Struggling student gets low grade
        studentManager.addGradeToStudent("ST002", 
            new Grade<>(50.0, 100.0, mathSubject, GradeType.EXAM, "Test"));

        List<Student> needingSupport = studentManager.getStudentsNeedingSupport();
        assertEquals(1, needingSupport.size());
        assertEquals("Struggling Student", needingSupport.get(0).getName());
    }

    @Test
    void testSearchStudentsByName() {
        studentManager.addStudent(new Student("ST001", "John Doe", "CS", 2023));
        studentManager.addStudent(new Student("ST002", "Jane Doe", "Math", 2023));
        studentManager.addStudent(new Student("ST003", "Bob Smith", "Physics", 2023));

        List<Student> results = studentManager.searchStudentsByName("Doe");
        assertEquals(2, results.size());

        List<Student> johnResults = studentManager.searchStudentsByName("John");
        assertEquals(1, johnResults.size());
        assertEquals("John Doe", johnResults.get(0).getName());

        List<Student> noResults = studentManager.searchStudentsByName("XYZ");
        assertTrue(noResults.isEmpty());
    }

    @Test
    void testGetStudentsByMajor() {
        studentManager.addStudent(new Student("ST001", "Student1", "Computer Science", 2023));
        studentManager.addStudent(new Student("ST002", "Student2", "Computer Science", 2023));
        studentManager.addStudent(new Student("ST003", "Student3", "Mathematics", 2023));

        List<Student> csStudents = studentManager.getStudentsByMajor("Computer Science");
        assertEquals(2, csStudents.size());

        List<Student> mathStudents = studentManager.getStudentsByMajor("Mathematics");
        assertEquals(1, mathStudents.size());

        List<Student> noStudents = studentManager.getStudentsByMajor("Physics");
        assertTrue(noStudents.isEmpty());
    }

    @Test
    void testPerformanceStatistics() {
        // Add students with grades
        for (int i = 1; i <= 3; i++) {
            Student student = new Student("ST00" + i, "Student" + i, "CS", 2023);
            studentManager.addStudent(student);
            studentManager.addGradeToStudent("ST00" + i, 
                new Grade<>(80.0 + i * 5, 100.0, mathSubject, GradeType.EXAM, "Test"));
        }

        Map<String, Double> stats = studentManager.getPerformanceStatistics();
        assertFalse(stats.isEmpty());
        assertTrue(stats.containsKey("totalStudents"));
        assertTrue(stats.containsKey("averageGPA"));
        assertEquals(3.0, stats.get("totalStudents"));
        assertTrue(stats.get("averageGPA") > 0.0);
    }

    @Test
    void testAllSubjects() {
        Student student = new Student("ST001", "Student1", "CS", 2023);
        studentManager.addStudent(student);

        studentManager.addGradeToStudent("ST001", 
            new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        studentManager.addGradeToStudent("ST001", 
            new Grade<>(90.0, 100.0, physicsSubject, GradeType.QUIZ, "Quiz"));

        Set<Subject> subjects = studentManager.getAllSubjects();
        assertEquals(2, subjects.size());
        assertTrue(subjects.contains(mathSubject));
        assertTrue(subjects.contains(physicsSubject));
    }

    @Test
    void testAcademicAlerts() {
        Student strugglingStudent = new Student("ST001", "Struggling Student", "CS", 2023);
        studentManager.addStudent(strugglingStudent);
        
        // Add low grade to trigger alert
        studentManager.addGradeToStudent("ST001", 
            new Grade<>(40.0, 100.0, mathSubject, GradeType.EXAM, "Test"));

        Student alertStudent = studentManager.getNextStudentForAcademicAlert();
        assertNotNull(alertStudent);
        assertEquals("Struggling Student", alertStudent.getName());

        // Process alert
        Student processedStudent = studentManager.processNextAcademicAlert();
        assertNotNull(processedStudent);
        assertEquals("Struggling Student", processedStudent.getName());
    }
}