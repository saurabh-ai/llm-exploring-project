# Distributed Task Scheduler System

A comprehensive distributed task scheduler system demonstrating advanced Java concepts including microservices architecture, job queuing, monitoring dashboard, and fault tolerance. This capstone project consolidates all Phase 3 learning objectives: Design Patterns, JDBC Database Integration, and Spring Framework.

## 🏗️ Architecture Overview

This system implements a microservices architecture with the following components:

### Core Services
- **Eureka Server** (Port 8761): Service Registry and Discovery Server
- **Config Service** (Port 8888): Centralized configuration management
- **User Service** (Port 8081): User authentication, JWT tokens, role-based access control
- **Scheduler Service** (Port 8082): Core scheduling engine with job management using Quartz
- **Executor Service** (Port 8083): Distributed task execution workers with load balancing
- **Notification Service** (Port 8084): Multi-channel notification delivery (Email, SMS, Push)
- **Monitoring Service** (Port 8085): Real-time dashboard and metrics collection
- **API Gateway** (Port 8080): Single entry point with routing and circuit breaker patterns

### Technology Stack
- **Spring Boot 3.x**: Microservices framework
- **Spring Cloud**: Service discovery, configuration, gateway
- **Spring Security**: JWT authentication and role-based authorization
- **Spring Data JPA**: Database persistence with H2/PostgreSQL
- **RabbitMQ**: Message queuing for asynchronous communication
- **Quartz Scheduler**: Advanced job scheduling with cron expressions
- **Circuit Breaker**: Resilience4j for fault tolerance
- **Docker**: Containerization and orchestration
- **Swagger/OpenAPI**: API documentation
- **Micrometer/Prometheus**: Metrics collection
- **Thymeleaf**: Web dashboard templates

## 🎯 Design Patterns Implemented

- **Microservices Pattern**: Distributed service architecture
- **Gateway Pattern**: API gateway for unified access
- **Circuit Breaker Pattern**: Fault tolerance and resilience
- **Observer Pattern**: Event-driven job notifications and monitoring
- **Strategy Pattern**: Multiple job execution strategies
- **Factory Pattern**: Job and task creation
- **Repository Pattern**: Data access abstraction

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, for containerized deployment)

### Local Development Setup

1. **Build the project**
   ```bash
   mvn clean install
   ```

2. **Start services in order**
   ```bash
   # 1. Start Eureka Server (Service Discovery)
   cd eureka-server && mvn spring-boot:run &
   
   # 2. Start Config Service (Configuration Management)
   cd config-service && mvn spring-boot:run &
   
   # 3. Start User Service (Authentication)
   cd user-service && mvn spring-boot:run &
   
   # 4. Start Scheduler Service
   cd scheduler-service && mvn spring-boot:run &
   
   # 5. Start Executor Service
   cd executor-service && mvn spring-boot:run &
   
   # 6. Start Notification Service
   cd notification-service && mvn spring-boot:run &
   
   # 7. Start Monitoring Service
   cd monitoring-service && mvn spring-boot:run &
   
   # 8. Start API Gateway
   cd api-gateway && mvn spring-boot:run &
   ```

3. **Access the application**
   - **Dashboard**: http://localhost:8085/dashboard
   - **API Gateway**: http://localhost:8080
   - **User Service API**: http://localhost:8081/user/api/v1/auth
   - **Scheduler API**: http://localhost:8082/scheduler/api/v1/jobs
   - **Executor API**: http://localhost:8083/executor/api/v1/executor
   - **Notification API**: http://localhost:8084/notification/api/v1/notifications
   - **Service Discovery**: http://localhost:8761/
   - **Config Service**: http://localhost:8888/

## 📊 Features Demonstration

### 1. Job Management
```bash
# Create a new job
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Report Job",
    "description": "Generate daily reports",
    "priority": "HIGH",
    "cronExpression": "0 0 9 * * ?",
    "jobClass": "com.example.ReportJob",
    "maxRetries": 3
  }'

# Schedule the job
curl -X POST http://localhost:8080/api/v1/jobs/{job-id}/schedule

# Get all jobs
curl http://localhost:8080/api/v1/jobs
```

### 2. Job Execution Types
The executor service supports multiple job types:
- `com.example.EmailJob`: Email sending simulation (2s execution)
- `com.example.ReportJob`: Report generation simulation (5s execution)  
- `com.example.DataImportJob`: Data import simulation (10s execution)
- Default jobs: Generic task simulation (3s execution)

### 3. Monitoring Dashboard
Visit http://localhost:8083/dashboard to see:
- Real-time job statistics
- System health monitoring
- Recent job executions
- Job status distribution

## 🧪 Testing

### Unit Tests
```bash
# Run all unit tests
mvn test

# Run tests for specific service
cd scheduler-service && mvn test
```

## 🎓 Learning Objectives Achieved

### ✅ Design Patterns Mastery
- **Microservices**: Complete distributed architecture
- **Circuit Breaker**: Fault tolerance implementation
- **Observer**: Event-driven monitoring system
- **Strategy**: Multiple job execution strategies
- **Factory**: Job creation and management
- **Repository**: Data access abstraction

### ✅ Database Integration Excellence  
- **JPA/Hibernate**: Entity relationships and queries
- **Transaction Management**: Service-level transactions
- **Connection Pooling**: Optimized database access
- **Multiple Databases**: H2 for development, PostgreSQL ready

### ✅ Spring Framework Expertise
- **Spring Boot**: Microservices development
- **Spring Cloud**: Service discovery and configuration
- **Spring Data JPA**: Repository pattern implementation
- **Spring Security**: API protection ready
- **Spring Actuator**: Health checks and metrics

## 🚦 System Status

### ✅ Implemented Features
- [x] Microservices architecture with 8 services
- [x] Separate Eureka Server for service discovery (Port 8761)
- [x] User Management Service with JWT authentication (Port 8081)
- [x] Job creation, scheduling, and management (Port 8082)
- [x] Distributed job execution with load balancing (Port 8083)
- [x] Notification Service with multi-channel delivery (Port 8084)
- [x] Real-time monitoring dashboard (Port 8085)
- [x] API Gateway with routing and fault tolerance (Port 8080)
- [x] Circuit breaker fault tolerance patterns
- [x] Centralized configuration management
- [x] REST API with Swagger documentation
- [x] Docker containerization setup with RabbitMQ
- [x] PostgreSQL database schema with initialization script
- [x] Health checks and metrics collection
- [x] Unit and integration tests infrastructure

## 📄 License

This project is part of the Java Mastery learning curriculum and is intended for educational purposes.