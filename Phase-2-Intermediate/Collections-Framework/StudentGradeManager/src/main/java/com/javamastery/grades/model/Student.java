package com.javamastery.grades.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Student class implementing Comparable for natural ordering by GPA.
 * Uses various collections to demonstrate Collections Framework mastery.
 */
public class Student implements Comparable<Student> {
    private final String studentId;
    private String name;
    private String major;
    private int enrollmentYear;
    
    // LinkedList for maintaining chronological grade history
    private final LinkedList<Grade<?>> gradeHistory;
    
    // HashMap for fast subject-based grade lookups
    private final Map<Subject, List<Grade<?>>> gradesBySubject;
    
    // TreeSet for maintaining sorted grade rankings within each subject
    private final Map<Subject, TreeSet<Grade<?>>> rankedGradesBySubject;

    public Student(String studentId, String name, String major, int enrollmentYear) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.major = Objects.requireNonNull(major, "Major cannot be null");
        this.enrollmentYear = enrollmentYear;
        
        this.gradeHistory = new LinkedList<>();
        this.gradesBySubject = new ConcurrentHashMap<>();
        this.rankedGradesBySubject = new ConcurrentHashMap<>();
        
        validateEnrollmentYear();
    }

    private void validateEnrollmentYear() {
        int currentYear = java.time.Year.now().getValue();
        if (enrollmentYear < 1900 || enrollmentYear > currentYear + 1) {
            throw new IllegalArgumentException("Invalid enrollment year: " + enrollmentYear);
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = Objects.requireNonNull(major, "Major cannot be null");
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
        validateEnrollmentYear();
    }

    /**
     * Adds a grade to the student's record.
     * Updates multiple collections for different access patterns.
     */
    public void addGrade(Grade<?> grade) {
        Objects.requireNonNull(grade, "Grade cannot be null");
        
        // Add to chronological history
        gradeHistory.add(grade);
        
        // Add to subject-based grouping
        gradesBySubject.computeIfAbsent(grade.getSubject(), k -> new ArrayList<>()).add(grade);
        
        // Add to ranked subject-based grouping
        rankedGradesBySubject.computeIfAbsent(grade.getSubject(), k -> new TreeSet<>()).add(grade);
    }

    /**
     * Gets all grades in chronological order.
     */
    public List<Grade<?>> getGradeHistory() {
        return new ArrayList<>(gradeHistory);
    }

    /**
     * Gets grades for a specific subject.
     */
    public List<Grade<?>> getGradesForSubject(Subject subject) {
        return new ArrayList<>(gradesBySubject.getOrDefault(subject, Collections.emptyList()));
    }

    /**
     * Gets sorted grades for a specific subject (best grades first).
     */
    public Set<Grade<?>> getRankedGradesForSubject(Subject subject) {
        TreeSet<Grade<?>> emptySet = new TreeSet<>();
        return new TreeSet<>(rankedGradesBySubject.getOrDefault(subject, emptySet));
    }

    /**
     * Gets all subjects this student has grades in.
     */
    public Set<Subject> getSubjects() {
        return new LinkedHashSet<>(gradesBySubject.keySet());
    }

    /**
     * Calculates overall GPA across all subjects.
     */
    public double calculateGPA() {
        if (gradeHistory.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        double totalCredits = 0.0;

        for (Subject subject : getSubjects()) {
            double subjectGPA = calculateSubjectGPA(subject);
            int credits = subject.getCreditHours();
            totalPoints += subjectGPA * credits;
            totalCredits += credits;
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    /**
     * Calculates GPA for a specific subject with weighted grade types.
     */
    public double calculateSubjectGPA(Subject subject) {
        List<Grade<?>> subjectGrades = gradesBySubject.get(subject);
        if (subjectGrades == null || subjectGrades.isEmpty()) {
            return 0.0;
        }

        Map<GradeType, List<Grade<?>>> gradesByType = new HashMap<>();
        for (Grade<?> grade : subjectGrades) {
            gradesByType.computeIfAbsent(grade.getGradeType(), k -> new ArrayList<>()).add(grade);
        }

        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (Map.Entry<GradeType, List<Grade<?>>> entry : gradesByType.entrySet()) {
            GradeType type = entry.getKey();
            List<Grade<?>> grades = entry.getValue();
            
            double avgPercentage = grades.stream()
                                        .mapToDouble(Grade::getPercentage)
                                        .average()
                                        .orElse(0.0);
            
            double weight = type.getDefaultWeight();
            totalWeightedScore += avgPercentage * weight;
            totalWeight += weight;
        }

        double percentage = totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
        return convertPercentageToGPA(percentage);
    }

    private double convertPercentageToGPA(double percentage) {
        if (percentage >= 97) return 4.0;
        else if (percentage >= 93) return 3.7;
        else if (percentage >= 90) return 3.3;
        else if (percentage >= 87) return 3.0;
        else if (percentage >= 83) return 2.7;
        else if (percentage >= 80) return 2.3;
        else if (percentage >= 77) return 2.0;
        else if (percentage >= 73) return 1.7;
        else if (percentage >= 70) return 1.3;
        else if (percentage >= 67) return 1.0;
        else if (percentage >= 65) return 0.7;
        else return 0.0;
    }

    /**
     * Gets the total number of grades for this student.
     */
    public int getTotalGradeCount() {
        return gradeHistory.size();
    }

    /**
     * Natural ordering by GPA (descending - higher GPAs first).
     */
    @Override
    public int compareTo(Student other) {
        int gpaComparison = Double.compare(other.calculateGPA(), this.calculateGPA());
        if (gpaComparison != 0) {
            return gpaComparison;
        }
        // If GPAs are equal, sort by student ID for consistent ordering
        return this.studentId.compareTo(other.studentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', major='%s', year=%d, GPA=%.2f, grades=%d}", 
                           studentId, name, major, enrollmentYear, calculateGPA(), getTotalGradeCount());
    }
}