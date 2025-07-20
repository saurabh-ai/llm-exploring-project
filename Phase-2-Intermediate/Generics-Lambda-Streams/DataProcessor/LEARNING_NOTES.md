# Learning Notes: Data Processing with Streams

## 📚 Core Concepts Mastered

### Java 8+ Streams API Fundamentals

#### What are Streams?
Streams represent a sequence of elements supporting sequential and parallel aggregate operations. They're not data structures but rather a view of data that allows functional-style operations.

**Key Characteristics:**
- **Lazy Evaluation**: Operations are not executed until a terminal operation is called
- **Immutability**: Streams don't modify the underlying data source
- **One-time Use**: Once consumed, a stream cannot be reused
- **Functional Style**: Encourages declarative programming over imperative

#### Stream Pipeline Structure
```java
collection.stream()           // Source
    .filter(predicate)       // Intermediate Operation
    .map(function)           // Intermediate Operation  
    .collect(collector);     // Terminal Operation
```

### Lambda Expressions Deep Dive

#### Syntax Evolution
```java
// Anonymous Inner Class (Pre-Java 8)
Comparator<Employee> byName = new Comparator<Employee>() {
    public int compare(Employee e1, Employee e2) {
        return e1.getName().compareTo(e2.getName());
    }
};

// Lambda Expression (Java 8+)
Comparator<Employee> byName = (e1, e2) -> e1.getName().compareTo(e2.getName());

// Method Reference (Most Concise)
Comparator<Employee> byName = Comparator.comparing(Employee::getName);
```

#### Lambda Syntax Patterns
```java
// No parameters
() -> System.out.println("Hello")

// Single parameter (parentheses optional)
x -> x * 2
(x) -> x * 2

// Multiple parameters
(x, y) -> x + y

// Block body with return
x -> {
    int result = x * 2;
    return result;
}
```

### Method References Mastery

#### Four Types of Method References
```java
// 1. Static method reference
Function<String, Integer> parser = Integer::parseInt;

// 2. Instance method reference on particular object
Consumer<String> printer = System.out::println;

// 3. Instance method reference on arbitrary object
Function<String, String> toUpper = String::toUpperCase;

// 4. Constructor reference
Supplier<List<String>> listSupplier = ArrayList::new;
```

### Functional Interfaces Understanding

#### Built-in Functional Interfaces
```java
// Predicate<T> - Returns boolean
Predicate<Employee> isHighEarner = emp -> emp.getSalary() > 100000;

// Function<T, R> - Transforms T to R
Function<Employee, String> nameExtractor = Employee::getName;

// Consumer<T> - Consumes T, returns void
Consumer<Employee> printer = emp -> System.out.println(emp.getName());

// Supplier<T> - Supplies T with no input
Supplier<String> uuidGenerator = () -> UUID.randomUUID().toString();

// BinaryOperator<T> - Takes two T, returns T
BinaryOperator<BigDecimal> adder = BigDecimal::add;
```

#### Custom Functional Interfaces
```java
@FunctionalInterface
interface EmployeeValidator {
    boolean validate(Employee employee);
    
    // Default methods allowed
    default EmployeeValidator and(EmployeeValidator other) {
        return emp -> this.validate(emp) && other.validate(emp);
    }
}
```

## 🔧 Advanced Stream Operations

### Intermediate Operations (Lazy)

#### Filtering Operations
```java
// Basic filtering
.filter(emp -> emp.getSalary() > 50000)

// Complex predicates with method chaining
.filter(Employee::isActive)
.filter(emp -> emp.getDepartment().equals("Engineering"))
.filter(emp -> emp.getYearsOfExperience() >= 5)

// Using Predicate.and(), Predicate.or()
Predicate<Employee> highEarner = emp -> emp.getSalary() > 100000;
Predicate<Employee> senior = emp -> emp.getYearsOfExperience() >= 5;
.filter(highEarner.and(senior))
```

#### Transformation Operations
```java
// Simple mapping
.map(Employee::getName)
.map(String::toUpperCase)

// Complex transformations
.map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), emp.getSalary()))

// Flat mapping for nested structures
.flatMap(dept -> dept.getEmployees().stream())
```

#### Utility Operations
```java
// Remove duplicates
.distinct()

// Sort elements
.sorted(Comparator.comparing(Employee::getName))
.sorted(Comparator.comparing(Employee::getSalary).reversed())

// Limit results
.limit(10)

// Skip elements
.skip(5)

// Peek for debugging (doesn't transform)
.peek(emp -> System.out.println("Processing: " + emp.getName()))
```

### Terminal Operations (Eager)

#### Collection Operations
```java
// Basic collection
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.toMap(Employee::getId, Employee::getName))

// Advanced collectors
.collect(Collectors.joining(", "))
.collect(Collectors.groupingBy(Employee::getDepartment))
.collect(Collectors.partitioningBy(Employee::isHighEarner))
```

#### Reduction Operations
```java
// Finding elements
.findFirst()                    // Optional<Employee>
.findAny()                      // Optional<Employee>
.anyMatch(Employee::isActive)   // boolean
.allMatch(Employee::isActive)   // boolean
.noneMatch(Employee::isActive)  // boolean

// Aggregation
.count()                        // long
.min(Comparator.comparing(Employee::getSalary))    // Optional<Employee>
.max(Comparator.comparing(Employee::getSalary))    // Optional<Employee>

// Custom reduction
.reduce(BigDecimal.ZERO, (sum, emp) -> sum.add(emp.getSalary()), BigDecimal::add)
```

## 🧮 Custom Collectors Deep Dive

### Collector Interface Structure
```java
public interface Collector<T, A, R> {
    Supplier<A> supplier();           // Creates accumulator
    BiConsumer<A, T> accumulator();   // Adds element to accumulator
    BinaryOperator<A> combiner();     // Combines accumulators (parallel)
    Function<A, R> finisher();        // Transforms accumulator to result
    Set<Characteristics> characteristics(); // Collector properties
}
```

### Custom Collector Implementation
```java
public static <T> Collector<T, ?, Statistics> toStatistics(ToDoubleFunction<T> mapper) {
    return Collector.of(
        StatisticsAccumulator::new,                    // supplier
        (acc, item) -> acc.accept(mapper.applyAsDouble(item)), // accumulator
        StatisticsAccumulator::combine,                // combiner
        StatisticsAccumulator::getStatistics          // finisher
    );
}
```

### Built-in Collector Combinations
```java
// Downstream collectors
.collect(Collectors.groupingBy(
    Employee::getDepartment,
    Collectors.averagingDouble(emp -> emp.getSalary().doubleValue())
))

// Multiple collectors
.collect(Collectors.teeing(
    Collectors.averagingDouble(Employee::getSalary),
    Collectors.counting(),
    (avg, count) -> new DepartmentStats(avg, count)
))
```

## 🔄 Parallel Processing Insights

### When to Use Parallel Streams
**Good Candidates:**
- Large datasets (typically 1000+ elements)
- CPU-intensive operations
- Stateless operations
- Independent computations

**Poor Candidates:**
- Small datasets (overhead exceeds benefits)
- I/O bound operations
- Stateful operations (sorting, distinct)
- Operations requiring ordered processing

### Parallel Processing Example
```java
// Sequential
long sequentialSum = employees.stream()
    .mapToLong(emp -> expensiveCalculation(emp))
    .sum();

// Parallel (automatically uses ForkJoinPool)
long parallelSum = employees.parallelStream()
    .mapToLong(emp -> expensiveCalculation(emp))
    .sum();

// Custom thread pool
ForkJoinPool customThreadPool = new ForkJoinPool(4);
try {
    long result = customThreadPool.submit(() ->
        employees.parallelStream()
            .mapToLong(this::expensiveCalculation)
            .sum()
    ).get();
} finally {
    customThreadPool.shutdown();
}
```

## 🎯 Generic Programming Mastery

### Bounded Type Parameters
```java
// Upper bound (extends)
public class NumberProcessor<T extends Number> {
    public double getAverage(List<T> numbers) {
        return numbers.stream()
            .mapToDouble(Number::doubleValue)
            .average()
            .orElse(0.0);
    }
}

// Multiple bounds
public class ComparableProcessor<T extends Number & Comparable<T>> {
    public T findMax(List<T> items) {
        return items.stream().max(T::compareTo).orElse(null);
    }
}
```

### Wildcard Types
```java
// Upper bounded wildcard (producer)
public void processNumbers(List<? extends Number> numbers) {
    // Can read as Number or its supertypes
    numbers.forEach(num -> System.out.println(num.doubleValue()));
}

// Lower bounded wildcard (consumer)
public void addNumbers(List<? super Integer> numbers) {
    // Can add Integer or its subtypes
    numbers.add(42);
    numbers.add(100);
}

// Unbounded wildcard
public int getSize(List<?> list) {
    return list.size(); // Only Object methods available
}
```

### PECS Principle (Producer Extends, Consumer Super)
```java
// Copying from source (producer) to destination (consumer)
public static <T> void copy(List<? extends T> source, List<? super T> destination) {
    for (T item : source) {
        destination.add(item);  // source produces, destination consumes
    }
}
```

## 🏗️ Functional Programming Patterns

### Immutability Benefits
```java
// Immutable record (Java 14+)
public record Employee(Long id, String name, BigDecimal salary, Department department) {
    // Validation in compact constructor
    public Employee {
        Objects.requireNonNull(name, "Name cannot be null");
        if (salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }
    
    // Derived properties
    public boolean isHighEarner() {
        return salary.compareTo(new BigDecimal("100000")) > 0;
    }
}
```

### Pure Functions
```java
// Pure function - no side effects, same input always produces same output
public BigDecimal calculateBonus(Employee employee, BigDecimal rate) {
    return employee.salary().multiply(rate);
}

// Impure function - has side effects
public BigDecimal calculateBonusImpure(Employee employee, BigDecimal rate) {
    BigDecimal bonus = employee.salary().multiply(rate);
    logger.info("Calculated bonus: " + bonus); // Side effect!
    return bonus;
}
```

### Function Composition
```java
// Compose functions for complex transformations
Function<Employee, String> nameExtractor = Employee::name;
Function<String, String> upperCase = String::toUpperCase;
Function<String, String> addPrefix = name -> "EMP: " + name;

// Compose functions
Function<Employee, String> transform = nameExtractor
    .andThen(upperCase)
    .andThen(addPrefix);

String result = transform.apply(employee); // "EMP: JOHN DOE"
```

### Higher-Order Functions
```java
// Function that takes another function as parameter
public <T, R> List<R> transformList(List<T> items, Function<T, R> transformer) {
    return items.stream()
        .map(transformer)
        .collect(Collectors.toList());
}

// Function that returns a function
public Function<Employee, Boolean> salaryFilter(BigDecimal threshold) {
    return employee -> employee.salary().compareTo(threshold) > 0;
}
```

## 🚀 Performance Optimization Techniques

### Stream vs Traditional Loops
```java
// Traditional approach
List<String> highEarnerNames = new ArrayList<>();
for (Employee emp : employees) {
    if (emp.getSalary().compareTo(threshold) > 0) {
        highEarnerNames.add(emp.getName().toUpperCase());
    }
}

// Stream approach (more readable, potentially parallel)
List<String> highEarnerNames = employees.stream()
    .filter(emp -> emp.getSalary().compareTo(threshold) > 0)
    .map(Employee::getName)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Optimization Best Practices
```java
// 1. Filter early to reduce downstream processing
employees.stream()
    .filter(Employee::isActive)        // Filter first
    .map(Employee::getName)            // Then transform
    .collect(Collectors.toList());

// 2. Use primitive streams for numeric operations
employees.stream()
    .mapToDouble(emp -> emp.getSalary().doubleValue())  // Avoid boxing
    .sum();

// 3. Short-circuit when possible
boolean hasHighEarner = employees.stream()
    .anyMatch(emp -> emp.getSalary().compareTo(threshold) > 0);

// 4. Reuse expensive computations
Map<String, List<Employee>> departmentMap = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));
```

## 📊 Real-world Applications

### Data Analysis Pipeline
```java
public class EmployeeAnalytics {
    
    public DepartmentReport analyzeDepartment(List<Employee> employees, String department) {
        List<Employee> deptEmployees = employees.stream()
            .filter(emp -> department.equals(emp.getDepartment()))
            .collect(Collectors.toList());
            
        double avgSalary = deptEmployees.stream()
            .mapToDouble(emp -> emp.getSalary().doubleValue())
            .average()
            .orElse(0.0);
            
        long seniorCount = deptEmployees.stream()
            .filter(Employee::isSenior)
            .count();
            
        Map<String, Long> skillDistribution = deptEmployees.stream()
            .flatMap(emp -> emp.getSkills().stream())
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));
            
        return new DepartmentReport(department, deptEmployees.size(), 
                                  avgSalary, seniorCount, skillDistribution);
    }
}
```

## 🔍 Common Pitfalls and Solutions

### 1. Stream Reuse
```java
// WRONG - Stream can only be consumed once
Stream<Employee> stream = employees.stream();
long count = stream.count();
List<Employee> list = stream.collect(Collectors.toList()); // IllegalStateException!

// CORRECT - Create new stream for each operation
long count = employees.stream().count();
List<Employee> list = employees.stream().collect(Collectors.toList());
```

### 2. Parallel Stream Side Effects
```java
// WRONG - Side effects in parallel streams
List<String> results = new ArrayList<>();  // Not thread-safe!
employees.parallelStream()
    .map(Employee::getName)
    .forEach(results::add);  // Race condition!

// CORRECT - Use thread-safe operations
List<String> results = employees.parallelStream()
    .map(Employee::getName)
    .collect(Collectors.toList());  // Thread-safe
```

### 3. Optional Misuse
```java
// WRONG - Defeating the purpose of Optional
Optional<Employee> optional = findEmployee(id);
if (optional.isPresent()) {
    Employee emp = optional.get();
    // ...
}

// BETTER - Use Optional's functional methods
findEmployee(id)
    .map(Employee::getName)
    .ifPresent(System.out::println);
```

## 🎓 Key Learning Outcomes

### Technical Skills Acquired
1. **Stream API Mastery**: Comprehensive understanding of intermediate and terminal operations
2. **Lambda Expressions**: Functional programming paradigms in Java
3. **Generic Programming**: Type-safe, reusable code with advanced type parameters
4. **Performance Optimization**: Parallel processing and memory-efficient operations
5. **Custom Collectors**: Advanced aggregation and data transformation patterns

### Best Practices Internalized
1. **Functional Composition**: Building complex operations from simple, pure functions
2. **Immutability**: Thread-safe, predictable code using immutable data structures
3. **Type Safety**: Leveraging generics for compile-time error detection
4. **Performance Awareness**: Understanding when and how to optimize stream operations
5. **Clean Code**: Readable, maintainable functional programming patterns

### Preparation for Advanced Topics
This foundation prepares for:
- **Spring Framework**: Dependency injection and aspect-oriented programming
- **Reactive Programming**: RxJava and Spring WebFlux
- **Microservices**: Functional decomposition and data processing pipelines
- **Big Data**: Stream processing frameworks like Apache Kafka and Apache Spark
- **Modern Java**: Pattern matching, sealed classes, and future functional enhancements

The comprehensive understanding of streams, functional programming, and generic programming demonstrated in this project forms the bedrock for advanced enterprise Java development.