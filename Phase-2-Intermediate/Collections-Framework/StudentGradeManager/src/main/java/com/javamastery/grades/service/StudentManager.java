package com.javamastery.grades.service;

import com.javamastery.grades.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing students using various Collections Framework data structures.
 * Demonstrates usage of HashMap, TreeSet, LinkedHashSet, and other collections.
 */
public class StudentManager {
    
    // HashMap for O(1) student lookups by ID
    private final Map<String, Student> studentById;
    
    // TreeSet for maintaining sorted student rankings by GPA
    private final TreeSet<Student> studentRankings;
    
    // LinkedHashSet for preserving unique subject order across all students
    private final LinkedHashSet<Subject> allSubjects;
    
    // PriorityQueue for academic alerts (students needing support)
    private final PriorityQueue<Student> academicAlerts;

    public StudentManager() {
        this.studentById = new ConcurrentHashMap<>();
        this.studentRankings = new TreeSet<>();
        this.allSubjects = new LinkedHashSet<>();
        
        // Priority queue for students with low GPAs (lowest GPA first)
        this.academicAlerts = new PriorityQueue<>((s1, s2) -> 
            Double.compare(s1.calculateGPA(), s2.calculateGPA()));
    }

    /**
     * Adds a new student to the system.
     */
    public boolean addStudent(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        
        if (studentById.containsKey(student.getStudentId())) {
            return false; // Student already exists
        }
        
        studentById.put(student.getStudentId(), student);
        studentRankings.add(student);
        
        // Update academic alerts if GPA is below threshold
        updateAcademicAlerts(student);
        
        return true;
    }

    /**
     * Updates student information.
     */
    public boolean updateStudent(String studentId, String name, String major, Integer enrollmentYear) {
        Student student = studentById.get(studentId);
        if (student == null) {
            return false;
        }
        
        // Remove from rankings before update
        studentRankings.remove(student);
        academicAlerts.remove(student);
        
        // Update fields
        if (name != null) student.setName(name);
        if (major != null) student.setMajor(major);
        if (enrollmentYear != null) student.setEnrollmentYear(enrollmentYear);
        
        // Re-add to rankings after update
        studentRankings.add(student);
        updateAcademicAlerts(student);
        
        return true;
    }

    /**
     * Removes a student from the system.
     */
    public boolean removeStudent(String studentId) {
        Student student = studentById.remove(studentId);
        if (student == null) {
            return false;
        }
        
        studentRankings.remove(student);
        academicAlerts.remove(student);
        return true;
    }

    /**
     * Gets a student by ID with O(1) lookup.
     */
    public Student getStudent(String studentId) {
        return studentById.get(studentId);
    }

    /**
     * Gets all students as an unmodifiable collection.
     */
    public Collection<Student> getAllStudents() {
        return Collections.unmodifiableCollection(studentById.values());
    }

    /**
     * Gets students ranked by GPA (highest first).
     */
    public List<Student> getStudentRankings() {
        return new ArrayList<>(studentRankings);
    }

    /**
     * Gets top N students by GPA.
     */
    public List<Student> getTopStudents(int n) {
        return studentRankings.stream()
                             .limit(n)
                             .collect(Collectors.toList());
    }

    /**
     * Gets students needing academic support (GPA below 2.0).
     */
    public List<Student> getStudentsNeedingSupport() {
        return studentById.values().stream()
                         .filter(s -> s.calculateGPA() < 2.0)
                         .sorted((s1, s2) -> Double.compare(s1.calculateGPA(), s2.calculateGPA()))
                         .collect(Collectors.toList());
    }

    /**
     * Searches students by name (case-insensitive partial match).
     */
    public List<Student> searchStudentsByName(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String query = nameQuery.toLowerCase().trim();
        return studentById.values().stream()
                         .filter(s -> s.getName().toLowerCase().contains(query))
                         .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                         .collect(Collectors.toList());
    }

    /**
     * Gets students by major.
     */
    public List<Student> getStudentsByMajor(String major) {
        if (major == null) {
            return new ArrayList<>();
        }
        
        return studentById.values().stream()
                         .filter(s -> major.equalsIgnoreCase(s.getMajor()))
                         .sorted()
                         .collect(Collectors.toList());
    }

    /**
     * Gets students by enrollment year.
     */
    public List<Student> getStudentsByEnrollmentYear(int year) {
        return studentById.values().stream()
                         .filter(s -> s.getEnrollmentYear() == year)
                         .sorted()
                         .collect(Collectors.toList());
    }

    /**
     * Adds a grade to a student and updates internal collections.
     */
    public boolean addGradeToStudent(String studentId, Grade<?> grade) {
        Student student = studentById.get(studentId);
        if (student == null) {
            return false;
        }
        
        // Remove from rankings before adding grade
        studentRankings.remove(student);
        academicAlerts.remove(student);
        
        // Add grade to student
        student.addGrade(grade);
        
        // Update subject tracking
        allSubjects.add(grade.getSubject());
        
        // Re-add to rankings after grade update
        studentRankings.add(student);
        updateAcademicAlerts(student);
        
        return true;
    }

    /**
     * Gets all unique subjects across all students.
     */
    public Set<Subject> getAllSubjects() {
        return new LinkedHashSet<>(allSubjects);
    }

    /**
     * Gets students taking a specific subject.
     */
    public List<Student> getStudentsInSubject(Subject subject) {
        return studentById.values().stream()
                         .filter(s -> s.getSubjects().contains(subject))
                         .sorted()
                         .collect(Collectors.toList());
    }

    /**
     * Gets academic performance statistics.
     */
    public Map<String, Double> getPerformanceStatistics() {
        if (studentById.isEmpty()) {
            return Collections.emptyMap();
        }
        
        List<Double> gpas = studentById.values().stream()
                                      .mapToDouble(Student::calculateGPA)
                                      .sorted()
                                      .boxed()
                                      .collect(Collectors.toList());
        
        Map<String, Double> stats = new HashMap<>();
        stats.put("totalStudents", (double) studentById.size());
        stats.put("averageGPA", gpas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        stats.put("medianGPA", calculateMedian(gpas));
        stats.put("minGPA", gpas.get(0));
        stats.put("maxGPA", gpas.get(gpas.size() - 1));
        stats.put("studentsNeedingSupport", (double) getStudentsNeedingSupport().size());
        
        return stats;
    }

    private double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        if (size == 0) return 0.0;
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2);
        }
    }

    private void updateAcademicAlerts(Student student) {
        if (student.calculateGPA() < 2.0 && student.getTotalGradeCount() > 0) {
            academicAlerts.offer(student);
        }
    }

    /**
     * Gets the next student requiring academic attention.
     */
    public Student getNextStudentForAcademicAlert() {
        return academicAlerts.peek();
    }

    /**
     * Processes the next student requiring academic attention.
     */
    public Student processNextAcademicAlert() {
        return academicAlerts.poll();
    }

    /**
     * Gets the total number of students in the system.
     */
    public int getTotalStudentCount() {
        return studentById.size();
    }
}