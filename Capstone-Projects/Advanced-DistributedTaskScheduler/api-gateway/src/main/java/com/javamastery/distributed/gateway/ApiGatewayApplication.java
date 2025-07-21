package com.javamastery.distributed.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * Single entry point with load balancing and routing
 * Demonstrates Gateway Pattern for unified access
 */
@SpringBootApplication(scanBasePackages = {"com.javamastery.distributed.gateway", "com.javamastery.distributed.common"})
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}