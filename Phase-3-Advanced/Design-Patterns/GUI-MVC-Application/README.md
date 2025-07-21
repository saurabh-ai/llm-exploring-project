# Task Management GUI Application

A desktop task management application demonstrating the Model-View-Controller (MVC) design pattern using Java Swing.

## Project Overview

This application showcases the implementation of the MVC pattern with the following features:

### Core Functionality
- **Task Management**: Create, edit, delete, and manage tasks
- **Task Properties**: Title, description, priority (1-5), due date, and status
- **Status Management**: Mark tasks as complete or pending
- **Filtering**: View all tasks, pending only, completed only, or overdue tasks
- **Sorting**: Sort tasks by priority, due date, creation date, or title
- **Data Persistence**: Automatic save/load functionality using JSON format

### Design Patterns Implemented
1. **MVC Pattern**: Clear separation between Model, View, and Controller
2. **Observer Pattern**: Model notifies views of data changes
3. **Command Pattern**: Encapsulated actions for undo/redo capability
4. **Factory Pattern**: Creation of UI components

### Architecture

```
src/main/java/com/javamastery/taskmgmt/
├── model/
│   ├── Task.java              # Task entity with properties
│   ├── TaskModel.java         # Business logic and data management
│   └── TaskStatus.java        # Enum for task status
├── view/
│   ├── TaskView.java          # Main GUI frame
│   ├── TaskListPanel.java     # Panel for displaying task list
│   ├── TaskFormDialog.java    # Dialog for creating/editing tasks
│   └── StatusBarPanel.java    # Status bar component
├── controller/
│   ├── TaskController.java    # Main controller coordinating model and view
│   └── TaskActionHandler.java # Event handlers for user actions
└── TaskManagementApp.java     # Main application entry point
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Package
```bash
mvn clean package
```

### Run Application
```bash
mvn exec:java
```

Or run the packaged JAR:
```bash
java -jar target/task-management-gui-1.0-SNAPSHOT.jar
```

## Features

### Task Management
- Create new tasks with title, description, priority, and due date
- Edit existing tasks
- Delete tasks with confirmation
- Mark tasks as complete or pending

### User Interface
- Clean, intuitive Swing-based GUI
- Task list with color-coded priorities and status
- Toolbar with quick action buttons
- Menu bar with File and Help options
- Status bar showing task statistics

### Data Persistence
- Tasks are automatically saved to `~/.taskmgmt/tasks.json`
- Data is loaded on application startup
- Auto-save after each modification

### Filtering and Sorting
- Filter by: All, Pending, Completed, Overdue
- Sort by: Creation Date, Due Date, Priority, Title
- Real-time updates when filter/sort options change

## Key Learning Demonstrations

### MVC Pattern
- **Model**: TaskModel manages business logic and data
- **View**: TaskView and related UI components handle presentation
- **Controller**: TaskController coordinates between model and view

### Observer Pattern
- TaskModel notifies observers when data changes
- Views automatically update when model changes

### Event-Driven Programming
- Proper Swing event handling
- Action listeners for user interactions
- Property change listeners for component communication

### Data Binding
- Synchronization between model and view
- Automatic UI updates when data changes

### Input Validation
- Form validation with user-friendly error messages
- Date format validation
- Required field validation

## Testing

The project includes unit tests for:
- Task entity validation
- TaskModel business logic
- CRUD operations
- Filtering and sorting
- Data persistence

Run tests with: `mvn test`

## Technical Details

- **Java Version**: 17+
- **GUI Framework**: Java Swing
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **JSON Processing**: Jackson
- **Architecture**: MVC with Observer pattern
