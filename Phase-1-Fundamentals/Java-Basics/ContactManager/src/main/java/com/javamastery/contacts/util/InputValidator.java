package com.javamastery.contacts.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input.
 */
public final class InputValidator {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(Constants.PHONE_PATTERN);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constants.EMAIL_PATTERN);
    private static final Pattern NAME_PATTERN = Pattern.compile(Constants.NAME_PATTERN);
    
    private InputValidator() {
        // Utility class should not be instantiated
        throw new AssertionError("InputValidator class should not be instantiated");
    }
    
    /**
     * Validates a phone number.
     *
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validates an email address.
     *
     * @param email the email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email is optional
        }
        String trimmedEmail = email.trim();
        
        // Check for consecutive dots
        if (trimmedEmail.contains("..")) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }
    
    /**
     * Validates a name.
     *
     * @param name the name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validates that a string is not null or empty.
     *
     * @param value the string to validate
     * @return true if not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Normalizes a phone number by removing common formatting characters.
     *
     * @param phone the phone number to normalize
     * @return normalized phone number
     */
    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        // Remove spaces and parentheses, but keep plus, hyphens and dots
        String normalized = phone.trim().replaceAll("[()\\s]", "");
        
        // For (555) 123-4567 format, convert to 555-123-4567
        if (normalized.matches("\\d{3}-\\d{3}-\\d{4}")) {
            return normalized;
        }
        
        // Keep original format for other valid patterns
        return normalized;
    }
    
    /**
     * Normalizes an email address.
     *
     * @param email the email address to normalize
     * @return normalized email address
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
    
    /**
     * Normalizes a name.
     *
     * @param name the name to normalize
     * @return normalized name
     */
    public static String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        // Split by spaces and capitalize each word, preserving hyphens and apostrophes
        String[] words = name.trim().split("\\s+");
        StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                normalized.append(" ");
            }
            String word = words[i];
            if (!word.isEmpty()) {
                normalized.append(capitalizeWord(word));
            }
        }
        return normalized.toString();
    }
    
    /**
     * Capitalizes a word while preserving hyphens and apostrophes.
     */
    private static String capitalizeWord(String word) {
        if (word.isEmpty()) {
            return word;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : word.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else if (c == '-' || c == '\'') {
                result.append(c);
                capitalizeNext = true;  // Capitalize after hyphen or apostrophe
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Validates a menu choice.
     *
     * @param choice the choice to validate
     * @param minChoice minimum valid choice
     * @param maxChoice maximum valid choice
     * @return true if valid, false otherwise
     */
    public static boolean isValidMenuChoice(String choice, int minChoice, int maxChoice) {
        if (choice == null || choice.trim().isEmpty()) {
            return false;
        }
        try {
            int choiceNum = Integer.parseInt(choice.trim());
            return choiceNum >= minChoice && choiceNum <= maxChoice;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}