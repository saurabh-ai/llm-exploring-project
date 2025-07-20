package com.javamastery.grades.util;

import com.javamastery.grades.model.*;
import java.util.Comparator;

/**
 * Utility class providing various Comparator implementations for Student sorting.
 * Demonstrates flexible sorting strategies and comparator chaining.
 */
public final class StudentComparators {

    private StudentComparators() {
        // Utility class - prevent instantiation
    }

    /**
     * Comparator for sorting by GPA (highest first).
     */
    public static final Comparator<Student> BY_GPA = 
        (s1, s2) -> Double.compare(s2.calculateGPA(), s1.calculateGPA());

    /**
     * Comparator for sorting by name (alphabetical).
     */
    public static final Comparator<Student> BY_NAME = 
        Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);

    /**
     * Comparator for sorting by student ID.
     */
    public static final Comparator<Student> BY_STUDENT_ID = 
        Comparator.comparing(Student::getStudentId);

    /**
     * Comparator for sorting by major.
     */
    public static final Comparator<Student> BY_MAJOR = 
        Comparator.comparing(Student::getMajor, String.CASE_INSENSITIVE_ORDER);

    /**
     * Comparator for sorting by enrollment year (newest first).
     */
    public static final Comparator<Student> BY_ENROLLMENT_YEAR = 
        (s1, s2) -> Integer.compare(s2.getEnrollmentYear(), s1.getEnrollmentYear());

    /**
     * Comparator for sorting by total number of grades (most grades first).
     */
    public static final Comparator<Student> BY_GRADE_COUNT = 
        (s1, s2) -> Integer.compare(s2.getTotalGradeCount(), s1.getTotalGradeCount());

    /**
     * Comparator for sorting by GPA for a specific subject.
     */
    public static Comparator<Student> bySubjectGPA(Subject subject) {
        return (s1, s2) -> Double.compare(s2.calculateSubjectGPA(subject), s1.calculateSubjectGPA(subject));
    }

    /**
     * Combined comparator: By GPA, then by name for tie-breaking.
     */
    public static final Comparator<Student> BY_GPA_THEN_NAME = 
        BY_GPA.thenComparing(BY_NAME);

    /**
     * Combined comparator: By major, then by GPA within each major.
     */
    public static final Comparator<Student> BY_MAJOR_THEN_GPA = 
        BY_MAJOR.thenComparing(BY_GPA);

    /**
     * Combined comparator: By enrollment year, then by GPA within each year.
     */
    public static final Comparator<Student> BY_YEAR_THEN_GPA = 
        BY_ENROLLMENT_YEAR.thenComparing(BY_GPA);

    /**
     * Complex chained comparator: By major, then year, then GPA, then name.
     */
    public static final Comparator<Student> COMPREHENSIVE_SORT = 
        BY_MAJOR
        .thenComparing(BY_ENROLLMENT_YEAR)
        .thenComparing(BY_GPA)
        .thenComparing(BY_NAME);

    /**
     * Comparator for academic standing (performance level).
     */
    public static final Comparator<Student> BY_ACADEMIC_STANDING = 
        Comparator.comparing(StudentComparators::getAcademicStanding)
                  .thenComparing(BY_GPA);

    /**
     * Custom comparator factory for students needing support (lowest GPA first).
     */
    public static Comparator<Student> supportPriorityComparator() {
        return (s1, s2) -> {
            // Students with lower GPA get higher priority (come first)
            int gpaComparison = Double.compare(s1.calculateGPA(), s2.calculateGPA());
            if (gpaComparison != 0) {
                return gpaComparison;
            }
            // If GPA is same, prioritize by grade count (more grades = more data)
            int gradeCountComparison = Integer.compare(s2.getTotalGradeCount(), s1.getTotalGradeCount());
            if (gradeCountComparison != 0) {
                return gradeCountComparison;
            }
            // Final tie-breaker: student ID
            return s1.getStudentId().compareTo(s2.getStudentId());
        };
    }

    /**
     * Creates a comparator for sorting by improvement in a specific subject.
     */
    public static Comparator<Student> bySubjectImprovement(Subject subject) {
        return (s1, s2) -> {
            double improvement1 = calculateImprovement(s1, subject);
            double improvement2 = calculateImprovement(s2, subject);
            return Double.compare(improvement2, improvement1); // Higher improvement first
        };
    }

    /**
     * Creates a comparator for sorting by recent performance (last 5 grades average).
     */
    public static Comparator<Student> byRecentPerformance(Subject subject) {
        return (s1, s2) -> {
            double recent1 = calculateRecentAverage(s1, subject);
            double recent2 = calculateRecentAverage(s2, subject);
            return Double.compare(recent2, recent1); // Higher recent average first
        };
    }

    // Helper methods for complex comparisons

    private static String getAcademicStanding(Student student) {
        double gpa = student.calculateGPA();
        if (gpa >= 3.5) return "A_Excellent";
        else if (gpa >= 3.0) return "B_Good";
        else if (gpa >= 2.5) return "C_Satisfactory";
        else if (gpa >= 2.0) return "D_NeedsImprovement";
        else return "E_Critical";
    }

    private static double calculateImprovement(Student student, Subject subject) {
        var grades = student.getGradesForSubject(subject);
        if (grades.size() < 2) return 0.0;
        
        // Sort by date
        var sortedGrades = grades.stream()
                .sorted(Comparator.comparing(grade -> grade.getDateRecorded()))
                .toArray(Grade[]::new);
        
        return sortedGrades[sortedGrades.length - 1].getPercentage() - 
               sortedGrades[0].getPercentage();
    }

    private static double calculateRecentAverage(Student student, Subject subject) {
        var grades = student.getGradesForSubject(subject);
        if (grades.isEmpty()) return 0.0;
        
        return grades.stream()
                .sorted(Comparator.comparing((Grade<?> grade) -> grade.getDateRecorded()).reversed())
                .limit(5)
                .mapToDouble(Grade::getPercentage)
                .average()
                .orElse(0.0);
    }
}