package com.javamastery.contacts.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Unit tests for {@link Contact}.
 */
class ContactTest {

    @Test
    void createContact_ValidData() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        
        assertEquals("John Doe", contact.getName());
        assertEquals("+1-555-0123", contact.getPhoneNumber());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("123 Main St", contact.getAddress());
        assertNotNull(contact.getCreatedDate());
        assertNotNull(contact.getLastModified());
    }

    @Test
    void createContact_RequiredFieldsOnly() {
        Contact contact = new Contact("Jane Smith", "+1-555-0124", null, null);
        
        assertEquals("Jane Smith", contact.getName());
        assertEquals("+1-555-0124", contact.getPhoneNumber());
        assertNull(contact.getEmail());
        assertNull(contact.getAddress());
    }

    @Test
    void createContact_InvalidName() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("", "+1-555-0123", null, null));
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact(null, "+1-555-0123", null, null));
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("A", "+1-555-0123", null, null));  // Too short
    }

    @Test
    void createContact_InvalidPhone() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("John Doe", "", null, null));
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("John Doe", null, null, null));
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("John Doe", "abc-def-ghij", null, null));  // Letters
    }

    @Test
    void createContact_InvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("John Doe", "+1-555-0123", "invalid-email", null));
        assertThrows(IllegalArgumentException.class, () -> 
            new Contact("John Doe", "+1-555-0123", "@domain.com", null));
    }

    @Test
    void setters_UpdateLastModified() throws InterruptedException {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        LocalDateTime originalModified = contact.getLastModified();
        
        Thread.sleep(1); // Ensure time difference
        contact.setName("Jane Doe");
        
        assertTrue(contact.getLastModified().isAfter(originalModified));
        assertEquals("Jane Doe", contact.getName());
    }

    @Test
    void toCsvString() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 20, 10, 0, 0);
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St", now, now);
        
        String csv = contact.toCsvString();
        String expected = "John Doe,+1-555-0123,john@example.com,123 Main St,2025-07-20T10:00:00,2025-07-20T10:00:00";
        assertEquals(expected, csv);
    }

    @Test
    void toCsvString_WithCommasInAddress() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 20, 10, 0, 0);
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St, Springfield, IL", now, now);
        
        String csv = contact.toCsvString();
        assertTrue(csv.contains("\"123 Main St, Springfield, IL\""));
    }

    @Test
    void fromCsvString_ValidData() {
        String csvLine = "John Doe,+1-555-0123,john@example.com,123 Main St,2025-07-20T10:00:00,2025-07-20T10:00:00";
        Contact contact = Contact.fromCsvString(csvLine);
        
        assertEquals("John Doe", contact.getName());
        assertEquals("+1-555-0123", contact.getPhoneNumber());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("123 Main St", contact.getAddress());
    }

    @Test
    void fromCsvString_EmptyOptionalFields() {
        String csvLine = "John Doe,+1-555-0123,,,2025-07-20T10:00:00,2025-07-20T10:00:00";
        Contact contact = Contact.fromCsvString(csvLine);
        
        assertEquals("John Doe", contact.getName());
        assertEquals("+1-555-0123", contact.getPhoneNumber());
        assertNull(contact.getEmail());
        assertNull(contact.getAddress());
    }

    @Test
    void fromCsvString_QuotedFields() {
        String csvLine = "\"John Doe\",\"+1-555-0123\",\"john@example.com\",\"123 Main St, Springfield, IL\",2025-07-20T10:00:00,2025-07-20T10:00:00";
        Contact contact = Contact.fromCsvString(csvLine);
        
        assertEquals("John Doe", contact.getName());
        assertEquals("123 Main St, Springfield, IL", contact.getAddress());
    }

    @Test
    void fromCsvString_InvalidData() {
        assertThrows(IllegalArgumentException.class, () -> 
            Contact.fromCsvString(""));
        assertThrows(IllegalArgumentException.class, () -> 
            Contact.fromCsvString(null));
        assertThrows(IllegalArgumentException.class, () -> 
            Contact.fromCsvString("invalid,csv"));  // Not enough fields
    }

    @Test
    void equals_SamePhoneNumber() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0123", null, null);
        
        assertEquals(contact1, contact2);  // Same phone number
    }

    @Test
    void equals_DifferentPhoneNumber() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("John Doe", "+1-555-0124", null, null);
        
        assertNotEquals(contact1, contact2);  // Different phone number
    }

    @Test
    void hashCode_ConsistentWithEquals() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0123", null, null);
        
        assertEquals(contact1.hashCode(), contact2.hashCode());
    }

    @Test
    void toString_ContainsKeyInformation() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        String str = contact.toString();
        
        assertTrue(str.contains("John Doe"));
        assertTrue(str.contains("+1-555-0123"));
        assertTrue(str.contains("john@example.com"));
        assertTrue(str.contains("123 Main St"));
    }

    @Test
    void toDisplayString_FormattedOutput() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        String display = contact.toDisplayString();
        
        assertTrue(display.contains("Name: John Doe"));
        assertTrue(display.contains("Phone: +1-555-0123"));
        assertTrue(display.contains("Email: john@example.com"));
        assertTrue(display.contains("Address: 123 Main St"));
    }

    @Test
    void nameNormalization() {
        Contact contact = new Contact("john doe", "+1-555-0123", null, null);
        assertEquals("John Doe", contact.getName());
    }

    @Test
    void phoneNormalization() {
        Contact contact = new Contact("John Doe", "(555) 123-4567", null, null);
        assertEquals("555123-4567", contact.getPhoneNumber());
    }

    @Test
    void emailNormalization() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "JOHN@EXAMPLE.COM", null);
        assertEquals("john@example.com", contact.getEmail());
    }
}