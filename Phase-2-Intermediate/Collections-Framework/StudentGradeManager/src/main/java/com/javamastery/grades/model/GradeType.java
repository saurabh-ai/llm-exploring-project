package com.javamastery.grades.model;

/**
 * Enumeration representing different types of grades in the academic system.
 * Used to categorize and weight different types of assessments.
 */
public enum GradeType {
    HOMEWORK("Homework", 0.20),
    QUIZ("Quiz", 0.15),
    EXAM("Exam", 0.40),
    PROJECT("Project", 0.25);

    private final String displayName;
    private final double defaultWeight;

    GradeType(String displayName, double defaultWeight) {
        this.displayName = displayName;
        this.defaultWeight = defaultWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultWeight() {
        return defaultWeight;
    }

    @Override
    public String toString() {
        return displayName;
    }
}