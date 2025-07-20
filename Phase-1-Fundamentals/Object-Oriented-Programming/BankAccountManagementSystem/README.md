# Bank Account Management System

A comprehensive Java application demonstrating Object-Oriented Programming principles through a banking system implementation.

## Project Overview

This project implements a Bank Account Management System that showcases core Java OOP concepts including:

- **Inheritance**: Three account types (Checking, Savings, Business) extending an abstract Account base class
- **Polymorphism**: Different account behaviors through method overriding
- **Encapsulation**: Proper data hiding and access control
- **Exception Handling**: Custom exceptions for banking-specific error conditions
- **Collections**: ArrayList for managing multiple accounts
- **Input Validation**: Robust user input handling

## Features

### Account Types

1. **Checking Account**
   - $500 overdraft limit
   - $10 monthly fee
   - No interest earned

2. **Savings Account**
   - $100 minimum balance requirement
   - 2.5% annual interest rate
   - Maximum 6 withdrawals per month
   - Interest calculation and application

3. **Business Account**
   - $10,000 minimum balance requirement
   - 1.5% annual interest rate
   - No overdraft allowed
   - Business name and tax ID tracking

### Banking Operations

- Account creation for all three types
- Deposit and withdrawal operations
- Money transfers between accounts (same account holder)
- Balance inquiries
- Interest calculation and application
- Monthly fee processing
- Account search and management

### Menu-Driven Interface

- Console-based user interface
- Input validation and error handling
- Comprehensive feature demonstration
- Account summary and reporting

## Technical Implementation

### Core Classes

- **Account**: Abstract base class with common functionality
- **CheckingAccount**: Implements overdraft and fee features
- **SavingsAccount**: Implements withdrawal limits and interest
- **BusinessAccount**: Implements business-specific requirements
- **BankAccountManager**: Service layer for account operations
- **InputValidator**: Utility class for input validation
- **Custom Exceptions**: Banking-specific error handling

### Design Patterns

- **Template Method**: Account class defines common operations
- **Strategy Pattern**: Different account types implement specific behaviors
- **Service Layer**: Separation of business logic from presentation

## Build and Run

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

### Run Application
```bash
mvn exec:java
```

## Testing

The project includes comprehensive unit tests with 55+ test cases covering:

- Account creation and validation
- Business rule enforcement
- Exception handling
- Service layer operations
- Edge cases and error conditions

Test coverage includes:
- All account types and their specific features
- Banking operations (deposit, withdraw, transfer)
- Exception scenarios
- Business rule validation

## Usage Examples

### Creating Accounts
```java
BankAccountManager manager = new BankAccountManager();

// Create checking account
CheckingAccount checking = manager.createCheckingAccount("John Doe", new BigDecimal("1000.00"));

// Create savings account
SavingsAccount savings = manager.createSavingsAccount("Jane Smith", new BigDecimal("2000.00"));

// Create business account
BusinessAccount business = manager.createBusinessAccount("Bob Johnson", "Johnson Corp", "12-3456789", new BigDecimal("15000.00"));
```

### Banking Operations
```java
// Deposit money
manager.deposit(accountNumber, new BigDecimal("500.00"));

// Withdraw money
manager.withdraw(accountNumber, new BigDecimal("200.00"));

// Transfer between accounts
manager.transfer(fromAccount, toAccount, new BigDecimal("300.00"));
```

### Interest and Fees
```java
// Calculate interest for all accounts
manager.calculateInterestForAllAccounts();

// Apply monthly fees
manager.applyMonthlyFees();

// Reset monthly withdrawal limits
manager.resetMonthlyWithdrawals();
```

## Business Rules

### Checking Account
- Overdraft limit: $500
- Monthly fee: $10
- No interest earned
- Fee cannot exceed overdraft limit

### Savings Account
- Minimum balance: $100
- Annual interest rate: 2.5%
- Maximum withdrawals per month: 6
- Withdrawals cannot violate minimum balance

### Business Account
- Minimum balance: $10,000
- Annual interest rate: 1.5%
- No overdraft allowed
- Withdrawals cannot violate minimum balance

### Transfers
- Currently limited to accounts of the same holder
- Subject to source account withdrawal rules
- No fees for transfers

## Error Handling

The system includes comprehensive error handling for:

- **InsufficientFundsException**: When withdrawal exceeds available funds
- **InvalidAccountException**: When account is not found or invalid
- **WithdrawalLimitExceededException**: When monthly withdrawal limits are exceeded
- **IllegalArgumentException**: For invalid input parameters

## Project Structure

```
src/
├── main/java/com/javamastery/banking/
│   ├── model/                 # Account classes
│   ├── service/              # Business logic
│   ├── exception/            # Custom exceptions
│   ├── util/                 # Utility classes
│   └── BankingApp.java       # Main application
└── test/java/com/javamastery/banking/
    ├── model/                # Model tests
    └── service/              # Service tests
```

## Learning Outcomes

This project demonstrates:

1. **Object-Oriented Design**: Well-structured class hierarchy with proper inheritance
2. **Encapsulation**: Data hiding and controlled access through methods
3. **Polymorphism**: Different behaviors for different account types
4. **Exception Handling**: Proper error management and user feedback
5. **Testing**: Comprehensive test coverage with JUnit 5
6. **Code Quality**: Clean code principles and documentation
7. **Maven Build**: Professional project structure and dependency management

## Future Enhancements

Potential improvements for Phase 2:
- File persistence for account data
- Transaction history tracking
- Multi-user support with authentication
- Account statements and reporting
- GUI interface
- Database integration