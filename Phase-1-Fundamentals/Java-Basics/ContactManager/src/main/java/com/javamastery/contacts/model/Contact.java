package com.javamastery.contacts.model;

import com.javamastery.contacts.util.Constants;
import com.javamastery.contacts.util.DateFormatter;
import com.javamastery.contacts.util.InputValidator;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a contact with validation and CSV serialization capabilities.
 */
public class Contact {
    
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    
    /**
     * Creates a new Contact with the specified details.
     *
     * @param name the contact's name (required)
     * @param phoneNumber the contact's phone number (required)
     * @param email the contact's email address (optional)
     * @param address the contact's address (optional)
     * @throws IllegalArgumentException if required fields are invalid
     */
    public Contact(String name, String phoneNumber, String email, String address) {
        validateAndSetName(name);
        validateAndSetPhoneNumber(phoneNumber);
        validateAndSetEmail(email);
        setAddress(address);
        
        LocalDateTime now = DateFormatter.now();
        this.createdDate = now;
        this.lastModified = now;
    }
    
    /**
     * Creates a Contact from CSV data (for loading from file).
     *
     * @param name the contact's name
     * @param phoneNumber the contact's phone number
     * @param email the contact's email address
     * @param address the contact's address
     * @param createdDate the creation date
     * @param lastModified the last modified date
     */
    public Contact(String name, String phoneNumber, String email, String address, 
                   LocalDateTime createdDate, LocalDateTime lastModified) {
        validateAndSetName(name);
        validateAndSetPhoneNumber(phoneNumber);
        validateAndSetEmail(email);
        setAddress(address);
        this.createdDate = createdDate != null ? createdDate : DateFormatter.now();
        this.lastModified = lastModified != null ? lastModified : DateFormatter.now();
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public LocalDateTime getLastModified() {
        return lastModified;
    }
    
    // Setters with validation
    public void setName(String name) {
        validateAndSetName(name);
        updateLastModified();
    }
    
    public void setPhoneNumber(String phoneNumber) {
        validateAndSetPhoneNumber(phoneNumber);
        updateLastModified();
    }
    
    public void setEmail(String email) {
        validateAndSetEmail(email);
        updateLastModified();
    }
    
    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
        updateLastModified();
    }
    
    /**
     * Converts the contact to a CSV string.
     *
     * @return CSV representation of the contact
     */
    public String toCsvString() {
        return String.join(Constants.CSV_DELIMITER,
            escapeCsvValue(name),
            escapeCsvValue(phoneNumber),
            escapeCsvValue(email != null ? email : ""),
            escapeCsvValue(address != null ? address : ""),
            DateFormatter.formatForCsv(createdDate),
            DateFormatter.formatForCsv(lastModified)
        );
    }
    
    /**
     * Creates a Contact from a CSV string.
     *
     * @param csvLine the CSV line to parse
     * @return Contact object
     * @throws IllegalArgumentException if the CSV line is invalid
     */
    public static Contact fromCsvString(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV line cannot be null or empty");
        }
        
        String[] parts = parseCsvLine(csvLine);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid CSV format: expected 6 fields, got " + parts.length);
        }
        
        try {
            String name = unescapeCsvValue(parts[0]);
            String phone = unescapeCsvValue(parts[1]);
            String email = unescapeCsvValue(parts[2]);
            String address = unescapeCsvValue(parts[3]);
            LocalDateTime created = DateFormatter.parseFromCsv(parts[4]);
            LocalDateTime modified = DateFormatter.parseFromCsv(parts[5]);
            
            // Handle empty email and address
            if (email.isEmpty()) email = null;
            if (address.isEmpty()) address = null;
            
            return new Contact(name, phone, email, address, created, modified);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV line: " + csvLine, e);
        }
    }
    
    /**
     * Updates the lastModified timestamp.
     */
    private void updateLastModified() {
        this.lastModified = DateFormatter.now();
    }
    
    /**
     * Validates and sets the name.
     */
    private void validateAndSetName(String name) {
        if (!InputValidator.isValidName(name)) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_NAME);
        }
        this.name = InputValidator.normalizeName(name);
    }
    
    /**
     * Validates and sets the phone number.
     */
    private void validateAndSetPhoneNumber(String phoneNumber) {
        if (!InputValidator.isValidPhone(phoneNumber)) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_PHONE);
        }
        this.phoneNumber = InputValidator.normalizePhone(phoneNumber);
    }
    
    /**
     * Validates and sets the email.
     */
    private void validateAndSetEmail(String email) {
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_EMAIL);
        }
        this.email = email != null ? InputValidator.normalizeEmail(email) : null;
    }
    
    /**
     * Escapes a value for CSV output.
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        // If value contains comma, newline, or quote, wrap in quotes and escape quotes
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Unescapes a CSV value.
     */
    private static String unescapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * Parses a CSV line, handling quoted values correctly.
     */
    private static String[] parseCsvLine(String line) {
        String[] result = new String[6];
        int index = 0;
        int start = 0;
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length() && index < 6; i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result[index++] = line.substring(start, i);
                start = i + 1;
            }
        }
        
        // Add the last field
        if (index < 6) {
            result[index] = line.substring(start);
        }
        
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return Objects.equals(phoneNumber, contact.phoneNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contact{");
        sb.append("name='").append(name).append('\'');
        sb.append(", phone='").append(phoneNumber).append('\'');
        if (email != null) {
            sb.append(", email='").append(email).append('\'');
        }
        if (address != null) {
            sb.append(", address='").append(address).append('\'');
        }
        sb.append(", created=").append(DateFormatter.formatForDisplay(createdDate));
        sb.append(", modified=").append(DateFormatter.formatForDisplay(lastModified));
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Returns a formatted string for display purposes.
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phoneNumber).append("\n");
        if (email != null && !email.isEmpty()) {
            sb.append("Email: ").append(email).append("\n");
        }
        if (address != null && !address.isEmpty()) {
            sb.append("Address: ").append(address).append("\n");
        }
        sb.append("Created: ").append(DateFormatter.formatForDisplay(createdDate)).append("\n");
        sb.append("Modified: ").append(DateFormatter.formatForDisplay(lastModified));
        return sb.toString();
    }
}