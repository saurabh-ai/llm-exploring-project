package com.javamastery.grades.io;

import com.opencsv.*;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSV processing utility using OpenCSV library for file operations.
 * Handles reading and writing of student and grade data in CSV format.
 */
public class CsvProcessor {

    private static final String[] STUDENT_HEADERS = {
        "studentId", "name", "major", "enrollmentYear", "currentGPA", "totalGrades"
    };

    private static final String[] GRADE_HEADERS = {
        "studentId", "subjectCode", "gradeType", "value", "maxValue", 
        "percentage", "letterGrade", "dateRecorded", "description"
    };

    /**
     * Writes student data to a CSV file.
     */
    public void writeStudentsToCSV(List<Map<String, String>> students, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write header
            csvWriter.writeNext(STUDENT_HEADERS);

            // Write data rows
            for (Map<String, String> student : students) {
                String[] row = new String[STUDENT_HEADERS.length];
                for (int i = 0; i < STUDENT_HEADERS.length; i++) {
                    row[i] = student.getOrDefault(STUDENT_HEADERS[i], "");
                }
                csvWriter.writeNext(row);
            }
        }
    }

    /**
     * Reads student data from a CSV file.
     */
    public List<Map<String, String>> readStudentsFromCSV(String filename) throws IOException, CsvException {
        List<Map<String, String>> students = new ArrayList<>();

        try (FileReader reader = new FileReader(filename);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                return students;
            }

            // Get headers from first row
            String[] headers = records.get(0);
            
            // Validate required headers
            Set<String> headerSet = Set.of(headers);
            List<String> requiredHeaders = List.of("studentId", "name", "major", "enrollmentYear");
            for (String required : requiredHeaders) {
                if (!headerSet.contains(required)) {
                    throw new IllegalArgumentException("Missing required header: " + required);
                }
            }

            // Process data rows
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                Map<String, String> student = new LinkedHashMap<>();
                
                for (int j = 0; j < Math.min(headers.length, row.length); j++) {
                    student.put(headers[j], row[j].trim());
                }
                
                // Skip empty rows
                if (!student.getOrDefault("studentId", "").isEmpty()) {
                    students.add(student);
                }
            }
        }

        return students;
    }

    /**
     * Writes grade data to a CSV file.
     */
    public void writeGradesToCSV(List<Map<String, String>> grades, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write header
            csvWriter.writeNext(GRADE_HEADERS);

            // Write data rows
            for (Map<String, String> grade : grades) {
                String[] row = new String[GRADE_HEADERS.length];
                for (int i = 0; i < GRADE_HEADERS.length; i++) {
                    row[i] = grade.getOrDefault(GRADE_HEADERS[i], "");
                }
                csvWriter.writeNext(row);
            }
        }
    }

    /**
     * Reads grade data from a CSV file.
     */
    public List<Map<String, String>> readGradesFromCSV(String filename) throws IOException, CsvException {
        List<Map<String, String>> grades = new ArrayList<>();

        try (FileReader reader = new FileReader(filename);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                return grades;
            }

            // Get headers from first row
            String[] headers = records.get(0);
            
            // Validate required headers
            Set<String> headerSet = Set.of(headers);
            List<String> requiredHeaders = List.of("studentId", "subjectCode", "gradeType", "value", "maxValue");
            for (String required : requiredHeaders) {
                if (!headerSet.contains(required)) {
                    throw new IllegalArgumentException("Missing required header: " + required);
                }
            }

            // Process data rows
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                Map<String, String> grade = new LinkedHashMap<>();
                
                for (int j = 0; j < Math.min(headers.length, row.length); j++) {
                    grade.put(headers[j], row[j].trim());
                }
                
                // Skip empty rows
                if (!grade.getOrDefault("studentId", "").isEmpty() && 
                    !grade.getOrDefault("subjectCode", "").isEmpty()) {
                    grades.add(grade);
                }
            }
        }

        return grades;
    }

    /**
     * Writes a generic report to CSV format.
     */
    public void writeReportToCSV(LinkedHashMap<String, Object> report, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write report title as first row
            csvWriter.writeNext(new String[]{"Report", report.get("reportTitle").toString()});
            csvWriter.writeNext(new String[]{"Generated", new Date().toString()});
            csvWriter.writeNext(new String[]{""}); // Empty row

            // Process report sections
            writeReportSection(report, csvWriter, 0);
        }
    }

    /**
     * Reads configuration data from a CSV file.
     */
    public Map<String, String> readConfigFromCSV(String filename) throws IOException, CsvException {
        Map<String, String> config = new LinkedHashMap<>();

        try (FileReader reader = new FileReader(filename);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();

            for (String[] record : records) {
                if (record.length >= 2 && !record[0].trim().isEmpty()) {
                    config.put(record[0].trim(), record[1].trim());
                }
            }
        }

        return config;
    }

    /**
     * Writes configuration data to a CSV file.
     */
    public void writeConfigToCSV(Map<String, String> config, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(new String[]{"Key", "Value"});

            for (Map.Entry<String, String> entry : config.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getKey(), entry.getValue()});
            }
        }
    }

    /**
     * Validates CSV file format and structure.
     */
    public ValidationResult validateCSVFile(String filename, String[] expectedHeaders) {
        ValidationResult result = new ValidationResult();

        try {
            if (!Files.exists(Paths.get(filename))) {
                result.addError("File does not exist: " + filename);
                return result;
            }

            try (FileReader reader = new FileReader(filename);
                 CSVReader csvReader = new CSVReader(reader)) {

                List<String[]> records = csvReader.readAll();

                if (records.isEmpty()) {
                    result.addError("File is empty");
                    return result;
                }

                // Validate headers
                String[] actualHeaders = records.get(0);
                Set<String> actualHeaderSet = Arrays.stream(actualHeaders)
                        .map(String::trim)
                        .collect(Collectors.toSet());

                for (String expectedHeader : expectedHeaders) {
                    if (!actualHeaderSet.contains(expectedHeader)) {
                        result.addError("Missing required header: " + expectedHeader);
                    }
                }

                // Validate data rows
                for (int i = 1; i < records.size(); i++) {
                    String[] row = records.get(i);
                    if (row.length < expectedHeaders.length) {
                        result.addWarning("Row " + (i + 1) + " has fewer columns than expected");
                    }
                    
                    // Check for completely empty rows
                    boolean isEmpty = Arrays.stream(row).allMatch(cell -> cell == null || cell.trim().isEmpty());
                    if (isEmpty) {
                        result.addWarning("Row " + (i + 1) + " is empty");
                    }
                }

                result.setRowCount(records.size() - 1); // Subtract header row
            }

        } catch (Exception e) {
            result.addError("Error reading file: " + e.getMessage());
        }

        return result;
    }

    /**
     * Creates a sample student CSV file for template purposes.
     */
    public void createSampleStudentCSV(String filename) throws IOException {
        List<Map<String, String>> sampleStudents = new ArrayList<>();
        
        Map<String, String> sample1 = new LinkedHashMap<>();
        sample1.put("studentId", "ST123456");
        sample1.put("name", "John Smith");
        sample1.put("major", "Computer Science");
        sample1.put("enrollmentYear", "2023");
        sample1.put("currentGPA", "0.00");
        sample1.put("totalGrades", "0");
        sampleStudents.add(sample1);
        
        Map<String, String> sample2 = new LinkedHashMap<>();
        sample2.put("studentId", "ST234567");
        sample2.put("name", "Jane Doe");
        sample2.put("major", "Mathematics");
        sample2.put("enrollmentYear", "2022");
        sample2.put("currentGPA", "0.00");
        sample2.put("totalGrades", "0");
        sampleStudents.add(sample2);
        
        writeStudentsToCSV(sampleStudents, filename);
    }

    /**
     * Creates a sample grade CSV file for template purposes.
     */
    public void createSampleGradeCSV(String filename) throws IOException {
        List<Map<String, String>> sampleGrades = new ArrayList<>();
        
        Map<String, String> sample1 = new LinkedHashMap<>();
        sample1.put("studentId", "ST123456");
        sample1.put("subjectCode", "MATH101");
        sample1.put("gradeType", "EXAM");
        sample1.put("value", "85.0");
        sample1.put("maxValue", "100.0");
        sample1.put("percentage", "85.00");
        sample1.put("letterGrade", "B");
        sample1.put("dateRecorded", "2024-01-15T10:30:00");
        sample1.put("description", "Midterm Exam");
        sampleGrades.add(sample1);
        
        writeGradesToCSV(sampleGrades, filename);
    }

    // Helper methods

    @SuppressWarnings("unchecked")
    private void writeReportSection(Map<String, Object> section, CSVWriter csvWriter, int level) throws IOException {
        String indent = "  ".repeat(level);
        
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            Object value = entry.getValue();
            
            if (value instanceof Map) {
                csvWriter.writeNext(new String[]{indent + entry.getKey()});
                writeReportSection((Map<String, Object>) value, csvWriter, level + 1);
            } else if (value instanceof List) {
                csvWriter.writeNext(new String[]{indent + entry.getKey()});
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof Map) {
                        writeReportSection((Map<String, Object>) item, csvWriter, level + 1);
                    } else {
                        csvWriter.writeNext(new String[]{indent + "  " + item.toString()});
                    }
                }
            } else {
                csvWriter.writeNext(new String[]{indent + entry.getKey(), value.toString()});
            }
        }
    }

    /**
     * Result class for CSV validation operations.
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        private int rowCount = 0;

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }

        public int getRowCount() {
            return rowCount;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Validation Result - ");
            sb.append("Rows: ").append(rowCount);
            sb.append(", Errors: ").append(errors.size());
            sb.append(", Warnings: ").append(warnings.size());
            
            if (!errors.isEmpty()) {
                sb.append("\nErrors:\n");
                errors.forEach(error -> sb.append("  - ").append(error).append("\n"));
            }
            
            if (!warnings.isEmpty()) {
                sb.append("\nWarnings:\n");
                warnings.forEach(warning -> sb.append("  - ").append(warning).append("\n"));
            }
            
            return sb.toString();
        }
    }
}