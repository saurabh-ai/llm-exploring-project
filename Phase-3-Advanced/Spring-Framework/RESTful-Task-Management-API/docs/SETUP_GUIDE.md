# Setup Guide - Task Management API

This guide will help you set up and run the Task Management API on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or later
- **Maven 3.6+**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)
- **Postman** or **curl** (for API testing)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Phase-3-Advanced/Spring-Framework/RESTful-Task-Management-API
```

### 2. Build the Project

```bash
mvn clean compile
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### 4. Verify Installation

Open your browser or use curl to test:

```bash
curl http://localhost:8080/api/users
```

You should see a JSON response with sample users.

## Development Setup

### IDE Configuration

#### IntelliJ IDEA
1. Import the project as a Maven project
2. Set Project SDK to Java 17
3. Enable annotation processing for Lombok (if using)
4. Install Spring Boot plugin for better development experience

#### Eclipse
1. Import as Existing Maven Project
2. Configure Java Build Path to use JDK 17
3. Install Spring Tools Suite (STS) plugin

#### VS Code
1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Configure Java runtime to Java 17

### Configuration Files

The application uses three configuration files:

#### `application.yml` (Main configuration)
- Default configuration
- Database settings for H2 in-memory database
- Logging configuration
- Swagger settings

#### `application-dev.yml` (Development profile)
- Development-specific settings
- H2 console enabled
- Debug logging enabled

#### `application-prod.yml` (Production profile)
- Production-optimized settings
- MySQL database configuration
- Minimal logging

### Environment Profiles

To run with specific profiles:

```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Database Setup

### H2 Database (Development - Default)

The application uses H2 in-memory database by default:
- **URL**: `jdbc:h2:mem:devdb`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: `http://localhost:8080/api/h2-console`

Sample data is automatically loaded on startup.

### MySQL Database (Production)

For production, configure MySQL:

1. Create database:
```sql
CREATE DATABASE task_management_db;
```

2. Set environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export SERVER_PORT=8080
```

3. Update `application-prod.yml` if needed

## Testing the API

### Using Swagger UI

1. Start the application
2. Open `http://localhost:8080/api/swagger-ui.html`
3. Explore and test all endpoints interactively

### Using Postman

1. Import the API collection (if available)
2. Set base URL to `http://localhost:8080/api`
3. Test various endpoints

### Using curl

#### Get all users:
```bash
curl -X GET "http://localhost:8080/api/users"
```

#### Create a new user:
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new_user",
    "email": "new@example.com",
    "firstName": "New",
    "lastName": "User"
  }'
```

#### Get all tasks:
```bash
curl -X GET "http://localhost:8080/api/tasks"
```

#### Create a new task:
```bash
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Task",
    "description": "Task description",
    "priority": 2,
    "userId": 1,
    "categoryId": 1
  }'
```

#### Get analytics:
```bash
curl -X GET "http://localhost:8080/api/tasks/analytics"
```

## Building for Production

### Create JAR file

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory.

### Run the JAR

```bash
java -jar target/task-management-api-1.0.0.jar --spring.profiles.active=prod
```

### Docker Setup (Optional)

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:

```bash
docker build -t task-management-api .
docker run -p 8080:8080 task-management-api
```

## Monitoring and Health Checks

### Health Endpoints

- **Application health**: `http://localhost:8080/api/actuator/health`
- **Application info**: `http://localhost:8080/api/actuator/info`
- **Metrics**: `http://localhost:8080/api/actuator/metrics`

### Logging

Logs are configured to output to console. For production, configure file logging:

```yaml
logging:
  file:
    name: app.log
  level:
    com.javamastery.taskapi: INFO
```

## Development Tips

### Hot Reload

Spring Boot DevTools is included for automatic restart during development.

### Database Changes

When you modify entities, Hibernate will automatically update the schema due to `ddl-auto: create-drop` in development.

### Adding New Features

1. Create/modify entities in `model` package
2. Add repository methods in `repository` package
3. Implement business logic in `service` package
4. Add REST endpoints in `controller` package
5. Create DTOs in `dto` package
6. Add tests in `test` package

### Code Coverage

Generate test coverage report:

```bash
mvn test
mvn jacoco:report
```

Report will be in `target/site/jacoco/index.html`

## Troubleshooting

### Common Issues

#### Port 8080 already in use
```bash
# Kill process using port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml
server:
  port: 8081
```

#### Database connection errors
- Check H2 console at `/h2-console`
- Verify JDBC URL matches configuration
- Ensure no other applications are using the database

#### Build errors
```bash
# Clean and rebuild
mvn clean compile

# Check Java version
java -version

# Check Maven version
mvn -version
```

#### Sample data not loading
- Check application logs for SQL errors
- Verify `defer-datasource-initialization: true` is set
- Ensure `data.sql` file is in `src/main/resources`

### Getting Help

1. Check application logs for detailed error messages
2. Use H2 console to inspect database state
3. Test individual endpoints with Postman or curl
4. Review Swagger documentation for API details
5. Check actuator endpoints for application health

## Next Steps

After setting up the application:

1. Explore the Swagger UI documentation
2. Test all API endpoints
3. Review the sample data structure
4. Implement additional features or customizations
5. Set up proper authentication for production use
6. Configure production database and monitoring
7. Write comprehensive tests for your use cases

## Performance Considerations

- The application uses connection pooling (HikariCP)
- JPA queries are optimized with proper fetch strategies
- Pagination is implemented for large data sets
- Consider adding caching for frequently accessed data

For production deployments, consider:
- Database connection pool sizing
- JVM memory settings
- Load balancing
- Database optimization
- Monitoring and alerting setup