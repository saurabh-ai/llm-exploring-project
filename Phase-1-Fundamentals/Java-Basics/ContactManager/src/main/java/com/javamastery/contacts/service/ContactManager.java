package com.javamastery.contacts.service;

import com.javamastery.contacts.model.Contact;
import com.javamastery.contacts.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that manages contact operations and business logic.
 */
public class ContactManager {
    
    private List<Contact> contacts;
    private final String dataFile;
    
    /**
     * Creates a new ContactManager with the default data file.
     */
    public ContactManager() {
        this(Constants.DEFAULT_CONTACTS_FILE);
    }
    
    /**
     * Creates a new ContactManager with the specified data file.
     *
     * @param dataFile the path to the data file
     */
    public ContactManager(String dataFile) {
        this.dataFile = dataFile;
        this.contacts = new ArrayList<>();
        loadContacts();
    }
    
    /**
     * Adds a new contact.
     *
     * @param contact the contact to add
     * @throws IllegalArgumentException if contact is null or phone number already exists
     */
    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }
        
        // Check for duplicate phone number
        if (findContactByPhone(contact.getPhoneNumber()) != null) {
            throw new IllegalArgumentException(Constants.ERROR_DUPLICATE_PHONE);
        }
        
        contacts.add(contact);
        saveContacts();
    }
    
    /**
     * Finds contacts by name (case-insensitive partial match).
     *
     * @param name the name to search for
     * @return list of contacts matching the name
     */
    public List<Contact> findContactsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchName = name.trim().toLowerCase();
        return contacts.stream()
                .filter(contact -> contact.getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds a contact by phone number (exact match).
     *
     * @param phone the phone number to search for
     * @return the contact with the specified phone number, or null if not found
     */
    public Contact findContactByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        
        // Normalize phone for comparison
        String normalizedPhone = phone.trim().replaceAll("[()\\s]", "");
        
        return contacts.stream()
                .filter(contact -> contact.getPhoneNumber().equals(normalizedPhone))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Updates an existing contact.
     *
     * @param phone the phone number of the contact to update
     * @param updatedContact the updated contact information
     * @return true if contact was updated, false if not found
     * @throws IllegalArgumentException if the new phone number already exists (unless it's the same contact)
     */
    public boolean updateContact(String phone, Contact updatedContact) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (updatedContact == null) {
            throw new IllegalArgumentException("Updated contact cannot be null");
        }
        
        Contact existingContact = findContactByPhone(phone);
        if (existingContact == null) {
            return false;
        }
        
        // Check if new phone number conflicts with another contact
        if (!existingContact.getPhoneNumber().equals(updatedContact.getPhoneNumber())) {
            Contact conflictContact = findContactByPhone(updatedContact.getPhoneNumber());
            if (conflictContact != null) {
                throw new IllegalArgumentException(Constants.ERROR_DUPLICATE_PHONE);
            }
        }
        
        // Update the contact
        existingContact.setName(updatedContact.getName());
        existingContact.setPhoneNumber(updatedContact.getPhoneNumber());
        existingContact.setEmail(updatedContact.getEmail());
        existingContact.setAddress(updatedContact.getAddress());
        
        saveContacts();
        return true;
    }
    
    /**
     * Deletes a contact by phone number.
     *
     * @param phone the phone number of the contact to delete
     * @return true if contact was deleted, false if not found
     */
    public boolean deleteContact(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        Contact contact = findContactByPhone(phone);
        if (contact != null) {
            contacts.remove(contact);
            saveContacts();
            return true;
        }
        return false;
    }
    
    /**
     * Gets all contacts.
     *
     * @return list of all contacts (defensive copy)
     */
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
    
    /**
     * Imports contacts from a file.
     *
     * @param filename the file to import from
     * @return number of contacts imported
     * @throws IOException if file operation fails
     */
    public int importContacts(String filename) throws IOException {
        List<Contact> importedContacts = FileManager.loadContactsFromFile(filename);
        int imported = 0;
        
        for (Contact contact : importedContacts) {
            try {
                // Check for duplicate phone numbers
                if (findContactByPhone(contact.getPhoneNumber()) == null) {
                    contacts.add(contact);
                    imported++;
                } else {
                    System.out.println("Skipping duplicate contact: " + contact.getName() + " (" + contact.getPhoneNumber() + ")");
                }
            } catch (Exception e) {
                System.err.println("Error importing contact: " + e.getMessage());
            }
        }
        
        if (imported > 0) {
            saveContacts();
        }
        
        return imported;
    }
    
    /**
     * Exports contacts to a file.
     *
     * @param filename the file to export to
     * @throws IOException if file operation fails
     */
    public void exportContacts(String filename) throws IOException {
        FileManager.saveContactsToFile(contacts, filename);
    }
    
    /**
     * Gets contacts with duplicate phone numbers (should not occur with validation).
     *
     * @return list of contacts with duplicate phone numbers
     */
    public List<Contact> getDuplicateContacts() {
        List<Contact> duplicates = new ArrayList<>();
        
        for (int i = 0; i < contacts.size(); i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                if (contacts.get(i).getPhoneNumber().equals(contacts.get(j).getPhoneNumber())) {
                    if (!duplicates.contains(contacts.get(i))) {
                        duplicates.add(contacts.get(i));
                    }
                    if (!duplicates.contains(contacts.get(j))) {
                        duplicates.add(contacts.get(j));
                    }
                }
            }
        }
        
        return duplicates;
    }
    
    /**
     * Gets contact statistics.
     *
     * @return formatted string with contact statistics
     */
    public String getContactStatistics() {
        int totalContacts = contacts.size();
        long contactsWithEmail = contacts.stream().filter(c -> c.getEmail() != null && !c.getEmail().isEmpty()).count();
        long contactsWithAddress = contacts.stream().filter(c -> c.getAddress() != null && !c.getAddress().isEmpty()).count();
        
        return String.format("""
                Contact Statistics:
                - Total Contacts: %d
                - Contacts with Email: %d (%.1f%%)
                - Contacts with Address: %d (%.1f%%)
                - File Size: %d bytes
                """,
                totalContacts,
                contactsWithEmail,
                totalContacts > 0 ? (contactsWithEmail * 100.0 / totalContacts) : 0,
                contactsWithAddress,
                totalContacts > 0 ? (contactsWithAddress * 100.0 / totalContacts) : 0,
                FileManager.getFileSize(dataFile)
        );
    }
    
    /**
     * Searches contacts by partial match in any field.
     *
     * @param searchTerm the term to search for
     * @return list of contacts matching the search term
     */
    public List<Contact> searchContacts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String term = searchTerm.trim().toLowerCase();
        return contacts.stream()
                .filter(contact -> 
                    contact.getName().toLowerCase().contains(term) ||
                    contact.getPhoneNumber().contains(term) ||
                    (contact.getEmail() != null && contact.getEmail().toLowerCase().contains(term)) ||
                    (contact.getAddress() != null && contact.getAddress().toLowerCase().contains(term))
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the number of contacts.
     *
     * @return the number of contacts
     */
    public int getContactCount() {
        return contacts.size();
    }
    
    /**
     * Loads contacts from the data file.
     */
    private void loadContacts() {
        try {
            FileManager.ensureDataDirectoryExists();
            if (FileManager.fileExists(dataFile)) {
                contacts = FileManager.loadContactsFromFile(dataFile);
                System.out.println(Constants.SUCCESS_FILE_LOADED + " (" + contacts.size() + " contacts)");
            } else {
                contacts = new ArrayList<>();
                System.out.println("No existing data file found. Starting with empty contact list.");
            }
        } catch (IOException e) {
            System.err.println("Error loading contacts: " + e.getMessage());
            contacts = new ArrayList<>();
        }
    }
    
    /**
     * Saves contacts to the data file.
     */
    private void saveContacts() {
        try {
            // Create automatic backup before saving
            if (FileManager.fileExists(dataFile)) {
                FileManager.createAutoBackup(dataFile);
            }
            
            FileManager.saveContactsToFile(contacts, dataFile);
        } catch (IOException e) {
            System.err.println("Error saving contacts: " + e.getMessage());
        }
    }
}