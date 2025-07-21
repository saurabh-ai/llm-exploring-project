package com.javamastery.taskmgmt.controller;

import com.javamastery.taskmgmt.model.Task;
import com.javamastery.taskmgmt.model.TaskModel;
import com.javamastery.taskmgmt.view.TaskView;
import com.javamastery.taskmgmt.view.TaskFormDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

/**
 * TaskController is the main controller in the MVC pattern.
 * It coordinates between the TaskModel and TaskView, handling user interactions
 * and updating the view based on model changes.
 */
public class TaskController implements TaskModel.TaskModelObserver {
    
    private TaskModel model;
    private TaskView view;
    private TaskActionHandler actionHandler;
    
    public TaskController(TaskModel model, TaskView view) {
        this.model = model;
        this.view = view;
        this.actionHandler = new TaskActionHandler();
        
        initializeController();
    }
    
    private void initializeController() {
        // Register as observer of the model
        model.addObserver(this);
        
        // Set up action listeners
        setupActionListeners();
        
        // Load initial data
        loadInitialData();
        
        // Set up window closing behavior
        setupWindowClosing();
        
        // Initial UI update
        updateView();
    }
    
    private void setupActionListeners() {
        view.addNewTaskListener(actionHandler::handleNewTask);
        view.addEditTaskListener(actionHandler::handleEditTask);
        view.addDeleteTaskListener(actionHandler::handleDeleteTask);
        view.addMarkCompleteListener(actionHandler::handleMarkComplete);
        view.addMarkPendingListener(actionHandler::handleMarkPending);
        view.addExitListener(actionHandler::handleExit);
        view.addAboutListener(actionHandler::handleAbout);
        view.addFilterListener(actionHandler::handleFilterChange);
        view.addSortListener(actionHandler::handleSortChange);
        
        // Listen to task list selection changes
        view.getTaskListPanel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateTaskActionStates();
            }
        });
        
        // Listen to double-click on tasks
        view.getTaskListPanel().addPropertyChangeListener("taskDoubleClicked", 
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue() instanceof Task) {
                        actionHandler.handleEditTask(null);
                    }
                }
            });
    }
    
    private void setupWindowClosing() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleApplicationExit();
            }
        });
    }
    
    private void loadInitialData() {
        try {
            model.loadFromFile();
            view.getStatusBarPanel().showTemporaryMessage("Tasks loaded successfully", 3000);
        } catch (IOException e) {
            // File might not exist on first run, which is normal
            view.getStatusBarPanel().setStatus("Ready - No saved tasks found");
        }
    }
    
    private void updateView() {
        List<Task> tasks = getCurrentFilteredAndSortedTasks();
        view.getTaskListPanel().updateTasks(tasks);
        updateStatusBar();
        updateTaskActionStates();
    }
    
    private List<Task> getCurrentFilteredAndSortedTasks() {
        TaskModel.FilterBy filter = (TaskModel.FilterBy) view.getFilterComboBox().getSelectedItem();
        TaskModel.SortBy sortBy = (TaskModel.SortBy) view.getSortComboBox().getSelectedItem();
        return model.getTasks(filter, sortBy);
    }
    
    private void updateStatusBar() {
        int total = model.getTotalTaskCount();
        int pending = model.getPendingTaskCount();
        int completed = model.getCompletedTaskCount();
        int overdue = model.getOverdueTaskCount();
        
        view.getStatusBarPanel().updateStatistics(total, pending, completed, overdue);
    }
    
    private void updateTaskActionStates() {
        Task selectedTask = view.getTaskListPanel().getSelectedTask();
        boolean hasSelection = selectedTask != null;
        view.updateTaskActionStates(hasSelection, selectedTask);
    }
    
    private void handleApplicationExit() {
        try {
            model.saveToFile();
        } catch (IOException e) {
            int result = view.showConfirmDialog(
                "Failed to save tasks. Exit anyway?\n" + e.getMessage());
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        System.exit(0);
    }
    
    // TaskModel.TaskModelObserver implementation
    @Override
    public void onTaskAdded(Task task) {
        updateView();
        view.getStatusBarPanel().showTemporaryMessage(
            "Task '" + task.getTitle() + "' added", 2000);
    }
    
    @Override
    public void onTaskUpdated(Task task) {
        updateView();
        view.getStatusBarPanel().showTemporaryMessage(
            "Task '" + task.getTitle() + "' updated", 2000);
    }
    
    @Override
    public void onTaskRemoved(Task task) {
        updateView();
        view.getStatusBarPanel().showTemporaryMessage(
            "Task '" + task.getTitle() + "' deleted", 2000);
    }
    
    @Override
    public void onTasksLoaded() {
        updateView();
    }
    
    @Override
    public void onModelCleared() {
        updateView();
    }
    
    // Inner class for handling actions
    private class TaskActionHandler {
        
        public void handleNewTask(ActionEvent e) {
            TaskFormDialog dialog = TaskFormDialog.createNewTaskDialog(view);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Task newTask = dialog.getTask();
                model.addTask(newTask);
            }
        }
        
        public void handleEditTask(ActionEvent e) {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask == null) {
                view.showErrorMessage("Please select a task to edit.");
                return;
            }
            
            // Create a copy of the task for editing
            Task taskCopy = createTaskCopy(selectedTask);
            
            TaskFormDialog dialog = TaskFormDialog.createEditTaskDialog(view, taskCopy);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                model.updateTask(dialog.getTask());
            }
        }
        
        public void handleDeleteTask(ActionEvent e) {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask == null) {
                view.showErrorMessage("Please select a task to delete.");
                return;
            }
            
            int result = view.showConfirmDialog(
                "Are you sure you want to delete the task '" + 
                selectedTask.getTitle() + "'?");
            
            if (result == JOptionPane.YES_OPTION) {
                model.removeTask(selectedTask);
            }
        }
        
        public void handleMarkComplete(ActionEvent e) {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                model.markTaskComplete(selectedTask.getId());
            }
        }
        
        public void handleMarkPending(ActionEvent e) {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                model.markTaskPending(selectedTask.getId());
            }
        }
        
        public void handleFilterChange(ActionEvent e) {
            updateView();
        }
        
        public void handleSortChange(ActionEvent e) {
            updateView();
        }
        
        public void handleExit(ActionEvent e) {
            handleApplicationExit();
        }
        
        public void handleAbout(ActionEvent e) {
            String aboutMessage = 
                "Task Management System\n" +
                "Version 1.0\n\n" +
                "A desktop application demonstrating the MVC pattern\n" +
                "with Java Swing.\n\n" +
                "Features:\n" +
                "• Create, edit, and delete tasks\n" +
                "• Mark tasks as complete or pending\n" +
                "• Filter and sort tasks\n" +
                "• Automatic data persistence\n\n" +
                "Built with Java " + System.getProperty("java.version");
            
            view.showInfoMessage(aboutMessage);
        }
        
        private Task createTaskCopy(Task original) {
            Task copy = new Task(
                original.getTitle(),
                original.getDescription(),
                original.getPriority(),
                original.getDueDate()
            );
            copy.setId(original.getId());
            copy.setStatus(original.getStatus());
            copy.setCreationDate(original.getCreationDate());
            return copy;
        }
    }
}