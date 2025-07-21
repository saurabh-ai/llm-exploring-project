package com.javamastery.contacts.model;

import com.javamastery.contacts.exception.ContactValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Contact model class.
 */
class ContactTest {
    
    @Test
    void testValidContactCreation() {
        Contact contact = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        
        assertNotNull(contact.getId());
        assertEquals("John Doe", contact.getName());
        assertEquals("123-456-7890", contact.getPhoneNumber());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("123 Main St", contact.getAddress());
        assertNotNull(contact.getCreatedDate());
        assertNotNull(contact.getLastModified());
    }
    
    @Test
    void testContactWithOptionalFields() {
        Contact contact = new Contact("Jane Smith", "+1234567890", null, null);
        
        assertEquals("Jane Smith", contact.getName());
        assertEquals("+1234567890", contact.getPhoneNumber());
        assertNull(contact.getEmail());
        assertNull(contact.getAddress());
    }
    
    @Test
    void testInvalidName() {
        assertThrows(ContactValidationException.class, () -> 
            new Contact("", "123-456-7890", null, null));
        
        assertThrows(ContactValidationException.class, () -> 
            new Contact("A", "123-456-7890", null, null));
        
        assertThrows(ContactValidationException.class, () -> 
            new Contact(null, "123-456-7890", null, null));
    }
    
    @Test
    void testInvalidPhoneNumber() {
        assertThrows(ContactValidationException.class, () -> 
            new Contact("John Doe", "", null, null));
        
        assertThrows(ContactValidationException.class, () -> 
            new Contact("John Doe", "123", null, null));
        
        assertThrows(ContactValidationException.class, () -> 
            new Contact("John Doe", null, null, null));
    }
    
    @Test
    void testInvalidEmail() {
        assertThrows(ContactValidationException.class, () -> 
            new Contact("John Doe", "123-456-7890", "invalid-email", null));
        
        assertThrows(ContactValidationException.class, () -> 
            new Contact("John Doe", "123-456-7890", "@example.com", null));
    }
    
    @Test
    void testContactUpdate() {
        Contact original = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        Contact updated = original.withUpdatedInfo("John Smith", "098-765-4321", "johnsmith@example.com", "456 Oak Ave");
        
        assertEquals(original.getId(), updated.getId());
        assertEquals(original.getCreatedDate(), updated.getCreatedDate());
        assertEquals("John Smith", updated.getName());
        assertEquals("098-765-4321", updated.getPhoneNumber());
        assertEquals("johnsmith@example.com", updated.getEmail());
        assertEquals("456 Oak Ave", updated.getAddress());
        assertNotEquals(original.getLastModified(), updated.getLastModified());
    }
    
    @Test
    void testCsvSerialization() {
        Contact contact = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        String csvString = contact.toCsvString();
        
        assertNotNull(csvString);
        assertTrue(csvString.contains("John Doe"));
        assertTrue(csvString.contains("123-456-7890"));
        assertTrue(csvString.contains("john@example.com"));
    }
    
    @Test
    void testCsvDeserialization() {
        Contact original = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        String csvString = original.toCsvString();
        
        Contact deserialized = Contact.fromCsvString(csvString);
        
        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getPhoneNumber(), deserialized.getPhoneNumber());
        assertEquals(original.getEmail(), deserialized.getEmail());
        assertEquals(original.getAddress(), deserialized.getAddress());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Contact contact1 = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        Contact contact2 = new Contact("Jane Smith", "098-765-4321", "jane@example.com", "456 Oak Ave");
        
        assertEquals(contact1, contact1);
        assertNotEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact1.hashCode());
    }
}