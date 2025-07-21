package com.javamastery.taskapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for Swagger/OpenAPI documentation
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI taskManagementOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080/api");
        devServer.setDescription("Server URL in Development environment");
        
        Server prodServer = new Server();
        prodServer.setUrl("https://api.taskmanagement.com/api");
        prodServer.setDescription("Server URL in Production environment");
        
        Contact contact = new Contact();
        contact.setEmail("developer@javamastery.com");
        contact.setName("Java Mastery Team");
        contact.setUrl("https://javamastery.com");
        
        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
        
        Info info = new Info()
                .title("Task Management API")
                .version("1.0.0")
                .contact(contact)
                .description("A comprehensive RESTful API for task management with Spring Boot. " +
                           "This API provides endpoints for managing tasks, users, and categories " +
                           "with full CRUD operations, search functionality, and analytics.")
                .termsOfService("https://javamastery.com/terms")
                .license(mitLicense);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}