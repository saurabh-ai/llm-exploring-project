package com.javamastery.distributed.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Executor Service Application
 * Distributed task execution workers
 */
@SpringBootApplication(scanBasePackages = {"com.javamastery.distributed.executor", "com.javamastery.distributed.common"})
@EnableDiscoveryClient
@EnableAsync
public class ExecutorServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExecutorServiceApplication.class, args);
    }
}