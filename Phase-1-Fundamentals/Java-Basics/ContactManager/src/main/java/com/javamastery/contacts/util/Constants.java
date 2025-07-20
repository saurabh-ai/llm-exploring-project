package com.javamastery.contacts.util;

/**
 * Constants used throughout the Contact Manager application.
 */
public final class Constants {
    
    // File constants
    public static final String DEFAULT_CONTACTS_FILE = "data/contacts.csv";
    public static final String BACKUP_DIRECTORY = "data/backups";
    public static final String SAMPLE_CONTACTS_FILE = "src/main/resources/sample-contacts.csv";
    
    // CSV constants
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_HEADER = "name,phone,email,address,createdDate,lastModified";
    
    // Validation patterns
    public static final String PHONE_PATTERN = "^(\\+?[1-9]\\d{0,3}[-.]?\\d{2,4}[-.]?\\d{4})|^\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$|^\\+?[1-9]\\d{7,14}$";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$";
    public static final String NAME_PATTERN = "^[a-zA-ZÀ-ÿ\\u0100-\\u017F\\s''-]{2,50}$";
    
    // Menu options
    public static final String MENU_SEPARATOR = "=" + "=".repeat(50);
    public static final String MENU_TITLE = "Contact Manager - Phase 1 Final Project";
    
    // Error messages
    public static final String ERROR_INVALID_PHONE = "Invalid phone number format. Please use formats like: +1-555-0123, (555) 123-4567, or +15550123";
    public static final String ERROR_INVALID_EMAIL = "Invalid email format. Please enter a valid email address.";
    public static final String ERROR_INVALID_NAME = "Invalid name. Name must be 2-50 characters and contain only letters, spaces, hyphens, and apostrophes.";
    public static final String ERROR_DUPLICATE_PHONE = "A contact with this phone number already exists.";
    public static final String ERROR_CONTACT_NOT_FOUND = "Contact not found.";
    public static final String ERROR_FILE_NOT_FOUND = "File not found: ";
    public static final String ERROR_FILE_READ = "Error reading file: ";
    public static final String ERROR_FILE_WRITE = "Error writing to file: ";
    
    // Success messages
    public static final String SUCCESS_CONTACT_ADDED = "Contact added successfully.";
    public static final String SUCCESS_CONTACT_UPDATED = "Contact updated successfully.";
    public static final String SUCCESS_CONTACT_DELETED = "Contact deleted successfully.";
    public static final String SUCCESS_FILE_SAVED = "Contacts saved successfully.";
    public static final String SUCCESS_FILE_LOADED = "Contacts loaded successfully.";
    
    private Constants() {
        // Utility class should not be instantiated
        throw new AssertionError("Constants class should not be instantiated");
    }
}