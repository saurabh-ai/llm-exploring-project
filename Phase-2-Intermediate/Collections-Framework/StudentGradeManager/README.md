# 🎓 Student Grade Management System

A comprehensive Java application demonstrating mastery of the **Collections Framework**, **Generics**, and **advanced data manipulation techniques**. This project serves as a complete showcase of Java's collections ecosystem with real-world practical applications.

## 📋 Table of Contents

- [Overview](#overview)
- [Learning Objectives](#learning-objectives)
- [Architecture](#architecture)
- [Collections Framework Usage](#collections-framework-usage)
- [Features](#features)
- [Getting Started](#getting-started)
- [Usage Examples](#usage-examples)
- [Testing](#testing)
- [Performance Considerations](#performance-considerations)
- [Advanced Features](#advanced-features)

## 🎯 Overview

This Student Grade Management System is designed to demonstrate advanced Java programming concepts through a practical, feature-rich application. It manages student records, grades, and academic analytics using various data structures and algorithms optimized for different use cases.

### Key Highlights

- **13 Java classes** with comprehensive functionality
- **8+ Collection types** used appropriately for different scenarios
- **Generic programming** with bounded type parameters
- **Multiple Comparator implementations** for flexible sorting
- **Statistical analysis** with advanced mathematical operations
- **File I/O operations** with CSV import/export
- **Comprehensive testing** with 31 unit tests

## 🎓 Learning Objectives

This project demonstrates mastery of:

- ✅ **Java Collections Framework** (ArrayList, LinkedList, HashMap, TreeSet, etc.)
- ✅ **Generic Programming** with bounded type parameters and wildcards
- ✅ **Comparable and Comparator interfaces** for custom sorting
- ✅ **Advanced iteration and stream operations**
- ✅ **Complex data filtering and statistical analysis**
- ✅ **File I/O with collections**
- ✅ **Performance optimization** and Big O complexity awareness
- ✅ **Exception handling** and data validation

## 🏗️ Architecture

```
src/main/java/com/javamastery/grades/
├── model/                          # Core data models
│   ├── Student.java               # Student entity with collections
│   ├── Grade.java                 # Generic grade class
│   ├── Subject.java              # Subject representation
│   └── GradeType.java            # Grade type enumeration
├── service/                       # Business logic layer
│   ├── StudentManager.java       # Student CRUD operations
│   ├── GradeCalculator.java      # Statistical calculations
│   └── ReportGenerator.java      # Report generation
├── util/                          # Utility classes
│   ├── StudentComparators.java   # Custom comparators
│   ├── StatisticsCalculator.java # Generic statistical methods
│   └── DataValidator.java        # Data validation utilities
├── io/                            # File I/O operations
│   ├── FileManager.java          # File management
│   └── CsvProcessor.java         # CSV processing
└── GradeManagementApp.java       # Main application
```

## 🧩 Collections Framework Usage

### Core Collections Demonstrated

| Collection | Use Case | Performance | Features Demonstrated |
|------------|----------|-------------|----------------------|
| **HashMap** | Student ID lookups | O(1) average | Fast key-based access, collision handling |
| **TreeSet** | Student rankings by GPA | O(log n) | Automatic sorting, Comparable interface |
| **ArrayList** | Dynamic grade storage | O(1) amortized | Dynamic resizing, index-based access |
| **LinkedList** | Chronological grade history | O(1) insertion | Doubly-linked structure, insertion order |
| **TreeMap** | Sorted grade distributions | O(log n) | Sorted keys, NavigableMap operations |
| **LinkedHashSet** | Unique subject ordering | O(1) average | Insertion order + uniqueness |
| **PriorityQueue** | Academic alerts | O(log n) | Priority-based processing |
| **ConcurrentHashMap** | Thread-safe operations | O(1) average | Concurrent access, thread safety |

### Advanced Collection Features

- **Custom Comparators**: 10+ different sorting strategies
- **Generic Wildcards**: Bounded type parameters for type safety
- **Stream Operations**: Complex filtering and transformation
- **Collection Algorithms**: Sorting, searching, and statistical analysis

## ✨ Features

### 1. Student Management
- ➕ Add, update, remove students
- 🔍 Search by name, major, enrollment year
- 📊 Automatic GPA calculation and ranking
- ✅ Comprehensive data validation

### 2. Grade Management
- 📝 Support for multiple grade types (Exams, Quizzes, Homework, Projects)
- ⚖️ Weighted grade calculations
- 📈 Grade history tracking
- 📊 Subject-specific performance analysis

### 3. Analytics & Reporting
- 📊 Statistical analysis (mean, median, standard deviation, percentiles)
- 📈 Performance trends and improvement tracking
- 🏆 Top performer identification
- 🚨 Academic alert system
- 📋 Comprehensive report generation

### 4. Data Operations
- 📁 CSV import/export functionality
- 💾 Backup and restore operations
- 🔄 Bulk data processing
- ✅ Data validation and error handling

### 5. Advanced Features
- 🧮 Generic statistical utilities
- ⚖️ Multiple comparator implementations
- 🎯 Outlier detection
- 📊 Grade distribution analysis
- 🔄 Moving averages and trend analysis

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd StudentGradeManager
   ```

2. **Compile the project**
   ```bash
   mvn compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Run the application**
   ```bash
   mvn exec:java
   ```

### Sample Data

The application includes sample data initialization:
- **10 students** across different majors
- **Multiple subjects** with varying credit hours
- **80+ grades** with realistic distributions
- **Comprehensive test scenarios**

## 💻 Usage Examples

### Basic Operations

```java
// Create student manager
StudentManager manager = new StudentManager();

// Add a student
Student student = new Student("ST123456", "John Doe", "Computer Science", 2023);
manager.addStudent(student);

// Add grades
Subject math = new Subject("MATH101", "Calculus I", 4);
Grade<Double> grade = new Grade<>(85.0, 100.0, math, GradeType.EXAM, "Midterm");
manager.addGradeToStudent("ST123456", grade);

// Get rankings
List<Student> topStudents = manager.getTopStudents(10);
```

### Statistical Analysis

```java
GradeCalculator calculator = new GradeCalculator();

// Calculate subject statistics
Map<String, Double> stats = calculator.calculateSubjectStatistics(students, subject);

// Generate reports
ReportGenerator reporter = new ReportGenerator(calculator);
LinkedHashMap<String, Object> report = reporter.generateClassPerformanceReport(students, subjects);
```

### Custom Sorting

```java
// Sort by GPA
students.sort(StudentComparators.BY_GPA);

// Sort by multiple criteria
students.sort(StudentComparators.COMPREHENSIVE_SORT);

// Custom comparator for subject performance
Comparator<Student> subjectPerformance = StudentComparators.bySubjectGPA(mathSubject);
```

## 🧪 Testing

The project includes comprehensive unit tests covering:

- **31 test methods** across 3 test classes
- **Model validation** and business logic
- **Collection operations** and data integrity
- **Statistical calculations** and edge cases
- **Generic utility methods** with various data types

Run tests with:
```bash
mvn test
```

## ⚡ Performance Considerations

### Time Complexity Analysis

| Operation | Data Structure | Complexity | Justification |
|-----------|----------------|------------|---------------|
| Student lookup | HashMap | O(1) | Key-based access for frequent operations |
| Student rankings | TreeSet | O(log n) | Maintains sorted order automatically |
| Grade insertion | ArrayList | O(1) amortized | Dynamic resizing for flexible storage |
| Top-K students | TreeSet + limit | O(k) | Pre-sorted data structure |
| Statistical calculations | Various | O(n) | Single-pass algorithms where possible |

### Memory Optimization

- **Immutable objects** where appropriate (Subject, Grade fields)
- **Efficient collections** chosen for specific use cases
- **Lazy initialization** for optional features
- **Generic type erasure** awareness for memory efficiency

## 🔬 Advanced Features

### Generic Programming

```java
// Bounded type parameters
public class Grade<T extends Number> implements Comparable<Grade<T>> {
    // Generic implementation supporting various numeric types
}

// Generic utility methods
public static <T extends Number> double calculateAverage(Collection<T> values) {
    // Works with any Number subtype
}
```

### Custom Comparators

```java
// Flexible sorting strategies
Comparator<Student> byGPAThenName = BY_GPA.thenComparing(BY_NAME);
Comparator<Student> comprehensiveSort = BY_MAJOR
    .thenComparing(BY_ENROLLMENT_YEAR)
    .thenComparing(BY_GPA)
    .thenComparing(BY_NAME);
```

### Stream Operations

```java
// Complex filtering and transformation
students.stream()
    .filter(s -> s.calculateGPA() < 2.0)
    .sorted(Comparator.comparing(Student::calculateGPA))
    .limit(10)
    .collect(Collectors.toList());
```

### Statistical Analysis

- Descriptive statistics (mean, median, mode, standard deviation)
- Quartile calculations and percentiles
- Outlier detection using IQR method
- Correlation analysis between variables
- Moving averages for trend analysis
- Skewness calculation for distribution analysis

## 📊 Demo Features

The interactive application provides:

1. **🎮 Interactive Menu System** - Navigate through all features
2. **📊 Real-time Statistics** - Live performance metrics
3. **📈 Visual Data Representation** - ASCII charts and histograms
4. **🔍 Search and Filter** - Advanced query capabilities
5. **📁 File Operations** - Import/export with validation
6. **⚖️ Comparator Demonstrations** - Live sorting examples
7. **🧩 Collection Showcases** - Real-time collection usage stats

## 🎯 Learning Outcomes

After exploring this project, you will have demonstrated:

- **Deep understanding** of Java Collections Framework
- **Practical application** of generic programming
- **Performance-aware** data structure selection
- **Clean architecture** and separation of concerns
- **Comprehensive testing** strategies
- **Real-world application** development skills

## 📝 License

This project is created for educational purposes as part of the Java Mastery learning journey.

---

*This project represents Phase 2 of the Java Mastery Journey, focusing on intermediate concepts and practical application of Collections Framework mastery.*