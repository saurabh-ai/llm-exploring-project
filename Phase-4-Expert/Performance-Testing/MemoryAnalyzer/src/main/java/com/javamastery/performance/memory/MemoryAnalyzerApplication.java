package com.javamastery.performance.memory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the JVM Memory Usage Analyzer.
 * Provides comprehensive memory profiling and analysis capabilities.
 */
@SpringBootApplication
@EnableScheduling
public class MemoryAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryAnalyzerApplication.class, args);
    }
}