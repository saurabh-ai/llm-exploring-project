# Bank Account Management System

A comprehensive banking system demonstrating Object-Oriented Programming principles, exception handling, and modular design in Java.

## Features

### Account Types
- **Checking Account**: Basic banking with overdraft protection up to $500
- **Savings Account**: Interest-earning account with withdrawal restrictions
- **Business Account**: Commercial banking with high minimum balance requirements

### Core Banking Operations
- Account creation with validation
- Deposit and withdrawal transactions
- Money transfers between accounts
- Balance inquiries and account management
- Interest calculations
- Comprehensive error handling

### Business Rules
- **Account Numbers**: Auto-generated 8-digit numbers starting from 10000000
- **Checking Accounts**: $500 overdraft limit, $10 monthly fee, no interest
- **Savings Accounts**: $100 minimum balance, 2.5% annual interest, max 6 withdrawals/month
- **Business Accounts**: $10,000 minimum balance, 1.5% annual interest, no overdraft

## Project Structure

```
src/
├── main/java/com/javamastery/banking/
│   ├── model/
│   │   ├── Account.java           # Abstract base class
│   │   ├── CheckingAccount.java   # Checking account implementation
│   │   ├── SavingsAccount.java    # Savings account implementation
│   │   └── BusinessAccount.java   # Business account implementation
│   ├── service/
│   │   └── BankAccountManager.java # Banking operations service
│   ├── exception/
│   │   ├── InsufficientFundsException.java
│   │   ├── InvalidAccountException.java
│   │   └── WithdrawalLimitExceededException.java
│   ├── util/
│   │   └── InputValidator.java    # Input validation utilities
│   └── BankingApp.java           # Main application
└── test/java/com/javamastery/banking/
    ├── model/AccountTest.java
    ├── service/BankAccountManagerTest.java
    ├── exception/ExceptionTest.java
    └── util/InputValidatorTest.java
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run

1. **Compile the project:**
   ```bash
   mvn compile
   ```

2. **Run tests:**
   ```bash
   mvn test
   ```

3. **Start the application:**
   ```bash
   mvn compile exec:java -Dexec.mainClass="com.javamastery.banking.BankingApp"
   ```

### Interactive Menu

The application provides an interactive console menu with the following options:

1. **Create Account** - Create new checking, savings, or business accounts
2. **Deposit Money** - Add funds to any account
3. **Withdraw Money** - Remove funds (subject to account rules)
4. **Transfer Money** - Move funds between accounts
5. **Check Balance** - View account details and balance
6. **List All Accounts** - Display all accounts in the system
7. **Calculate Interest** - View and apply interest to eligible accounts
8. **Account Statistics** - View system-wide statistics
9. **Exit** - Close the application

### Sample Usage

The application creates sample accounts on startup for demonstration:
- Checking Account (John Doe): $1,000
- Savings Account (Jane Smith): $5,000  
- Business Account (Bob Wilson - Wilson LLC): $15,000

## Technical Details

### Design Patterns
- **Inheritance**: Abstract Account class with concrete implementations
- **Polymorphism**: Common interface for all account types
- **Encapsulation**: Private fields with controlled access
- **Service Layer**: Separation of business logic from presentation

### Exception Handling
- Custom exceptions for banking scenarios
- Comprehensive input validation
- Graceful error recovery
- Meaningful error messages

### Testing
- JUnit 5 test suite with 57+ test cases
- Unit tests for all major components
- Edge case testing for business rules
- 100% test coverage of core functionality

### Input Validation
- Account number format validation (8 digits)
- Name validation with proper formatting
- Amount validation for positive values
- Business-specific validation for tax IDs

## Architecture Highlights

### Object-Oriented Design
- Abstract base class enforces common behavior
- Each account type implements specific business rules
- Clean separation of concerns

### Error Handling
- Checked exceptions for recoverable errors
- Runtime exceptions for programming errors
- Detailed error messages for user guidance

### Data Integrity
- Immutable account numbers
- Protected balance modifications
- Validation at multiple layers

## Development

### Adding New Account Types
1. Extend the `Account` abstract class
2. Implement required abstract methods
3. Add validation rules to `InputValidator`
4. Update `BankAccountManager` factory method
5. Add comprehensive test coverage

### Extending Functionality
- Transaction history tracking
- Account statements
- Automated fee processing
- Multi-currency support
- Audit logging

## License

This project is part of the Java Mastery learning curriculum and is intended for educational purposes.