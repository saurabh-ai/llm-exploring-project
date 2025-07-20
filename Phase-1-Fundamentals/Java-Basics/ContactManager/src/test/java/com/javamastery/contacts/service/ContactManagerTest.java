package com.javamastery.contacts.service;

import com.javamastery.contacts.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ContactManager}.
 */
class ContactManagerTest {

    @TempDir
    Path tempDir;

    private ContactManager contactManager;
    private String testDataFile;

    @BeforeEach
    void setUp() {
        testDataFile = tempDir.resolve("test-contacts.csv").toString();
        contactManager = new ContactManager(testDataFile);
    }

    @Test
    void addContact_Success() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        
        contactManager.addContact(contact);
        
        assertEquals(1, contactManager.getContactCount());
        List<Contact> contacts = contactManager.getAllContacts();
        assertEquals(contact.getName(), contacts.get(0).getName());
    }

    @Test
    void addContact_NullContact() {
        assertThrows(IllegalArgumentException.class, () -> 
            contactManager.addContact(null));
    }

    @Test
    void addContact_DuplicatePhone() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0123", null, null);
        
        contactManager.addContact(contact1);
        
        assertThrows(IllegalArgumentException.class, () -> 
            contactManager.addContact(contact2));
    }

    @Test
    void findContactsByName_ExactMatch() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        List<Contact> results = contactManager.findContactsByName("John Doe");
        
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }

    @Test
    void findContactsByName_PartialMatch() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("John Smith", "+1-555-0124", null, null);
        Contact contact3 = new Contact("Jane Doe", "+1-555-0125", null, null);
        
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        contactManager.addContact(contact3);
        
        List<Contact> results = contactManager.findContactsByName("John");
        
        assertEquals(2, results.size());
    }

    @Test
    void findContactsByName_CaseInsensitive() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        List<Contact> results = contactManager.findContactsByName("john doe");
        
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }

    @Test
    void findContactsByName_NotFound() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        List<Contact> results = contactManager.findContactsByName("Jane");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void findContactByPhone_Success() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        Contact found = contactManager.findContactByPhone("+1-555-0123");
        
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }

    @Test
    void findContactByPhone_NormalizedMatch() {
        Contact contact = new Contact("John Doe", "(555) 123-4567", null, null);
        contactManager.addContact(contact);
        
        Contact found = contactManager.findContactByPhone("(555) 123-4567");
        
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }

    @Test
    void findContactByPhone_NotFound() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        Contact found = contactManager.findContactByPhone("+1-555-9999");
        
        assertNull(found);
    }

    @Test
    void updateContact_Success() {
        Contact original = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        contactManager.addContact(original);
        
        Contact updated = new Contact("John Smith", "+1-555-0123", "john.smith@example.com", "456 Oak Ave");
        boolean result = contactManager.updateContact("+1-555-0123", updated);
        
        assertTrue(result);
        Contact found = contactManager.findContactByPhone("+1-555-0123");
        assertEquals("John Smith", found.getName());
        assertEquals("john.smith@example.com", found.getEmail());
        assertEquals("456 Oak Ave", found.getAddress());
    }

    @Test
    void updateContact_PhoneNumberChange() {
        Contact original = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(original);
        
        Contact updated = new Contact("John Doe", "+1-555-9999", null, null);
        boolean result = contactManager.updateContact("+1-555-0123", updated);
        
        assertTrue(result);
        assertNull(contactManager.findContactByPhone("+1-555-0123"));
        assertNotNull(contactManager.findContactByPhone("+1-555-9999"));
    }

    @Test
    void updateContact_PhoneConflict() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, null);
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        
        Contact updated = new Contact("John Doe", "+1-555-0124", null, null);
        
        assertThrows(IllegalArgumentException.class, () -> 
            contactManager.updateContact("+1-555-0123", updated));
    }

    @Test
    void updateContact_NotFound() {
        Contact updated = new Contact("John Doe", "+1-555-0123", null, null);
        
        boolean result = contactManager.updateContact("+1-555-9999", updated);
        
        assertFalse(result);
    }

    @Test
    void deleteContact_Success() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        boolean result = contactManager.deleteContact("+1-555-0123");
        
        assertTrue(result);
        assertEquals(0, contactManager.getContactCount());
        assertNull(contactManager.findContactByPhone("+1-555-0123"));
    }

    @Test
    void deleteContact_NotFound() {
        boolean result = contactManager.deleteContact("+1-555-9999");
        
        assertFalse(result);
    }

    @Test
    void getAllContacts_DefensiveCopy() {
        Contact contact = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(contact);
        
        List<Contact> contacts1 = contactManager.getAllContacts();
        List<Contact> contacts2 = contactManager.getAllContacts();
        
        assertNotSame(contacts1, contacts2);  // Different list instances
        assertEquals(contacts1.size(), contacts2.size());
    }

    @Test
    void importContacts_Success() throws IOException {
        // Create test file with contacts
        String importFile = tempDir.resolve("import-test.csv").toString();
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, null);
        FileManager.saveContactsToFile(List.of(contact1, contact2), importFile);
        
        int imported = contactManager.importContacts(importFile);
        
        assertEquals(2, imported);
        assertEquals(2, contactManager.getContactCount());
    }

    @Test
    void importContacts_SkipDuplicates() throws IOException {
        // Add existing contact
        Contact existing = new Contact("John Doe", "+1-555-0123", null, null);
        contactManager.addContact(existing);
        
        // Create import file with duplicate and new contact
        String importFile = tempDir.resolve("import-test.csv").toString();
        Contact duplicate = new Contact("John Smith", "+1-555-0123", null, null);  // Same phone
        Contact newContact = new Contact("Jane Smith", "+1-555-0124", null, null);
        FileManager.saveContactsToFile(List.of(duplicate, newContact), importFile);
        
        int imported = contactManager.importContacts(importFile);
        
        assertEquals(1, imported);  // Only new contact imported
        assertEquals(2, contactManager.getContactCount());  // Original + new
    }

    @Test
    void exportContacts_Success() throws IOException {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, null);
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        
        String exportFile = tempDir.resolve("export-test.csv").toString();
        contactManager.exportContacts(exportFile);
        
        // Verify file was created and contains data
        assertTrue(FileManager.fileExists(exportFile));
        List<Contact> imported = FileManager.loadContactsFromFile(exportFile);
        assertEquals(2, imported.size());
    }

    @Test
    void searchContacts_AllFields() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", "jane@test.org", "456 Oak Ave");
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        
        // Search by name
        assertEquals(1, contactManager.searchContacts("John").size());
        
        // Search by phone
        assertEquals(1, contactManager.searchContacts("0123").size());
        
        // Search by email
        assertEquals(1, contactManager.searchContacts("example.com").size());
        
        // Search by address
        assertEquals(1, contactManager.searchContacts("Main").size());
    }

    @Test
    void searchContacts_CaseInsensitive() {
        Contact contact = new Contact("John Doe", "+1-555-0123", "John@Example.COM", "123 Main St");
        contactManager.addContact(contact);
        
        List<Contact> results = contactManager.searchContacts("john");
        assertEquals(1, results.size());
        
        results = contactManager.searchContacts("EXAMPLE");
        assertEquals(1, results.size());
    }

    @Test
    void getDuplicateContacts_NoDuplicates() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, null);
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        
        List<Contact> duplicates = contactManager.getDuplicateContacts();
        
        assertTrue(duplicates.isEmpty());
    }

    @Test
    void getContactStatistics() {
        Contact contact1 = new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St");
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, "456 Oak Ave");
        Contact contact3 = new Contact("Bob Johnson", "+1-555-0125", "bob@example.com", null);
        
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        contactManager.addContact(contact3);
        
        String stats = contactManager.getContactStatistics();
        
        assertTrue(stats.contains("Total Contacts: 3"));
        assertTrue(stats.contains("Contacts with Email: 2"));
        assertTrue(stats.contains("Contacts with Address: 2"));
    }

    @Test
    void getContactCount() {
        assertEquals(0, contactManager.getContactCount());
        
        contactManager.addContact(new Contact("John Doe", "+1-555-0123", null, null));
        assertEquals(1, contactManager.getContactCount());
        
        contactManager.addContact(new Contact("Jane Smith", "+1-555-0124", null, null));
        assertEquals(2, contactManager.getContactCount());
        
        contactManager.deleteContact("+1-555-0123");
        assertEquals(1, contactManager.getContactCount());
    }

    @Test
    void persistenceTest() {
        // Add contacts to manager
        Contact contact1 = new Contact("John Doe", "+1-555-0123", null, null);
        Contact contact2 = new Contact("Jane Smith", "+1-555-0124", null, null);
        contactManager.addContact(contact1);
        contactManager.addContact(contact2);
        
        // Create new manager with same data file
        ContactManager newManager = new ContactManager(testDataFile);
        
        // Verify contacts were loaded
        assertEquals(2, newManager.getContactCount());
        assertNotNull(newManager.findContactByPhone("+1-555-0123"));
        assertNotNull(newManager.findContactByPhone("+1-555-0124"));
    }
}