package com.javamastery.distributed.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Monitoring Service Application
 * Dashboard and metrics collection service
 */
@SpringBootApplication(scanBasePackages = {"com.javamastery.distributed.monitoring", "com.javamastery.distributed.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class MonitoringServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MonitoringServiceApplication.class, args);
    }
}