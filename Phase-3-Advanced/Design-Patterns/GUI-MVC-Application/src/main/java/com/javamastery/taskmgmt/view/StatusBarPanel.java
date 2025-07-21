package com.javamastery.taskmgmt.view;

import javax.swing.*;
import java.awt.*;

/**
 * Status bar panel that displays task statistics and application status
 */
public class StatusBarPanel extends JPanel {
    
    private JLabel totalTasksLabel;
    private JLabel pendingTasksLabel;
    private JLabel completedTasksLabel;
    private JLabel overdueTasksLabel;
    private JLabel statusLabel;
    
    public StatusBarPanel() {
        initializeComponents();
        setupLayout();
        updateStatistics(0, 0, 0, 0);
    }
    
    private void initializeComponents() {
        totalTasksLabel = new JLabel("Total: 0");
        pendingTasksLabel = new JLabel("Pending: 0");
        completedTasksLabel = new JLabel("Completed: 0");
        overdueTasksLabel = new JLabel("Overdue: 0");
        statusLabel = new JLabel("Ready");
        
        // Set colors for different statistics
        pendingTasksLabel.setForeground(new Color(0, 100, 200)); // Blue
        completedTasksLabel.setForeground(new Color(0, 150, 0)); // Green
        overdueTasksLabel.setForeground(Color.RED);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        setPreferredSize(new Dimension(0, 25));
        
        // Create statistics panel
        JPanel statisticsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statisticsPanel.add(totalTasksLabel);
        statisticsPanel.add(createSeparator());
        statisticsPanel.add(pendingTasksLabel);
        statisticsPanel.add(createSeparator());
        statisticsPanel.add(completedTasksLabel);
        statisticsPanel.add(createSeparator());
        statisticsPanel.add(overdueTasksLabel);
        
        add(statisticsPanel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.EAST);
    }
    
    private JLabel createSeparator() {
        JLabel separator = new JLabel("|");
        separator.setForeground(Color.GRAY);
        return separator;
    }
    
    public void updateStatistics(int total, int pending, int completed, int overdue) {
        totalTasksLabel.setText("Total: " + total);
        pendingTasksLabel.setText("Pending: " + pending);
        completedTasksLabel.setText("Completed: " + completed);
        overdueTasksLabel.setText("Overdue: " + overdue);
        
        // Update overdue label color intensity based on count
        if (overdue > 0) {
            overdueTasksLabel.setForeground(Color.RED);
            overdueTasksLabel.setFont(overdueTasksLabel.getFont().deriveFont(Font.BOLD));
        } else {
            overdueTasksLabel.setForeground(Color.GRAY);
            overdueTasksLabel.setFont(overdueTasksLabel.getFont().deriveFont(Font.PLAIN));
        }
        
        repaint();
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
        repaint();
    }
    
    public void showTemporaryMessage(String message, int durationMs) {
        String originalStatus = statusLabel.getText();
        statusLabel.setText(message);
        
        // Use Timer to revert the message after the specified duration
        Timer timer = new Timer(durationMs, e -> statusLabel.setText(originalStatus));
        timer.setRepeats(false);
        timer.start();
    }
}