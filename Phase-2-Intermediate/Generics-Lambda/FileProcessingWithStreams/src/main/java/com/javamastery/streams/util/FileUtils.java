package com.javamastery.streams.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for file operations using Java NIO and Streams API
 * Demonstrates functional approach to file processing
 */
public class FileUtils {
    
    private FileUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Read all lines from a file as a Stream
     * Uses try-with-resources to ensure proper resource management
     */
    public static Stream<String> readLinesAsStream(Path filePath) {
        try {
            return Files.lines(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }
    
    /**
     * Read all lines from a file as a List
     */
    public static List<String> readLines(Path filePath) {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }
    
    /**
     * Write lines to a file
     */
    public static void writeLines(Path filePath, List<String> lines) {
        try {
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + filePath, e);
        }
    }
    
    /**
     * Count lines in a file using streams
     */
    public static long countLines(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.count();
        } catch (IOException e) {
            throw new RuntimeException("Failed to count lines in file: " + filePath, e);
        }
    }
    
    /**
     * Count words in a file using streams
     */
    public static long countWords(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                .flatMap(line -> Stream.of(line.split("\\s+")))
                .filter(word -> !word.trim().isEmpty())
                .count();
        } catch (IOException e) {
            throw new RuntimeException("Failed to count words in file: " + filePath, e);
        }
    }
    
    /**
     * Get file size using streams and parallel processing
     */
    public static long getDirectorySize(Path directoryPath) {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0L;
                    }
                })
                .sum();
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate directory size: " + directoryPath, e);
        }
    }
    
    /**
     * Find files with specific extension using streams
     */
    public static Stream<Path> findFilesByExtension(Path directory, String extension) {
        try {
            return Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(extension.toLowerCase()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to search files in directory: " + directory, e);
        }
    }
    
    /**
     * Create directory if it doesn't exist
     */
    public static void ensureDirectoryExists(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directory, e);
            }
        }
    }
    
    /**
     * Copy file with progress tracking using streams
     */
    public static void copyFileWithProgress(Path source, Path target, ProgressCallback callback) {
        try {
            long fileSize = Files.size(source);
            AtomicLong bytesCopied = new AtomicLong(0);
            
            try (var inputStream = Files.newInputStream(source);
                 var outputStream = Files.newOutputStream(target)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    long copied = bytesCopied.addAndGet(bytesRead);
                    if (callback != null) {
                        callback.onProgress(copied, fileSize);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file from " + source + " to " + target, e);
        }
    }
    
    /**
     * Functional interface for progress callback
     */
    @FunctionalInterface
    public interface ProgressCallback {
        void onProgress(long bytesCopied, long totalBytes);
    }
}