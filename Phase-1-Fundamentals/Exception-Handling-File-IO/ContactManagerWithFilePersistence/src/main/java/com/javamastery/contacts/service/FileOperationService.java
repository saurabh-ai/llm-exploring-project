package com.javamastery.contacts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javamastery.contacts.exception.DataPersistenceException;
import com.javamastery.contacts.model.Contact;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles file persistence operations with comprehensive exception handling.
 * 
 * Learning Objectives:
 * - File I/O operations (CSV, JSON, binary)
 * - Exception handling and error recovery
 * - Resource management with try-with-resources
 * - Backup and recovery strategies
 * - Logging and monitoring
 */
public class FileOperationService {
    
    private static final Logger LOGGER = Logger.getLogger(FileOperationService.class.getName());
    private static final String BACKUP_SUFFIX = ".backup";
    private static final String TEMP_SUFFIX = ".tmp";
    
    private final ObjectMapper objectMapper;
    
    public FileOperationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
    }
    
    /**
     * Saves contacts to CSV file with comprehensive error handling.
     * 
     * @param contacts the contacts to save
     * @param filePath the file path to save to
     * @throws DataPersistenceException if save operation fails
     */
    public void saveContactsToCsv(List<Contact> contacts, String filePath) throws DataPersistenceException {
        validateSaveParameters(contacts, filePath);
        
        Path path = Paths.get(filePath);
        Path tempPath = Paths.get(filePath + TEMP_SUFFIX);
        Path backupPath = Paths.get(filePath + BACKUP_SUFFIX);
        
        try {
            // Create directories if they don't exist
            createDirectories(path.getParent());
            
            // Create backup if original file exists
            createBackup(path, backupPath);
            
            // Write to temporary file first
            writeContactsToCsvFile(contacts, tempPath);
            
            // Atomic move from temp to final location
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            
            LOGGER.info(String.format("Successfully saved %d contacts to %s", contacts.size(), filePath));
            
        } catch (IOException e) {
            // Clean up temp file if it exists
            cleanupTempFile(tempPath);
            
            // Try to restore from backup
            restoreFromBackup(backupPath, path);
            
            String message = "Failed to save contacts to CSV file";
            LOGGER.log(Level.SEVERE, message, e);
            throw new DataPersistenceException(message, "save", filePath, e);
        }
    }
    
    /**
     * Loads contacts from CSV file with error recovery.
     * 
     * @param filePath the file path to load from
     * @return list of contacts
     * @throws DataPersistenceException if load operation fails
     */
    public List<Contact> loadContactsFromCsv(String filePath) throws DataPersistenceException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new DataPersistenceException("File path cannot be null or empty", "load", filePath);
        }
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            LOGGER.info("File does not exist, returning empty contact list: " + filePath);
            return new ArrayList<>();
        }
        
        try {
            return readContactsFromCsvFile(path);
        } catch (IOException | RuntimeException e) {
            // Try backup file if available
            Path backupPath = Paths.get(filePath + BACKUP_SUFFIX);
            if (Files.exists(backupPath)) {
                LOGGER.warning("Primary file corrupted, attempting backup recovery: " + filePath);
                try {
                    return readContactsFromCsvFile(backupPath);
                } catch (IOException backupException) {
                    LOGGER.log(Level.SEVERE, "Both primary and backup files failed", backupException);
                }
            }
            
            String message = "Failed to load contacts from CSV file";
            LOGGER.log(Level.SEVERE, message, e);
            throw new DataPersistenceException(message, "load", filePath, e);
        }
    }
    
    /**
     * Saves contacts to JSON file.
     * 
     * @param contacts the contacts to save
     * @param filePath the file path to save to
     * @throws DataPersistenceException if save operation fails
     */
    public void saveContactsToJson(List<Contact> contacts, String filePath) throws DataPersistenceException {
        validateSaveParameters(contacts, filePath);
        
        Path path = Paths.get(filePath);
        Path tempPath = Paths.get(filePath + TEMP_SUFFIX);
        
        try {
            createDirectories(path.getParent());
            
            // Write to temp file first
            try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                objectMapper.writeValue(writer, contacts);
            }
            
            // Atomic move
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            
            LOGGER.info(String.format("Successfully saved %d contacts to JSON: %s", contacts.size(), filePath));
            
        } catch (IOException e) {
            cleanupTempFile(tempPath);
            String message = "Failed to save contacts to JSON file";
            LOGGER.log(Level.SEVERE, message, e);
            throw new DataPersistenceException(message, "save", filePath, e);
        }
    }
    
    /**
     * Loads contacts from JSON file.
     * 
     * @param filePath the file path to load from
     * @return list of contacts
     * @throws DataPersistenceException if load operation fails
     */
    public List<Contact> loadContactsFromJson(String filePath) throws DataPersistenceException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new DataPersistenceException("File path cannot be null or empty", "load", filePath);
        }
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            LOGGER.info("JSON file does not exist, returning empty contact list: " + filePath);
            return new ArrayList<>();
        }
        
        try {
            return objectMapper.readValue(path.toFile(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Contact.class));
        } catch (IOException e) {
            String message = "Failed to load contacts from JSON file";
            LOGGER.log(Level.SEVERE, message, e);
            throw new DataPersistenceException(message, "load", filePath, e);
        }
    }
    
    /**
     * Exports contacts to multiple formats for backup.
     * 
     * @param contacts the contacts to export
     * @param baseFileName base file name without extension
     * @throws DataPersistenceException if export fails
     */
    public void exportContactsMultipleFormats(List<Contact> contacts, String baseFileName) throws DataPersistenceException {
        List<Exception> exceptions = new ArrayList<>();
        
        // Try CSV export
        try {
            saveContactsToCsv(contacts, baseFileName + ".csv");
        } catch (DataPersistenceException e) {
            exceptions.add(e);
            LOGGER.warning("CSV export failed: " + e.getMessage());
        }
        
        // Try JSON export
        try {
            saveContactsToJson(contacts, baseFileName + ".json");
        } catch (DataPersistenceException e) {
            exceptions.add(e);
            LOGGER.warning("JSON export failed: " + e.getMessage());
        }
        
        // If all exports failed, throw exception
        if (exceptions.size() == 2) {
            throw new DataPersistenceException("All export formats failed", "export", baseFileName);
        }
    }
    
    private void validateSaveParameters(List<Contact> contacts, String filePath) throws DataPersistenceException {
        if (contacts == null) {
            throw new DataPersistenceException("Contacts list cannot be null", "save", filePath);
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new DataPersistenceException("File path cannot be null or empty", "save", filePath);
        }
    }
    
    private void createDirectories(Path parent) throws IOException {
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
    
    private void createBackup(Path original, Path backup) {
        if (Files.exists(original)) {
            try {
                Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.warning("Failed to create backup: " + e.getMessage());
            }
        }
    }
    
    private void writeContactsToCsvFile(List<Contact> contacts, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            // Write header
            writer.write("id,name,phoneNumber,email,address,createdDate,lastModified");
            writer.newLine();
            
            // Write contacts
            for (Contact contact : contacts) {
                writer.write(contact.toCsvString());
                writer.newLine();
            }
        }
    }
    
    private List<Contact> readContactsFromCsvFile(Path path) throws IOException {
        List<Contact> contacts = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Contact contact = Contact.fromCsvString(line);
                        contacts.add(contact);
                    } catch (RuntimeException e) {
                        LOGGER.warning("Failed to parse CSV line: " + line + " - " + e.getMessage());
                        // Continue processing other lines
                    }
                }
            }
        }
        
        return contacts;
    }
    
    private void cleanupTempFile(Path tempPath) {
        try {
            if (Files.exists(tempPath)) {
                Files.delete(tempPath);
            }
        } catch (IOException e) {
            LOGGER.warning("Failed to cleanup temp file: " + tempPath + " - " + e.getMessage());
        }
    }
    
    private void restoreFromBackup(Path backup, Path original) {
        if (Files.exists(backup)) {
            try {
                Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Restored file from backup: " + original);
            } catch (IOException e) {
                LOGGER.warning("Failed to restore from backup: " + e.getMessage());
            }
        }
    }
}