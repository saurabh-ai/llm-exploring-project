package com.javamastery.streams.processor;

import com.javamastery.streams.model.LogEntry;
import com.javamastery.streams.collector.CustomCollectors;
import com.javamastery.streams.util.FileUtils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Processor for log analysis demonstrating text processing with streams
 * Shows pattern matching, text analysis, and performance monitoring
 */
public class LogAnalysisProcessor {
    
    private final List<LogEntry> logEntries;
    private static final Pattern ERROR_PATTERN = Pattern.compile("\\b(error|exception|fail|timeout)\\b", Pattern.CASE_INSENSITIVE);
    
    public LogAnalysisProcessor(List<LogEntry> logEntries) {
        this.logEntries = Objects.requireNonNull(logEntries, "Log entries cannot be null");
    }
    
    /**
     * Static factory method to create processor from file
     */
    public static LogAnalysisProcessor fromFile(Path logFile, Function<String, LogEntry> parser) {
        List<LogEntry> entries = FileUtils.readLinesAsStream(logFile)
            .skip(1) // Skip header if present
            .map(parser)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return new LogAnalysisProcessor(entries);
    }
    
    /**
     * Get error distribution by log level
     */
    public Map<LogEntry.LogLevel, Long> getErrorDistribution() {
        return logEntries.stream()
            .collect(Collectors.groupingBy(
                LogEntry::level,
                Collectors.counting()
            ));
    }
    
    /**
     * Find components with the most errors
     */
    public List<Map.Entry<String, Long>> getTopErrorComponents(int limit) {
        return logEntries.stream()
            .filter(LogEntry::isError)
            .collect(Collectors.groupingBy(
                LogEntry::component,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Analyze performance issues (slow operations)
     */
    public List<LogEntry> getSlowOperations() {
        return logEntries.stream()
            .filter(LogEntry::isSlowOperation)
            .sorted(Comparator.comparing(LogEntry::responseTime).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Get response time statistics using custom collector
     */
    public CustomCollectors.Statistics getResponseTimeStatistics() {
        return logEntries.stream()
            .collect(CustomCollectors.toStatistics(LogEntry::responseTime));
    }
    
    /**
     * Group log entries by date and severity level
     */
    public Map<LocalDate, Map<LogEntry.LogLevel, Long>> getDailySeverityBreakdown() {
        return logEntries.stream()
            .collect(Collectors.groupingBy(
                LogEntry::date,
                Collectors.groupingBy(
                    LogEntry::level,
                    Collectors.counting()
                )
            ));
    }
    
    /**
     * Find patterns in error messages using regex
     */
    public List<String> findErrorPatterns() {
        return logEntries.stream()
            .filter(LogEntry::isError)
            .map(LogEntry::message)
            .filter(message -> ERROR_PATTERN.matcher(message).find())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Analyze thread usage patterns
     */
    public Map<String, ThreadActivity> getThreadActivity() {
        return logEntries.stream()
            .collect(Collectors.groupingBy(LogEntry::threadName))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ThreadActivity(entry.getValue())
            ));
    }
    
    /**
     * Find logs with response times in different buckets
     */
    public Map<String, List<LogEntry>> groupByResponseTimeBuckets() {
        return logEntries.stream()
            .collect(CustomCollectors.toBuckets(
                LogEntry::responseTime,
                100.0, 500.0, 1000.0, 5000.0
            ));
    }
    
    /**
     * Generate daily activity report
     */
    public Map<LocalDate, DailyLogSummary> getDailyActivity() {
        return logEntries.stream()
            .collect(Collectors.groupingBy(LogEntry::date))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new DailyLogSummary(entry.getValue())
            ));
    }
    
    /**
     * Find anomalies using statistical analysis
     */
    public List<LogEntry> findAnomalies() {
        double averageResponseTime = logEntries.stream()
            .mapToDouble(LogEntry::responseTime)
            .average()
            .orElse(0.0);
        
        double threshold = averageResponseTime * 3; // 3x average as threshold
        
        return logEntries.stream()
            .filter(entry -> entry.responseTime() > threshold)
            .sorted(Comparator.comparing(LogEntry::responseTime).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Parallel processing for large log analysis
     */
    public SystemHealthReport generateSystemHealthReport() {
        // Use parallel streams for performance on large datasets
        long totalEntries = logEntries.size();
        
        long errorCount = logEntries.parallelStream()
            .filter(LogEntry::isError)
            .count();
        
        double averageResponseTime = logEntries.parallelStream()
            .mapToDouble(LogEntry::responseTime)
            .average()
            .orElse(0.0);
        
        Set<String> activeComponents = logEntries.parallelStream()
            .map(LogEntry::component)
            .collect(Collectors.toSet());
        
        Map<LogEntry.LogLevel, Long> levelDistribution = logEntries.parallelStream()
            .collect(Collectors.groupingByConcurrent(
                LogEntry::level,
                Collectors.counting()
            ));
        
        return new SystemHealthReport(totalEntries, errorCount, averageResponseTime, 
                                    activeComponents.size(), levelDistribution);
    }
    
    /**
     * Search logs by criteria using functional composition
     */
    public List<LogEntry> searchLogs(LogSearchCriteria criteria) {
        return logEntries.stream()
            .filter(criteria.toPredicate())
            .sorted(Comparator.comparing(LogEntry::date).thenComparing(LogEntry::time))
            .collect(Collectors.toList());
    }
    
    /**
     * Thread activity analysis class
     */
    public static class ThreadActivity {
        private final int logCount;
        private final long totalResponseTime;
        private final double averageResponseTime;
        private final long errorCount;
        
        public ThreadActivity(List<LogEntry> logs) {
            this.logCount = logs.size();
            this.totalResponseTime = logs.stream().mapToLong(LogEntry::responseTime).sum();
            this.averageResponseTime = (double) totalResponseTime / logCount;
            this.errorCount = logs.stream().filter(LogEntry::isError).count();
        }
        
        public int getLogCount() { return logCount; }
        public long getTotalResponseTime() { return totalResponseTime; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public long getErrorCount() { return errorCount; }
        
        @Override
        public String toString() {
            return String.format("ThreadActivity{logs=%d, avgResponse=%.2fms, errors=%d}", 
                logCount, averageResponseTime, errorCount);
        }
    }
    
    /**
     * Daily log summary class
     */
    public static class DailyLogSummary {
        private final int totalLogs;
        private final int errorLogs;
        private final double averageResponseTime;
        private final Set<String> activeComponents;
        
        public DailyLogSummary(List<LogEntry> logs) {
            this.totalLogs = logs.size();
            this.errorLogs = (int) logs.stream().filter(LogEntry::isError).count();
            this.averageResponseTime = logs.stream()
                .mapToDouble(LogEntry::responseTime)
                .average()
                .orElse(0.0);
            this.activeComponents = logs.stream()
                .map(LogEntry::component)
                .collect(Collectors.toSet());
        }
        
        public int getTotalLogs() { return totalLogs; }
        public int getErrorLogs() { return errorLogs; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public Set<String> getActiveComponents() { return activeComponents; }
        
        public double getErrorRate() { return totalLogs > 0 ? (double) errorLogs / totalLogs * 100 : 0.0; }
        
        @Override
        public String toString() {
            return String.format("DailyLogSummary{total=%d, errors=%d(%.1f%%), avgResponse=%.2fms, components=%d}", 
                totalLogs, errorLogs, getErrorRate(), averageResponseTime, activeComponents.size());
        }
    }
    
    /**
     * System health report class
     */
    public static class SystemHealthReport {
        private final long totalEntries;
        private final long errorCount;
        private final double averageResponseTime;
        private final int componentCount;
        private final Map<LogEntry.LogLevel, Long> levelDistribution;
        
        public SystemHealthReport(long totalEntries, long errorCount, double averageResponseTime,
                                int componentCount, Map<LogEntry.LogLevel, Long> levelDistribution) {
            this.totalEntries = totalEntries;
            this.errorCount = errorCount;
            this.averageResponseTime = averageResponseTime;
            this.componentCount = componentCount;
            this.levelDistribution = levelDistribution;
        }
        
        public double getErrorRate() {
            return totalEntries > 0 ? (double) errorCount / totalEntries * 100 : 0.0;
        }
        
        public String getHealthStatus() {
            double errorRate = getErrorRate();
            if (errorRate < 1.0) return "HEALTHY";
            else if (errorRate < 5.0) return "WARNING";
            else return "CRITICAL";
        }
        
        @Override
        public String toString() {
            return String.format("SystemHealth{status=%s, entries=%d, errors=%d(%.2f%%), avgResponse=%.2fms, components=%d}",
                getHealthStatus(), totalEntries, errorCount, getErrorRate(), averageResponseTime, componentCount);
        }
    }
    
    /**
     * Search criteria builder for flexible log searching
     */
    public static class LogSearchCriteria {
        private LocalDate dateFrom, dateTo;
        private LogEntry.LogLevel minLevel;
        private String componentPattern;
        private String messagePattern;
        private Long minResponseTime, maxResponseTime;
        
        public LogSearchCriteria dateRange(LocalDate from, LocalDate to) {
            this.dateFrom = from;
            this.dateTo = to;
            return this;
        }
        
        public LogSearchCriteria minimumLevel(LogEntry.LogLevel level) {
            this.minLevel = level;
            return this;
        }
        
        public LogSearchCriteria component(String pattern) {
            this.componentPattern = pattern;
            return this;
        }
        
        public LogSearchCriteria message(String pattern) {
            this.messagePattern = pattern;
            return this;
        }
        
        public LogSearchCriteria responseTimeRange(long min, long max) {
            this.minResponseTime = min;
            this.maxResponseTime = max;
            return this;
        }
        
        public java.util.function.Predicate<LogEntry> toPredicate() {
            java.util.function.Predicate<LogEntry> predicate = entry -> true;
            
            if (dateFrom != null) {
                predicate = predicate.and(entry -> !entry.date().isBefore(dateFrom));
            }
            if (dateTo != null) {
                predicate = predicate.and(entry -> !entry.date().isAfter(dateTo));
            }
            if (minLevel != null) {
                predicate = predicate.and(entry -> entry.level().getSeverity() >= minLevel.getSeverity());
            }
            if (componentPattern != null) {
                predicate = predicate.and(entry -> entry.component().matches(componentPattern));
            }
            if (messagePattern != null) {
                predicate = predicate.and(entry -> entry.message().contains(messagePattern));
            }
            if (minResponseTime != null) {
                predicate = predicate.and(entry -> entry.responseTime() >= minResponseTime);
            }
            if (maxResponseTime != null) {
                predicate = predicate.and(entry -> entry.responseTime() <= maxResponseTime);
            }
            
            return predicate;
        }
    }
}