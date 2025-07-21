# Task Management API Documentation

## Overview

The Task Management API is a comprehensive RESTful web service built with Spring Boot 3.x that provides complete task management functionality. The API supports user management, task CRUD operations, category organization, and advanced features like search, analytics, and filtering.

## Base URL

- **Development**: `http://localhost:8080/api`
- **Production**: `https://api.taskmanagement.com/api`

## Authentication

Currently, the API does not require authentication. In a production environment, you would implement JWT tokens or OAuth2.

## Response Format

All API responses follow a consistent format using the `ApiResponse` wrapper:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {}, // Response data
  "timestamp": "2025-07-21T09:34:16.177797867"
}
```

For errors:
```json
{
  "success": false,
  "error": "Error description",
  "message": "Additional error context",
  "timestamp": "2025-07-21T09:34:16.177797867"
}
```

## API Endpoints

### User Management

#### Get All Users
- **GET** `/users`
- **Parameters**: 
  - `page` (int, default: 0) - Page number (0-based)
  - `size` (int, default: 20) - Page size
  - `sortBy` (string, default: "createdAt") - Sort field
  - `sortDir` (string, default: "desc") - Sort direction (asc/desc)
- **Response**: Paginated list of users

#### Get User by ID
- **GET** `/users/{id}`
- **Response**: Single user with task count

#### Create User
- **POST** `/users`
- **Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Update User
- **PUT** `/users/{id}`
- **Request Body**: Same as create user

#### Delete User
- **DELETE** `/users/{id}`
- **Note**: Cannot delete users with existing tasks

#### Get User's Tasks
- **GET** `/users/{id}/tasks`
- **Response**: List of tasks belonging to the user

### Task Management

#### Get All Tasks
- **GET** `/tasks`
- **Parameters**: Same pagination as users
- **Response**: Paginated list of tasks with full details

#### Get Task by ID
- **GET** `/tasks/{id}`
- **Response**: Single task with user and category information

#### Create Task
- **POST** `/tasks`
- **Request Body**:
```json
{
  "title": "Complete Project",
  "description": "Finish the Q1 project proposal",
  "status": "TODO",
  "dueDate": "2025-07-28 09:33:29",
  "priority": 3,
  "userId": 1,
  "categoryId": 1
}
```

#### Update Task
- **PUT** `/tasks/{id}`
- **Request Body**:
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": 2,
  "clearDueDate": false,
  "clearCategory": false
}
```

#### Update Task Status
- **PATCH** `/tasks/{id}/status?status=COMPLETED`
- **Parameters**: 
  - `status` - New task status (TODO, IN_PROGRESS, COMPLETED)

#### Delete Task
- **DELETE** `/tasks/{id}`

#### Advanced Task Search
- **GET** `/tasks/search`
- **Parameters**:
  - `userId` - Filter by user ID
  - `categoryId` - Filter by category ID
  - `status` - Filter by task status
  - `priority` - Filter by priority (1-3)
  - `dueDateFrom` - Start date for due date range
  - `dueDateTo` - End date for due date range
  - `searchTerm` - Search in title and description
  - Pagination parameters: `page`, `size`, `sortBy`, `sortDirection`

#### Get Overdue Tasks
- **GET** `/tasks/overdue`
- **Response**: List of tasks past their due date

#### Get Tasks Due Soon
- **GET** `/tasks/due-soon?days=7`
- **Parameters**: 
  - `days` (int, default: 7) - Number of days to look ahead

#### Get Task Analytics
- **GET** `/tasks/analytics`
- **Response**: 
```json
{
  "tasksByStatus": {"TODO": 5, "IN_PROGRESS": 4, "COMPLETED": 2},
  "tasksByPriority": {"Low": 2, "Medium": 5, "High": 4},
  "tasksByCategory": {"Work": 6, "Personal": 1, "Learning": 1},
  "totalTasks": 11,
  "overdueTasks": 2
}
```

### Category Management

#### Get All Categories
- **GET** `/categories`
- **Parameters**: Same pagination as users
- **Parameters**:
  - `includeTaskCount` (boolean) - Include task counts for each category

#### Get Categories with Task Counts
- **GET** `/categories/with-counts`
- **Response**: List of categories with task counts

#### Get Category by ID
- **GET** `/categories/{id}`

#### Create Category
- **POST** `/categories`
- **Request Body**:
```json
{
  "name": "Work",
  "description": "Work-related tasks",
  "colorCode": "#FF6B6B"
}
```

#### Update Category
- **PUT** `/categories/{id}`
- **Request Body**: Same as create category

#### Delete Category
- **DELETE** `/categories/{id}`
- **Note**: Cannot delete categories with existing tasks

#### Search Categories
- **GET** `/categories/search?name=work`
- **Parameters**:
  - `name` - Category name to search (case-insensitive)

## Data Models

### User
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "fullName": "John Doe",
  "taskCount": 4,
  "createdAt": "2025-07-21 09:33:29",
  "updatedAt": "2025-07-21 09:33:29"
}
```

### Task
```json
{
  "id": 1,
  "title": "Complete Project",
  "description": "Finish the Q1 project proposal",
  "status": "IN_PROGRESS",
  "dueDate": "2025-07-28 09:33:29",
  "priority": 3,
  "priorityName": "High",
  "overdue": false,
  "userId": 1,
  "username": "john_doe",
  "userFullName": "John Doe",
  "categoryId": 1,
  "categoryName": "Work",
  "categoryColorCode": "#FF6B6B",
  "createdAt": "2025-07-21 09:33:29",
  "updatedAt": "2025-07-21 09:33:29",
  "completedAt": null
}
```

### Category
```json
{
  "id": 1,
  "name": "Work",
  "description": "Work-related tasks",
  "colorCode": "#FF6B6B",
  "taskCount": 6,
  "createdAt": "2025-07-21 09:33:29",
  "updatedAt": "2025-07-21 09:33:29"
}
```

## Task Status Values
- `TODO` - Task not started
- `IN_PROGRESS` - Task in progress
- `COMPLETED` - Task completed

## Priority Values
- `1` - Low priority
- `2` - Medium priority
- `3` - High priority

## Error Codes

- **200** - Success
- **201** - Created
- **400** - Bad Request (validation errors)
- **404** - Not Found
- **500** - Internal Server Error

## Interactive Documentation

The API provides interactive Swagger documentation:
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/api-docs`

## Health Check

- **GET** `/actuator/health` - Application health status
- **GET** `/actuator/info` - Application information
- **GET** `/actuator/metrics` - Application metrics

## Database Console (Development)

In development mode, you can access the H2 database console:
- **URL**: `http://localhost:8080/api/h2-console`
- **JDBC URL**: `jdbc:h2:mem:devdb`
- **Username**: `sa`
- **Password**: (empty)

## Sample Data

The application comes with pre-loaded sample data including:
- 3 users (john_doe, jane_smith, bob_wilson)
- 5 categories (Work, Personal, Learning, Health, Shopping)
- 11 tasks with various statuses and priorities
- Some overdue tasks for testing

## CORS Support

The API supports CORS for frontend integration and accepts requests from any origin in development mode.