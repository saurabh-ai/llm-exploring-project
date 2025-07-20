package com.javamastery.contacts;

import com.javamastery.contacts.exception.DataPersistenceException;
import com.javamastery.contacts.exception.DuplicateContactException;
import com.javamastery.contacts.model.Contact;
import com.javamastery.contacts.service.ContactManagerService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class demonstrating Contact Manager with File Persistence.
 * 
 * Learning Objectives:
 * - Console application development
 * - Exception handling in user interactions
 * - Menu-driven application design
 * - Integration of all components
 */
public class ContactManagerApp {
    
    private static final Logger LOGGER = Logger.getLogger(ContactManagerApp.class.getName());
    
    private final ContactManagerService contactManager;
    private final Scanner scanner;
    private boolean running = true;
    
    public ContactManagerApp() {
        this.contactManager = new ContactManagerService();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("=== Contact Manager with File Persistence ===");
        System.out.println("Demonstrating Exception Handling and File I/O");
        System.out.println();
        
        ContactManagerApp app = new ContactManagerApp();
        app.run();
    }
    
    public void run() {
        while (running) {
            try {
                showMainMenu();
                handleUserChoice();
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Unexpected application error", e);
            }
        }
        
        System.out.println("Thank you for using Contact Manager!");
        scanner.close();
    }
    
    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add Contact");
        System.out.println("2. View All Contacts");
        System.out.println("3. Search Contact");
        System.out.println("4. Update Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Import Contacts");
        System.out.println("7. Export Contacts");
        System.out.println("8. View Statistics");
        System.out.println("9. Advanced Search");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice (0-9): ");
    }
    
    private void handleUserChoice() {
        try {
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    addContact();
                    break;
                case "2":
                    viewAllContacts();
                    break;
                case "3":
                    searchContact();
                    break;
                case "4":
                    updateContact();
                    break;
                case "5":
                    deleteContact();
                    break;
                case "6":
                    importContacts();
                    break;
                case "7":
                    exportContacts();
                    break;
                case "8":
                    viewStatistics();
                    break;
                case "9":
                    advancedSearch();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 9.");
            }
        } catch (Exception e) {
            System.err.println("Error processing your request: " + e.getMessage());
            LOGGER.log(Level.WARNING, "Error in user choice handling", e);
        }
    }
    
    private void addContact() {
        System.out.println("\n=== ADD NEW CONTACT ===");
        
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Phone Number: ");
            String phoneNumber = scanner.nextLine().trim();
            
            System.out.print("Email (optional): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = null;
            
            System.out.print("Address (optional): ");
            String address = scanner.nextLine().trim();
            if (address.isEmpty()) address = null;
            
            Contact contact = new Contact(name, phoneNumber, email, address);
            contactManager.addContact(contact);
            
            System.out.println("✅ Contact added successfully!");
            System.out.println("Contact ID: " + contact.getId());
            
        } catch (DuplicateContactException e) {
            System.err.println("❌ " + e.getMessage());
            System.out.println("A contact with this phone number already exists.");
        } catch (DataPersistenceException e) {
            System.err.println("❌ Failed to save contact: " + e.getMessage());
            System.out.println("Contact was created but could not be saved to file.");
        } catch (RuntimeException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
        }
    }
    
    private void viewAllContacts() {
        System.out.println("\n=== ALL CONTACTS ===");
        
        List<Contact> contacts = contactManager.getAllContacts();
        
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }
        
        displayContacts(contacts);
    }
    
    private void searchContact() {
        System.out.println("\n=== SEARCH CONTACT ===");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by Phone Number");
        System.out.println("3. Search by Contact ID");
        System.out.print("Choose search type (1-3): ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                searchByName();
                break;
            case "2":
                searchByPhone();
                break;
            case "3":
                searchById();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private void searchByName() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();
        
        List<Contact> contacts = contactManager.searchContactsByName(name);
        
        if (contacts.isEmpty()) {
            System.out.println("No contacts found matching: " + name);
        } else {
            System.out.println("\nFound " + contacts.size() + " contact(s):");
            displayContacts(contacts);
        }
    }
    
    private void searchByPhone() {
        System.out.print("Enter phone number to search: ");
        String phone = scanner.nextLine().trim();
        
        Contact contact = contactManager.findContactByPhoneNumber(phone);
        
        if (contact == null) {
            System.out.println("No contact found with phone number: " + phone);
        } else {
            System.out.println("\nContact found:");
            displayContact(contact);
        }
    }
    
    private void searchById() {
        System.out.print("Enter contact ID: ");
        String id = scanner.nextLine().trim();
        
        Contact contact = contactManager.findContactById(id);
        
        if (contact == null) {
            System.out.println("No contact found with ID: " + id);
        } else {
            System.out.println("\nContact found:");
            displayContact(contact);
        }
    }
    
    private void updateContact() {
        System.out.println("\n=== UPDATE CONTACT ===");
        System.out.print("Enter Contact ID to update: ");
        String contactId = scanner.nextLine().trim();
        
        Contact existingContact = contactManager.findContactById(contactId);
        if (existingContact == null) {
            System.out.println("Contact not found with ID: " + contactId);
            return;
        }
        
        System.out.println("\nCurrent contact details:");
        displayContact(existingContact);
        
        try {
            System.out.print("New Name (current: " + existingContact.getName() + "): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = existingContact.getName();
            
            System.out.print("New Phone (current: " + existingContact.getPhoneNumber() + "): ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) phone = existingContact.getPhoneNumber();
            
            System.out.print("New Email (current: " + (existingContact.getEmail() != null ? existingContact.getEmail() : "none") + "): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = existingContact.getEmail();
            
            System.out.print("New Address (current: " + (existingContact.getAddress() != null ? existingContact.getAddress() : "none") + "): ");
            String address = scanner.nextLine().trim();
            if (address.isEmpty()) address = existingContact.getAddress();
            
            Contact updatedContact = contactManager.updateContact(contactId, name, phone, email, address);
            
            System.out.println("✅ Contact updated successfully!");
            System.out.println("\nUpdated contact:");
            displayContact(updatedContact);
            
        } catch (DuplicateContactException e) {
            System.err.println("❌ " + e.getMessage());
        } catch (DataPersistenceException e) {
            System.err.println("❌ Failed to save changes: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("❌ Validation error: " + e.getMessage());
        }
    }
    
    private void deleteContact() {
        System.out.println("\n=== DELETE CONTACT ===");
        System.out.print("Enter Contact ID to delete: ");
        String contactId = scanner.nextLine().trim();
        
        Contact contact = contactManager.findContactById(contactId);
        if (contact == null) {
            System.out.println("Contact not found with ID: " + contactId);
            return;
        }
        
        System.out.println("\nContact to be deleted:");
        displayContact(contact);
        
        System.out.print("\nAre you sure you want to delete this contact? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            try {
                boolean deleted = contactManager.deleteContact(contactId);
                if (deleted) {
                    System.out.println("✅ Contact deleted successfully!");
                } else {
                    System.out.println("❌ Failed to delete contact.");
                }
            } catch (DataPersistenceException e) {
                System.err.println("❌ Failed to save changes: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    private void importContacts() {
        System.out.println("\n=== IMPORT CONTACTS ===");
        System.out.print("Enter file path to import: ");
        String filePath = scanner.nextLine().trim();
        
        System.out.print("Skip duplicate contacts? (yes/no): ");
        String skipDuplicates = scanner.nextLine().trim().toLowerCase();
        boolean skip = skipDuplicates.equals("yes") || skipDuplicates.equals("y");
        
        try {
            int importedCount = contactManager.importContacts(filePath, skip);
            System.out.println("✅ Successfully imported " + importedCount + " contacts!");
        } catch (DataPersistenceException e) {
            System.err.println("❌ Import failed: " + e.getMessage());
        }
    }
    
    private void exportContacts() {
        System.out.println("\n=== EXPORT CONTACTS ===");
        System.out.print("Enter base file name (without extension): ");
        String baseFileName = scanner.nextLine().trim();
        
        try {
            contactManager.exportContacts(baseFileName);
            System.out.println("✅ Contacts exported successfully!");
            System.out.println("Files created: " + baseFileName + ".csv, " + baseFileName + ".json");
        } catch (DataPersistenceException e) {
            System.err.println("❌ Export failed: " + e.getMessage());
        }
    }
    
    private void viewStatistics() {
        System.out.println("\n=== CONTACT STATISTICS ===");
        
        Map<String, Object> stats = contactManager.getContactStatistics();
        
        System.out.println("Total Contacts: " + stats.get("totalContacts"));
        System.out.println("Contacts with Email: " + stats.get("contactsWithEmail"));
        System.out.println("Contacts with Address: " + stats.get("contactsWithAddress"));
        
        if (stats.containsKey("oldestContact")) {
            System.out.println("Oldest Contact: " + stats.get("oldestContact"));
            System.out.println("Newest Contact: " + stats.get("newestContact"));
        }
    }
    
    private void advancedSearch() {
        System.out.println("\n=== ADVANCED SEARCH ===");
        
        System.out.print("Name filter (optional): ");
        String nameFilter = scanner.nextLine().trim();
        if (nameFilter.isEmpty()) nameFilter = null;
        
        System.out.print("Show only contacts with email? (yes/no): ");
        String emailFilter = scanner.nextLine().trim().toLowerCase();
        boolean hasEmail = emailFilter.equals("yes") || emailFilter.equals("y");
        
        System.out.print("Sort by (name/phone/created): ");
        String sortBy = scanner.nextLine().trim();
        if (sortBy.isEmpty()) sortBy = "name";
        
        List<Contact> contacts = contactManager.getContacts(nameFilter, hasEmail, sortBy);
        
        if (contacts.isEmpty()) {
            System.out.println("No contacts found matching the criteria.");
        } else {
            System.out.println("\nFound " + contacts.size() + " contact(s):");
            displayContacts(contacts);
        }
    }
    
    private void displayContacts(List<Contact> contacts) {
        System.out.println("\n" + "=".repeat(80));
        for (Contact contact : contacts) {
            displayContact(contact);
            System.out.println("-".repeat(80));
        }
    }
    
    private void displayContact(Contact contact) {
        System.out.printf("ID: %s | Name: %s | Phone: %s%n", 
                contact.getId(), contact.getName(), contact.getPhoneNumber());
        System.out.printf("Email: %s | Address: %s%n", 
                contact.getEmail() != null ? contact.getEmail() : "N/A",
                contact.getAddress() != null ? contact.getAddress() : "N/A");
        System.out.printf("Created: %s | Modified: %s%n", 
                contact.getCreatedDate(), contact.getLastModified());
    }
}