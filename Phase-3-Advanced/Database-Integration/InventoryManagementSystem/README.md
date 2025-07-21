# Inventory Management System

A comprehensive inventory management system demonstrating advanced database integration using JDBC, proper layered architecture, and enterprise-level features.

## Features

### Core Functionality
- **Product Management**: Complete CRUD operations with validation
- **Category Management**: Organize products with hierarchical categories
- **Inventory Tracking**: Real-time stock levels with location support
- **Stock Movement Audit**: Complete audit trail of all inventory changes
- **Business Rule Validation**: Enforce data integrity and business constraints

### Advanced Features
- **Connection Pooling**: Efficient database connection management with HikariCP
- **Multi-Database Support**: H2 for development, MySQL for production
- **Stock Allocation**: Reserve inventory for pending orders
- **Low Stock Alerts**: Automatic identification of items below reorder levels
- **Profitability Analysis**: Calculate margins and categorize product profitability
- **Location Management**: Track inventory across multiple locations
- **Search & Filtering**: Advanced search capabilities across all entities

### User Interface
- **Swing GUI**: Professional desktop application interface
- **CLI Interface**: Complete command-line interface for system interaction and testing
- **Dashboard**: Overview of inventory status and key metrics
- **Tabbed Interface**: Organized access to different functional areas
- **Real-time Updates**: Automatic refresh capabilities
- **Data Validation**: Input validation with user-friendly error messages

## Technical Architecture

### Layered Design
```
├── UI Layer (Swing)           # User interface components
├── Service Layer              # Business logic and validation
├── DAO Layer                  # Data access operations
├── Model Layer                # Entity classes
└── Database Layer             # JDBC with connection pooling
```

### Database Schema
- **products**: Product catalog with SKU management
- **categories**: Product categorization
- **suppliers**: Supplier information
- **inventory**: Real-time stock levels by location
- **stock_movements**: Complete audit trail
- **purchase_orders**: Purchase order workflow
- **purchase_order_items**: Order line items

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Optional: MySQL 8.0+ for production use

### Running the Application

1. **Clone and build:**
   ```bash
   mvn clean compile
   ```

2. **Run with GUI (Swing interface):**
   ```bash
   mvn exec:java
   # or specifically
   mvn exec:java@gui
   ```

3. **Run with CLI (Command Line Interface):**
   ```bash
   mvn exec:java -Dexec.mainClass="com.javamastery.inventory.cli.InventoryCLI"
   # or use the predefined execution
   mvn exec:java@cli
   ```

4. **Run tests:**
   ```bash
   # Test database layer
   mvn exec:java -Dexec.mainClass="com.javamastery.inventory.DatabaseTestApp"
   
   # Test service layer
   mvn exec:java -Dexec.mainClass="com.javamastery.inventory.ServiceTestApp"
   
   # Run unit tests
   mvn test
   ```

### Configuration

The application uses H2 in-memory database by default. To use MySQL:

1. Edit `src/main/resources/database.properties`
2. Uncomment MySQL configuration
3. Update connection details
4. Ensure MySQL server is running

## Key Components

### Services
- **CategoryService**: Category management with business validation
- **ProductService**: Product lifecycle management with SKU generation
- **InventoryService**: Stock operations with movement tracking
- **PurchaseOrderService**: Order workflow management with status transitions

### Data Access
- **BaseDao**: Common CRUD operations for all entities
- **Specific DAOs**: Entity-specific data access with advanced queries
- **Transaction Management**: ACID compliance with proper rollback
- **Connection Pooling**: Efficient resource management

### Models
- **Rich Entities**: Business logic embedded in model classes
- **Validation**: Comprehensive data validation
- **Enumerations**: Type-safe status and movement tracking
- **Workflow Management**: Proper state transitions for purchase orders

## Business Rules Implemented

- Unique SKU enforcement across all products
- Category deletion protection when products exist
- Stock availability checking before allocation
- Reorder level monitoring and alerts
- Audit trail for all stock movements
- Price validation (unit price vs cost price)
- Location-based inventory tracking

## Testing

The application includes comprehensive testing:

- **DatabaseTestApp**: Validates core database operations
- **ServiceTestApp**: Tests business logic and validation rules
- **Integration Tests**: End-to-end workflow validation

## Technology Stack

- **Java 17**: Modern Java features and performance
- **JDBC**: Direct database access with prepared statements
- **HikariCP**: High-performance connection pooling
- **H2 Database**: Embedded database for development
- **MySQL**: Production database support
- **Swing**: Rich desktop UI framework
- **SLF4J + Logback**: Comprehensive logging
- **Maven**: Build and dependency management

## Demo Data

The application initializes with sample data including:
- 4 product categories (Electronics, Office Supplies, Furniture, Software)
- 3 suppliers with contact information
- 5 sample products with pricing and inventory
- Initial stock levels for demonstration

## Enterprise Features

- **Layered Architecture**: Proper separation of concerns
- **Exception Handling**: Comprehensive error management
- **Data Validation**: Input validation at multiple layers
- **Audit Trail**: Complete tracking of all changes
- **Business Rules**: Enforced data integrity
- **Connection Pooling**: Production-ready database access
- **Configuration Management**: Externalized configuration
- **Logging**: Comprehensive application logging

This inventory management system demonstrates enterprise-level development practices with Java, JDBC, and proper software architecture patterns.