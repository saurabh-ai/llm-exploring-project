# E-commerce Microservices Platform

A comprehensive e-commerce platform built using microservices architecture with Spring Boot and Spring Cloud.

## Project Overview

This project demonstrates advanced Spring Framework features, microservice design patterns, and enterprise-level Java development skills through a complete e-commerce implementation.

## Architecture

The platform consists of the following microservices:

1. **Discovery Service** - Service registration and discovery using Netflix Eureka
2. **API Gateway** - Central entry point with routing, load balancing, and authentication
3. **User Service** - User management, authentication, and profiles
4. **Product Service** - Product catalog and inventory management
5. **Order Service** - Order processing and history
6. **Payment Service** - Payment processing simulation
7. **Notification Service** - Email and SMS notifications

## Technology Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security 6.x** 
- **Spring Data JPA**
- **Spring Cloud Gateway**
- **Spring Cloud Netflix Eureka**
- **H2/MySQL Database**
- **Maven** for build management
- **JUnit 5** for testing
- **OpenAPI 3** for API documentation

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL (optional, H2 is used by default)

### Running the Application

1. Clone the repository
2. Build all services: `mvn clean install`
3. Start services in the following order:
   - Discovery Service: `cd discovery-service && mvn spring-boot:run`
   - API Gateway: `cd api-gateway && mvn spring-boot:run`
   - User Service: `cd user-service && mvn spring-boot:run`
   - Product Service: `cd product-service && mvn spring-boot:run`
   - Order Service: `cd order-service && mvn spring-boot:run`
   - Payment Service: `cd payment-service && mvn spring-boot:run`
   - Notification Service: `cd notification-service && mvn spring-boot:run`

### Service Endpoints

- **Discovery Service**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081
- **Product Service**: http://localhost:8082
- **Order Service**: http://localhost:8083
- **Payment Service**: http://localhost:8084
- **Notification Service**: http://localhost:8085

### API Documentation

Each service provides OpenAPI documentation available at:
- `http://localhost:<port>/swagger-ui.html`

## Features

### User Management
- User registration and login
- JWT-based authentication
- Role-based authorization (ADMIN, USER)
- User profile management

### Product Management
- CRUD operations for products
- Category management
- Inventory tracking
- Product search and filtering

### Order Processing
- Shopping cart functionality
- Order creation and management
- Order status tracking
- Order history

### Payment Integration
- Payment processing simulation
- Payment status tracking
- Refund processing

### Notification System
- Order confirmation emails
- Payment notifications
- Status update notifications

### API Gateway Features
- Request routing
- Load balancing
- Authentication verification
- Rate limiting
- Request/response logging

## Testing

Run tests for all services:
```bash
mvn test
```

## Learning Objectives Achieved

- ✅ Microservices architecture and design patterns
- ✅ Spring Security for authentication and authorization
- ✅ Spring Data JPA for advanced database operations
- ✅ Service discovery and API gateway patterns
- ✅ Inter-service communication
- ✅ Comprehensive monitoring and logging
- ✅ Distributed system design principles