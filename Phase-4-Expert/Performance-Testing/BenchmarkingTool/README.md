# Enhanced Performance Benchmarking Tool

A comprehensive performance testing framework that integrates JMeter load testing, database performance analysis, microservice stress testing, and advanced reporting capabilities.

## Overview

This enhanced tool provides a Spring Boot-based REST API for running comprehensive performance tests including traditional load testing, database performance benchmarking, and microservice stress testing with detailed reporting and analysis.

## Enhanced Features

### Core Capabilities
- **JMeter Integration** - Native Apache JMeter integration for professional load testing
- **Database Performance Testing** - Comprehensive database operation benchmarking
- **Microservice Stress Testing** - Multi-service concurrent testing and circuit breaker analysis
- **Advanced Reporting** - HTML reports with charts, graphs, and recommendations
- **REST API Interface** - Comprehensive REST endpoints for all testing capabilities
- **Real-time Metrics** - Detailed performance metrics collection and analysis

### Database Performance Testing
- **Connection Pool Testing** - Concurrent connection performance analysis
- **CRUD Operations Benchmarking** - Insert, Select, Update, Delete performance testing
- **Concurrent Operations Testing** - Multi-threaded database operation stress testing
- **Performance Statistics** - Detailed execution time analysis and recommendations

### Microservice Testing
- **Multi-Service Testing** - Concurrent testing of multiple microservice endpoints
- **Circuit Breaker Testing** - Fault tolerance and resilience testing
- **Service Health Monitoring** - Success rate and response time analysis
- **Load Distribution** - Configurable concurrent thread and request distribution

### Advanced Reporting
- **HTML Reports** - Professional reports with interactive charts
- **Performance Charts** - Visual response time and throughput analysis
- **Executive Summary** - High-level performance metrics dashboard
- **Recommendations Engine** - Automated performance improvement suggestions
- **JSON Export** - Machine-readable test result summaries

### Load Test Scenarios
1. **Light Load Test** - 10 threads, 30s ramp-up, 100 iterations, 60s duration
2. **Medium Load Test** - 50 threads, 60s ramp-up, 200 iterations, 300s duration
3. **Heavy Load Test** - 200 threads, 120s ramp-up, 500 iterations, 600s duration
4. **Stress Test** - 500 threads, 300s ramp-up, 1000 iterations, 900s duration

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- H2 Database (included)
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

The application will start on port 8091 with enhanced capabilities.

## API Usage

### Base URL
```
http://localhost:8091/api/benchmark
```

### Enhanced API Endpoints

#### Load Testing Endpoints

**Run Custom Load Test (Synchronous)**
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

**Run Quick Test**
```bash
POST /quick-test?host=localhost&endpoint=/api/health&threads=10
```

#### Database Performance Testing

**Run Database Performance Test**
```bash
POST /database/run-sync
Content-Type: application/json

{
  "testName": "Database Performance Test",
  "insertCount": 1000,
  "selectCount": 500,
  "updateCount": 300,
  "deleteCount": 100,
  "concurrentConnections": 10,
  "concurrentOperations": 200
}
```

#### Microservice Stress Testing

**Run Microservice Stress Test**
```bash
POST /microservice/run-sync
Content-Type: application/json

{
  "testName": "Microservice Stress Test",
  "serviceUrls": [
    "http://localhost:8081/api/users",
    "http://localhost:8082/api/products",
    "http://localhost:8083/api/orders"
  ],
  "requestsPerService": 100,
  "concurrentThreads": 10,
  "httpMethod": "GET"
}
```

#### Reporting Endpoints

**Generate Comprehensive Report**
```bash
POST /report/generate
Content-Type: application/json

{
  "reportName": "Performance Analysis Report"
}
```

**Get Test Summary**
```bash
GET /report/summary
```

### Database Console
Access the H2 database console at: `http://localhost:8091/h2-console`
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### API Documentation
Interactive API documentation available at:
- Swagger UI: `http://localhost:8091/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8091/api-docs`

## Example Results

### Load Test Result
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
  "executionTime": "2025-01-21 14:30:25",
  "durationSeconds": 60
}
```

### Database Performance Result
```json
{
  "testName": "Database Performance Test",
  "success": true,
  "totalExecutionTime": 5432,
  "averageOperationTime": 906.33,
  "testResults": {
    "connection_pool": 234,
    "insert_performance": 1876,
    "select_performance": 543,
    "update_performance": 987,
    "delete_performance": 123,
    "concurrent_operations": 1669
  }
}
```

### Microservice Test Result
```json
{
  "testName": "Microservice Stress Test",
  "success": true,
  "totalRequests": 300,
  "totalSuccessfulRequests": 285,
  "totalFailedRequests": 15,
  "overallSuccessRate": 95.0,
  "averageResponseTime": 245.7,
  "maxResponseTime": 1234,
  "minResponseTime": 89
}
```

## Configuration

### Application Properties
```yaml
# Enhanced configuration in application.yml
performance:
  database:
    test:
      table: performance_test
      insert-count: 1000
      concurrent-connections: 10

microservice:
  test:
    default-timeout: 30
    max-concurrent-threads: 20

reporting:
  output-directory: target/reports
  include-charts: true
```

## Learning Objectives Achieved

- ✅ Advanced JMeter integration and automation
- ✅ Database performance testing and optimization
- ✅ Microservice architecture stress testing
- ✅ Circuit breaker and resilience testing
- ✅ Performance metrics collection and analysis
- ✅ Advanced reporting with data visualization
- ✅ REST API design for testing frameworks
- ✅ CI/CD integration capabilities
- ✅ Enterprise performance testing patterns
- ✅ Automated performance recommendation systems

## Integration with CI/CD

The tool provides REST APIs that can be easily integrated into CI/CD pipelines for:
- Automated performance regression testing
- Database performance validation
- Microservice health and resilience monitoring
- Performance threshold validation
- Automated reporting and alerting

## Future Enhancements

- Integration with APM tools (New Relic, AppDynamics)
- Distributed tracing correlation
- Advanced machine learning-based performance analysis
- Integration with monitoring systems (Prometheus, Grafana)
- Real-time alerting and notification systems