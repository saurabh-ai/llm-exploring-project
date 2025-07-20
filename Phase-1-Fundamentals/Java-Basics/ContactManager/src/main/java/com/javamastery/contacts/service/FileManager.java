package com.javamastery.contacts.service;

import com.javamastery.contacts.model.Contact;
import com.javamastery.contacts.util.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file operations for contact data persistence.
 */
public class FileManager {
    
    /**
     * Saves contacts to a CSV file.
     *
     * @param contacts the list of contacts to save
     * @param filename the filename to save to
     * @throws IOException if file operation fails
     */
    public static void saveContactsToFile(List<Contact> contacts, String filename) throws IOException {
        if (contacts == null) {
            throw new IllegalArgumentException("Contacts list cannot be null");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        // Create directory if it doesn't exist
        Path filePath = Paths.get(filename);
        Path parentDir = filePath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Write header
            writer.write(Constants.CSV_HEADER);
            writer.newLine();
            
            // Write contacts
            for (Contact contact : contacts) {
                writer.write(contact.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException(Constants.ERROR_FILE_WRITE + filename, e);
        }
    }
    
    /**
     * Loads contacts from a CSV file.
     *
     * @param filename the filename to load from
     * @return list of contacts loaded from file
     * @throws IOException if file operation fails
     */
    public static List<Contact> loadContactsFromFile(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException(Constants.ERROR_FILE_NOT_FOUND + filename);
        }
        
        List<Contact> contacts = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line = reader.readLine(); // Skip header
            if (line != null && !line.equals(Constants.CSV_HEADER)) {
                // If first line is not header, treat it as data
                try {
                    contacts.add(Contact.fromCsvString(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping invalid line: " + line);
                }
            }
            
            // Read data lines
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        contacts.add(Contact.fromCsvString(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: Skipping invalid line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException(Constants.ERROR_FILE_READ + filename, e);
        }
        
        return contacts;
    }
    
    /**
     * Creates a backup of the specified file.
     *
     * @param originalFile the original file to backup
     * @param backupFile the backup file path
     * @throws IOException if backup operation fails
     */
    public static void createBackup(String originalFile, String backupFile) throws IOException {
        if (originalFile == null || originalFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Original filename cannot be null or empty");
        }
        if (backupFile == null || backupFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Backup filename cannot be null or empty");
        }
        
        Path originalPath = Paths.get(originalFile);
        Path backupPath = Paths.get(backupFile);
        
        if (!Files.exists(originalPath)) {
            throw new FileNotFoundException(Constants.ERROR_FILE_NOT_FOUND + originalFile);
        }
        
        // Create backup directory if it doesn't exist
        Path backupDir = backupPath.getParent();
        if (backupDir != null) {
            Files.createDirectories(backupDir);
        }
        
        try {
            Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to create backup: " + backupFile, e);
        }
    }
    
    /**
     * Creates an automatic backup with timestamp.
     *
     * @param originalFile the original file to backup
     * @throws IOException if backup operation fails
     */
    public static void createAutoBackup(String originalFile) throws IOException {
        if (originalFile == null || originalFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Original filename cannot be null or empty");
        }
        
        Path originalPath = Paths.get(originalFile);
        if (!Files.exists(originalPath)) {
            return; // No file to backup
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = Constants.BACKUP_DIRECTORY + "/contacts_backup_" + timestamp + ".csv";
        
        createBackup(originalFile, backupFileName);
    }
    
    /**
     * Validates that a file has the correct CSV format.
     *
     * @param filename the filename to validate
     * @return true if file format is valid, false otherwise
     */
    public static boolean validateFileFormat(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            return false;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return true; // Empty file is valid
            }
            
            // Check if first line is header or valid data
            if (firstLine.equals(Constants.CSV_HEADER)) {
                return true;
            }
            
            // Try to parse first line as contact data
            try {
                Contact.fromCsvString(firstLine);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Checks if a file exists.
     *
     * @param filename the filename to check
     * @return true if file exists, false otherwise
     */
    public static boolean fileExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(filename));
    }
    
    /**
     * Gets the size of a file.
     *
     * @param filename the filename
     * @return file size in bytes, or -1 if file doesn't exist
     */
    public static long getFileSize(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return -1;
        }
        
        Path filePath = Paths.get(filename);
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            return -1;
        }
    }
    
    /**
     * Creates the data directory if it doesn't exist.
     *
     * @throws IOException if directory creation fails
     */
    public static void ensureDataDirectoryExists() throws IOException {
        Path dataDir = Paths.get("data");
        Path backupDir = Paths.get(Constants.BACKUP_DIRECTORY);
        
        Files.createDirectories(dataDir);
        Files.createDirectories(backupDir);
    }
}