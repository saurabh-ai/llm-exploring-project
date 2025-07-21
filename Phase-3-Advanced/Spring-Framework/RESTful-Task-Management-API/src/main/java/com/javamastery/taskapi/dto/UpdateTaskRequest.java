package com.javamastery.taskapi.dto;

import com.javamastery.taskapi.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for updating existing tasks
 */
public class UpdateTaskRequest {
    
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private TaskStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    
    @Min(value = 1, message = "Priority must be between 1 and 3")
    @Max(value = 3, message = "Priority must be between 1 and 3")
    private Integer priority;
    
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    // Flag to indicate if due date should be cleared (null)
    private Boolean clearDueDate = false;
    
    // Flag to indicate if category should be cleared (null)
    private Boolean clearCategory = false;
    
    // Default constructor
    public UpdateTaskRequest() {
    }
    
    // Constructor with basic parameters
    public UpdateTaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    // Helper method to check if any field is being updated
    public boolean hasUpdates() {
        return title != null || 
               description != null || 
               status != null || 
               dueDate != null || 
               priority != null || 
               categoryId != null ||
               clearDueDate ||
               clearCategory;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Boolean getClearDueDate() {
        return clearDueDate;
    }
    
    public void setClearDueDate(Boolean clearDueDate) {
        this.clearDueDate = clearDueDate;
    }
    
    public Boolean getClearCategory() {
        return clearCategory;
    }
    
    public void setClearCategory(Boolean clearCategory) {
        this.clearCategory = clearCategory;
    }
    
    @Override
    public String toString() {
        return "UpdateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", categoryId=" + categoryId +
                ", clearDueDate=" + clearDueDate +
                ", clearCategory=" + clearCategory +
                '}';
    }
}