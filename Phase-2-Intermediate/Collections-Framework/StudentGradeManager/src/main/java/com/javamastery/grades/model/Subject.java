package com.javamastery.grades.model;

import java.util.Objects;

/**
 * Represents a subject in the academic system.
 * Uses proper encapsulation and implements equals/hashCode for use in collections.
 */
public class Subject {
    private final String code;
    private final String name;
    private final int creditHours;

    public Subject(String code, String name, int creditHours) {
        this.code = Objects.requireNonNull(code, "Subject code cannot be null");
        this.name = Objects.requireNonNull(name, "Subject name cannot be null");
        if (creditHours <= 0) {
            throw new IllegalArgumentException("Credit hours must be positive");
        }
        this.creditHours = creditHours;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCreditHours() {
        return creditHours;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subject subject = (Subject) obj;
        return Objects.equals(code, subject.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d credits)", code, name, creditHours);
    }
}