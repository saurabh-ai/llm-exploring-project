package com.javamastery.contacts.model;

import com.javamastery.contacts.exception.ContactValidationException;
import com.javamastery.contacts.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a contact with comprehensive validation and serialization support.
 * 
 * Learning Objectives:
 * - Model class design and encapsulation
 * - Input validation with custom exceptions
 * - Data transformation and serialization
 * - Immutable object practices
 */
public class Contact {
    
    private final String id;
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final String address;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModified;
    
    /**
     * Creates a new Contact with the specified details.
     * 
     * @param name the contact's name (required)
     * @param phoneNumber the contact's phone number (required)
     * @param email the contact's email address (optional)
     * @param address the contact's address (optional)
     * @throws ContactValidationException if required fields are invalid
     */
    public Contact(String name, String phoneNumber, String email, String address) {
        this.id = generateId();
        this.name = validateName(name);
        this.phoneNumber = validatePhoneNumber(phoneNumber);
        this.email = validateEmail(email);
        this.address = validateAddress(address);
        
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.lastModified = now;
    }
    
    /**
     * Creates a Contact from existing data (for deserialization).
     */
    public Contact(String id, String name, String phoneNumber, String email, 
                   String address, LocalDateTime createdDate, LocalDateTime lastModified) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }
    
    /**
     * Creates an updated copy of this contact with new information.
     * 
     * @param name the updated name
     * @param phoneNumber the updated phone number
     * @param email the updated email
     * @param address the updated address
     * @return a new Contact instance with updated information
     */
    public Contact withUpdatedInfo(String name, String phoneNumber, String email, String address) {
        return new Contact(
            this.id,
            validateName(name),
            validatePhoneNumber(phoneNumber),
            validateEmail(email),
            validateAddress(address),
            this.createdDate,
            LocalDateTime.now()
        );
    }
    
    private String generateId() {
        return "CONTACT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    private String validateName(String name) {
        if (!ValidationUtils.isValidName(name)) {
            throw new ContactValidationException("Name must be between 2 and 100 characters", "name", name);
        }
        return name.trim();
    }
    
    private String validatePhoneNumber(String phoneNumber) {
        if (!ValidationUtils.isValidPhoneNumber(phoneNumber)) {
            throw new ContactValidationException("Invalid phone number format", "phoneNumber", phoneNumber);
        }
        return phoneNumber.trim();
    }
    
    private String validateEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !ValidationUtils.isValidEmail(email)) {
            throw new ContactValidationException("Invalid email format", "email", email);
        }
        return email != null ? email.trim() : null;
    }
    
    private String validateAddress(String address) {
        if (address != null && address.trim().length() > 500) {
            throw new ContactValidationException("Address cannot exceed 500 characters", "address", address);
        }
        return address != null ? address.trim() : null;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Contact{id='%s', name='%s', phoneNumber='%s', email='%s'}", 
                           id, name, phoneNumber, email);
    }
    
    /**
     * Converts the contact to CSV format.
     * 
     * @return CSV string representation
     */
    public String toCsvString() {
        return String.join(",", 
            escapeForCsv(id),
            escapeForCsv(name),
            escapeForCsv(phoneNumber),
            escapeForCsv(email != null ? email : ""),
            escapeForCsv(address != null ? address : ""),
            createdDate.toString(),
            lastModified.toString()
        );
    }
    
    private String escapeForCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Creates a Contact from CSV string.
     * 
     * @param csvLine the CSV line to parse
     * @return Contact instance
     * @throws ContactValidationException if CSV parsing fails
     */
    public static Contact fromCsvString(String csvLine) {
        try {
            String[] parts = parseCsvLine(csvLine);
            if (parts.length != 7) {
                throw new ContactValidationException("Invalid CSV format - expected 7 fields", "csvLine", csvLine);
            }
            
            return new Contact(
                parts[0], // id
                parts[1], // name
                parts[2], // phoneNumber
                parts[3].isEmpty() ? null : parts[3], // email
                parts[4].isEmpty() ? null : parts[4], // address
                LocalDateTime.parse(parts[5]), // createdDate
                LocalDateTime.parse(parts[6])  // lastModified
            );
        } catch (Exception e) {
            throw new ContactValidationException("Failed to parse CSV line: " + e.getMessage(), "csvLine", csvLine);
        }
    }
    
    private static String[] parseCsvLine(String line) {
        // Simple CSV parser - handles quoted fields
        String[] result = new String[7];
        int fieldIndex = 0;
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length() && fieldIndex < 7; i++) {
            char ch = line.charAt(i);
            
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result[fieldIndex++] = currentField.toString();
                currentField = new StringBuilder();
            } else {
                currentField.append(ch);
            }
        }
        
        if (fieldIndex < 7) {
            result[fieldIndex] = currentField.toString();
        }
        
        return result;
    }
}