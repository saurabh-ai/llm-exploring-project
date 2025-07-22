package com.javamastery.distributed.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduler Service Application
 * Core scheduling engine with job management capabilities
 */
@SpringBootApplication(scanBasePackages = {"com.javamastery.distributed.scheduler", "com.javamastery.distributed.common"})
@EnableDiscoveryClient
@EnableScheduling
@EntityScan(basePackages = {"com.javamastery.distributed.scheduler.entity"})
@EnableJpaRepositories(basePackages = {"com.javamastery.distributed.scheduler.repository"})
public class SchedulerServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SchedulerServiceApplication.class, args);
    }
}