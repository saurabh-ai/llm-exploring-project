package com.javamastery.filedownloader;

import com.javamastery.filedownloader.core.DownloadEngine;
import com.javamastery.filedownloader.config.DownloadConfig;
import com.javamastery.filedownloader.progress.ProgressTracker;
import com.javamastery.filedownloader.queue.PriorityDownloadItem;

import java.util.concurrent.CompletableFuture;

/**
 * Simple demo application for testing the downloader with a known URL.
 */
public class SimpleDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Multi-threaded File Downloader Demo ===");
        System.out.println();
        
        // Create configuration
        DownloadConfig config = DownloadConfig.builder()
            .threadPoolSize(2)
            .maxConcurrentDownloads(2)
            .connectionTimeout(10000)
            .chunkSize(64 * 1024) // 64 KB chunks
            .downloadDirectory("downloads")
            .resumeSupported(true)
            .progressReporting(true)
            .build();
        
        // Create download engine
        DownloadEngine engine = DownloadEngine.builder()
            .config(config)
            .build();
        
        try {
            // Add some test files (using httpbin.org for testing)
            String[] testUrls = {
                "https://httpbin.org/json", // Small JSON file
                "https://httpbin.org/bytes/1024", // 1KB of random bytes
                "https://httpbin.org/bytes/10240" // 10KB of random bytes
            };
            
            System.out.println("Adding downloads to queue...");
            for (int i = 0; i < testUrls.length; i++) {
                String downloadId = engine.addDownload(testUrls[i], "downloads/test_file_" + (i + 1), 
                                                     PriorityDownloadItem.Priority.NORMAL);
                System.out.printf("Added: %s (ID: %s)%n", testUrls[i], downloadId.substring(0, 8) + "...");
            }
            
            System.out.println();
            System.out.println("Starting downloads...");
            
            // Start downloads
            CompletableFuture<Void> downloadFuture = engine.startDownloads();
            
            // Monitor progress for a short time
            ProgressTracker tracker = engine.getProgressTracker();
            int maxChecks = 30; // Check for up to 30 seconds
            int checks = 0;
            
            while (checks < maxChecks && (tracker.getCompletedDownloads() + tracker.getFailedDownloads()) < testUrls.length) {
                Thread.sleep(1000); // Wait 1 second
                checks++;
                
                System.out.printf("Progress: %d completed, %d failed, %d active, %.2f%% overall%n",
                                tracker.getCompletedDownloads(),
                                tracker.getFailedDownloads(),
                                tracker.getActiveDownloads(),
                                tracker.getOverallProgressPercent());
                
                if (tracker.getActiveDownloads() == 0 && tracker.getTotalDownloads() > 0) {
                    break; // All downloads finished
                }
            }
            
            // Wait for completion or timeout
            System.out.println("\nWaiting for downloads to complete...");
            downloadFuture.get();
            
            // Final status
            System.out.println("\n=== Final Status ===");
            System.out.printf("Total Downloads: %d%n", tracker.getTotalDownloads());
            System.out.printf("Completed: %d%n", tracker.getCompletedDownloads());
            System.out.printf("Failed: %d%n", tracker.getFailedDownloads());
            System.out.printf("Overall Progress: %.2f%%%n", tracker.getOverallProgressPercent());
            
            if (tracker.getCompletedDownloads() > 0) {
                System.out.println("\nDownloads completed successfully!");
            } else {
                System.out.println("\nSome downloads may have failed due to network connectivity.");
            }
            
        } catch (Exception e) {
            System.err.println("Error during download: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nShutting down download engine...");
            engine.shutdown();
            System.out.println("Demo complete!");
        }
    }
}