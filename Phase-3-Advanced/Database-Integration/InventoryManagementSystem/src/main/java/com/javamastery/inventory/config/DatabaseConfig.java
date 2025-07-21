package com.javamastery.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration and connection pool management using HikariCP.
 * Supports both H2 (development) and MySQL (production) databases.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    
    private static DatabaseConfig instance;
    private HikariDataSource dataSource;
    private DatabaseType databaseType;
    
    /**
     * Database types supported by the application
     */
    public enum DatabaseType {
        H2, MYSQL
    }
    
    private DatabaseConfig() {
        initializeDataSource();
    }
    
    /**
     * Get singleton instance of DatabaseConfig
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Initialize HikariCP data source based on configuration
     */
    private void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();
            HikariConfig config = createHikariConfig(props);
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized successfully with {} database", 
                       databaseType);
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Load database properties from configuration file
     */
    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        
        // Try to load from external config first, fallback to defaults
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Loaded database configuration from database.properties");
            } else {
                // Load default configuration
                loadDefaultProperties(props);
                logger.info("Using default database configuration");
            }
        }
        
        return props;
    }
    
    /**
     * Load default database properties for H2 development database
     */
    private void loadDefaultProperties(Properties props) {
        props.setProperty("database.type", "H2");
        props.setProperty("database.url", "jdbc:h2:mem:inventory;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        props.setProperty("database.username", "sa");
        props.setProperty("database.password", "");
        props.setProperty("database.driver", "org.h2.Driver");
        props.setProperty("hikari.maximumPoolSize", "10");
        props.setProperty("hikari.minimumIdle", "2");
        props.setProperty("hikari.connectionTimeout", "30000");
        props.setProperty("hikari.idleTimeout", "300000");
        props.setProperty("hikari.maxLifetime", "900000");
    }
    
    /**
     * Create HikariCP configuration from properties
     */
    private HikariConfig createHikariConfig(Properties props) {
        HikariConfig config = new HikariConfig();
        
        // Determine database type
        String dbTypeStr = props.getProperty("database.type", "H2");
        databaseType = DatabaseType.valueOf(dbTypeStr.toUpperCase());
        
        // Basic connection properties
        config.setJdbcUrl(props.getProperty("database.url"));
        config.setUsername(props.getProperty("database.username"));
        config.setPassword(props.getProperty("database.password"));
        config.setDriverClassName(props.getProperty("database.driver"));
        
        // Connection pool settings
        config.setMaximumPoolSize(Integer.parseInt(
            props.getProperty("hikari.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(
            props.getProperty("hikari.minimumIdle", "2")));
        config.setConnectionTimeout(Long.parseLong(
            props.getProperty("hikari.connectionTimeout", "30000")));
        config.setIdleTimeout(Long.parseLong(
            props.getProperty("hikari.idleTimeout", "300000")));
        config.setMaxLifetime(Long.parseLong(
            props.getProperty("hikari.maxLifetime", "900000")));
        
        // Additional HikariCP optimizations
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("InventoryConnectionPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return config;
    }
    
    /**
     * Get the data source for database operations
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Get the current database type
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
    
    /**
     * Check if the database connection is healthy
     */
    public boolean isHealthy() {
        try {
            return dataSource != null && !dataSource.isClosed() && 
                   dataSource.getConnection().isValid(5);
        } catch (Exception e) {
            logger.warn("Database health check failed", e);
            return false;
        }
    }
    
    /**
     * Get connection pool statistics
     */
    public String getPoolStats() {
        if (dataSource == null) {
            return "DataSource not initialized";
        }
        
        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
    
    /**
     * Close the data source and cleanup resources
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shutdown completed");
        }
    }
}