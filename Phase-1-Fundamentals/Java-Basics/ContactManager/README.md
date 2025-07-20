# Contact Manager - Phase 1 Final Project

A comprehensive Contact Manager application demonstrating file I/O operations, data persistence, and CRUD functionality as the capstone project for Phase 1 of the Java Mastery Journey.

## 🎯 Features

### Core Functionality
- **Add New Contacts**: Create contacts with name, phone (required), email and address (optional)
- **Search Contacts**: Find contacts by name, phone, email, or address with fuzzy matching
- **List All Contacts**: Display all contacts with formatting and timestamps
- **Update Contacts**: Modify existing contact information
- **Delete Contacts**: Remove contacts with confirmation prompts
- **Import/Export**: CSV file import and export capabilities
- **Statistics**: View contact counts and data analytics

### Data Management
- **CSV File Format**: Structured data storage with proper escaping
- **Auto-Save**: Automatic persistence on every change
- **Auto-Load**: Contacts loaded automatically on startup
- **Backup System**: Automatic backups created before operations
- **Data Validation**: Comprehensive input validation with user-friendly error messages

### Input Validation
- **Phone Numbers**: Multiple formats supported (+1-555-0123, (555) 123-4567, etc.)
- **Email Addresses**: RFC-compliant validation with consecutive dot detection
- **Names**: Support for international characters, hyphens, and apostrophes
- **Menu Choices**: Range validation with clear error messages

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Compile and run:**
   ```bash
   mvn clean compile exec:java
   ```

2. **Run tests:**
   ```bash
   mvn test
   ```

3. **Import sample data:**
   - Use option 6 from the menu
   - Import from `src/main/resources/sample-contacts.csv`

## 📁 Project Structure

```
ContactManager/
├── src/
│   ├── main/
│   │   ├── java/com/javamastery/contacts/
│   │   │   ├── model/
│   │   │   │   └── Contact.java              # Contact data model
│   │   │   ├── service/
│   │   │   │   ├── ContactManager.java       # Business logic
│   │   │   │   └── FileManager.java          # File I/O operations
│   │   │   ├── util/
│   │   │   │   ├── InputValidator.java       # Input validation
│   │   │   │   ├── Constants.java            # Application constants
│   │   │   │   └── DateFormatter.java        # Date formatting
│   │   │   └── ContactApp.java               # Main application
│   │   └── resources/
│   │       └── sample-contacts.csv           # Sample data
│   └── test/                                 # Comprehensive unit tests
├── data/                                     # Runtime data directory
│   ├── contacts.csv                         # Main data file
│   └── backups/                             # Automatic backups
├── pom.xml                                  # Maven configuration
├── README.md                                # This file
└── LEARNING_NOTES.md                        # Learning insights
```

## 🎮 Menu Options

1. **Add New Contact** - Create a new contact entry
2. **Search Contacts** - Find contacts by any field
3. **List All Contacts** - Display all contacts
4. **Update Contact** - Modify existing contact
5. **Delete Contact** - Remove contact with confirmation
6. **Import Contacts** - Load contacts from CSV file
7. **Export Contacts** - Save contacts to CSV file
8. **Show Statistics** - Display contact analytics
9. **Exit** - Close the application

## 📊 Data Format

Contacts are stored in CSV format with the following fields:
- `name` - Contact's full name (required)
- `phone` - Phone number (required, validated)
- `email` - Email address (optional, validated)
- `address` - Physical address (optional)
- `createdDate` - ISO timestamp of creation
- `lastModified` - ISO timestamp of last modification

## 🛡️ Error Handling

The application includes comprehensive error handling for:
- File operations (missing files, permissions, corrupted data)
- Input validation (invalid formats, empty required fields)
- Duplicate prevention (phone number uniqueness)
- User confirmation for destructive operations

## 🧪 Testing

The project includes 80+ unit tests covering:
- **Contact Model**: Validation, CSV serialization, edge cases
- **File Manager**: I/O operations, backup functionality, error handling
- **Contact Manager**: CRUD operations, search functionality, business logic
- **Input Validator**: All validation patterns and edge cases

Run tests with: `mvn test`

## 📈 Learning Outcomes

This project demonstrates mastery of:
- ✅ File I/O Operations (BufferedReader, BufferedWriter, Files class)
- ✅ Data Persistence (maintaining state between runs)
- ✅ CSV Processing (parsing and generating structured data)
- ✅ Exception Handling (comprehensive error management)
- ✅ Input Validation (robust user input processing)
- ✅ CRUD Operations (complete data management lifecycle)
- ✅ Code Organization (professional service layer architecture)
- ✅ Unit Testing (comprehensive test coverage)

## 🎉 Success Metrics

- **80 Unit Tests**: All passing with comprehensive coverage
- **Full CRUD Operations**: Create, Read, Update, Delete functionality
- **Data Persistence**: Contacts saved and loaded between sessions
- **Error Handling**: Graceful handling of all error conditions
- **Input Validation**: Robust validation with user-friendly messages
- **Professional Architecture**: Clean separation of concerns

## 🔧 Advanced Features Implemented

- **Auto-backup**: Automatic backup before major operations
- **Contact Statistics**: Analytics and data insights
- **Fuzzy Search**: Search across all contact fields
- **Data Validation**: Multiple phone formats and email validation
- **International Support**: Unicode name support
- **Professional UI**: Clean menu-driven interface