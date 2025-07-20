# Learning Notes: Bank Account Management System

This document captures key Object-Oriented Programming concepts and Java fundamentals demonstrated in the Bank Account Management System.

## Object-Oriented Programming Concepts

### 1. Inheritance
**Concept**: Creating new classes based on existing classes to promote code reuse.

**Implementation**:
- `Account` abstract base class defines common attributes and behaviors
- `CheckingAccount`, `SavingsAccount`, and `BusinessAccount` extend the base class
- Each subclass inherits common functionality while adding specific features

**Key Learnings**:
- Abstract classes can't be instantiated directly
- Subclasses must implement abstract methods
- `super()` calls parent constructor
- Method overriding allows specialized behavior

```java
public abstract class Account {
    // Common fields and methods
    public abstract BigDecimal calculateInterest();
}

public class SavingsAccount extends Account {
    @Override
    public BigDecimal calculateInterest() {
        // Specific implementation for savings
    }
}
```

### 2. Polymorphism
**Concept**: Objects of different types responding to the same interface in their own way.

**Implementation**:
- All account types can be treated as `Account` objects
- Method calls resolve to the correct implementation at runtime
- Collections can hold different account types uniformly

**Key Learnings**:
- Runtime method resolution (dynamic dispatch)
- Interface-based programming
- Code flexibility and extensibility

```java
List<Account> accounts = new ArrayList<>();
accounts.add(new CheckingAccount(...));
accounts.add(new SavingsAccount(...));

// Polymorphic method calls
for (Account account : accounts) {
    account.deposit(amount);  // Calls appropriate implementation
    BigDecimal interest = account.calculateInterest();  // Different for each type
}
```

### 3. Encapsulation
**Concept**: Bundling data and methods that operate on data within a single unit.

**Implementation**:
- Private fields with controlled access through methods
- Getter and setter methods for external access
- Protected methods for subclass access only
- Validation in setter methods

**Key Learnings**:
- Data hiding and protection
- Controlled access to object state
- Validation and business rule enforcement

```java
public class Account {
    private BigDecimal balance;  // Private - cannot be accessed directly
    
    public BigDecimal getBalance() {  // Controlled read access
        return balance;
    }
    
    protected void setBalance(BigDecimal balance) {  // Protected write access
        this.balance = balance;
    }
}
```

### 4. Abstraction
**Concept**: Hiding complex implementation details while exposing only essential features.

**Implementation**:
- Abstract `Account` class defines interface without implementation
- Concrete classes provide specific implementations
- Service layer abstracts business logic from presentation

**Key Learnings**:
- Abstract methods force implementation in subclasses
- Interface segregation
- Separation of concerns

## Exception Handling Patterns

### 1. Custom Exceptions
**Purpose**: Create domain-specific exceptions for better error handling.

**Implementation**:
```java
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
```

**Key Learnings**:
- Checked vs unchecked exceptions
- Exception hierarchy and inheritance
- Meaningful error messages

### 2. Exception Propagation
**Pattern**: Let exceptions bubble up to appropriate handling level.

**Implementation**:
- Model classes throw specific exceptions
- Service layer catches and handles business exceptions
- Application layer provides user-friendly error messages

### 3. Validation and Defensive Programming
**Practice**: Validate inputs early and often.

**Implementation**:
- Input validation in utility classes
- Parameter validation in methods
- State validation before operations

## Collections Framework Usage

### 1. ArrayList for Dynamic Storage
**Usage**: Storing variable number of accounts.

**Key Learnings**:
- Dynamic sizing
- Indexed access
- Integration with streams and lambda expressions

```java
private List<Account> accounts = new ArrayList<>();

// Stream operations for filtering and processing
List<Account> checkingAccounts = accounts.stream()
    .filter(account -> account.getAccountType().equals("Checking"))
    .collect(Collectors.toList());
```

### 2. Stream API
**Usage**: Functional-style operations on collections.

**Key Learnings**:
- Filter, map, reduce operations
- Method references
- Collectors utility

## Design Patterns Applied

### 1. Factory Pattern (Implicit)
**Implementation**: `BankAccountManager.createAccount()` method creates appropriate account types.

**Benefits**:
- Centralized object creation
- Type-safe instantiation
- Easy to extend with new account types

### 2. Service Layer Pattern
**Implementation**: `BankAccountManager` encapsulates business logic.

**Benefits**:
- Separation of business logic from presentation
- Reusable business operations
- Centralized transaction management

## Java Language Features

### 1. BigDecimal for Financial Calculations
**Reason**: Precise decimal arithmetic required for monetary values.

**Key Learnings**:
- Avoiding floating-point precision issues
- Immutable objects
- Scale and rounding modes

```java
BigDecimal balance = new BigDecimal("1000.00");
BigDecimal interest = balance.multiply(interestRate)
                           .setScale(2, RoundingMode.HALF_UP);
```

### 2. LocalDateTime for Timestamps
**Usage**: Account creation dates.

**Benefits**:
- Type-safe date/time handling
- Immutable objects
- Rich API for date/time operations

### 3. Enum-like Constants
**Pattern**: Static final constants for configuration.

```java
private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("500.00");
private static final int MAX_WITHDRAWALS_PER_MONTH = 6;
```

## Testing Strategies

### 1. Unit Testing with JUnit 5
**Approach**: Test individual components in isolation.

**Key Learnings**:
- `@Test`, `@BeforeEach` annotations
- Assertion methods (`assertEquals`, `assertThrows`)
- Test organization and naming

### 2. Test-Driven Development Principles
**Practice**: Write tests first, then implementation.

**Benefits**:
- Better code design
- Comprehensive test coverage
- Regression protection

### 3. Edge Case Testing
**Focus**: Testing boundary conditions and error scenarios.

**Examples**:
- Minimum balance violations
- Withdrawal limit exceeded
- Invalid input handling

## Architecture Lessons

### 1. Layered Architecture
**Structure**:
- Presentation Layer (BankingApp)
- Service Layer (BankAccountManager)
- Domain Layer (Account models)
- Utility Layer (InputValidator)

### 2. Separation of Concerns
**Principle**: Each class has a single responsibility.

**Implementation**:
- Models focus on data and behavior
- Services handle business logic
- Utilities provide reusable functionality
- Application manages user interaction

### 3. Input Validation Strategy
**Approach**: Multi-layer validation for robust error handling.

**Layers**:
- Syntax validation (format, type)
- Semantic validation (business rules)
- State validation (current object state)

## Best Practices Demonstrated

1. **Consistent naming conventions** for classes, methods, and variables
2. **Comprehensive documentation** with JavaDoc comments
3. **Error handling** with specific exception types and meaningful messages
4. **Input validation** at multiple levels
5. **Test coverage** for all major functionality
6. **Code organization** with logical package structure
7. **Immutable data** where appropriate (account numbers, creation dates)
8. **Defensive programming** with null checks and boundary validation

## Next Steps for Learning

1. **Design Patterns**: Study and implement more patterns (Observer, Strategy, Command)
2. **Persistence**: Add database integration with JPA/Hibernate
3. **Concurrency**: Thread-safe operations for multi-user scenarios
4. **Logging**: Implement proper logging with SLF4J/Logback
5. **Configuration**: Externalize configuration with properties files
6. **REST APIs**: Create web services for the banking operations
7. **Security**: Add authentication and authorization
8. **Transaction Management**: Implement ACID transactions
9. **Performance**: Add caching and optimization techniques
10. **Monitoring**: Implement metrics and health checks