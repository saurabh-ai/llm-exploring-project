package com.javamastery.grades.util;

import com.javamastery.grades.model.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility class for validating student and grade data.
 * Provides comprehensive validation with detailed error reporting.
 */
public final class DataValidator {

    private DataValidator() {
        // Utility class - prevent instantiation
    }

    // Validation patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[A-Z]{2}\\d{6}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]{2,50}$");
    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("^[A-Z]{2,4}\\d{3}$");

    /**
     * Validates student data and returns a list of validation errors.
     */
    public static List<String> validateStudent(Student student) {
        List<String> errors = new ArrayList<>();

        if (student == null) {
            errors.add("Student object cannot be null");
            return errors;
        }

        // Validate student ID
        validateStudentId(student.getStudentId(), errors);

        // Validate name
        validateName(student.getName(), errors);

        // Validate major
        validateMajor(student.getMajor(), errors);

        // Validate enrollment year
        validateEnrollmentYear(student.getEnrollmentYear(), errors);

        return errors;
    }

    /**
     * Validates grade data and returns a list of validation errors.
     */
    public static <T extends Number> List<String> validateGrade(Grade<T> grade) {
        List<String> errors = new ArrayList<>();

        if (grade == null) {
            errors.add("Grade object cannot be null");
            return errors;
        }

        // Validate grade values
        if (grade.getValue() == null) {
            errors.add("Grade value cannot be null");
        } else if (grade.getValue().doubleValue() < 0) {
            errors.add("Grade value cannot be negative");
        }

        if (grade.getMaxValue() == null) {
            errors.add("Max value cannot be null");
        } else if (grade.getMaxValue().doubleValue() <= 0) {
            errors.add("Max value must be positive");
        }

        if (grade.getValue() != null && grade.getMaxValue() != null &&
            grade.getValue().doubleValue() > grade.getMaxValue().doubleValue()) {
            errors.add("Grade value cannot exceed max value");
        }

        // Validate subject
        if (grade.getSubject() == null) {
            errors.add("Subject cannot be null");
        } else {
            errors.addAll(validateSubject(grade.getSubject()));
        }

        // Validate grade type
        if (grade.getGradeType() == null) {
            errors.add("Grade type cannot be null");
        }

        // Validate date
        if (grade.getDateRecorded() == null) {
            errors.add("Date recorded cannot be null");
        }

        return errors;
    }

    /**
     * Validates subject data and returns a list of validation errors.
     */
    public static List<String> validateSubject(Subject subject) {
        List<String> errors = new ArrayList<>();

        if (subject == null) {
            errors.add("Subject object cannot be null");
            return errors;
        }

        // Validate subject code
        validateSubjectCode(subject.getCode(), errors);

        // Validate subject name
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            errors.add("Subject name cannot be null or empty");
        } else if (subject.getName().length() > 100) {
            errors.add("Subject name cannot exceed 100 characters");
        }

        // Validate credit hours
        if (subject.getCreditHours() <= 0) {
            errors.add("Credit hours must be positive");
        } else if (subject.getCreditHours() > 10) {
            errors.add("Credit hours cannot exceed 10");
        }

        return errors;
    }

    /**
     * Validates a collection of students and returns validation results.
     */
    public static ValidationResult<Student> validateStudents(Collection<Student> students) {
        ValidationResult<Student> result = new ValidationResult<>();

        if (students == null) {
            result.addGlobalError("Student collection cannot be null");
            return result;
        }

        Set<String> seenIds = new HashSet<>();
        
        for (Student student : students) {
            List<String> errors = validateStudent(student);
            
            if (errors.isEmpty()) {
                // Check for duplicate student IDs
                if (student.getStudentId() != null && seenIds.contains(student.getStudentId())) {
                    errors.add("Duplicate student ID: " + student.getStudentId());
                } else if (student.getStudentId() != null) {
                    seenIds.add(student.getStudentId());
                }
            }

            if (errors.isEmpty()) {
                result.addValidItem(student);
            } else {
                result.addInvalidItem(student, errors);
            }
        }

        return result;
    }

    /**
     * Validates a collection of grades and returns validation results.
     */
    public static <T extends Number> ValidationResult<Grade<T>> validateGrades(Collection<Grade<T>> grades) {
        ValidationResult<Grade<T>> result = new ValidationResult<>();

        if (grades == null) {
            result.addGlobalError("Grade collection cannot be null");
            return result;
        }

        for (Grade<T> grade : grades) {
            List<String> errors = validateGrade(grade);

            if (errors.isEmpty()) {
                result.addValidItem(grade);
            } else {
                result.addInvalidItem(grade, errors);
            }
        }

        return result;
    }

    /**
     * Validates if a GPA value is within acceptable range.
     */
    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }

    /**
     * Validates if a percentage is within acceptable range.
     */
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }

    /**
     * Validates student data for bulk import operations.
     */
    public static Map<String, List<String>> validateForBulkImport(List<Map<String, String>> studentData) {
        Map<String, List<String>> validationResults = new LinkedHashMap<>();
        Set<String> seenIds = new HashSet<>();

        for (int i = 0; i < studentData.size(); i++) {
            Map<String, String> data = studentData.get(i);
            String rowKey = "Row " + (i + 1);
            List<String> errors = new ArrayList<>();

            // Validate required fields
            String studentId = data.get("studentId");
            String name = data.get("name");
            String major = data.get("major");
            String enrollmentYearStr = data.get("enrollmentYear");

            if (studentId == null || studentId.trim().isEmpty()) {
                errors.add("Student ID is required");
            } else {
                validateStudentId(studentId, errors);
                if (seenIds.contains(studentId)) {
                    errors.add("Duplicate student ID: " + studentId);
                } else {
                    seenIds.add(studentId);
                }
            }

            if (name == null || name.trim().isEmpty()) {
                errors.add("Name is required");
            } else {
                validateName(name, errors);
            }

            if (major == null || major.trim().isEmpty()) {
                errors.add("Major is required");
            } else {
                validateMajor(major, errors);
            }

            if (enrollmentYearStr == null || enrollmentYearStr.trim().isEmpty()) {
                errors.add("Enrollment year is required");
            } else {
                try {
                    int enrollmentYear = Integer.parseInt(enrollmentYearStr);
                    validateEnrollmentYear(enrollmentYear, errors);
                } catch (NumberFormatException e) {
                    errors.add("Enrollment year must be a valid integer");
                }
            }

            if (!errors.isEmpty()) {
                validationResults.put(rowKey, errors);
            }
        }

        return validationResults;
    }

    // Private validation helper methods

    private static void validateStudentId(String studentId, List<String> errors) {
        if (studentId == null || studentId.trim().isEmpty()) {
            errors.add("Student ID cannot be null or empty");
        } else if (!STUDENT_ID_PATTERN.matcher(studentId).matches()) {
            errors.add("Student ID must follow format: XX000000 (2 letters followed by 6 digits)");
        }
    }

    private static void validateName(String name, List<String> errors) {
        if (name == null || name.trim().isEmpty()) {
            errors.add("Name cannot be null or empty");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            errors.add("Name must contain only letters, spaces, hyphens, and apostrophes (2-50 characters)");
        }
    }

    private static void validateMajor(String major, List<String> errors) {
        if (major == null || major.trim().isEmpty()) {
            errors.add("Major cannot be null or empty");
        } else if (major.length() > 50) {
            errors.add("Major cannot exceed 50 characters");
        }
    }

    private static void validateEnrollmentYear(int enrollmentYear, List<String> errors) {
        int currentYear = java.time.Year.now().getValue();
        if (enrollmentYear < 1900) {
            errors.add("Enrollment year cannot be before 1900");
        } else if (enrollmentYear > currentYear + 1) {
            errors.add("Enrollment year cannot be more than one year in the future");
        }
    }

    private static void validateSubjectCode(String subjectCode, List<String> errors) {
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            errors.add("Subject code cannot be null or empty");
        } else if (!SUBJECT_CODE_PATTERN.matcher(subjectCode).matches()) {
            errors.add("Subject code must follow format: ABC123 (2-4 letters followed by 3 digits)");
        }
    }

    /**
     * Result class for validation operations.
     */
    public static class ValidationResult<T> {
        private final List<T> validItems = new ArrayList<>();
        private final Map<T, List<String>> invalidItems = new LinkedHashMap<>();
        private final List<String> globalErrors = new ArrayList<>();

        public void addValidItem(T item) {
            validItems.add(item);
        }

        public void addInvalidItem(T item, List<String> errors) {
            invalidItems.put(item, new ArrayList<>(errors));
        }

        public void addGlobalError(String error) {
            globalErrors.add(error);
        }

        public List<T> getValidItems() {
            return Collections.unmodifiableList(validItems);
        }

        public Map<T, List<String>> getInvalidItems() {
            return Collections.unmodifiableMap(invalidItems);
        }

        public List<String> getGlobalErrors() {
            return Collections.unmodifiableList(globalErrors);
        }

        public boolean hasErrors() {
            return !invalidItems.isEmpty() || !globalErrors.isEmpty();
        }

        public int getValidCount() {
            return validItems.size();
        }

        public int getInvalidCount() {
            return invalidItems.size();
        }

        public double getValidityRate() {
            int total = validItems.size() + invalidItems.size();
            return total > 0 ? (double) validItems.size() / total : 0.0;
        }

        public List<String> getAllErrors() {
            List<String> allErrors = new ArrayList<>(globalErrors);
            invalidItems.values().forEach(allErrors::addAll);
            return allErrors;
        }
    }
}