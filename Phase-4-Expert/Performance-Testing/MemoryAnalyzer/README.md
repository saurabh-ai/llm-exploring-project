# JVM Memory Usage Analyzer

A comprehensive JVM memory profiling and analysis tool built with Spring Boot that provides real-time memory monitoring, leak detection, and performance analysis capabilities.

## Features

### Real-time Memory Monitoring
- **Heap Memory Tracking**: Monitor heap usage, committed memory, and maximum heap size
- **Non-Heap Memory Analysis**: Track metaspace, code cache, and other non-heap areas
- **Memory Pool Monitoring**: Detailed analysis of Eden space, Survivor space, Old Generation, and Metaspace
- **Garbage Collection Metrics**: Track GC frequency, duration, and impact on performance

### Memory Leak Detection
- **Automated Analysis**: Continuous monitoring with configurable thresholds
- **Trend Analysis**: Pattern recognition for identifying memory leaks
- **Alert System**: Real-time notifications when potential issues are detected
- **Historical Data**: Maintain snapshots for trend analysis

### Performance Analysis
- **Thread Monitoring**: Track thread count and peak thread usage
- **Usage Percentages**: Calculate memory utilization ratios
- **Health Assessments**: Automated health status with recommendations
- **Metric Collection**: Integration with Micrometer and Prometheus

## Technology Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Actuator** for monitoring
- **Micrometer** for metrics
- **OpenAPI 3** for API documentation
- **JUnit 5** for testing

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Running the Application

1. Clone the repository
2. Navigate to the Memory Analyzer directory:
   ```bash
   cd Phase-4-Expert/Performance-Testing/MemoryAnalyzer
   ```
3. Build the application:
   ```bash
   mvn clean compile
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8090 with context path `/memory-analyzer`.

### API Endpoints

**Base URL**: `http://localhost:8090/memory-analyzer`

#### Memory Analysis Endpoints

- **GET** `/api/memory/current` - Get current memory snapshot
- **GET** `/api/memory/snapshots?count=50` - Get recent memory snapshots
- **GET** `/api/memory/snapshots/all` - Get all stored snapshots
- **DELETE** `/api/memory/snapshots` - Clear all snapshots
- **POST** `/api/memory/gc` - Force garbage collection
- **GET** `/api/memory/leak-detection` - Check for memory leaks
- **GET** `/api/memory/health` - Get memory health status

#### Monitoring Endpoints

- **GET** `/actuator/health` - Application health
- **GET** `/actuator/metrics` - Application metrics
- **GET** `/actuator/prometheus` - Prometheus metrics

### API Documentation

Interactive API documentation is available at:
- Swagger UI: `http://localhost:8090/memory-analyzer/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8090/memory-analyzer/api-docs`

## Usage Examples

### Get Current Memory Usage
```bash
curl -X GET "http://localhost:8090/memory-analyzer/api/memory/current"
```

### Check Memory Health
```bash
curl -X GET "http://localhost:8090/memory-analyzer/api/memory/health"
```

### Detect Memory Leaks
```bash
curl -X GET "http://localhost:8090/memory-analyzer/api/memory/leak-detection"
```

### Force Garbage Collection
```bash
curl -X POST "http://localhost:8090/memory-analyzer/api/memory/gc"
```

## Memory Snapshot Structure

```json
{
  "timestamp": "2025-01-21 10:30:00",
  "heapUsed": 134217728,
  "heapCommitted": 268435456,
  "heapMax": 1073741824,
  "heapUsagePercentage": 12.5,
  "nonHeapUsed": 67108864,
  "nonHeapCommitted": 134217728,
  "gcCollectionCount": 15,
  "gcCollectionTime": 123,
  "edenSpaceUsed": 33554432,
  "survivorSpaceUsed": 8388608,
  "oldGenUsed": 92274688,
  "metaspaceUsed": 45088768,
  "threadCount": 25,
  "peakThreadCount": 30
}
```

## Configuration

### Application Properties

```properties
# Server configuration
server.port=8090
server.servlet.context-path=/memory-analyzer

# Memory monitoring configuration
memory.analyzer.collection.interval=30000
memory.analyzer.max.snapshots=1000
memory.analyzer.leak.detection.threshold=80.0

# Actuator endpoints
management.endpoints.web.exposure.include=health,metrics,prometheus
```

### Memory Leak Detection

The tool uses a simple algorithm to detect potential memory leaks:
- Monitors the last 10 memory snapshots
- Calculates average heap usage percentage
- Triggers alert if average usage exceeds 80% (configurable)

## Testing

Run the test suite:
```bash
mvn test
```

## Integration with Other Tools

### Prometheus Integration
The application exports metrics in Prometheus format at `/actuator/prometheus`.

### Grafana Dashboard
Use the exported metrics to create comprehensive monitoring dashboards.

### CI/CD Integration
Include memory analysis in your build pipeline for performance regression testing.

## Learning Objectives Achieved

- ✅ JVM Memory Management and Monitoring
- ✅ Real-time Performance Analysis
- ✅ Memory Leak Detection Algorithms
- ✅ Spring Boot Actuator Integration
- ✅ Metrics Collection with Micrometer
- ✅ RESTful API Design for Monitoring Tools
- ✅ Enterprise Monitoring Patterns

## Future Enhancements

- Memory dump analysis
- Advanced leak detection algorithms
- Integration with APM tools
- Historical data persistence
- Advanced alerting mechanisms
- Memory optimization recommendations