package com.javamastery.contacts.service;

import com.javamastery.contacts.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileManager}.
 */
class FileManagerTest {

    @TempDir
    Path tempDir;

    private Path testFile;
    private List<Contact> testContacts;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test-contacts.csv");
        testContacts = Arrays.asList(
            new Contact("John Doe", "+1-555-0123", "john@example.com", "123 Main St"),
            new Contact("Jane Smith", "+1-555-0124", "jane@example.com", "456 Oak Ave"),
            new Contact("Bob Johnson", "+1-555-0125", null, null)
        );
    }

    @Test
    void saveContactsToFile_Success() throws IOException {
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        assertTrue(Files.exists(testFile));
        List<String> lines = Files.readAllLines(testFile);
        
        // Should have header + 3 contacts
        assertEquals(4, lines.size());
        assertTrue(lines.get(0).contains("name,phone,email,address"));
        assertTrue(lines.get(1).contains("John Doe"));
        assertTrue(lines.get(2).contains("Jane Smith"));
        assertTrue(lines.get(3).contains("Bob Johnson"));
    }

    @Test
    void saveContactsToFile_CreatesDirectory() throws IOException {
        Path subDir = tempDir.resolve("subdir");
        Path fileInSubDir = subDir.resolve("contacts.csv");
        
        FileManager.saveContactsToFile(testContacts, fileInSubDir.toString());
        
        assertTrue(Files.exists(subDir));
        assertTrue(Files.exists(fileInSubDir));
    }

    @Test
    void saveContactsToFile_NullContacts() {
        assertThrows(IllegalArgumentException.class, () -> 
            FileManager.saveContactsToFile(null, testFile.toString()));
    }

    @Test
    void saveContactsToFile_NullFilename() {
        assertThrows(IllegalArgumentException.class, () -> 
            FileManager.saveContactsToFile(testContacts, null));
    }

    @Test
    void loadContactsFromFile_Success() throws IOException {
        // First save contacts
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        // Then load them
        List<Contact> loadedContacts = FileManager.loadContactsFromFile(testFile.toString());
        
        assertEquals(3, loadedContacts.size());
        assertEquals("John Doe", loadedContacts.get(0).getName());
        assertEquals("Jane Smith", loadedContacts.get(1).getName());
        assertEquals("Bob Johnson", loadedContacts.get(2).getName());
    }

    @Test
    void loadContactsFromFile_FileNotFound() {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        
        assertThrows(IOException.class, () -> 
            FileManager.loadContactsFromFile(nonExistentFile.toString()));
    }

    @Test
    void loadContactsFromFile_EmptyFile() throws IOException {
        Files.createFile(testFile);
        
        List<Contact> contacts = FileManager.loadContactsFromFile(testFile.toString());
        assertTrue(contacts.isEmpty());
    }

    @Test
    void loadContactsFromFile_InvalidData() throws IOException {
        // Create file with invalid data
        String invalidData = "name,phone,email,address,createdDate,lastModified\n" +
                           "Invalid,invalid-phone,invalid-email,address,2025-07-20T10:00:00,2025-07-20T10:00:00\n" +
                           "John Doe,+1-555-0123,john@example.com,123 Main St,2025-07-20T10:00:00,2025-07-20T10:00:00";
        Files.writeString(testFile, invalidData);
        
        List<Contact> contacts = FileManager.loadContactsFromFile(testFile.toString());
        
        // Should load only the valid contact (invalid lines are skipped with warning)
        assertEquals(1, contacts.size());
        assertEquals("John Doe", contacts.get(0).getName());
    }

    @Test
    void createBackup_Success() throws IOException {
        // Create original file
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        Path backupFile = tempDir.resolve("backup.csv");
        FileManager.createBackup(testFile.toString(), backupFile.toString());
        
        assertTrue(Files.exists(backupFile));
        
        // Verify backup content matches original
        List<String> originalLines = Files.readAllLines(testFile);
        List<String> backupLines = Files.readAllLines(backupFile);
        assertEquals(originalLines, backupLines);
    }

    @Test
    void createBackup_OriginalFileNotFound() {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        Path backupFile = tempDir.resolve("backup.csv");
        
        assertThrows(IOException.class, () -> 
            FileManager.createBackup(nonExistentFile.toString(), backupFile.toString()));
    }

    @Test
    void createBackup_CreatesBackupDirectory() throws IOException {
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        Path backupDir = tempDir.resolve("backups");
        Path backupFile = backupDir.resolve("backup.csv");
        
        FileManager.createBackup(testFile.toString(), backupFile.toString());
        
        assertTrue(Files.exists(backupDir));
        assertTrue(Files.exists(backupFile));
    }

    @Test
    void createAutoBackup_Success() throws IOException {
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        FileManager.createAutoBackup(testFile.toString());
        
        // Should create backup in data/backups directory (though we can't easily verify exact filename due to timestamp)
        Path backupDir = Path.of("data/backups");
        assertTrue(Files.exists(backupDir) || !Files.exists(testFile)); // Backup dir created or no file to backup
    }

    @Test
    void createAutoBackup_NoOriginalFile() throws IOException {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        
        // Should not throw exception, just do nothing
        assertDoesNotThrow(() -> FileManager.createAutoBackup(nonExistentFile.toString()));
    }

    @Test
    void validateFileFormat_ValidFile() throws IOException {
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        assertTrue(FileManager.validateFileFormat(testFile.toString()));
    }

    @Test
    void validateFileFormat_EmptyFile() throws IOException {
        Files.createFile(testFile);
        
        assertTrue(FileManager.validateFileFormat(testFile.toString()));
    }

    @Test
    void validateFileFormat_FileNotFound() {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        
        assertFalse(FileManager.validateFileFormat(nonExistentFile.toString()));
    }

    @Test
    void validateFileFormat_InvalidFile() throws IOException {
        Files.writeString(testFile, "invalid,csv,format");
        
        assertFalse(FileManager.validateFileFormat(testFile.toString()));
    }

    @Test
    void fileExists_True() throws IOException {
        Files.createFile(testFile);
        
        assertTrue(FileManager.fileExists(testFile.toString()));
    }

    @Test
    void fileExists_False() {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        
        assertFalse(FileManager.fileExists(nonExistentFile.toString()));
    }

    @Test
    void getFileSize_Success() throws IOException {
        String content = "test content";
        Files.writeString(testFile, content);
        
        long size = FileManager.getFileSize(testFile.toString());
        assertEquals(content.length(), size);
    }

    @Test
    void getFileSize_FileNotFound() {
        Path nonExistentFile = tempDir.resolve("nonexistent.csv");
        
        assertEquals(-1, FileManager.getFileSize(nonExistentFile.toString()));
    }

    @Test
    void ensureDataDirectoryExists() {
        assertDoesNotThrow(() -> FileManager.ensureDataDirectoryExists());
        
        assertTrue(Files.exists(Path.of("data")));
        assertTrue(Files.exists(Path.of("data/backups")));
    }

    @Test
    void roundTripTest() throws IOException {
        // Save contacts to file
        FileManager.saveContactsToFile(testContacts, testFile.toString());
        
        // Load contacts from file
        List<Contact> loadedContacts = FileManager.loadContactsFromFile(testFile.toString());
        
        // Verify all contacts are preserved
        assertEquals(testContacts.size(), loadedContacts.size());
        
        for (int i = 0; i < testContacts.size(); i++) {
            Contact original = testContacts.get(i);
            Contact loaded = loadedContacts.get(i);
            
            assertEquals(original.getName(), loaded.getName());
            assertEquals(original.getPhoneNumber(), loaded.getPhoneNumber());
            assertEquals(original.getEmail(), loaded.getEmail());
            assertEquals(original.getAddress(), loaded.getAddress());
        }
    }
}