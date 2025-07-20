package com.javamastery.grades.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Generic Grade class supporting different grade value types (Double, Integer, etc.).
 * Demonstrates use of bounded type parameters and generic programming.
 * 
 * @param <T> The type of the grade value, must extend Number for calculations
 */
public class Grade<T extends Number> implements Comparable<Grade<T>> {
    private final T value;
    private final T maxValue;
    private final Subject subject;
    private final GradeType gradeType;
    private final LocalDateTime dateRecorded;
    private final String description;

    public Grade(T value, T maxValue, Subject subject, GradeType gradeType, String description) {
        this.value = Objects.requireNonNull(value, "Grade value cannot be null");
        this.maxValue = Objects.requireNonNull(maxValue, "Max value cannot be null");
        this.subject = Objects.requireNonNull(subject, "Subject cannot be null");
        this.gradeType = Objects.requireNonNull(gradeType, "Grade type cannot be null");
        this.description = description != null ? description : "";
        this.dateRecorded = LocalDateTime.now();
        
        validateGradeValues();
    }

    private void validateGradeValues() {
        if (value.doubleValue() < 0) {
            throw new IllegalArgumentException("Grade value cannot be negative");
        }
        if (maxValue.doubleValue() <= 0) {
            throw new IllegalArgumentException("Max value must be positive");
        }
        if (value.doubleValue() > maxValue.doubleValue()) {
            throw new IllegalArgumentException("Grade value cannot exceed max value");
        }
    }

    public T getValue() {
        return value;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public Subject getSubject() {
        return subject;
    }

    public GradeType getGradeType() {
        return gradeType;
    }

    public LocalDateTime getDateRecorded() {
        return dateRecorded;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Calculates the percentage score for this grade.
     * 
     * @return percentage as double (0.0 to 100.0)
     */
    public double getPercentage() {
        return (value.doubleValue() / maxValue.doubleValue()) * 100.0;
    }

    /**
     * Gets the letter grade based on percentage.
     * 
     * @return letter grade (A, B, C, D, F)
     */
    public char getLetterGrade() {
        double percentage = getPercentage();
        if (percentage >= 90) return 'A';
        else if (percentage >= 80) return 'B';
        else if (percentage >= 70) return 'C';
        else if (percentage >= 60) return 'D';
        else return 'F';
    }

    /**
     * Natural ordering by percentage (descending - higher grades first).
     */
    @Override
    public int compareTo(Grade<T> other) {
        return Double.compare(other.getPercentage(), this.getPercentage());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grade<?> grade = (Grade<?>) obj;
        return Objects.equals(value, grade.value) &&
               Objects.equals(maxValue, grade.maxValue) &&
               Objects.equals(subject, grade.subject) &&
               gradeType == grade.gradeType &&
               Objects.equals(dateRecorded, grade.dateRecorded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, maxValue, subject, gradeType, dateRecorded);
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %.1f/%.1f (%.1f%% - %c)", 
                           subject.getCode(), gradeType.getDisplayName(), 
                           value.doubleValue(), maxValue.doubleValue(), 
                           getPercentage(), getLetterGrade());
    }
}