# Distributed Task Scheduler System

A comprehensive distributed task scheduler system demonstrating advanced Java concepts including microservices architecture, job queuing, monitoring dashboard, and fault tolerance. This capstone project consolidates all Phase 3 learning objectives: Design Patterns, JDBC Database Integration, and Spring Framework.

## 🏗️ Architecture Overview

This system implements a microservices architecture with the following components:

### Core Services
- **Config Service** (Port 8888): Service discovery (Eureka) + centralized configuration management
- **Scheduler Service** (Port 8081): Core scheduling engine with job management using Quartz
- **Executor Service** (Port 8082): Distributed task execution workers with load balancing
- **Monitoring Service** (Port 8083): Real-time dashboard and metrics collection
- **API Gateway** (Port 8080): Single entry point with routing and circuit breaker patterns

### Technology Stack
- **Spring Boot 3.x**: Microservices framework
- **Spring Cloud**: Service discovery, configuration, gateway
- **Spring Data JPA**: Database persistence with H2/PostgreSQL
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
   # 1. Start Config Service (Service Discovery)
   cd config-service && mvn spring-boot:run &
   
   # 2. Start Scheduler Service
   cd scheduler-service && mvn spring-boot:run &
   
   # 3. Start Executor Service
   cd executor-service && mvn spring-boot:run &
   
   # 4. Start Monitoring Service
   cd monitoring-service && mvn spring-boot:run &
   
   # 5. Start API Gateway
   cd api-gateway && mvn spring-boot:run &
   ```

3. **Access the application**
   - **Dashboard**: http://localhost:8083/dashboard
   - **API Gateway**: http://localhost:8080
   - **Scheduler API**: http://localhost:8081/scheduler/api/v1/jobs
   - **Executor API**: http://localhost:8082/executor/api/v1/executor
   - **Service Discovery**: http://localhost:8888/

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
- [x] Microservices architecture with 5 services
- [x] Job creation, scheduling, and management
- [x] Distributed job execution with load balancing
- [x] Real-time monitoring dashboard
- [x] Circuit breaker fault tolerance
- [x] Service discovery and configuration
- [x] REST API with Swagger documentation
- [x] Docker containerization setup
- [x] Health checks and metrics collection
- [x] Unit and integration tests

## 📄 License

This project is part of the Java Mastery learning curriculum and is intended for educational purposes.