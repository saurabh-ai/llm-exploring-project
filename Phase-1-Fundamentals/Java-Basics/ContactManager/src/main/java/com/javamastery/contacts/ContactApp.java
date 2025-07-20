package com.javamastery.contacts;

import com.javamastery.contacts.model.Contact;
import com.javamastery.contacts.service.ContactManager;
import com.javamastery.contacts.util.Constants;
import com.javamastery.contacts.util.InputValidator;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Contact Manager application with menu-driven interface.
 */
public class ContactApp {
    
    private final ContactManager contactManager;
    private final Scanner scanner;
    
    public ContactApp() {
        this.contactManager = new ContactManager();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ContactApp app = new ContactApp();
        app.run();
    }
    
    /**
     * Runs the main application loop.
     */
    public void run() {
        System.out.println(Constants.MENU_SEPARATOR);
        System.out.println("    " + Constants.MENU_TITLE);
        System.out.println(Constants.MENU_SEPARATOR);
        System.out.println();
        
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = readInput("Enter your choice: ");
            
            if (InputValidator.isValidMenuChoice(choice, 1, 9)) {
                running = handleMenuChoice(Integer.parseInt(choice.trim()));
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
            
            if (running) {
                pressEnterToContinue();
            }
        }
        
        scanner.close();
        System.out.println("Thank you for using Contact Manager!");
    }
    
    /**
     * Displays the main menu.
     */
    private void displayMenu() {
        System.out.println("\n" + Constants.MENU_SEPARATOR);
        System.out.println("CONTACT MANAGER MENU");
        System.out.println(Constants.MENU_SEPARATOR);
        System.out.println("1. Add New Contact");
        System.out.println("2. Search Contacts");
        System.out.println("3. List All Contacts");
        System.out.println("4. Update Contact");
        System.out.println("5. Delete Contact");
        System.out.println("6. Import Contacts");
        System.out.println("7. Export Contacts");
        System.out.println("8. Show Statistics");
        System.out.println("9. Exit");
        System.out.println(Constants.MENU_SEPARATOR);
    }
    
    /**
     * Handles the selected menu choice.
     *
     * @param choice the menu choice
     * @return true to continue, false to exit
     */
    private boolean handleMenuChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> addNewContact();
                case 2 -> searchContacts();
                case 3 -> listAllContacts();
                case 4 -> updateContact();
                case 5 -> deleteContact();
                case 6 -> importContacts();
                case 7 -> exportContacts();
                case 8 -> showStatistics();
                case 9 -> {
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return true;
    }
    
    /**
     * Adds a new contact.
     */
    private void addNewContact() {
        System.out.println("\n--- Add New Contact ---");
        
        String name = readValidatedInput("Enter name: ", InputValidator::isValidName, Constants.ERROR_INVALID_NAME);
        String phone = readValidatedInput("Enter phone number: ", InputValidator::isValidPhone, Constants.ERROR_INVALID_PHONE);
        
        String email = readInput("Enter email (optional): ");
        if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
            System.out.println(Constants.ERROR_INVALID_EMAIL);
            email = readInput("Enter valid email (or press Enter to skip): ");
            if (!email.isEmpty() && !InputValidator.isValidEmail(email)) {
                email = "";
                System.out.println("Skipping email due to invalid format.");
            }
        }
        
        String address = readInput("Enter address (optional): ");
        
        try {
            Contact contact = new Contact(name, phone, 
                    email.isEmpty() ? null : email, 
                    address.isEmpty() ? null : address);
            contactManager.addContact(contact);
            System.out.println(Constants.SUCCESS_CONTACT_ADDED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }
    
    /**
     * Searches for contacts.
     */
    private void searchContacts() {
        System.out.println("\n--- Search Contacts ---");
        
        String searchTerm = readInput("Enter search term (name, phone, email, or address): ");
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }
        
        List<Contact> results = contactManager.searchContacts(searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("No contacts found matching: " + searchTerm);
        } else {
            System.out.println("\nFound " + results.size() + " contact(s):");
            displayContactList(results);
        }
    }
    
    /**
     * Lists all contacts.
     */
    private void listAllContacts() {
        System.out.println("\n--- All Contacts ---");
        
        List<Contact> contacts = contactManager.getAllContacts();
        
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
        } else {
            System.out.println("Total contacts: " + contacts.size());
            displayContactList(contacts);
        }
    }
    
    /**
     * Updates an existing contact.
     */
    private void updateContact() {
        System.out.println("\n--- Update Contact ---");
        
        String phone = readInput("Enter phone number of contact to update: ");
        if (phone.isEmpty()) {
            System.out.println("Phone number cannot be empty.");
            return;
        }
        
        Contact existingContact = contactManager.findContactByPhone(phone);
        if (existingContact == null) {
            System.out.println(Constants.ERROR_CONTACT_NOT_FOUND);
            return;
        }
        
        System.out.println("\nCurrent contact details:");
        displayContact(existingContact);
        
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        String newName = readInput("Name [" + existingContact.getName() + "]: ");
        if (newName.isEmpty()) {
            newName = existingContact.getName();
        } else if (!InputValidator.isValidName(newName)) {
            System.out.println(Constants.ERROR_INVALID_NAME);
            return;
        }
        
        String newPhone = readInput("Phone [" + existingContact.getPhoneNumber() + "]: ");
        if (newPhone.isEmpty()) {
            newPhone = existingContact.getPhoneNumber();
        } else if (!InputValidator.isValidPhone(newPhone)) {
            System.out.println(Constants.ERROR_INVALID_PHONE);
            return;
        }
        
        String newEmail = readInput("Email [" + (existingContact.getEmail() != null ? existingContact.getEmail() : "none") + "]: ");
        if (newEmail.isEmpty()) {
            newEmail = existingContact.getEmail();
        } else if (!InputValidator.isValidEmail(newEmail)) {
            System.out.println(Constants.ERROR_INVALID_EMAIL);
            return;
        }
        
        String newAddress = readInput("Address [" + (existingContact.getAddress() != null ? existingContact.getAddress() : "none") + "]: ");
        if (newAddress.isEmpty()) {
            newAddress = existingContact.getAddress();
        }
        
        try {
            Contact updatedContact = new Contact(newName, newPhone, 
                    newEmail != null && !newEmail.equals("none") ? newEmail : null,
                    newAddress != null && !newAddress.equals("none") ? newAddress : null);
            
            if (contactManager.updateContact(phone, updatedContact)) {
                System.out.println(Constants.SUCCESS_CONTACT_UPDATED);
            } else {
                System.out.println(Constants.ERROR_CONTACT_NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error updating contact: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a contact.
     */
    private void deleteContact() {
        System.out.println("\n--- Delete Contact ---");
        
        String phone = readInput("Enter phone number of contact to delete: ");
        if (phone.isEmpty()) {
            System.out.println("Phone number cannot be empty.");
            return;
        }
        
        Contact contact = contactManager.findContactByPhone(phone);
        if (contact == null) {
            System.out.println(Constants.ERROR_CONTACT_NOT_FOUND);
            return;
        }
        
        System.out.println("\nContact to delete:");
        displayContact(contact);
        
        String confirmation = readInput("\nAre you sure you want to delete this contact? (yes/no): ");
        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            if (contactManager.deleteContact(phone)) {
                System.out.println(Constants.SUCCESS_CONTACT_DELETED);
            } else {
                System.out.println(Constants.ERROR_CONTACT_NOT_FOUND);
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }
    
    /**
     * Imports contacts from a file.
     */
    private void importContacts() {
        System.out.println("\n--- Import Contacts ---");
        
        String filename = readInput("Enter filename to import from: ");
        if (filename.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }
        
        try {
            int imported = contactManager.importContacts(filename);
            System.out.println("Successfully imported " + imported + " contacts.");
        } catch (IOException e) {
            System.err.println("Error importing contacts: " + e.getMessage());
        }
    }
    
    /**
     * Exports contacts to a file.
     */
    private void exportContacts() {
        System.out.println("\n--- Export Contacts ---");
        
        String filename = readInput("Enter filename to export to: ");
        if (filename.isEmpty()) {
            System.out.println("Filename cannot be empty.");
            return;
        }
        
        try {
            contactManager.exportContacts(filename);
            System.out.println("Successfully exported " + contactManager.getContactCount() + " contacts to " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting contacts: " + e.getMessage());
        }
    }
    
    /**
     * Shows contact statistics.
     */
    private void showStatistics() {
        System.out.println("\n--- Contact Statistics ---");
        System.out.println(contactManager.getContactStatistics());
        
        List<Contact> duplicates = contactManager.getDuplicateContacts();
        if (!duplicates.isEmpty()) {
            System.out.println("Warning: Found " + duplicates.size() + " contacts with duplicate phone numbers!");
        }
    }
    
    /**
     * Displays a list of contacts.
     *
     * @param contacts the contacts to display
     */
    private void displayContactList(List<Contact> contacts) {
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println("\n" + (i + 1) + ". ");
            displayContact(contacts.get(i));
        }
    }
    
    /**
     * Displays a single contact.
     *
     * @param contact the contact to display
     */
    private void displayContact(Contact contact) {
        System.out.println(contact.toDisplayString());
    }
    
    /**
     * Reads input from the user.
     *
     * @param prompt the prompt to display
     * @return user input
     */
    private String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Reads and validates input from the user.
     *
     * @param prompt the prompt to display
     * @param validator the validation function
     * @param errorMessage the error message to display for invalid input
     * @return valid user input
     */
    private String readValidatedInput(String prompt, java.util.function.Predicate<String> validator, String errorMessage) {
        String input;
        do {
            input = readInput(prompt);
            if (!validator.test(input)) {
                System.out.println(errorMessage);
            }
        } while (!validator.test(input));
        return input;
    }
    
    /**
     * Waits for the user to press Enter.
     */
    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}