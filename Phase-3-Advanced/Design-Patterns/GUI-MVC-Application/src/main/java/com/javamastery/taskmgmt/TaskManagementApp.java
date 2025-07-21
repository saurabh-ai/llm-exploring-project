package com.javamastery.taskmgmt;

import com.javamastery.taskmgmt.controller.TaskController;
import com.javamastery.taskmgmt.model.TaskModel;
import com.javamastery.taskmgmt.view.TaskView;

import javax.swing.*;

/**
 * TaskManagementApp is the main entry point for the Task Management application.
 * This class demonstrates the MVC pattern by creating and wiring together
 * the Model, View, and Controller components.
 */
public class TaskManagementApp {
    
    public static void main(String[] args) {
        // Set system look and feel for better native appearance
        setSystemLookAndFeel();
        
        // Create and run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowApplication();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAndExit("Failed to start application: " + e.getMessage());
            }
        });
    }
    
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If setting system L&F fails, continue with default
            System.err.println("Warning: Could not set system look and feel: " + e.getMessage());
        }
    }
    
    private static void createAndShowApplication() {
        // Create the MVC components
        TaskModel model = new TaskModel();
        TaskView view = new TaskView();
        TaskController controller = new TaskController(model, view);
        
        // Configure and show the main window
        view.setVisible(true);
        
        // Print startup message
        System.out.println("Task Management Application started successfully!");
        System.out.println("Data will be saved to: " + model.getDataFile().getAbsolutePath());
    }
    
    private static void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(null, message, "Application Error", 
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}