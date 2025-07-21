package com.javamastery.inventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database schema initialization and sample data setup.
 * Creates all required tables and optionally loads sample data.
 */
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    private final DataSource dataSource;
    private final DatabaseConfig.DatabaseType databaseType;
    
    public DatabaseInitializer(DataSource dataSource, DatabaseConfig.DatabaseType databaseType) {
        this.dataSource = dataSource;
        this.databaseType = databaseType;
    }
    
    /**
     * Initialize the complete database schema
     */
    public void initializeDatabase() {
        try {
            createTables();
            createIndexes();
            logger.info("Database schema initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize database schema", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Initialize database with sample data for demonstration
     */
    public void initializeWithSampleData() {
        try {
            initializeDatabase();
            insertSampleData();
            logger.info("Database initialized with sample data");
        } catch (SQLException e) {
            logger.error("Failed to initialize database with sample data", e);
            throw new RuntimeException("Database initialization with sample data failed", e);
        }
    }
    
    /**
     * Create all required tables
     */
    private void createTables() throws SQLException {
        List<String> createTableStatements = getCreateTableStatements();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : createTableStatements) {
                logger.debug("Executing: {}", sql);
                stmt.execute(sql);
            }
            
            logger.info("All tables created successfully");
        }
    }
    
    /**
     * Create database indexes for performance optimization
     */
    private void createIndexes() throws SQLException {
        List<String> indexStatements = getCreateIndexStatements();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : indexStatements) {
                try {
                    logger.debug("Executing: {}", sql);
                    stmt.execute(sql);
                } catch (SQLException e) {
                    // Index might already exist, log warning but continue
                    logger.warn("Failed to create index: {}", e.getMessage());
                }
            }
            
            logger.info("Database indexes created successfully");
        }
    }
    
    /**
     * Get table creation statements based on database type
     */
    private List<String> getCreateTableStatements() {
        List<String> statements = new ArrayList<>();
        
        // Users table
        statements.add("""
            CREATE TABLE IF NOT EXISTS users (
                user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(100) NOT NULL UNIQUE,
                email VARCHAR(255) NOT NULL UNIQUE,
                role ENUM('ADMIN', 'MANAGER', 'EMPLOYEE') NOT NULL DEFAULT 'EMPLOYEE',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """);

        // Categories table
        statements.add("""
            CREATE TABLE IF NOT EXISTS categories (
                category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL UNIQUE,
                description TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);
        
        // Suppliers table
        statements.add("""
            CREATE TABLE IF NOT EXISTS suppliers (
                supplier_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL,
                contact_person VARCHAR(255),
                email VARCHAR(255),
                phone VARCHAR(50),
                address TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);
        
        // Products table
        statements.add("""
            CREATE TABLE IF NOT EXISTS products (
                product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                sku VARCHAR(100) UNIQUE NOT NULL,
                category_id BIGINT,
                supplier_id BIGINT,
                unit_price DECIMAL(10,2) NOT NULL,
                cost_price DECIMAL(10,2) NOT NULL,
                quantity_in_stock INT NOT NULL DEFAULT 0,
                reorder_level INT DEFAULT 10,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(category_id),
                FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
            )
            """);
        
        // Inventory table
        statements.add("""
            CREATE TABLE IF NOT EXISTS inventory (
                inventory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                product_id BIGINT NOT NULL,
                quantity_on_hand INT NOT NULL DEFAULT 0,
                quantity_allocated INT NOT NULL DEFAULT 0,
                location VARCHAR(100) DEFAULT 'MAIN',
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (product_id) REFERENCES products(product_id)
            )
            """);
        
        // Inventory transactions table
        statements.add("""
            CREATE TABLE IF NOT EXISTS inventory_transactions (
                transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                product_id BIGINT NOT NULL,
                transaction_type ENUM('IN', 'OUT') NOT NULL,
                quantity INT NOT NULL,
                reason VARCHAR(255) NOT NULL,
                transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                user_id BIGINT NOT NULL,
                FOREIGN KEY (product_id) REFERENCES products(product_id),
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )
            """);

        // Stock movements table (keeping for backward compatibility)
        statements.add("""
            CREATE TABLE IF NOT EXISTS stock_movements (
                movement_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                product_id BIGINT NOT NULL,
                movement_type ENUM('IN', 'OUT', 'ADJUSTMENT', 'TRANSFER') NOT NULL,
                quantity INT NOT NULL,
                reference_type ENUM('PURCHASE_ORDER', 'SALES_ORDER', 'ADJUSTMENT', 'TRANSFER') NOT NULL,
                reference_id BIGINT,
                notes TEXT,
                created_by VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (product_id) REFERENCES products(product_id)
            )
            """);
        
        // Purchase orders table
        statements.add("""
            CREATE TABLE IF NOT EXISTS purchase_orders (
                po_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                supplier_id BIGINT NOT NULL,
                po_number VARCHAR(50) UNIQUE NOT NULL,
                status ENUM('DRAFT', 'SENT', 'CONFIRMED', 'RECEIVED', 'CANCELLED') DEFAULT 'DRAFT',
                total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
                order_date DATE NOT NULL,
                expected_date DATE,
                received_date DATE,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
            )
            """);
        
        // Purchase order items table
        statements.add("""
            CREATE TABLE IF NOT EXISTS purchase_order_items (
                poi_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                po_id BIGINT NOT NULL,
                product_id BIGINT NOT NULL,
                quantity_ordered INT NOT NULL,
                quantity_received INT DEFAULT 0,
                unit_cost DECIMAL(10,2) NOT NULL,
                FOREIGN KEY (po_id) REFERENCES purchase_orders(po_id),
                FOREIGN KEY (product_id) REFERENCES products(product_id)
            )
            """);
        
        return statements;
    }
    
    /**
     * Get index creation statements for performance optimization
     */
    private List<String> getCreateIndexStatements() {
        List<String> statements = new ArrayList<>();
        
        statements.add("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_products_supplier ON products(supplier_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_inventory_product ON inventory(product_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_inventory_transactions_product ON inventory_transactions(product_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_inventory_transactions_user ON inventory_transactions(user_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_inventory_transactions_date ON inventory_transactions(transaction_date)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_stock_movements_product ON stock_movements(product_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_stock_movements_created ON stock_movements(created_at)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_purchase_orders_supplier ON purchase_orders(supplier_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_purchase_orders_status ON purchase_orders(status)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_purchase_order_items_po ON purchase_order_items(po_id)");
        statements.add("CREATE INDEX IF NOT EXISTS idx_purchase_order_items_product ON purchase_order_items(product_id)");
        
        return statements;
    }
    
    /**
     * Insert sample data for demonstration purposes
     */
    private void insertSampleData() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement()) {
                // Check if sample data already exists
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        logger.info("Sample data already exists, skipping insert");
                        conn.commit();
                        return;
                    }
                }
                
                // Insert sample users
                stmt.execute("""
                    INSERT INTO users (username, email, role) VALUES
                    ('admin', 'admin@inventory.com', 'ADMIN'),
                    ('manager1', 'manager1@inventory.com', 'MANAGER'),
                    ('employee1', 'employee1@inventory.com', 'EMPLOYEE'),
                    ('employee2', 'employee2@inventory.com', 'EMPLOYEE')
                    """);
                
                // Insert sample categories
                stmt.execute("""
                    INSERT INTO categories (name, description) VALUES
                    ('Electronics', 'Electronic devices and components'),
                    ('Office Supplies', 'General office supplies and stationery'),
                    ('Furniture', 'Office and home furniture'),
                    ('Software', 'Software licenses and applications')
                    """);
                
                // Insert sample suppliers
                stmt.execute("""
                    INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES
                    ('TechCorp Solutions', 'John Smith', 'john.smith@techcorp.com', '555-0101', '123 Tech Street, Silicon Valley, CA'),
                    ('Office Plus Ltd', 'Sarah Johnson', 'sarah@officeplus.com', '555-0102', '456 Business Ave, New York, NY'),
                    ('Furniture World', 'Mike Brown', 'mike@furnitureworld.com', '555-0103', '789 Furniture Blvd, Chicago, IL')
                    """);
                
                // Insert sample products
                stmt.execute("""
                    INSERT INTO products (name, description, sku, category_id, unit_price, cost_price, reorder_level) VALUES
                    ('Laptop Computer', 'High-performance business laptop', 'TECH-LAP-001', 1, 1299.99, 899.99, 5),
                    ('Wireless Mouse', 'Ergonomic wireless optical mouse', 'TECH-MOU-001', 1, 29.99, 15.99, 25),
                    ('A4 Paper Pack', 'Premium white A4 paper, 500 sheets', 'OFF-PAP-001', 2, 12.99, 8.99, 50),
                    ('Office Desk', 'Adjustable height office desk', 'FUR-DSK-001', 3, 599.99, 399.99, 3),
                    ('Antivirus Software', 'Annual license for antivirus protection', 'SOF-ANT-001', 4, 79.99, 39.99, 10)
                    """);
                
                // Insert sample inventory
                stmt.execute("""
                    INSERT INTO inventory (product_id, quantity_on_hand, quantity_allocated, location) VALUES
                    (1, 15, 2, 'MAIN'),
                    (2, 50, 5, 'MAIN'),
                    (3, 100, 10, 'MAIN'),
                    (4, 8, 1, 'MAIN'),
                    (5, 25, 3, 'MAIN')
                    """);
                
                conn.commit();
                logger.info("Sample data inserted successfully");
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Check if the database schema exists
     */
    public boolean schemaExists() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeQuery("SELECT COUNT(*) FROM products LIMIT 1");
            return true;
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Drop all tables (for testing purposes)
     */
    public void dropAllTables() throws SQLException {
        List<String> dropStatements = List.of(
            "DROP TABLE IF EXISTS purchase_order_items",
            "DROP TABLE IF EXISTS purchase_orders",
            "DROP TABLE IF EXISTS stock_movements",
            "DROP TABLE IF EXISTS inventory",
            "DROP TABLE IF EXISTS products",
            "DROP TABLE IF EXISTS suppliers",
            "DROP TABLE IF EXISTS categories"
        );
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : dropStatements) {
                stmt.execute(sql);
            }
            
            logger.info("All tables dropped successfully");
        }
    }
}