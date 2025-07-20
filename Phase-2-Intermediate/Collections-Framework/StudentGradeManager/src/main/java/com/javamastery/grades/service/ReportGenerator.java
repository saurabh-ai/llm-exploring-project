package com.javamastery.grades.service;

import com.javamastery.grades.model.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

/**
 * Service class for generating various reports from student and grade data.
 * Uses LinkedHashMap to preserve insertion order for reports.
 */
public class ReportGenerator {
    
    private final GradeCalculator gradeCalculator;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportGenerator(GradeCalculator gradeCalculator) {
        this.gradeCalculator = Objects.requireNonNull(gradeCalculator);
    }

    /**
     * Generates a comprehensive class performance report.
     */
    public LinkedHashMap<String, Object> generateClassPerformanceReport(List<Student> students, Set<Subject> subjects) {
        LinkedHashMap<String, Object> report = new LinkedHashMap<>();
        
        report.put("reportTitle", "Class Performance Report");
        report.put("generatedAt", new Date().toString());
        report.put("totalStudents", students.size());
        report.put("totalSubjects", subjects.size());
        
        // Overall statistics
        Map<String, Double> overallStats = gradeCalculator.calculateOverallStatistics(students);
        report.put("overallStatistics", overallStats);
        
        // Subject-wise statistics
        LinkedHashMap<String, Map<String, Double>> subjectStats = new LinkedHashMap<>();
        for (Subject subject : subjects) {
            Map<String, Double> stats = gradeCalculator.calculateSubjectStatistics(students, subject);
            if (!stats.isEmpty()) {
                subjectStats.put(subject.getName(), stats);
            }
        }
        report.put("subjectStatistics", subjectStats);
        
        // Performance distribution
        Map<String, List<Student>> performanceGroups = gradeCalculator.groupStudentsByPerformance(students);
        LinkedHashMap<String, Integer> performanceCounts = new LinkedHashMap<>();
        performanceGroups.forEach((level, studentList) -> performanceCounts.put(level, studentList.size()));
        report.put("performanceDistribution", performanceCounts);
        
        // Top performers
        List<Map<String, Object>> topPerformers = gradeCalculator.getTopOverallPerformers(students, 10)
                .stream()
                .map(this::createStudentSummary)
                .collect(Collectors.toList());
        report.put("topPerformers", topPerformers);
        
        return report;
    }

    /**
     * Generates a subject-specific report.
     */
    public LinkedHashMap<String, Object> generateSubjectReport(List<Student> students, Subject subject) {
        LinkedHashMap<String, Object> report = new LinkedHashMap<>();
        
        List<Student> studentsInSubject = students.stream()
                .filter(s -> !s.getGradesForSubject(subject).isEmpty())
                .collect(Collectors.toList());
        
        report.put("reportTitle", "Subject Report: " + subject.getName());
        report.put("subjectCode", subject.getCode());
        report.put("creditHours", subject.getCreditHours());
        report.put("totalStudentsEnrolled", studentsInSubject.size());
        
        // Subject statistics
        Map<String, Double> stats = gradeCalculator.calculateSubjectStatistics(students, subject);
        report.put("statistics", stats);
        
        // Grade distribution
        TreeMap<String, Integer> distribution = gradeCalculator.calculateGradeDistribution(students, subject);
        report.put("gradeDistribution", distribution);
        
        // Top performers in this subject
        List<Map<String, Object>> topPerformers = gradeCalculator.getTopPerformers(students, subject, 10)
                .stream()
                .map(student -> {
                    Map<String, Object> summary = createStudentSummary(student);
                    summary.put("subjectGPA", student.calculateSubjectGPA(subject));
                    return summary;
                })
                .collect(Collectors.toList());
        report.put("topPerformers", topPerformers);
        
        // Students needing attention
        List<Map<String, Object>> needingAttention = studentsInSubject.stream()
                .filter(s -> s.calculateSubjectGPA(subject) < 2.0)
                .sorted((s1, s2) -> Double.compare(s1.calculateSubjectGPA(subject), s2.calculateSubjectGPA(subject)))
                .map(student -> {
                    Map<String, Object> summary = createStudentSummary(student);
                    summary.put("subjectGPA", student.calculateSubjectGPA(subject));
                    return summary;
                })
                .collect(Collectors.toList());
        report.put("studentsNeedingAttention", needingAttention);
        
        return report;
    }

    /**
     * Generates an individual student report.
     */
    public LinkedHashMap<String, Object> generateStudentReport(Student student) {
        LinkedHashMap<String, Object> report = new LinkedHashMap<>();
        
        report.put("reportTitle", "Student Academic Report");
        report.put("studentId", student.getStudentId());
        report.put("name", student.getName());
        report.put("major", student.getMajor());
        report.put("enrollmentYear", student.getEnrollmentYear());
        report.put("overallGPA", student.calculateGPA());
        report.put("totalGrades", student.getTotalGradeCount());
        
        // Subject-wise performance
        LinkedHashMap<String, Object> subjectPerformance = new LinkedHashMap<>();
        for (Subject subject : student.getSubjects()) {
            Map<String, Object> subjectData = new LinkedHashMap<>();
            subjectData.put("gpa", student.calculateSubjectGPA(subject));
            subjectData.put("totalGrades", student.getGradesForSubject(subject).size());
            
            // Grade trends
            Map<String, Double> trends = gradeCalculator.calculateGradeTrends(student, subject);
            if (!trends.isEmpty()) {
                subjectData.put("trends", trends);
            }
            
            subjectPerformance.put(subject.getName(), subjectData);
        }
        report.put("subjectPerformance", subjectPerformance);
        
        // Recent grade history
        List<Map<String, Object>> recentGrades = student.getGradeHistory().stream()
                .sorted((g1, g2) -> g2.getDateRecorded().compareTo(g1.getDateRecorded()))
                .limit(20)
                .map(this::createGradeSummary)
                .collect(Collectors.toList());
        report.put("recentGrades", recentGrades);
        
        return report;
    }

    /**
     * Generates an academic alert report for students needing support.
     */
    public LinkedHashMap<String, Object> generateAcademicAlertReport(List<Student> allStudents) {
        LinkedHashMap<String, Object> report = new LinkedHashMap<>();
        
        List<Student> needingSupport = allStudents.stream()
                .filter(s -> s.calculateGPA() < 2.0 && s.getTotalGradeCount() > 0)
                .sorted((s1, s2) -> Double.compare(s1.calculateGPA(), s2.calculateGPA()))
                .collect(Collectors.toList());
        
        report.put("reportTitle", "Academic Alert Report");
        report.put("totalStudentsAtRisk", needingSupport.size());
        
        // Categorize by risk level
        Map<String, List<Student>> riskLevels = needingSupport.stream()
                .collect(Collectors.groupingBy(this::getRiskLevel, LinkedHashMap::new, Collectors.toList()));
        
        LinkedHashMap<String, Object> riskSummary = new LinkedHashMap<>();
        riskLevels.forEach((level, students) -> {
            Map<String, Object> levelData = new LinkedHashMap<>();
            levelData.put("count", students.size());
            levelData.put("students", students.stream().map(this::createStudentSummary).collect(Collectors.toList()));
            riskSummary.put(level, levelData);
        });
        
        report.put("riskLevels", riskSummary);
        
        // Recommended actions
        List<String> recommendations = Arrays.asList(
            "Schedule academic counseling sessions for critical risk students",
            "Provide tutoring support for high risk students",
            "Monitor progress weekly for moderate risk students",
            "Consider course load reduction recommendations",
            "Connect with academic success resources"
        );
        report.put("recommendedActions", recommendations);
        
        return report;
    }

    /**
     * Generates grade trend analysis report.
     */
    public LinkedHashMap<String, Object> generateTrendAnalysisReport(List<Student> students, Subject subject) {
        LinkedHashMap<String, Object> report = new LinkedHashMap<>();
        
        report.put("reportTitle", "Grade Trend Analysis: " + subject.getName());
        report.put("subjectCode", subject.getCode());
        
        List<Map<String, Object>> studentTrends = new ArrayList<>();
        
        for (Student student : students) {
            if (!student.getGradesForSubject(subject).isEmpty()) {
                Map<String, Double> trends = gradeCalculator.calculateGradeTrends(student, subject);
                if (!trends.isEmpty()) {
                    Map<String, Object> studentTrend = new LinkedHashMap<>();
                    studentTrend.put("studentId", student.getStudentId());
                    studentTrend.put("name", student.getName());
                    studentTrend.putAll(trends);
                    studentTrends.add(studentTrend);
                }
            }
        }
        
        // Sort by improvement (highest improvement first)
        studentTrends.sort((t1, t2) -> 
            Double.compare((Double) t2.get("improvement"), (Double) t1.get("improvement")));
        
        report.put("studentTrends", studentTrends);
        
        // Summary statistics
        double avgImprovement = studentTrends.stream()
                .mapToDouble(t -> (Double) t.get("improvement"))
                .average()
                .orElse(0.0);
        
        long improvingStudents = studentTrends.stream()
                .mapToDouble(t -> (Double) t.get("improvement"))
                .filter(improvement -> improvement > 0)
                .count();
        
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalStudentsAnalyzed", studentTrends.size());
        summary.put("averageImprovement", avgImprovement);
        summary.put("studentsImproving", improvingStudents);
        summary.put("studentsDeclined", studentTrends.size() - improvingStudents);
        
        report.put("summary", summary);
        
        return report;
    }

    // Helper methods

    private Map<String, Object> createStudentSummary(Student student) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("studentId", student.getStudentId());
        summary.put("name", student.getName());
        summary.put("major", student.getMajor());
        summary.put("gpa", student.calculateGPA());
        summary.put("totalGrades", student.getTotalGradeCount());
        return summary;
    }

    private Map<String, Object> createGradeSummary(Grade<?> grade) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("subject", grade.getSubject().getName());
        summary.put("type", grade.getGradeType().getDisplayName());
        summary.put("score", grade.getValue() + "/" + grade.getMaxValue());
        summary.put("percentage", grade.getPercentage());
        summary.put("letterGrade", grade.getLetterGrade());
        summary.put("date", grade.getDateRecorded().format(DATE_FORMATTER));
        if (!grade.getDescription().isEmpty()) {
            summary.put("description", grade.getDescription());
        }
        return summary;
    }

    private String getRiskLevel(Student student) {
        double gpa = student.calculateGPA();
        if (gpa < 1.0) return "Critical Risk";
        else if (gpa < 1.5) return "High Risk";
        else return "Moderate Risk";
    }

    /**
     * Formats a report as a readable string.
     */
    public String formatReportAsString(LinkedHashMap<String, Object> report) {
        StringBuilder sb = new StringBuilder();
        formatReportSection(report, sb, 0);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private void formatReportSection(Map<String, Object> section, StringBuilder sb, int indent) {
        String indentation = "  ".repeat(indent);
        
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            String key = formatKey(entry.getKey());
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                sb.append(indentation).append(key).append(":\n");
                formatReportSection((Map<String, Object>) value, sb, indent + 1);
            } else if (value instanceof List) {
                sb.append(indentation).append(key).append(":\n");
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof Map) {
                        formatReportSection((Map<String, Object>) item, sb, indent + 1);
                        sb.append("\n");
                    } else {
                        sb.append(indentation).append("  - ").append(item).append("\n");
                    }
                }
            } else {
                sb.append(indentation).append(key).append(": ").append(formatValue(value)).append("\n");
            }
        }
    }

    private String formatKey(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1 $2")
                  .toLowerCase()
                  .replace(" ", " ")
                  .substring(0, 1).toUpperCase() + key.substring(1);
    }

    private String formatValue(Object value) {
        if (value instanceof Double) {
            return String.format("%.2f", (Double) value);
        }
        return value.toString();
    }
}