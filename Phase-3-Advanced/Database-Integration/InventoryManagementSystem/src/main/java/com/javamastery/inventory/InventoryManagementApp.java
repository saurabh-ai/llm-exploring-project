package com.javamastery.inventory;

import com.javamastery.inventory.config.DatabaseConfig;
import com.javamastery.inventory.config.DatabaseInitializer;
import com.javamastery.inventory.ui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;

/**
 * Main application entry point for the Inventory Management System
 */
public class InventoryManagementApp {
    private static final Logger logger = LoggerFactory.getLogger(InventoryManagementApp.class);
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel", e);
        }
        
        // Run on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Starting Inventory Management System");
                
                // Initialize database
                DatabaseConfig dbConfig = DatabaseConfig.getInstance();
                DataSource dataSource = dbConfig.getDataSource();
                
                // Check if database schema exists, if not initialize it
                DatabaseInitializer initializer = new DatabaseInitializer(dataSource, dbConfig.getDatabaseType());
                if (!initializer.schemaExists()) {
                    logger.info("Database schema not found, initializing with sample data...");
                    initializer.initializeWithSampleData();
                } else {
                    logger.info("Database schema exists, connecting to existing data");
                }
                
                logger.info("Database connection: {}", dbConfig.getPoolStats());
                
                // Create and show main window
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                
                logger.info("Inventory Management System started successfully");
                
            } catch (Exception e) {
                logger.error("Failed to start Inventory Management System", e);
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}