package com.javamastery.filedownloader.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe file utility functions for download operations.
 */
public final class FileUtils {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private FileUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Creates directories if they don't exist in a thread-safe manner.
     */
    public static void createDirectories(Path path) throws IOException {
        lock.writeLock().lock();
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Creates parent directories for a file path if they don't exist.
     */
    public static void createParentDirectories(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            createDirectories(parent);
        }
    }
    
    /**
     * Gets the file size in a thread-safe manner.
     */
    public static long getFileSize(Path path) throws IOException {
        lock.readLock().lock();
        try {
            return Files.exists(path) ? Files.size(path) : 0;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Checks if a file exists in a thread-safe manner.
     */
    public static boolean exists(Path path) {
        lock.readLock().lock();
        try {
            return Files.exists(path);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Deletes a file if it exists in a thread-safe manner.
     */
    public static boolean deleteIfExists(Path path) throws IOException {
        lock.writeLock().lock();
        try {
            return Files.deleteIfExists(path);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Moves a file atomically (for completing downloads).
     */
    public static void moveFile(Path source, Path target) throws IOException {
        createParentDirectories(target);
        lock.writeLock().lock();
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Creates a temporary file for downloading.
     */
    public static Path createTempFile(Path targetPath) throws IOException {
        Path parent = targetPath.getParent();
        String fileName = targetPath.getFileName().toString();
        String tempFileName = fileName + ".tmp";
        
        createDirectories(parent);
        
        lock.writeLock().lock();
        try {
            return parent.resolve(tempFileName);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Formats file size in human-readable format.
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log(bytes) / Math.log(1024));
        unitIndex = Math.min(unitIndex, units.length - 1);
        
        double size = bytes / Math.pow(1024, unitIndex);
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * Sanitizes a filename by removing invalid characters.
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unnamed_file";
        }
        
        // Remove invalid characters for most filesystems
        String sanitized = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // Remove leading/trailing whitespace and dots
        sanitized = sanitized.trim().replaceAll("^\\.*", "").replaceAll("\\.*$", "");
        
        if (sanitized.isEmpty()) {
            return "unnamed_file";
        }
        
        return sanitized;
    }
    
    /**
     * Extracts filename from URL.
     */
    public static String extractFileNameFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "downloaded_file";
        }
        
        // Remove query parameters and fragments
        int queryIndex = url.indexOf('?');
        if (queryIndex != -1) {
            url = url.substring(0, queryIndex);
        }
        
        int fragmentIndex = url.indexOf('#');
        if (fragmentIndex != -1) {
            url = url.substring(0, fragmentIndex);
        }
        
        // Extract filename
        int lastSlashIndex = url.lastIndexOf('/');
        String fileName = lastSlashIndex != -1 ? url.substring(lastSlashIndex + 1) : url;
        
        if (fileName.isEmpty()) {
            fileName = "downloaded_file";
        }
        
        return sanitizeFileName(fileName);
    }
}