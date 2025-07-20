package com.javamastery.contacts.service;

import com.javamastery.contacts.exception.DataPersistenceException;
import com.javamastery.contacts.exception.DuplicateContactException;
import com.javamastery.contacts.model.Contact;
import com.javamastery.contacts.util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class that manages contact operations with comprehensive CRUD functionality.
 * 
 * Learning Objectives:
 * - CRUD operations with exception handling
 * - Data validation and business rules
 * - Search and filtering capabilities
 * - Thread-safe operations
 * - Integration with file persistence
 */
public class ContactManagerService {
    
    private static final Logger LOGGER = Logger.getLogger(ContactManagerService.class.getName());
    
    private final Map<String, Contact> contacts;
    private final Map<String, String> phoneIndex; // phoneNumber -> contactId mapping
    private final FileOperationService fileService;
    private final String dataFilePath;
    
    public ContactManagerService() {
        this("contacts.csv");
    }
    
    public ContactManagerService(String dataFilePath) {
        this.contacts = new ConcurrentHashMap<>();
        this.phoneIndex = new ConcurrentHashMap<>();
        this.fileService = new FileOperationService();
        this.dataFilePath = dataFilePath;
        
        loadContactsFromFile();
    }
    
    /**
     * Adds a new contact with duplicate validation.
     * 
     * @param contact the contact to add
     * @throws DuplicateContactException if phone number already exists
     * @throws DataPersistenceException if save operation fails
     */
    public synchronized void addContact(Contact contact) throws DuplicateContactException, DataPersistenceException {
        validateContact(contact);
        
        // Check for duplicate phone number
        if (phoneIndex.containsKey(contact.getPhoneNumber())) {
            throw new DuplicateContactException(
                "Contact with this phone number already exists", 
                "phoneNumber", 
                contact.getPhoneNumber()
            );
        }
        
        // Add to collections
        contacts.put(contact.getId(), contact);
        phoneIndex.put(contact.getPhoneNumber(), contact.getId());
        
        // Persist to file
        saveContactsToFile();
        
        LOGGER.info("Added new contact: " + contact.getName() + " (" + contact.getId() + ")");
    }
    
    /**
     * Updates an existing contact.
     * 
     * @param contactId the ID of the contact to update
     * @param name the new name
     * @param phoneNumber the new phone number
     * @param email the new email
     * @param address the new address
     * @return the updated contact
     * @throws IllegalArgumentException if contact not found or phone number conflict
     * @throws DataPersistenceException if save operation fails
     */
    public synchronized Contact updateContact(String contactId, String name, String phoneNumber, 
                                            String email, String address) 
            throws IllegalArgumentException, DataPersistenceException {
        
        Contact existingContact = contacts.get(contactId);
        if (existingContact == null) {
            throw new IllegalArgumentException("Contact not found with ID: " + contactId);
        }
        
        // Check if phone number is changing and if it conflicts with another contact
        if (!existingContact.getPhoneNumber().equals(phoneNumber)) {
            String existingContactIdWithPhone = phoneIndex.get(phoneNumber);
            if (existingContactIdWithPhone != null && !existingContactIdWithPhone.equals(contactId)) {
                throw new DuplicateContactException(
                    "Phone number already belongs to another contact",
                    "phoneNumber",
                    phoneNumber
                );
            }
        }
        
        try {
            // Create updated contact
            Contact updatedContact = existingContact.withUpdatedInfo(name, phoneNumber, email, address);
            
            // Update collections
            contacts.put(contactId, updatedContact);
            
            // Update phone index if phone number changed
            if (!existingContact.getPhoneNumber().equals(phoneNumber)) {
                phoneIndex.remove(existingContact.getPhoneNumber());
                phoneIndex.put(phoneNumber, contactId);
            }
            
            // Persist to file
            saveContactsToFile();
            
            LOGGER.info("Updated contact: " + updatedContact.getName() + " (" + contactId + ")");
            return updatedContact;
            
        } catch (Exception e) {
            // Rollback changes on error
            contacts.put(contactId, existingContact);
            phoneIndex.put(existingContact.getPhoneNumber(), contactId);
            throw e;
        }
    }
    
    /**
     * Deletes a contact by ID.
     * 
     * @param contactId the ID of the contact to delete
     * @return true if deleted, false if not found
     * @throws DataPersistenceException if save operation fails
     */
    public synchronized boolean deleteContact(String contactId) throws DataPersistenceException {
        Contact contact = contacts.remove(contactId);
        if (contact != null) {
            phoneIndex.remove(contact.getPhoneNumber());
            saveContactsToFile();
            LOGGER.info("Deleted contact: " + contact.getName() + " (" + contactId + ")");
            return true;
        }
        return false;
    }
    
    /**
     * Finds a contact by ID.
     * 
     * @param contactId the contact ID to search for
     * @return the contact or null if not found
     */
    public Contact findContactById(String contactId) {
        return contacts.get(contactId);
    }
    
    /**
     * Finds a contact by phone number.
     * 
     * @param phoneNumber the phone number to search for
     * @return the contact or null if not found
     */
    public Contact findContactByPhoneNumber(String phoneNumber) {
        String normalizedPhone = ValidationUtils.normalizePhoneNumber(phoneNumber);
        
        // Try exact match first
        String contactId = phoneIndex.get(phoneNumber);
        if (contactId != null) {
            return contacts.get(contactId);
        }
        
        // Try normalized phone number match
        for (Map.Entry<String, String> entry : phoneIndex.entrySet()) {
            if (ValidationUtils.normalizePhoneNumber(entry.getKey()).equals(normalizedPhone)) {
                return contacts.get(entry.getValue());
            }
        }
        
        return null;
    }
    
    /**
     * Searches contacts by name (case-insensitive, partial match).
     * 
     * @param name the name to search for
     * @return list of matching contacts
     */
    public List<Contact> searchContactsByName(String name) {
        if (ValidationUtils.isNullOrEmpty(name)) {
            return new ArrayList<>();
        }
        
        String searchTerm = name.toLowerCase().trim();
        return contacts.values().stream()
                .filter(contact -> contact.getName().toLowerCase().contains(searchTerm))
                .sorted(Comparator.comparing(Contact::getName))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all contacts sorted by name.
     * 
     * @return list of all contacts
     */
    public List<Contact> getAllContacts() {
        return contacts.values().stream()
                .sorted(Comparator.comparing(Contact::getName))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets contacts with filtering and sorting options.
     * 
     * @param nameFilter optional name filter (partial match)
     * @param hasEmail if true, only return contacts with email addresses
     * @param sortBy sort field ("name", "phone", "created")
     * @return filtered and sorted contacts
     */
    public List<Contact> getContacts(String nameFilter, boolean hasEmail, String sortBy) {
        List<Contact> result = contacts.values().stream()
                .filter(contact -> nameFilter == null || 
                        contact.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(contact -> !hasEmail || 
                        (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()))
                .collect(Collectors.toList());
        
        // Apply sorting
        Comparator<Contact> comparator;
        switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "phone":
                comparator = Comparator.comparing(Contact::getPhoneNumber);
                break;
            case "created":
                comparator = Comparator.comparing(Contact::getCreatedDate);
                break;
            case "name":
            default:
                comparator = Comparator.comparing(Contact::getName);
                break;
        }
        
        result.sort(comparator);
        return result;
    }
    
    /**
     * Gets contact statistics.
     * 
     * @return map containing various statistics
     */
    public Map<String, Object> getContactStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Contact> allContacts = getAllContacts();
        stats.put("totalContacts", allContacts.size());
        stats.put("contactsWithEmail", allContacts.stream()
                .mapToInt(c -> c.getEmail() != null && !c.getEmail().trim().isEmpty() ? 1 : 0)
                .sum());
        stats.put("contactsWithAddress", allContacts.stream()
                .mapToInt(c -> c.getAddress() != null && !c.getAddress().trim().isEmpty() ? 1 : 0)
                .sum());
        
        if (!allContacts.isEmpty()) {
            stats.put("oldestContact", allContacts.stream()
                    .min(Comparator.comparing(Contact::getCreatedDate))
                    .get().getCreatedDate());
            stats.put("newestContact", allContacts.stream()
                    .max(Comparator.comparing(Contact::getCreatedDate))
                    .get().getCreatedDate());
        }
        
        return stats;
    }
    
    /**
     * Exports contacts to multiple file formats.
     * 
     * @param baseFileName base file name without extension
     * @throws DataPersistenceException if export fails
     */
    public void exportContacts(String baseFileName) throws DataPersistenceException {
        List<Contact> allContacts = getAllContacts();
        fileService.exportContactsMultipleFormats(allContacts, baseFileName);
        LOGGER.info("Exported " + allContacts.size() + " contacts to " + baseFileName);
    }
    
    /**
     * Imports contacts from a file, handling duplicates gracefully.
     * 
     * @param filePath the file path to import from
     * @param skipDuplicates if true, skip duplicates; if false, throw exception on duplicates
     * @return number of contacts imported
     * @throws DataPersistenceException if import fails
     */
    public synchronized int importContacts(String filePath, boolean skipDuplicates) throws DataPersistenceException {
        List<Contact> importedContacts;
        
        if (filePath.toLowerCase().endsWith(".json")) {
            importedContacts = fileService.loadContactsFromJson(filePath);
        } else {
            importedContacts = fileService.loadContactsFromCsv(filePath);
        }
        
        int importedCount = 0;
        List<String> skippedContacts = new ArrayList<>();
        
        for (Contact contact : importedContacts) {
            try {
                addContact(contact);
                importedCount++;
            } catch (DuplicateContactException e) {
                if (skipDuplicates) {
                    skippedContacts.add(contact.getName() + " (" + contact.getPhoneNumber() + ")");
                } else {
                    throw new DataPersistenceException("Import failed due to duplicate contact: " + 
                            contact.getName(), "import", filePath, e);
                }
            }
        }
        
        if (!skippedContacts.isEmpty()) {
            LOGGER.info("Skipped " + skippedContacts.size() + " duplicate contacts during import");
        }
        
        return importedCount;
    }
    
    private void validateContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }
        // Additional validation is handled in Contact constructor
    }
    
    private void loadContactsFromFile() {
        try {
            List<Contact> loadedContacts = fileService.loadContactsFromCsv(dataFilePath);
            for (Contact contact : loadedContacts) {
                contacts.put(contact.getId(), contact);
                phoneIndex.put(contact.getPhoneNumber(), contact.getId());
            }
            LOGGER.info("Loaded " + loadedContacts.size() + " contacts from " + dataFilePath);
        } catch (DataPersistenceException e) {
            LOGGER.warning("Failed to load contacts from file: " + e.getMessage());
            // Continue with empty contact list
        }
    }
    
    private void saveContactsToFile() throws DataPersistenceException {
        List<Contact> allContacts = new ArrayList<>(contacts.values());
        fileService.saveContactsToCsv(allContacts, dataFilePath);
    }
}