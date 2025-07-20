package com.javamastery.grades.service;

import com.javamastery.grades.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for performing statistical calculations on grades.
 * Demonstrates advanced collection operations and stream processing.
 */
public class GradeCalculator {

    /**
     * Calculates class statistics for a specific subject.
     */
    public Map<String, Double> calculateSubjectStatistics(List<Student> students, Subject subject) {
        List<Double> subjectGPAs = students.stream()
                .filter(s -> !s.getGradesForSubject(subject).isEmpty())
                .mapToDouble(s -> s.calculateSubjectGPA(subject))
                .boxed()
                .sorted()
                .collect(Collectors.toList());

        if (subjectGPAs.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> stats = new HashMap<>();
        stats.put("count", (double) subjectGPAs.size());
        stats.put("average", calculateAverage(subjectGPAs));
        stats.put("median", calculateMedian(subjectGPAs));
        stats.put("min", subjectGPAs.get(0));
        stats.put("max", subjectGPAs.get(subjectGPAs.size() - 1));
        stats.put("standardDeviation", calculateStandardDeviation(subjectGPAs));
        
        return stats;
    }

    /**
     * Calculates overall class statistics across all subjects.
     */
    public Map<String, Double> calculateOverallStatistics(List<Student> students) {
        List<Double> overallGPAs = students.stream()
                .filter(s -> s.getTotalGradeCount() > 0)
                .mapToDouble(Student::calculateGPA)
                .boxed()
                .sorted()
                .collect(Collectors.toList());

        if (overallGPAs.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> stats = new HashMap<>();
        stats.put("count", (double) overallGPAs.size());
        stats.put("average", calculateAverage(overallGPAs));
        stats.put("median", calculateMedian(overallGPAs));
        stats.put("min", overallGPAs.get(0));
        stats.put("max", overallGPAs.get(overallGPAs.size() - 1));
        stats.put("standardDeviation", calculateStandardDeviation(overallGPAs));
        
        // Calculate percentiles
        stats.put("percentile25", calculatePercentile(overallGPAs, 25));
        stats.put("percentile75", calculatePercentile(overallGPAs, 75));
        stats.put("percentile90", calculatePercentile(overallGPAs, 90));
        
        return stats;
    }

    /**
     * Generates grade distribution for a subject.
     * Returns a TreeMap to maintain sorted order by grade range.
     */
    public TreeMap<String, Integer> calculateGradeDistribution(List<Student> students, Subject subject) {
        TreeMap<String, Integer> distribution = new TreeMap<>();
        
        // Initialize grade ranges
        distribution.put("A (90-100)", 0);
        distribution.put("B (80-89)", 0);
        distribution.put("C (70-79)", 0);
        distribution.put("D (60-69)", 0);
        distribution.put("F (0-59)", 0);

        for (Student student : students) {
            List<Grade<?>> subjectGrades = student.getGradesForSubject(subject);
            if (!subjectGrades.isEmpty()) {
                double subjectGPA = student.calculateSubjectGPA(subject);
                double percentage = convertGPAToPercentage(subjectGPA);
                
                if (percentage >= 90) {
                    distribution.merge("A (90-100)", 1, Integer::sum);
                } else if (percentage >= 80) {
                    distribution.merge("B (80-89)", 1, Integer::sum);
                } else if (percentage >= 70) {
                    distribution.merge("C (70-79)", 1, Integer::sum);
                } else if (percentage >= 60) {
                    distribution.merge("D (60-69)", 1, Integer::sum);
                } else {
                    distribution.merge("F (0-59)", 1, Integer::sum);
                }
            }
        }
        
        return distribution;
    }

    /**
     * Finds top performers in a subject.
     */
    public List<Student> getTopPerformers(List<Student> students, Subject subject, int limit) {
        return students.stream()
                .filter(s -> !s.getGradesForSubject(subject).isEmpty())
                .sorted((s1, s2) -> Double.compare(s2.calculateSubjectGPA(subject), s1.calculateSubjectGPA(subject)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Finds top overall performers across all subjects.
     */
    public List<Student> getTopOverallPerformers(List<Student> students, int limit) {
        return students.stream()
                .filter(s -> s.getTotalGradeCount() > 0)
                .sorted()  // Uses Student's natural ordering (by GPA descending)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Calculates grade trends for a student in a specific subject.
     */
    public Map<String, Double> calculateGradeTrends(Student student, Subject subject) {
        List<Grade<?>> grades = student.getGradesForSubject(subject)
                .stream()
                .sorted(Comparator.comparing(Grade::getDateRecorded))
                .collect(Collectors.toList());

        if (grades.size() < 2) {
            return Collections.emptyMap();
        }

        Map<String, Double> trends = new HashMap<>();
        trends.put("totalGrades", (double) grades.size());
        trends.put("firstGradePercentage", grades.get(0).getPercentage());
        trends.put("lastGradePercentage", grades.get(grades.size() - 1).getPercentage());
        trends.put("improvement", grades.get(grades.size() - 1).getPercentage() - grades.get(0).getPercentage());
        
        // Calculate rolling average for recent grades (last 5)
        List<Grade<?>> recentGrades = grades.stream()
                .skip(Math.max(0, grades.size() - 5))
                .collect(Collectors.toList());
        
        double recentAverage = recentGrades.stream()
                .mapToDouble(Grade::getPercentage)
                .average()
                .orElse(0.0);
        
        trends.put("recentAverage", recentAverage);
        
        return trends;
    }

    /**
     * Groups students by performance level.
     */
    public Map<String, List<Student>> groupStudentsByPerformance(List<Student> students) {
        return students.stream()
                .filter(s -> s.getTotalGradeCount() > 0)
                .collect(Collectors.groupingBy(this::getPerformanceLevel));
    }

    private String getPerformanceLevel(Student student) {
        double gpa = student.calculateGPA();
        if (gpa >= 3.5) return "Excellent";
        else if (gpa >= 3.0) return "Good";
        else if (gpa >= 2.5) return "Satisfactory";
        else if (gpa >= 2.0) return "Needs Improvement";
        else return "Critical";
    }

    /**
     * Calculates weighted average for different grade types in a subject.
     */
    public double calculateWeightedAverage(List<Grade<?>> grades) {
        if (grades.isEmpty()) {
            return 0.0;
        }

        Map<GradeType, List<Grade<?>>> gradesByType = grades.stream()
                .collect(Collectors.groupingBy(Grade::getGradeType));

        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (Map.Entry<GradeType, List<Grade<?>>> entry : gradesByType.entrySet()) {
            GradeType type = entry.getKey();
            List<Grade<?>> typeGrades = entry.getValue();
            
            double avgPercentage = typeGrades.stream()
                    .mapToDouble(Grade::getPercentage)
                    .average()
                    .orElse(0.0);
            
            double weight = type.getDefaultWeight();
            totalWeightedScore += avgPercentage * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }

    // Utility methods for statistical calculations
    
    private double calculateAverage(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
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

    private double calculateStandardDeviation(List<Double> values) {
        double mean = calculateAverage(values);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    private double calculatePercentile(List<Double> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) return 0.0;
        
        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        
        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }
        
        double weight = index - lowerIndex;
        return sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
    }

    private double convertGPAToPercentage(double gpa) {
        // Approximate conversion from GPA to percentage
        if (gpa >= 3.7) return 95.0;
        else if (gpa >= 3.3) return 87.0;
        else if (gpa >= 3.0) return 83.0;
        else if (gpa >= 2.7) return 80.0;
        else if (gpa >= 2.3) return 77.0;
        else if (gpa >= 2.0) return 73.0;
        else if (gpa >= 1.7) return 70.0;
        else if (gpa >= 1.3) return 67.0;
        else if (gpa >= 1.0) return 65.0;
        else return 60.0;
    }
}