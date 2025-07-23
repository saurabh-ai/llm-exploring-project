# Configuration Management System

A comprehensive Spring Cloud Config-based configuration management system for microservices architecture, providing centralized configuration management, environment-specific settings, and dynamic configuration updates.

## Overview

This configuration management system provides a centralized approach to managing application configurations across different environments and microservices. It includes a Spring Cloud Config Server with security, environment-specific configurations, and dynamic refresh capabilities.

## Architecture

### Components

1. **Config Server** - Centralized configuration server using Spring Cloud Config
2. **Config Client** - Client library for consuming configuration from the server
3. **Configuration Repository** - File-based configuration storage with environment profiles
4. **Security Layer** - Authentication and authorization for configuration access
5. **Dynamic Refresh** - Spring Cloud Bus integration for configuration updates

### Features

- **Centralized Configuration** - Single source of truth for all application configurations
- **Environment-Specific Settings** - Separate configurations for dev, staging, and production
- **Security** - Authentication and authorization for configuration access
- **Dynamic Refresh** - Runtime configuration updates without service restart
- **Version Control** - Git-based configuration versioning and history
- **Encryption** - Sensitive data encryption for production environments

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- RabbitMQ (optional, for dynamic refresh)

### Running the Config Server

1. **Start the Config Server:**
   ```bash
   cd config-server
   mvn spring-boot:run
   ```

2. **The server will start on port 8888**

3. **Access configuration:**
   ```bash
   # Get default configuration
   curl -u config-client:client-secret http://localhost:8888/ecommerce-platform/default
   
   # Get development configuration
   curl -u config-client:client-secret http://localhost:8888/ecommerce-platform/dev
   
   # Get production configuration
   curl -u config-client:client-secret http://localhost:8888/ecommerce-platform/prod
   ```

### Configuration Structure

The configuration repository follows Spring Cloud Config naming conventions:

```
config-data/
├── ecommerce-platform.yml          # Default configuration
├── ecommerce-platform-dev.yml      # Development environment
├── ecommerce-platform-prod.yml     # Production environment
└── performance-tools.yml           # Performance testing tools config
```

### Security Configuration

The Config Server includes built-in security with:

- **Admin User**: `config-admin` / `admin-secret`
- **Client User**: `config-client` / `client-secret`
- **HTTPS Support**: Configure SSL certificates for production
- **Token-based Authentication**: JWT tokens for service-to-service communication

## Configuration Examples

### E-commerce Platform Configuration

**Default Configuration (`ecommerce-platform.yml`):**
```yaml
# Database configuration
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password

# Eureka Client Configuration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

# Circuit Breaker Configuration
resilience4j:
  circuitbreaker:
    instances:
      default:
        sliding-window-size: 10
        failure-rate-threshold: 50
```

**Development Environment (`ecommerce-platform-dev.yml`):**
```yaml
# Development-specific settings
logging:
  level:
    com.javamastery.ecommerce: DEBUG
    org.springframework.security: DEBUG

# Development JWT settings
jwt:
  expiration: 3600000  # 1 hour for testing
```

**Production Environment (`ecommerce-platform-prod.yml`):**
```yaml
# Production database
spring:
  datasource:
    url: jdbc:mysql://prod-mysql-server:3306/ecommerce_prod
    username: ${DB_USERNAME:ecommerce_user}
    password: ${DB_PASSWORD:secure_password}

# Production logging
logging:
  level:
    com.javamastery.ecommerce: INFO
  file:
    name: /var/log/ecommerce-platform/application.log
```

### Performance Tools Configuration

```yaml
# Memory Analyzer Configuration
memory:
  analyzer:
    collection:
      interval: 30000
    leak:
      detection:
        threshold: 80.0

# Performance Testing Configuration
performance:
  database:
    test:
      insert-count: 1000
      concurrent-connections: 10
```

## Client Integration

### Maven Dependency

Add to your microservice `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### Bootstrap Configuration

Create `bootstrap.yml` in your client application:

```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
      username: config-client
      password: client-secret
  profiles:
    active: dev
```

### Dynamic Refresh

Enable configuration refresh in your client:

```java
@RestController
@RefreshScope
public class ConfigurableController {
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        return Map.of("jwtExpiration", jwtExpiration);
    }
}
```

## Environment Management

### Environment Profiles

The system supports multiple environment profiles:

- **default** - Base configuration shared across all environments
- **dev** - Development environment with debug settings
- **staging** - Staging environment with production-like settings
- **prod** - Production environment with optimized settings

### Configuration Precedence

Configuration follows Spring's property precedence:

1. Environment-specific configuration (`application-{profile}.yml`)
2. Default configuration (`application.yml`)
3. Client-side configuration
4. Environment variables
5. System properties

## Security Features

### Authentication

- **Basic Authentication** for config server access
- **Username/Password** authentication for different user roles
- **Token-based** authentication for service-to-service communication

### Encryption

For sensitive data in production:

```bash
# Encrypt sensitive values
curl -u config-admin:admin-secret -X POST http://localhost:8888/encrypt -d 'mysecretpassword'

# Use encrypted values in configuration
spring:
  datasource:
    password: '{cipher}AQA7grTgTYd7Yx8Qj7...'
```

### Access Control

- **Role-based access** to different configuration sections
- **IP filtering** for production environments
- **Audit logging** for configuration access

## Monitoring and Management

### Actuator Endpoints

The Config Server exposes several management endpoints:

- `/actuator/health` - Server health status
- `/actuator/info` - Server information
- `/actuator/env` - Environment properties
- `/actuator/refresh` - Refresh configuration

### Health Checks

Monitor configuration server health:

```bash
curl http://localhost:8888/actuator/health
```

### Configuration Validation

Validate configuration before deployment:

```bash
# Test configuration retrieval
curl -u config-client:client-secret http://localhost:8888/ecommerce-platform/prod
```

## Best Practices

### Configuration Organization

1. **Separate concerns** - Keep environment-specific and service-specific configurations separate
2. **Use profiles** - Leverage Spring profiles for environment management
3. **Externalize secrets** - Use environment variables for sensitive data
4. **Version control** - Keep configurations in Git for versioning and rollback

### Security Best Practices

1. **Encrypt sensitive data** - Use Spring Cloud Config encryption
2. **Secure access** - Implement proper authentication and authorization
3. **Network security** - Use HTTPS and secure network configurations
4. **Audit access** - Log and monitor configuration access

### Performance Optimization

1. **Cache configurations** - Enable client-side caching
2. **Optimize refresh** - Use Spring Cloud Bus for efficient updates
3. **Monitor performance** - Track configuration server response times
4. **Load balancing** - Use multiple config server instances for high availability

## Troubleshooting

### Common Issues

1. **Connection refused** - Check if Config Server is running on correct port
2. **Authentication failed** - Verify username/password credentials
3. **Configuration not found** - Check application name and profile
4. **Refresh not working** - Verify RabbitMQ connection for Spring Cloud Bus

### Debug Configuration

Enable debug logging for configuration issues:

```yaml
logging:
  level:
    org.springframework.cloud.config: DEBUG
    org.springframework.security: DEBUG
```

## Learning Objectives Achieved

- ✅ Centralized Configuration Management
- ✅ Environment-Specific Configuration Strategies
- ✅ Spring Cloud Config Server Implementation
- ✅ Configuration Security and Encryption
- ✅ Dynamic Configuration Refresh Patterns
- ✅ Configuration Versioning and Management
- ✅ Microservices Configuration Best Practices
- ✅ DevOps Configuration Management Integration

## Integration with DevOps Pipeline

The Configuration Management System integrates with CI/CD pipelines for:

- **Automated configuration deployment**
- **Environment-specific configuration validation**
- **Configuration drift detection**
- **Automated rollback capabilities**
- **Configuration compliance monitoring**

This system provides enterprise-grade configuration management capabilities essential for microservices architecture and DevOps practices.