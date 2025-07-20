package com.javamastery.contacts.service;

import com.javamastery.contacts.exception.DataPersistenceException;
import com.javamastery.contacts.exception.DuplicateContactException;
import com.javamastery.contacts.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContactManagerService.
 */
class ContactManagerServiceTest {
    
    @TempDir
    Path tempDir;
    
    private ContactManagerService contactManager;
    
    @BeforeEach
    void setUp() {
        String testDataFile = tempDir.resolve("test_contacts.csv").toString();
        contactManager = new ContactManagerService(testDataFile);
    }
    
    @Test
    void testAddContact() throws Exception {
        Contact contact = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        
        contactManager.addContact(contact);
        
        Contact found = contactManager.findContactById(contact.getId());
        assertEquals(contact, found);
    }
    
    @Test
    void testAddDuplicateContact() throws Exception {
        Contact contact1 = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        Contact contact2 = new Contact("Jane Smith", "123-456-7890", "jane@example.com", "456 Oak Ave");
        
        contactManager.addContact(contact1);
        
        assertThrows(DuplicateContactException.class, () -> contactManager.addContact(contact2));
    }
    
    @Test
    void testUpdateContact() throws Exception {
        Contact original = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        contactManager.addContact(original);
        
        Contact updated = contactManager.updateContact(
                original.getId(), 
                "John Smith", 
                "098-765-4321", 
                "johnsmith@example.com", 
                "456 Oak Ave"
        );
        
        assertEquals("John Smith", updated.getName());
        assertEquals("098-765-4321", updated.getPhoneNumber());
        assertEquals("johnsmith@example.com", updated.getEmail());
        assertEquals("456 Oak Ave", updated.getAddress());
    }
    
    @Test
    void testDeleteContact() throws Exception {
        Contact contact = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        contactManager.addContact(contact);
        
        boolean deleted = contactManager.deleteContact(contact.getId());
        assertTrue(deleted);
        
        Contact found = contactManager.findContactById(contact.getId());
        assertNull(found);
    }
    
    @Test
    void testSearchContactsByName() throws Exception {
        contactManager.addContact(new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St"));
        contactManager.addContact(new Contact("Jane Doe", "098-765-4321", "jane@example.com", "456 Oak Ave"));
        contactManager.addContact(new Contact("Bob Smith", "555-123-4567", "bob@example.com", "789 Pine St"));
        
        List<Contact> results = contactManager.searchContactsByName("Doe");
        assertEquals(2, results.size());
        
        List<Contact> johnResults = contactManager.searchContactsByName("John");
        assertEquals(1, johnResults.size());
        assertEquals("John Doe", johnResults.get(0).getName());
    }
    
    @Test
    void testFindContactByPhoneNumber() throws Exception {
        Contact contact = new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St");
        contactManager.addContact(contact);
        
        Contact found = contactManager.findContactByPhoneNumber("123-456-7890");
        assertEquals(contact, found);
        
        Contact notFound = contactManager.findContactByPhoneNumber("999-999-9999");
        assertNull(notFound);
    }
    
    @Test
    void testGetAllContacts() throws Exception {
        contactManager.addContact(new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St"));
        contactManager.addContact(new Contact("Jane Smith", "098-765-4321", "jane@example.com", "456 Oak Ave"));
        
        List<Contact> allContacts = contactManager.getAllContacts();
        assertEquals(2, allContacts.size());
    }
    
    @Test
    void testGetContactsWithFilters() throws Exception {
        contactManager.addContact(new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St"));
        contactManager.addContact(new Contact("Jane Smith", "098-765-4321", null, "456 Oak Ave"));
        contactManager.addContact(new Contact("Bob Johnson", "555-123-4567", "bob@example.com", "789 Pine St"));
        
        // Filter by name
        List<Contact> johnsons = contactManager.getContacts("Johnson", false, "name");
        assertEquals(1, johnsons.size());
        assertEquals("Bob Johnson", johnsons.get(0).getName());
        
        // Filter by having email
        List<Contact> withEmail = contactManager.getContacts(null, true, "name");
        assertEquals(2, withEmail.size());
    }
    
    @Test
    void testContactStatistics() throws Exception {
        contactManager.addContact(new Contact("John Doe", "123-456-7890", "john@example.com", "123 Main St"));
        contactManager.addContact(new Contact("Jane Smith", "098-765-4321", null, "456 Oak Ave"));
        
        Map<String, Object> stats = contactManager.getContactStatistics();
        
        assertEquals(2, stats.get("totalContacts"));
        assertEquals(1, stats.get("contactsWithEmail"));
        assertEquals(2, stats.get("contactsWithAddress"));
        assertNotNull(stats.get("oldestContact"));
        assertNotNull(stats.get("newestContact"));
    }
}