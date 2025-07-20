package com.javamastery.contacts.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date formatting and parsing operations.
 */
public final class DateFormatter {
    
    private static final DateTimeFormatter CSV_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
    private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    
    private DateFormatter() {
        // Utility class should not be instantiated
        throw new AssertionError("DateFormatter class should not be instantiated");
    }
    
    /**
     * Formats a LocalDateTime for CSV storage (ISO format).
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted date string for CSV
     */
    public static String formatForCsv(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(CSV_FORMATTER);
    }
    
    /**
     * Parses a date string from CSV format.
     *
     * @param dateString the date string to parse
     * @return parsed LocalDateTime
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static LocalDateTime parseFromCsv(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateString.trim(), CSV_FORMATTER);
    }
    
    /**
     * Formats a LocalDateTime for user-friendly display.
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted date string for display
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }
    
    /**
     * Formats a LocalDateTime in short format.
     *
     * @param dateTime the LocalDateTime to format
     * @return formatted date string in short format
     */
    public static String formatShort(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(SHORT_FORMATTER);
    }
    
    /**
     * Gets the current timestamp.
     *
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}