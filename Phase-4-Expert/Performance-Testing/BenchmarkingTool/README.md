# Performance Benchmarking Tool

A comprehensive JMeter-integrated performance testing and benchmarking tool for Java applications.

## Overview

This tool provides a Spring Boot-based REST API for running performance tests using Apache JMeter. It includes predefined load test scenarios, custom test configuration, and detailed performance metrics collection.

## Features

### Core Capabilities
- **JMeter Integration** - Native Apache JMeter integration for professional load testing
- **REST API Interface** - Easy-to-use REST endpoints for test execution
- **Predefined Scenarios** - Ready-to-use load test scenarios (light, medium, heavy, stress)
- **Custom Test Configuration** - Flexible test parameter configuration
- **Real-time Metrics** - Comprehensive performance metrics collection
- **Async Execution** - Non-blocking test execution with result callbacks

### Load Test Scenarios
1. **Light Load Test** - 10 threads, 30s ramp-up, 100 iterations, 60s duration
2. **Medium Load Test** - 50 threads, 60s ramp-up, 200 iterations, 300s duration
3. **Heavy Load Test** - 200 threads, 120s ramp-up, 500 iterations, 600s duration
4. **Stress Test** - 500 threads, 300s ramp-up, 1000 iterations, 900s duration

### Performance Metrics
- Total/Successful/Failed request counts
- Response time statistics (min, max, average)
- Throughput (requests per second)
- Error rate percentage
- Test execution duration

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Apache JMeter 5.6+ (optional for full integration)

### Running the Application
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/benchmarking-tool-1.0.0.jar
```

The application will start on port 8090.

## API Usage

### Base URL
```
http://localhost:8090/api/benchmark
```

### Available Endpoints

#### 1. Run Custom Benchmark (Synchronous)
```bash
POST /run-sync
Content-Type: application/json

{
  "testName": "API Load Test",
  "host": "localhost",
  "port": 8080,
  "endpoint": "/api/users",
  "httpMethod": "GET",
  "threads": 20,
  "rampUpTime": 30,
  "iterations": 100,
  "durationSeconds": 60
}
```

#### 2. Run Quick Test
```bash
POST /quick-test?host=localhost&endpoint=/api/health&threads=10
```

#### 3. Run Predefined Scenario
```bash
POST /scenario/medium-load?host=localhost&endpoint=/api/products
```

#### 4. Get Available Scenarios
```bash
GET /scenarios
```

#### 5. Health Check
```bash
GET /health
```

### Example Response
```json
{
  "testName": "API Load Test",
  "totalRequests": 2000,
  "successfulRequests": 1960,
  "failedRequests": 40,
  "averageResponseTime": 125.5,
  "minResponseTime": 45.0,
  "maxResponseTime": 890.0,
  "throughput": 800.5,
  "errorRate": 0.02,
  "executionTime": "2024-01-15 14:30:25",
  "durationSeconds": 60,
  "formattedErrorRate": "2.00%",
  "formattedSuccessRate": "98.00%"
}
```

## Learning Objectives Achieved

- ✅ JMeter integration and automation
- ✅ Performance testing best practices
- ✅ Load testing scenario design
- ✅ Performance metrics collection and analysis
- ✅ REST API for test automation
- ✅ CI/CD integration capabilities
- ✅ Comprehensive performance reporting