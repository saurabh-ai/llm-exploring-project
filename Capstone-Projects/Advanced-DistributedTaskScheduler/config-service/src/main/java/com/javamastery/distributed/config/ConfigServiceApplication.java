package com.javamastery.distributed.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Configuration Service Application
 * Provides centralized configuration management and service discovery
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaServer
public class ConfigServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}