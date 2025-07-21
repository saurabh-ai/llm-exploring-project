package com.javamastery.taskmgmt.view;

import com.javamastery.taskmgmt.model.Task;
import com.javamastery.taskmgmt.model.TaskModel;
import com.javamastery.taskmgmt.model.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Main view of the Task Management application.
 * This class represents the View in the MVC pattern.
 */
public class TaskView extends JFrame {
    
    private TaskListPanel taskListPanel;
    private StatusBarPanel statusBarPanel;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    
    // Menu items
    private JMenuItem newTaskMenuItem;
    private JMenuItem editTaskMenuItem;
    private JMenuItem deleteTaskMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem aboutMenuItem;
    
    // Toolbar buttons
    private JButton newTaskButton;
    private JButton editTaskButton;
    private JButton deleteTaskButton;
    private JButton markCompleteButton;
    private JButton markPendingButton;
    
    // Filter and sort components
    private JComboBox<TaskModel.FilterBy> filterComboBox;
    private JComboBox<TaskModel.SortBy> sortComboBox;
    
    public TaskView() {
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupToolBar();
        configureFrame();
    }
    
    private void initializeComponents() {
        taskListPanel = new TaskListPanel();
        statusBarPanel = new StatusBarPanel();
        
        // Initialize filter and sort components
        filterComboBox = new JComboBox<>(TaskModel.FilterBy.values());
        sortComboBox = new JComboBox<>(TaskModel.SortBy.values());
        
        // Set default selections
        filterComboBox.setSelectedItem(TaskModel.FilterBy.ALL);
        sortComboBox.setSelectedItem(TaskModel.SortBy.CREATION_DATE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create control panel for filters and sorting
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(taskListPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("View Options"));
        
        panel.add(new JLabel("Filter:"));
        panel.add(filterComboBox);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Sort by:"));
        panel.add(sortComboBox);
        
        return panel;
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        newTaskMenuItem = new JMenuItem("New Task");
        newTaskMenuItem.setMnemonic('N');
        newTaskMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        
        editTaskMenuItem = new JMenuItem("Edit Task");
        editTaskMenuItem.setMnemonic('E');
        editTaskMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        editTaskMenuItem.setEnabled(false);
        
        deleteTaskMenuItem = new JMenuItem("Delete Task");
        deleteTaskMenuItem.setMnemonic('D');
        deleteTaskMenuItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        deleteTaskMenuItem.setEnabled(false);
        
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        
        fileMenu.add(newTaskMenuItem);
        fileMenu.add(editTaskMenuItem);
        fileMenu.add(deleteTaskMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic('A');
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        newTaskButton = new JButton("New Task");
        newTaskButton.setToolTipText("Create a new task (Ctrl+N)");
        
        editTaskButton = new JButton("Edit Task");
        editTaskButton.setToolTipText("Edit selected task (Ctrl+E)");
        editTaskButton.setEnabled(false);
        
        deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.setToolTipText("Delete selected task (Delete)");
        deleteTaskButton.setEnabled(false);
        
        markCompleteButton = new JButton("Mark Complete");
        markCompleteButton.setToolTipText("Mark selected task as complete");
        markCompleteButton.setEnabled(false);
        
        markPendingButton = new JButton("Mark Pending");
        markPendingButton.setToolTipText("Mark selected task as pending");
        markPendingButton.setEnabled(false);
        
        toolBar.add(newTaskButton);
        toolBar.addSeparator();
        toolBar.add(editTaskButton);
        toolBar.add(deleteTaskButton);
        toolBar.addSeparator();
        toolBar.add(markCompleteButton);
        toolBar.add(markPendingButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void configureFrame() {
        setTitle("Task Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Set application icon if available
        try {
            // For now, use a simple default icon
            setIconImage(createDefaultIcon());
        } catch (Exception e) {
            // Icon creation failed, continue without icon
        }
    }
    
    private Image createDefaultIcon() {
        // Create a simple default icon
        BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(Color.WHITE);
        g2d.drawString("T", 4, 12);
        g2d.dispose();
        return icon;
    }
    
    // Getter methods for components
    public TaskListPanel getTaskListPanel() {
        return taskListPanel;
    }
    
    public StatusBarPanel getStatusBarPanel() {
        return statusBarPanel;
    }
    
    public JComboBox<TaskModel.FilterBy> getFilterComboBox() {
        return filterComboBox;
    }
    
    public JComboBox<TaskModel.SortBy> getSortComboBox() {
        return sortComboBox;
    }
    
    // Methods to add action listeners
    public void addNewTaskListener(ActionListener listener) {
        newTaskButton.addActionListener(listener);
        newTaskMenuItem.addActionListener(listener);
    }
    
    public void addEditTaskListener(ActionListener listener) {
        editTaskButton.addActionListener(listener);
        editTaskMenuItem.addActionListener(listener);
    }
    
    public void addDeleteTaskListener(ActionListener listener) {
        deleteTaskButton.addActionListener(listener);
        deleteTaskMenuItem.addActionListener(listener);
    }
    
    public void addMarkCompleteListener(ActionListener listener) {
        markCompleteButton.addActionListener(listener);
    }
    
    public void addMarkPendingListener(ActionListener listener) {
        markPendingButton.addActionListener(listener);
    }
    
    public void addExitListener(ActionListener listener) {
        exitMenuItem.addActionListener(listener);
    }
    
    public void addAboutListener(ActionListener listener) {
        aboutMenuItem.addActionListener(listener);
    }
    
    public void addFilterListener(ActionListener listener) {
        filterComboBox.addActionListener(listener);
    }
    
    public void addSortListener(ActionListener listener) {
        sortComboBox.addActionListener(listener);
    }
    
    // Methods to update UI state
    public void updateTaskActionStates(boolean hasSelection, Task selectedTask) {
        editTaskButton.setEnabled(hasSelection);
        editTaskMenuItem.setEnabled(hasSelection);
        deleteTaskButton.setEnabled(hasSelection);
        deleteTaskMenuItem.setEnabled(hasSelection);
        
        if (hasSelection && selectedTask != null) {
            markCompleteButton.setEnabled(selectedTask.getStatus() != TaskStatus.COMPLETED);
            markPendingButton.setEnabled(selectedTask.getStatus() != TaskStatus.PENDING);
        } else {
            markCompleteButton.setEnabled(false);
            markPendingButton.setEnabled(false);
        }
    }
    
    // Utility methods
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
}