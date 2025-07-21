package com.javamastery.contacts.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation with comprehensive error handling.
 * 
 * Learning Objectives:
 * - Static utility methods
 * - Regular expression validation
 * - Input sanitization and validation
 * - Defensive programming practices
 */
public final class ValidationUtils {
    
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-'.]{2,100}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{7,14}$|^\\([0-9]{3}\\)\\s[0-9]{3}\\-[0-9]{4}$|^[0-9]{3}\\-[0-9]{3}\\-[0-9]{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private ValidationUtils() {
        // Utility class - prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates if a name is in valid format.
     * 
     * @param name the name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validates if a phone number is in valid format.
     * Supports multiple formats:
     * - +1234567890123
     * - (123) 456-7890
     * - 123-456-7890
     * 
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        String cleaned = phoneNumber.replaceAll("\\s+", " ").trim();
        return PHONE_PATTERN.matcher(cleaned).matches();
    }
    
    /**
     * Validates if an email address is in valid format.
     * 
     * @param email the email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String cleaned = email.trim();
        return !cleaned.isEmpty() && EMAIL_PATTERN.matcher(cleaned).matches();
    }
    
    /**
     * Sanitizes a string by trimming whitespace and removing dangerous characters.
     * 
     * @param input the input string
     * @return sanitized string or null if input is null
     */
    public static String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("[\\r\\n\\t]", " ").replaceAll("\\s+", " ");
    }
    
    /**
     * Checks if a string is null or empty after trimming.
     * 
     * @param str the string to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validates string length within specified bounds.
     * 
     * @param str the string to validate
     * @param minLength minimum length (inclusive)
     * @param maxLength maximum length (inclusive)
     * @return true if within bounds, false otherwise
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Normalizes a phone number by removing formatting characters.
     * 
     * @param phoneNumber the phone number to normalize
     * @return normalized phone number or null if input is null
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^0-9+]", "");
    }
}