# Multi-threaded File Downloader

A comprehensive multi-threaded file downloader application demonstrating advanced Java multithreading concepts, concurrent collections, and thread-safe programming practices.

## Features

### Core Features
- **Multi-threaded Download Engine**: Support for downloading multiple files concurrently with configurable thread pool size
- **Thread-Safe Progress Tracking**: Real-time progress reporting for individual downloads and overall progress aggregation
- **Concurrent Queue Management**: Priority-based download queue using BlockingQueue for producer-consumer pattern
- **Resource Management**: Proper thread lifecycle management with automatic cleanup

### Advanced Features
- **Download Resumption**: Capability to resume interrupted downloads
- **Priority-based Scheduling**: Downloads can be prioritized (LOW, NORMAL, HIGH, URGENT)
- **Configurable Settings**: Thread pool size, connection timeouts, chunk size, bandwidth limiting
- **Progress Monitoring**: Thread-safe statistics collection with speed and ETA calculations
- **Error Handling**: Comprehensive exception hierarchy with retry mechanisms

## Architecture

The application follows a modular architecture with clear separation of concerns:

```
src/main/java/com/javamastery/filedownloader/
├── core/                    # Core download engine and task management
│   ├── DownloadEngine.java  # Main orchestrator with ExecutorService management
│   ├── DownloadTask.java    # Individual download task (Callable)
│   ├── DownloadResult.java  # Download result wrapper
│   └── FileChunk.java       # File chunk representation (immutable)
├── queue/                   # Thread-safe queue management
│   ├── DownloadQueue.java   # BlockingQueue wrapper with priority support
│   └── PriorityDownloadItem.java # Priority-based queue item
├── progress/                # Progress tracking and reporting
│   ├── ProgressTracker.java # Thread-safe progress tracking
│   ├── DownloadProgress.java # Progress data model with atomic operations
│   ├── ProgressReporter.java # Progress reporting interface
│   └── ConsoleProgressReporter.java # Console-based progress display
├── config/                  # Configuration management
│   ├── DownloadConfig.java  # Main configuration with builder pattern
│   ├── ThreadConfig.java    # Thread-specific settings
│   └── NetworkConfig.java   # Network configuration
├── exception/               # Exception hierarchy
│   ├── DownloadException.java # Base download exception
│   ├── NetworkException.java # Network-related exceptions
│   └── ConcurrencyException.java # Concurrency-related exceptions
├── util/                    # Utility classes
│   └── FileUtils.java       # Thread-safe file operations
├── FileDownloaderApp.java   # Interactive main application
└── SimpleDemo.java          # Simple demonstration program
```

## Key Design Patterns

### Thread Safety
- **Atomic Variables**: Used throughout for thread-safe counters and flags
- **Concurrent Collections**: ConcurrentHashMap for progress tracking, PriorityBlockingQueue for download queue
- **Immutable Objects**: FileChunk class is immutable for thread safety
- **Builder Pattern**: Configuration classes use builder pattern for safe object construction

### Multithreading Concepts Demonstrated
- **ExecutorService**: Custom thread pool management with proper shutdown hooks
- **Callable/Future**: Download tasks implement Callable for result handling
- **CompletableFuture**: Asynchronous download coordination
- **Producer-Consumer**: Download queue using BlockingQueue
- **Thread Synchronization**: Proper use of volatile, atomic references, and synchronized blocks

## Usage Examples

### Basic Usage
```java
// Create and configure download engine
DownloadEngine engine = DownloadEngine.builder()
    .threadPoolSize(4)
    .maxConcurrentDownloads(3)
    .connectionTimeout(30000)
    .downloadDirectory("downloads")
    .build();

// Add downloads
String downloadId = engine.addDownload("https://example.com/file.zip", "downloads/file.zip");

// Start downloading
CompletableFuture<Void> future = engine.startDownloads();

// Monitor progress
ProgressTracker tracker = engine.getProgressTracker();
// ... monitor progress ...

// Wait for completion
future.get();
engine.shutdown();
```

### Interactive Mode
```bash
mvn exec:java
# Then use commands like:
# add https://example.com/file.zip
# start
# status
# quit
```

### Command Line Mode
```bash
mvn exec:java -Dexec.args="https://example.com/file1.zip https://example.com/file2.zip"
```

## Configuration

### Thread Configuration
- **Core Pool Size**: Number of core threads (default: 4)
- **Maximum Pool Size**: Maximum number of threads (default: 8)
- **Keep Alive Time**: Thread keep-alive time in milliseconds (default: 60000)
- **Max Concurrent Downloads**: Maximum simultaneous downloads (default: 10)

### Network Configuration
- **Connection Timeout**: Connection timeout in milliseconds (default: 30000)
- **Read Timeout**: Read timeout in milliseconds (default: 60000)
- **Max Retries**: Maximum retry attempts (default: 3)
- **Chunk Size**: Download chunk size in bytes (default: 1MB)
- **Bandwidth Limit**: Bandwidth limiting in bytes/second (default: unlimited)

## Testing

The project includes comprehensive unit tests covering:
- **FileChunk**: Immutable object creation and validation
- **ProgressTracker**: Thread-safe progress tracking and aggregation
- **DownloadQueue**: Priority-based queue operations and thread safety

Run tests with:
```bash
mvn test
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Run Application
```bash
# Interactive mode
mvn exec:java

# Demo mode
mvn exec:java -Dexec.mainClass="com.javamastery.filedownloader.SimpleDemo"
```

## Thread Safety Guarantees

1. **Progress Tracking**: All progress updates are atomic and thread-safe
2. **Queue Operations**: Download queue operations are thread-safe with proper blocking behavior
3. **Configuration**: All configuration objects use atomic references for thread-safe updates
4. **File Operations**: File utilities include proper synchronization for concurrent file access
5. **Resource Management**: Proper cleanup of threads, connections, and file handles

## Performance Characteristics

- **Concurrent Downloads**: Supports configurable number of simultaneous downloads
- **Memory Efficient**: Streaming downloads with configurable chunk sizes
- **CPU Efficient**: Uses thread pools to avoid thread creation overhead
- **Network Efficient**: Connection reuse and proper timeout handling

## Error Handling

The application provides robust error handling with:
- **Retry Mechanisms**: Automatic retry with exponential backoff
- **Graceful Degradation**: Failed downloads don't affect other downloads
- **Comprehensive Logging**: Detailed error reporting and status tracking
- **Resource Cleanup**: Automatic cleanup of partial downloads and temporary files

## Monitoring and Observability

- **Progress Reporting**: Real-time progress updates with speed and ETA calculations
- **Status Tracking**: Comprehensive status information for all downloads
- **Performance Metrics**: Download speeds, completion rates, and error statistics
- **Console Interface**: User-friendly progress display with visual progress bars