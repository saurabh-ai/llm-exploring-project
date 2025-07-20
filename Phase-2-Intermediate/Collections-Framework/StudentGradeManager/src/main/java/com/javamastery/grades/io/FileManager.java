package com.javamastery.grades.io;

import com.javamastery.grades.model.*;
import com.javamastery.grades.service.*;
import com.javamastery.grades.util.DataValidator;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File management utility for handling student and grade data persistence.
 * Supports backup, restore, and bulk operations.
 */
public class FileManager {
    
    private final String dataDirectory;
    private final CsvProcessor csvProcessor;

    public FileManager(String dataDirectory) {
        this.dataDirectory = Objects.requireNonNull(dataDirectory);
        this.csvProcessor = new CsvProcessor();
        ensureDirectoryExists();
    }

    /**
     * Exports all students to a CSV file.
     */
    public boolean exportStudentsToCSV(Collection<Student> students, String filename) {
        try {
            Path filePath = getFilePath(filename);
            
            List<Map<String, String>> studentData = students.stream()
                    .map(this::convertStudentToMap)
                    .collect(Collectors.toList());
            
            csvProcessor.writeStudentsToCSV(studentData, filePath.toString());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting students to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Imports students from a CSV file.
     */
    public List<Student> importStudentsFromCSV(String filename) {
        try {
            Path filePath = getFilePath(filename);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found: " + filename);
            }
            
            List<Map<String, String>> studentData = csvProcessor.readStudentsFromCSV(filePath.toString());
            
            // Validate data before creating students
            Map<String, List<String>> validationErrors = DataValidator.validateForBulkImport(studentData);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors found during import:");
                validationErrors.forEach((row, errors) -> {
                    System.err.println(row + ": " + String.join(", ", errors));
                });
            }
            
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < studentData.size(); i++) {
                String rowKey = "Row " + (i + 1);
                if (!validationErrors.containsKey(rowKey)) {
                    students.add(convertMapToStudent(studentData.get(i)));
                }
            }
            
            return students;
            
        } catch (Exception e) {
            System.err.println("Error importing students from CSV: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Exports grades for all students to a CSV file.
     */
    public boolean exportGradesToCSV(Collection<Student> students, String filename) {
        try {
            Path filePath = getFilePath(filename);
            
            List<Map<String, String>> gradeData = new ArrayList<>();
            for (Student student : students) {
                for (Grade<?> grade : student.getGradeHistory()) {
                    gradeData.add(convertGradeToMap(student, grade));
                }
            }
            
            csvProcessor.writeGradesToCSV(gradeData, filePath.toString());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting grades to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Imports grades from a CSV file and adds them to the appropriate students.
     */
    public boolean importGradesFromCSV(String filename, StudentManager studentManager, Map<String, Subject> subjectMap) {
        try {
            Path filePath = getFilePath(filename);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("File not found: " + filename);
            }
            
            List<Map<String, String>> gradeData = csvProcessor.readGradesFromCSV(filePath.toString());
            
            int successCount = 0;
            int errorCount = 0;
            
            for (Map<String, String> gradeMap : gradeData) {
                try {
                    String studentId = gradeMap.get("studentId");
                    Student student = studentManager.getStudent(studentId);
                    
                    if (student == null) {
                        System.err.println("Student not found: " + studentId);
                        errorCount++;
                        continue;
                    }
                    
                    Grade<Double> grade = convertMapToGrade(gradeMap, subjectMap);
                    if (studentManager.addGradeToStudent(studentId, grade)) {
                        successCount++;
                    } else {
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error processing grade record: " + e.getMessage());
                    errorCount++;
                }
            }
            
            System.out.println("Grade import completed: " + successCount + " successful, " + errorCount + " errors");
            return errorCount == 0;
            
        } catch (Exception e) {
            System.err.println("Error importing grades from CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a backup of all data.
     */
    public boolean createBackup(StudentManager studentManager) {
        try {
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            String backupDir = "backup_" + timestamp;
            Path backupPath = Paths.get(dataDirectory, backupDir);
            Files.createDirectories(backupPath);
            
            // Export students
            String studentsFile = Paths.get(backupPath.toString(), "students.csv").toString();
            if (!exportStudentsToCSV(studentManager.getAllStudents(), studentsFile)) {
                return false;
            }
            
            // Export grades
            String gradesFile = Paths.get(backupPath.toString(), "grades.csv").toString();
            if (!exportGradesToCSV(studentManager.getAllStudents(), gradesFile)) {
                return false;
            }
            
            // Create backup info file
            createBackupInfo(backupPath, studentManager);
            
            System.out.println("Backup created successfully: " + backupPath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restores data from a backup directory.
     */
    public boolean restoreFromBackup(String backupDirName, StudentManager studentManager, Map<String, Subject> subjectMap) {
        try {
            Path backupPath = Paths.get(dataDirectory, backupDirName);
            if (!Files.exists(backupPath) || !Files.isDirectory(backupPath)) {
                throw new FileNotFoundException("Backup directory not found: " + backupDirName);
            }
            
            // Import students
            Path studentsFile = backupPath.resolve("students.csv");
            if (Files.exists(studentsFile)) {
                List<Student> students = importStudentsFromCSV(studentsFile.toString());
                for (Student student : students) {
                    studentManager.addStudent(student);
                }
            }
            
            // Import grades
            Path gradesFile = backupPath.resolve("grades.csv");
            if (Files.exists(gradesFile)) {
                importGradesFromCSV(gradesFile.toString(), studentManager, subjectMap);
            }
            
            System.out.println("Data restored successfully from: " + backupPath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error restoring from backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lists available backup directories.
     */
    public List<String> listBackups() {
        try {
            return Files.list(Paths.get(dataDirectory))
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("backup_"))
                    .map(path -> path.getFileName().toString())
                    .sorted(Comparator.reverseOrder()) // Most recent first
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            System.err.println("Error listing backups: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Exports a specific report to a text file.
     */
    public boolean exportReportToFile(LinkedHashMap<String, Object> report, String filename) {
        try {
            Path filePath = getFilePath(filename);
            
            StringBuilder content = new StringBuilder();
            content.append("=".repeat(80)).append("\n");
            content.append(report.get("reportTitle")).append("\n");
            content.append("Generated: ").append(new Date()).append("\n");
            content.append("=".repeat(80)).append("\n\n");
            
            formatReportContent(report, content, 0);
            
            Files.write(filePath, content.toString().getBytes());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads sample data files and returns subjects.
     */
    public Map<String, Subject> loadSampleSubjects() {
        Map<String, Subject> subjects = new LinkedHashMap<>();
        
        // Default subjects
        subjects.put("MATH101", new Subject("MATH101", "Calculus I", 4));
        subjects.put("PHYS101", new Subject("PHYS101", "Physics I", 4));
        subjects.put("CHEM101", new Subject("CHEM101", "General Chemistry", 3));
        subjects.put("ENGL101", new Subject("ENGL101", "English Composition", 3));
        subjects.put("HIST101", new Subject("HIST101", "World History", 3));
        subjects.put("CSCI101", new Subject("CSCI101", "Introduction to Programming", 4));
        subjects.put("ECON101", new Subject("ECON101", "Microeconomics", 3));
        subjects.put("PSYC101", new Subject("PSYC101", "Introduction to Psychology", 3));
        
        return subjects;
    }

    // Private helper methods

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (Exception e) {
            throw new RuntimeException("Cannot create data directory: " + dataDirectory, e);
        }
    }

    private Path getFilePath(String filename) {
        return Paths.get(dataDirectory, filename);
    }

    private Map<String, String> convertStudentToMap(Student student) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("studentId", student.getStudentId());
        map.put("name", student.getName());
        map.put("major", student.getMajor());
        map.put("enrollmentYear", String.valueOf(student.getEnrollmentYear()));
        map.put("currentGPA", String.format("%.2f", student.calculateGPA()));
        map.put("totalGrades", String.valueOf(student.getTotalGradeCount()));
        return map;
    }

    private Student convertMapToStudent(Map<String, String> map) {
        return new Student(
                map.get("studentId"),
                map.get("name"),
                map.get("major"),
                Integer.parseInt(map.get("enrollmentYear"))
        );
    }

    private Map<String, String> convertGradeToMap(Student student, Grade<?> grade) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("studentId", student.getStudentId());
        map.put("subjectCode", grade.getSubject().getCode());
        map.put("gradeType", grade.getGradeType().name());
        map.put("value", grade.getValue().toString());
        map.put("maxValue", grade.getMaxValue().toString());
        map.put("percentage", String.format("%.2f", grade.getPercentage()));
        map.put("letterGrade", String.valueOf(grade.getLetterGrade()));
        map.put("dateRecorded", grade.getDateRecorded().toString());
        map.put("description", grade.getDescription());
        return map;
    }

    private Grade<Double> convertMapToGrade(Map<String, String> map, Map<String, Subject> subjectMap) {
        String subjectCode = map.get("subjectCode");
        Subject subject = subjectMap.get(subjectCode);
        if (subject == null) {
            throw new IllegalArgumentException("Unknown subject code: " + subjectCode);
        }

        Double value = Double.parseDouble(map.get("value"));
        Double maxValue = Double.parseDouble(map.get("maxValue"));
        GradeType gradeType = GradeType.valueOf(map.get("gradeType"));
        String description = map.getOrDefault("description", "");

        return new Grade<>(value, maxValue, subject, gradeType, description);
    }

    private void createBackupInfo(Path backupPath, StudentManager studentManager) throws IOException {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("backupDate", new Date().toString());
        info.put("totalStudents", studentManager.getTotalStudentCount());
        info.put("performanceStats", studentManager.getPerformanceStatistics());
        
        Path infoFile = backupPath.resolve("backup_info.txt");
        StringBuilder content = new StringBuilder();
        formatReportContent(info, content, 0);
        Files.write(infoFile, content.toString().getBytes());
    }

    @SuppressWarnings("unchecked")
    private void formatReportContent(Map<String, Object> data, StringBuilder sb, int indent) {
        String indentation = "  ".repeat(indent);
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = formatKey(entry.getKey());
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                sb.append(indentation).append(key).append(":\n");
                formatReportContent((Map<String, Object>) value, sb, indent + 1);
            } else if (value instanceof List) {
                sb.append(indentation).append(key).append(":\n");
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof Map) {
                        formatReportContent((Map<String, Object>) item, sb, indent + 1);
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
        return key.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
    }

    private String formatValue(Object value) {
        if (value instanceof Double) {
            return String.format("%.2f", (Double) value);
        }
        return value.toString();
    }
}