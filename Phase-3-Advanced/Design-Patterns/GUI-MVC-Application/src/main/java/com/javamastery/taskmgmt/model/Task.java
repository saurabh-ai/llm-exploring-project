package com.javamastery.taskmgmt.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Task entity representing a task in the task management system
 */
public class Task {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("priority")
    private int priority; // 1-5, where 1 is highest priority
    
    @JsonProperty("dueDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    
    @JsonProperty("status")
    private TaskStatus status;
    
    @JsonProperty("creationDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationDate;
    
    // Default constructor for Jackson
    public Task() {
        this.id = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.status = TaskStatus.PENDING;
    }
    
    // Constructor for creating new tasks
    public Task(String title, String description, int priority, LocalDate dueDate) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        this.priority = priority;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    // Utility methods
    @JsonIgnore
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status == TaskStatus.PENDING;
    }
    
    @JsonIgnore
    public String getPriorityText() {
        switch (priority) {
            case 1: return "Critical";
            case 2: return "High";
            case 3: return "Medium";
            case 4: return "Low";
            case 5: return "Lowest";
            default: return "Unknown";
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", dueDate=" + dueDate +
                '}';
    }
}