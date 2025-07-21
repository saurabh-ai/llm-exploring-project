package com.javamastery.taskmgmt.controller;

import com.javamastery.taskmgmt.model.Task;
import com.javamastery.taskmgmt.model.TaskModel;
import com.javamastery.taskmgmt.view.TaskView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TaskActionHandler contains specific event handlers for user actions.
 * This class demonstrates the Command pattern by encapsulating actions
 * that can be triggered from multiple sources (menu items, toolbar buttons, etc.)
 */
public class TaskActionHandler {
    
    private TaskModel model;
    private TaskView view;
    
    public TaskActionHandler(TaskModel model, TaskView view) {
        this.model = model;
        this.view = view;
    }
    
    // Command interfaces for different actions
    public interface TaskCommand {
        void execute();
    }
    
    // New Task Command
    public class NewTaskCommand implements TaskCommand {
        @Override
        public void execute() {
            // Implementation would be here - this demonstrates the Command pattern
            // The actual implementation is in TaskController's inner class
        }
    }
    
    // Edit Task Command
    public class EditTaskCommand implements TaskCommand {
        private Task task;
        
        public EditTaskCommand(Task task) {
            this.task = task;
        }
        
        @Override
        public void execute() {
            // Implementation would be here
        }
    }
    
    // Delete Task Command
    public class DeleteTaskCommand implements TaskCommand {
        private Task task;
        
        public DeleteTaskCommand(Task task) {
            this.task = task;
        }
        
        @Override
        public void execute() {
            // Implementation would be here
        }
    }
    
    // Mark Complete Command
    public class MarkCompleteCommand implements TaskCommand {
        private String taskId;
        
        public MarkCompleteCommand(String taskId) {
            this.taskId = taskId;
        }
        
        @Override
        public void execute() {
            model.markTaskComplete(taskId);
        }
    }
    
    // Mark Pending Command
    public class MarkPendingCommand implements TaskCommand {
        private String taskId;
        
        public MarkPendingCommand(String taskId) {
            this.taskId = taskId;
        }
        
        @Override
        public void execute() {
            model.markTaskPending(taskId);
        }
    }
    
    // Action listener implementations that can be used with Swing components
    public ActionListener createNewTaskListener() {
        return e -> new NewTaskCommand().execute();
    }
    
    public ActionListener createEditTaskListener() {
        return e -> {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                new EditTaskCommand(selectedTask).execute();
            }
        };
    }
    
    public ActionListener createDeleteTaskListener() {
        return e -> {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                new DeleteTaskCommand(selectedTask).execute();
            }
        };
    }
    
    public ActionListener createMarkCompleteListener() {
        return e -> {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                new MarkCompleteCommand(selectedTask.getId()).execute();
            }
        };
    }
    
    public ActionListener createMarkPendingListener() {
        return e -> {
            Task selectedTask = view.getTaskListPanel().getSelectedTask();
            if (selectedTask != null) {
                new MarkPendingCommand(selectedTask.getId()).execute();
            }
        };
    }
    
    // Utility method to execute commands
    public void executeCommand(TaskCommand command) {
        if (command != null) {
            command.execute();
        }
    }
}