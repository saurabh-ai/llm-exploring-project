package com.javamastery.taskapi.dto;

import com.javamastery.taskapi.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for task search criteria
 */
public class TaskSearchRequest {
    
    @Positive(message = "User ID must be positive")
    private Long userId;
    
    @Positive(message = "Category ID must be positive")
    private Long categoryId;
    
    private TaskStatus status;
    
    @Min(value = 1, message = "Priority must be between 1 and 3")
    @Max(value = 3, message = "Priority must be between 1 and 3")
    private Integer priority;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDateFrom;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDateTo;
    
    private String searchTerm;
    
    // Pagination parameters
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must be positive")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;
    
    // Sorting parameters
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    
    // Default constructor
    public TaskSearchRequest() {
    }
    
    // Helper method to check if any search criteria is provided
    public boolean hasSearchCriteria() {
        return userId != null || 
               categoryId != null || 
               status != null || 
               priority != null || 
               dueDateFrom != null || 
               dueDateTo != null || 
               (searchTerm != null && !searchTerm.trim().isEmpty());
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getDueDateFrom() {
        return dueDateFrom;
    }
    
    public void setDueDateFrom(LocalDateTime dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }
    
    public LocalDateTime getDueDateTo() {
        return dueDateTo;
    }
    
    public void setDueDateTo(LocalDateTime dueDateTo) {
        this.dueDateTo = dueDateTo;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    @Override
    public String toString() {
        return "TaskSearchRequest{" +
                "userId=" + userId +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDateFrom=" + dueDateFrom +
                ", dueDateTo=" + dueDateTo +
                ", searchTerm='" + searchTerm + '\'' +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}