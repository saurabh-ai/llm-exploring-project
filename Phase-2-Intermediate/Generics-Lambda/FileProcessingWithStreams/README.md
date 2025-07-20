# Data Processing with Streams

A comprehensive Java application demonstrating advanced stream processing, functional programming, and lambda expressions. This project showcases the power of Java 8+ Streams API through practical data analysis scenarios.

## 🌟 Features

### Core Stream Operations
- **Complex filtering and mapping**: Advanced data transformations using lambda expressions
- **Custom collectors**: Implementation of specialized collectors for statistical analysis and data aggregation
- **Parallel processing**: Performance optimization using parallel streams
- **Functional composition**: Elegant combination of multiple operations using method chaining

### Data Processing Capabilities
- **Person Data Analysis**: Employee demographics, salary analysis, and organizational insights
- **Transaction Processing**: Financial data analysis, fraud detection, and spending patterns
- **Log Analysis**: System monitoring, error tracking, and performance metrics
- **File Operations**: Stream-based file I/O operations with functional approaches

### Advanced Features
- **Statistical Analysis**: Custom collectors for mean, median, variance, and distribution analysis
- **Data Bucketing**: Dynamic grouping of data into configurable ranges
- **Pattern Detection**: Regular expression-based pattern matching in streams
- **Performance Monitoring**: Response time analysis and anomaly detection

## 🏗️ Architecture

The application follows a modular architecture demonstrating clean separation of concerns:

```
src/main/java/com/javamastery/streams/
├── core/                          # Core data generation and utilities
│   └── DataGenerator.java         # Functional data generation using streams
├── model/                         # Immutable data models (Records)
│   ├── Person.java                # Employee/person data model
│   ├── Transaction.java           # Financial transaction model  
│   └── LogEntry.java              # System log entry model
├── processor/                     # Stream processing engines
│   ├── PersonDataProcessor.java   # Person data analysis
│   ├── TransactionProcessor.java  # Transaction processing
│   └── LogAnalysisProcessor.java  # Log analysis and monitoring
├── collector/                     # Custom stream collectors
│   └── CustomCollectors.java      # Statistical and specialized collectors
├── util/                          # Utility classes
│   └── FileUtils.java             # Stream-based file operations
└── DataProcessorApp.java          # Interactive main application
```

## 🔧 Key Design Patterns

### Functional Programming Concepts
- **Immutable Data Models**: All data models are implemented as records for immutability
- **Pure Functions**: Stream operations without side effects
- **Method References**: Extensive use of method references for clean, readable code
- **Function Composition**: Combining simple functions to create complex behaviors

### Stream API Mastery
- **Intermediate Operations**: `filter()`, `map()`, `flatMap()`, `distinct()`, `sorted()`
- **Terminal Operations**: `collect()`, `reduce()`, `forEach()`, `findFirst()`, `anyMatch()`
- **Advanced Collectors**: `groupingBy()`, `partitioningBy()`, `toMap()`, custom collectors
- **Parallel Processing**: `parallelStream()` for performance-critical operations

### Custom Collectors Implementation
```java
// Statistical analysis collector
public static <T> Collector<T, ?, Statistics> toStatistics(ToDoubleFunction<T> mapper);

// Minimum count filtering collector
public static <T, K> Collector<T, ?, Map<K, Long>> groupingByWithMinCount(
    Function<T, K> classifier, long minCount);

// Data bucketing collector
public static <T> Collector<T, ?, Map<String, List<T>>> toBuckets(
    ToDoubleFunction<T> valueFunction, double... bucketLimits);
```

## 🚀 Usage Examples

### Basic Stream Operations
```java
// Filter and transform person data
List<String> highEarnerEmails = people.stream()
    .filter(person -> person.salary() > 100_000)
    .filter(person -> person.yearsOfExperience() >= 5)
    .map(Person::email)
    .sorted()
    .collect(Collectors.toList());
```

### Complex Aggregations
```java
// Multi-level grouping with statistics
Map<String, Map<String, Long>> departmentDemographics = people.stream()
    .collect(Collectors.groupingBy(
        Person::department,
        Collectors.groupingBy(
            this::getAgeGroup,
            Collectors.counting()
        )
    ));
```

### Parallel Processing
```java
// Parallel fraud detection
Set<String> suspiciousAccounts = transactions.parallelStream()
    .filter(Transaction::isLargeTransaction)
    .collect(Collectors.groupingByConcurrent(
        Transaction::accountId,
        Collectors.counting()
    ))
    .entrySet().parallelStream()
    .filter(entry -> entry.getValue() > 10)
    .map(Map.Entry::getKey)
    .collect(Collectors.toSet());
```

### Custom Collectors Usage
```java
// Statistical analysis
Statistics salaryStats = people.stream()
    .collect(CustomCollectors.toStatistics(Person::salary));

// Data bucketing
Map<String, List<Transaction>> amountBuckets = transactions.stream()
    .collect(CustomCollectors.toBuckets(
        Transaction::amount, 
        100.0, 500.0, 1000.0, 5000.0
    ));
```

## 🎮 Interactive Demo

The application provides an interactive console interface:

### Main Menu Options
1. **👥 Person Data Analysis** - Employee demographics and salary analysis
2. **💰 Transaction Analysis** - Financial data processing and pattern detection
3. **📄 Log Analysis** - System monitoring and error tracking
4. **⚡ Parallel Processing Demo** - Performance comparison examples
5. **🔧 Custom Collectors Demo** - Advanced aggregation examples
6. **📁 File Processing Demo** - Stream-based file operations

### Running the Application
```bash
# Interactive mode
mvn exec:java

# Or run directly
mvn exec:java -Dexec.mainClass="com.javamastery.streams.DataProcessorApp"
```

## 🧪 Testing

Comprehensive test suite covering:
- **Model Tests**: Validation of immutable data models
- **Collector Tests**: Custom collector functionality
- **Processor Tests**: Stream processing logic
- **Integration Tests**: End-to-end processing scenarios

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomCollectorsTest

# Run tests with coverage
mvn test jacoco:report
```

## 📊 Performance Characteristics

### Stream Processing Benefits
- **Lazy Evaluation**: Operations are only executed when terminal operation is called
- **Memory Efficiency**: Processing elements on-demand without storing intermediate results
- **Parallel Processing**: Automatic parallelization for CPU-intensive operations
- **Functional Composition**: Chain operations without creating intermediate collections

### Optimization Techniques
- **Parallel Streams**: Used for computationally expensive operations
- **Early Termination**: Operations like `findFirst()` and `anyMatch()` for efficiency
- **Primitive Streams**: `IntStream`, `LongStream`, `DoubleStream` for numeric operations
- **Stream Reuse**: Proper stream lifecycle management

## 🔍 Learning Objectives

This project demonstrates mastery of:

1. **Java 8+ Streams API**
   - Intermediate and terminal operations
   - Method references and lambda expressions
   - Parallel processing concepts

2. **Functional Programming**
   - Immutable data structures
   - Pure functions and side-effect-free operations
   - Function composition patterns

3. **Advanced Collections**
   - Custom collector implementations
   - Complex grouping and aggregation strategies
   - Statistical analysis and data mining

4. **Performance Optimization**
   - Parallel streams for scalability
   - Memory-efficient processing
   - Benchmarking and profiling techniques

5. **Software Design**
   - Clean architecture principles
   - Separation of concerns
   - Testable and maintainable code

## 🛠️ Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Build JAR file
mvn package

# Run application
mvn exec:java

# Generate documentation
mvn javadoc:javadoc
```

## 📈 Sample Output

```
🌊 Data Processing with Streams - Interactive Demo
================================================

📊 Salary Statistics:
   Statistics{count=1000, sum=87450000.00, avg=87450.00, min=42000.00, max=148000.00}

🏆 Top 5 Earners:
   Sarah Johnson - $148000.00
   Michael Brown - $145500.00
   Jennifer Davis - $143200.00
   Robert Wilson - $141800.00
   Lisa Anderson - $139600.00

🏢 Average Salary by Department:
   Engineering: $91245.50
   Marketing: $84320.75
   Sales: $78965.25
   Management: $112450.00
```

## 🎯 Key Takeaways

This project demonstrates:
- **Streams API mastery** through practical applications
- **Functional programming** principles in Java
- **Performance optimization** using parallel processing
- **Clean code practices** with immutable data models
- **Advanced aggregation** techniques with custom collectors
- **Real-world applications** of lambda expressions and method references

The implementation showcases how modern Java's functional features can create elegant, efficient, and maintainable data processing solutions.