# RESTful Task Management API

A comprehensive RESTful web service for task management built with Spring Boot 3.x, demonstrating enterprise development practices and modern Java development.

## 🎯 Overview

This Task Management API provides a complete backend solution for task management applications with features including user management, task CRUD operations, category organization, advanced search capabilities, and analytics. It's built following Spring Boot best practices and includes comprehensive error handling, validation, and documentation.

## ✨ Features

### Core Functionality
- **User Management**: Registration, profile management, task assignment
- **Task Management**: Complete CRUD operations with rich metadata
- **Category Organization**: Categorize tasks with color coding
- **Task Status Tracking**: TODO, IN_PROGRESS, COMPLETED status management
- **Due Date Management**: Track deadlines and identify overdue tasks
- **Priority System**: Three-level priority system (Low, Medium, High)

### Advanced Features
- **Advanced Search**: Filter by multiple criteria (status, priority, dates, keywords)
- **Analytics Dashboard**: Task statistics by status, priority, and category
- **Overdue Detection**: Automatic identification of overdue tasks
- **Pagination & Sorting**: Efficient data handling for large datasets
- **Comprehensive Validation**: Input validation with detailed error messages
- **CORS Support**: Ready for frontend integration

### Technical Features
- **RESTful Design**: Follows REST principles and conventions
- **OpenAPI Documentation**: Interactive Swagger UI documentation
- **Error Handling**: Global exception handling with consistent responses
- **Database Support**: H2 for development, MySQL for production
- **Health Monitoring**: Spring Actuator endpoints for monitoring
- **Hot Reload**: Development tools for faster iteration

## 🛠 Tech Stack

- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2.1** - Framework and auto-configuration
- **Spring Web** - REST API development
- **Spring Data JPA** - Data persistence and repository pattern
- **Spring Validation** - Request validation and error handling
- **Spring Boot DevTools** - Development productivity tools
- **H2 Database** - In-memory database for development
- **MySQL** - Production database support
- **Swagger/OpenAPI 3** - API documentation
- **Maven** - Dependency management and build tool
- **JUnit 5** - Testing framework
- **Jacoco** - Code coverage analysis

## 🚀 Quick Start

### Prerequisites
- Java 17 or later
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Phase-3-Advanced/Spring-Framework/RESTful-Task-Management-API
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Test the API**
   ```bash
   curl http://localhost:8080/api/users
   ```

The application will be available at `http://localhost:8080/api`

## 📚 Documentation

### API Documentation
- **Interactive Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/api-docs`
- **Detailed API Docs**: [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)
- **Setup Guide**: [SETUP_GUIDE.md](docs/SETUP_GUIDE.md)

### Health & Monitoring
- **Health Check**: `http://localhost:8080/api/actuator/health`
- **Application Info**: `http://localhost:8080/api/actuator/info`
- **Metrics**: `http://localhost:8080/api/actuator/metrics`
- **Database Console**: `http://localhost:8080/api/h2-console` (development)

## 🔧 API Endpoints

### Users
```http
GET    /api/users              # Get all users (paginated)
GET    /api/users/{id}         # Get user by ID
POST   /api/users              # Create new user
PUT    /api/users/{id}         # Update user
DELETE /api/users/{id}         # Delete user
GET    /api/users/{id}/tasks   # Get user's tasks
```

### Tasks
```http
GET    /api/tasks                    # Get all tasks (paginated)
GET    /api/tasks/{id}               # Get task by ID
POST   /api/tasks                    # Create new task
PUT    /api/tasks/{id}               # Update task
DELETE /api/tasks/{id}               # Delete task
PATCH  /api/tasks/{id}/status        # Update task status
GET    /api/tasks/search             # Advanced search
GET    /api/tasks/overdue            # Get overdue tasks
GET    /api/tasks/due-soon           # Get tasks due soon
GET    /api/tasks/analytics          # Get task analytics
```

### Categories
```http
GET    /api/categories               # Get all categories
GET    /api/categories/{id}          # Get category by ID
POST   /api/categories               # Create new category
PUT    /api/categories/{id}          # Update category
DELETE /api/categories/{id}          # Delete category
GET    /api/categories/search        # Search categories
GET    /api/categories/with-counts   # Categories with task counts
```

## 📊 Sample Data

The application comes with pre-loaded sample data:
- **3 Users**: john_doe, jane_smith, bob_wilson
- **5 Categories**: Work, Personal, Learning, Health, Shopping
- **11 Tasks**: Various statuses, priorities, and due dates
- **Analytics Ready**: Includes overdue tasks for testing

## 🏗 Project Structure

```
src/main/java/com/javamastery/taskapi/
├── TaskManagementApiApplication.java  # Main application class
├── controller/                        # REST controllers
│   ├── TaskController.java
│   ├── UserController.java
│   └── CategoryController.java
├── service/                          # Business logic layer
│   ├── TaskService.java
│   ├── UserService.java
│   └── CategoryService.java
├── repository/                       # Data access layer
│   ├── TaskRepository.java
│   ├── UserRepository.java
│   └── CategoryRepository.java
├── model/                           # JPA entities
│   ├── Task.java
│   ├── User.java
│   ├── Category.java
│   └── TaskStatus.java
├── dto/                             # Data transfer objects
│   ├── TaskDto.java
│   ├── CreateTaskRequest.java
│   ├── UpdateTaskRequest.java
│   ├── UserDto.java
│   ├── CreateUserRequest.java
│   ├── CategoryDto.java
│   ├── CreateCategoryRequest.java
│   ├── TaskSearchRequest.java
│   └── ApiResponse.java
├── exception/                       # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── TaskNotFoundException.java
│   ├── UserNotFoundException.java
│   ├── CategoryNotFoundException.java
│   └── ValidationException.java
└── config/                          # Configuration classes
    ├── SwaggerConfig.java
    └── CorsConfig.java
```

## 🔍 Example Usage

### Create a User
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_johnson",
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Johnson"
  }'
```

### Create a Task
```bash
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete API Documentation",
    "description": "Write comprehensive API documentation",
    "dueDate": "2025-08-01 12:00:00",
    "priority": 2,
    "userId": 1,
    "categoryId": 1
  }'
```

### Search Tasks
```bash
curl "http://localhost:8080/api/tasks/search?status=TODO&priority=3&page=0&size=10"
```

### Get Analytics
```bash
curl "http://localhost:8080/api/tasks/analytics"
```

## 📈 Response Format

All API responses use a consistent format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  },
  "timestamp": "2025-07-21T09:34:16.177797867"
}
```

Error responses:
```json
{
  "success": false,
  "error": "Validation failed",
  "message": "One or more fields have validation errors",
  "data": {
    "field1": "Error message",
    "field2": "Another error message"
  },
  "timestamp": "2025-07-21T09:34:16.177797867"
}
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Generate Coverage Report
```bash
mvn jacoco:report
```
Coverage report will be available at `target/site/jacoco/index.html`

### Manual Testing
Use the included Swagger UI at `http://localhost:8080/api/swagger-ui.html` for interactive testing.

## 🔧 Configuration

### Environment Profiles
- **Development** (default): H2 database, debug logging, console enabled
- **Production**: MySQL database, optimized logging, security settings

### Database Configuration
```yaml
# Development (H2)
spring:
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password: 

# Production (MySQL)
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/task_management_db
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:password}
```

## 🚀 Deployment

### Build JAR
```bash
mvn clean package
```

### Run in Production
```bash
java -jar target/task-management-api-1.0.0.jar --spring.profiles.active=prod
```

### Environment Variables
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `SERVER_PORT` - Server port (default: 8080)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- OpenAPI/Swagger for API documentation tools
- H2 Database for the excellent in-memory database
- Maven for dependency management

## 📞 Support

For questions and support:
- Check the [Setup Guide](docs/SETUP_GUIDE.md) for common issues
- Review the [API Documentation](docs/API_DOCUMENTATION.md) for endpoint details
- Use the interactive Swagger UI for testing
- Check application logs for detailed error information

---

**Built with ❤️ using Spring Boot 3.x and Java 17**