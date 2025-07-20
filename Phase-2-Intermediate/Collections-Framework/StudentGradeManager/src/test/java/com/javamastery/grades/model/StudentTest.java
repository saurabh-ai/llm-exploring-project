package com.javamastery.grades.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Student class demonstrating Collections Framework usage.
 */
public class StudentTest {

    private Student student;
    private Subject mathSubject;
    private Subject physicsSubject;

    @BeforeEach
    void setUp() {
        student = new Student("ST123456", "John Doe", "Computer Science", 2023);
        mathSubject = new Subject("MATH101", "Calculus I", 4);
        physicsSubject = new Subject("PHYS101", "Physics I", 4);
    }

    @Test
    void testStudentCreation() {
        assertEquals("ST123456", student.getStudentId());
        assertEquals("John Doe", student.getName());
        assertEquals("Computer Science", student.getMajor());
        assertEquals(2023, student.getEnrollmentYear());
        assertEquals(0.0, student.calculateGPA());
        assertEquals(0, student.getTotalGradeCount());
    }

    @Test
    void testAddGrade() {
        Grade<Double> grade = new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Midterm");
        student.addGrade(grade);

        assertEquals(1, student.getTotalGradeCount());
        assertTrue(student.getSubjects().contains(mathSubject));
        assertFalse(student.getGradesForSubject(mathSubject).isEmpty());
    }

    @Test
    void testGradeHistory() {
        Grade<Double> grade1 = new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Midterm");
        Grade<Double> grade2 = new Grade<>(92.0, 100.0, physicsSubject, GradeType.QUIZ, "Quiz 1");

        student.addGrade(grade1);
        student.addGrade(grade2);

        assertEquals(2, student.getGradeHistory().size());
        assertEquals(2, student.getSubjects().size());
    }

    @Test
    void testGPACalculation() {
        // Add grades in different subjects
        student.addGrade(new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        student.addGrade(new Grade<>(90.0, 100.0, mathSubject, GradeType.HOMEWORK, "HW"));
        student.addGrade(new Grade<>(80.0, 100.0, physicsSubject, GradeType.EXAM, "Test"));

        double gpa = student.calculateGPA();
        assertTrue(gpa > 0.0 && gpa <= 4.0);
    }

    @Test
    void testComparableImplementation() {
        Student student1 = new Student("ST001", "Alice", "CS", 2023);
        Student student2 = new Student("ST002", "Bob", "Math", 2023);

        // Add better grades to student1
        student1.addGrade(new Grade<>(95.0, 100.0, mathSubject, GradeType.EXAM, "Test"));
        student2.addGrade(new Grade<>(80.0, 100.0, mathSubject, GradeType.EXAM, "Test"));

        // Student1 should come before student2 (higher GPA first)
        assertTrue(student1.compareTo(student2) < 0);
    }

    @Test
    void testSubjectGradeOperations() {
        Grade<Double> grade1 = new Grade<>(85.0, 100.0, mathSubject, GradeType.EXAM, "Exam 1");
        Grade<Double> grade2 = new Grade<>(90.0, 100.0, mathSubject, GradeType.QUIZ, "Quiz 1");

        student.addGrade(grade1);
        student.addGrade(grade2);

        assertEquals(2, student.getGradesForSubject(mathSubject).size());
        assertEquals(2, student.getRankedGradesForSubject(mathSubject).size());
    }

    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Student("ST123456", "John Doe", "CS", 1800)); // Invalid year

        assertThrows(NullPointerException.class, () -> 
            new Student(null, "John", "CS", 2023)); // Null ID
    }
}