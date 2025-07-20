package com.javamastery.filedownloader;

import com.javamastery.filedownloader.core.DownloadEngine;
import com.javamastery.filedownloader.config.DownloadConfig;
import com.javamastery.filedownloader.progress.ConsoleProgressReporter;
import com.javamastery.filedownloader.progress.ProgressTracker;
import com.javamastery.filedownloader.queue.PriorityDownloadItem;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for the Multi-threaded File Downloader.
 * Demonstrates usage of the download engine with various configurations.
 */
public class FileDownloaderApp {
    
    private static DownloadEngine engine;
    private static ScheduledExecutorService progressService;
    private static ConsoleProgressReporter progressReporter;
    
    public static void main(String[] args) {
        System.out.println("=== Multi-threaded File Downloader ===");
        System.out.println();
        
        try {
            // Initialize the download engine
            initializeEngine();
            
            if (args.length > 0) {
                // Command line mode - download provided URLs
                runCommandLineMode(args);
            } else {
                // Interactive mode
                runInteractiveMode();
            }
            
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    private static void initializeEngine() {
        // Create configuration
        DownloadConfig config = DownloadConfig.builder()
            .threadPoolSize(4)
            .maxConcurrentDownloads(3)
            .connectionTimeout(30000)
            .chunkSize(1024 * 1024) // 1 MB chunks
            .downloadDirectory("downloads")
            .resumeSupported(true)
            .progressReporting(true)
            .build();
        
        // Create download engine
        engine = DownloadEngine.builder()
            .config(config)
            .build();
        
        // Set up progress reporting
        progressReporter = new ConsoleProgressReporter();
        progressService = Executors.newSingleThreadScheduledExecutor();
        
        // Start progress reporting every 2 seconds
        progressService.scheduleAtFixedRate(() -> {
            ProgressTracker tracker = engine.getProgressTracker();
            if (tracker.getTotalDownloads() > 0) {
                clearScreen();
                progressReporter.onProgressUpdate(tracker);
            }
        }, 2, 2, TimeUnit.SECONDS);
    }
    
    private static void runCommandLineMode(String[] urls) throws Exception {
        System.out.println("Command line mode - downloading " + urls.length + " files...");
        System.out.println();
        
        // Add all URLs to download queue
        for (String url : urls) {
            String downloadId = engine.addDownload(url, "downloads/", PriorityDownloadItem.Priority.NORMAL);
            System.out.println("Queued: " + url + " (ID: " + downloadId.substring(0, 8) + "...)");
        }
        
        // Start downloads
        CompletableFuture<Void> downloadFuture = engine.startDownloads();
        
        // Wait for all downloads to complete
        System.out.println("\nStarting downloads...");
        downloadFuture.get();
        
        // Wait a bit for final progress update
        Thread.sleep(3000);
        
        // Show final status
        System.out.println("\n=== Download Summary ===");
        ProgressTracker tracker = engine.getProgressTracker();
        System.out.printf("Total Downloads: %d%n", tracker.getTotalDownloads());
        System.out.printf("Completed: %d%n", tracker.getCompletedDownloads());
        System.out.printf("Failed: %d%n", tracker.getFailedDownloads());
    }
    
    private static void runInteractiveMode() throws Exception {
        System.out.println("Interactive mode - Enter commands (type 'help' for available commands)");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.print("downloader> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            
            try {
                switch (command) {
                    case "help":
                        showHelp();
                        break;
                        
                    case "add":
                        if (parts.length < 2) {
                            System.out.println("Usage: add <URL> [destination]");
                        } else {
                            addDownload(parts[1]);
                        }
                        break;
                        
                    case "start":
                        startDownloads();
                        break;
                        
                    case "stop":
                        stopDownloads();
                        break;
                        
                    case "status":
                        showStatus();
                        break;
                        
                    case "queue":
                        showQueue();
                        break;
                        
                    case "config":
                        showConfig();
                        break;
                        
                    case "clear":
                        clearScreen();
                        break;
                        
                    case "quit":
                    case "exit":
                        running = false;
                        break;
                        
                    default:
                        System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private static void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help              - Show this help message");
        System.out.println("  add <URL>         - Add a URL to the download queue");
        System.out.println("  start             - Start processing the download queue");
        System.out.println("  stop              - Stop all downloads");
        System.out.println("  status            - Show current download status");
        System.out.println("  queue             - Show current download queue");
        System.out.println("  config            - Show current configuration");
        System.out.println("  clear             - Clear the screen");
        System.out.println("  quit/exit         - Exit the application");
        System.out.println();
    }
    
    private static void addDownload(String url) throws InterruptedException {
        String downloadId = engine.addDownload(url, "downloads/", PriorityDownloadItem.Priority.NORMAL);
        System.out.println("Added to queue: " + url);
        System.out.println("Download ID: " + downloadId);
    }
    
    private static void startDownloads() {
        if (engine.isRunning()) {
            System.out.println("Downloads are already running.");
            return;
        }
        
        if (engine.getDownloadQueue().isEmpty()) {
            System.out.println("No downloads in queue. Use 'add <URL>' to add downloads.");
            return;
        }
        
        engine.startDownloads();
        System.out.println("Started processing downloads...");
    }
    
    private static void stopDownloads() throws Exception {
        if (!engine.isRunning()) {
            System.out.println("No downloads are currently running.");
            return;
        }
        
        System.out.println("Stopping downloads...");
        engine.stop().get();
        System.out.println("All downloads stopped.");
    }
    
    private static void showStatus() {
        ProgressTracker tracker = engine.getProgressTracker();
        System.out.println("=== Current Status ===");
        System.out.printf("Engine Running: %s%n", engine.isRunning());
        System.out.printf("Active Downloads: %d%n", engine.getActiveDownloadCount());
        System.out.printf("Queue Size: %d%n", engine.getDownloadQueue().size());
        System.out.printf("Total Downloads: %d%n", tracker.getTotalDownloads());
        System.out.printf("Completed: %d%n", tracker.getCompletedDownloads());
        System.out.printf("Failed: %d%n", tracker.getFailedDownloads());
        System.out.printf("Overall Progress: %.2f%%%n", tracker.getOverallProgressPercent());
        System.out.println();
    }
    
    private static void showQueue() {
        var queue = engine.getDownloadQueue();
        System.out.println("=== Download Queue ===");
        System.out.printf("Queue Size: %d%n", queue.size());
        
        if (queue.isEmpty()) {
            System.out.println("No items in queue.");
        } else {
            var items = queue.toArray();
            for (int i = 0; i < Math.min(10, items.length); i++) {
                var item = items[i];
                System.out.printf("%d. %s (Priority: %s)%n", 
                                i + 1, item.getUrl(), item.getPriority());
            }
            if (items.length > 10) {
                System.out.printf("... and %d more items%n", items.length - 10);
            }
        }
        System.out.println();
    }
    
    private static void showConfig() {
        var config = engine.getConfig();
        System.out.println("=== Configuration ===");
        System.out.println("Download Directory: " + config.getDownloadDirectory());
        System.out.println("Thread Pool Size: " + config.getThreadConfig().getCorePoolSize());
        System.out.println("Max Concurrent Downloads: " + config.getThreadConfig().getMaxConcurrentDownloads());
        System.out.println("Connection Timeout: " + config.getNetworkConfig().getConnectionTimeout() + " ms");
        System.out.println("Chunk Size: " + config.getNetworkConfig().getChunkSize() + " bytes");
        System.out.println("Resume Supported: " + config.isResumeSupported());
        System.out.println();
    }
    
    private static void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
    
    private static void cleanup() {
        System.out.println("\nShutting down...");
        
        if (progressService != null && !progressService.isShutdown()) {
            progressService.shutdown();
        }
        
        if (engine != null && !engine.isShutdown()) {
            engine.shutdown();
        }
        
        System.out.println("Goodbye!");
    }
}