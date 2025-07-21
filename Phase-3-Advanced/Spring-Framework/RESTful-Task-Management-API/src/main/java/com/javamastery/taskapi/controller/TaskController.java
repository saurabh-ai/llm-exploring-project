package com.javamastery.taskapi.controller;

import com.javamastery.taskapi.dto.*;
import com.javamastery.taskapi.model.TaskStatus;
import com.javamastery.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Task management operations
 */
@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "Operations related to task management")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    private final TaskService taskService;
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @Operation(summary = "Get all tasks with pagination and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks")
    })
    @GetMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<Page<TaskDto>>> getAllTasks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("GET /tasks - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TaskDto> tasks = taskService.getAllTasks(pageable);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(tasks, 
                "Successfully retrieved " + tasks.getTotalElements() + " tasks"));
    }
    
    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<TaskDto>> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        
        logger.info("GET /tasks/{}", id);
        
        TaskDto task = taskService.getTaskById(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(task, 
                "Task retrieved successfully"));
    }
    
    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User or Category not found")
    })
    @PostMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<TaskDto>> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        
        logger.info("POST /tasks - {}", request);
        
        TaskDto createdTask = taskService.createTask(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.javamastery.taskapi.dto.ApiResponse.success(createdTask, 
                        "Task created successfully"));
    }
    
    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<TaskDto>> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        
        logger.info("PUT /tasks/{} - {}", id, request);
        
        TaskDto updatedTask = taskService.updateTask(id, request);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(updatedTask, 
                "Task updated successfully"));
    }
    
    @Operation(summary = "Update task status only")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<TaskDto>> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "New task status") @RequestParam TaskStatus status) {
        
        logger.info("PATCH /tasks/{}/status - status: {}", id, status);
        
        TaskDto updatedTask = taskService.updateTaskStatus(id, status);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(updatedTask, 
                "Task status updated successfully"));
    }
    
    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<String>> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        
        logger.info("DELETE /tasks/{}", id);
        
        taskService.deleteTask(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(
                "Task deleted successfully"));
    }
    
    @Operation(summary = "Search tasks with advanced criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<Page<TaskDto>>> searchTasks(
            @Valid @ModelAttribute TaskSearchRequest searchRequest) {
        
        logger.info("GET /tasks/search - {}", searchRequest);
        
        Page<TaskDto> tasks = taskService.searchTasks(searchRequest);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(tasks, 
                "Search completed. Found " + tasks.getTotalElements() + " tasks"));
    }
    
    @Operation(summary = "Get overdue tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue tasks retrieved successfully")
    })
    @GetMapping("/overdue")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<List<TaskDto>>> getOverdueTasks() {
        
        logger.info("GET /tasks/overdue");
        
        List<TaskDto> overdueTasks = taskService.getOverdueTasks();
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(overdueTasks, 
                "Found " + overdueTasks.size() + " overdue tasks"));
    }
    
    @Operation(summary = "Get tasks due within specified days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks due within specified days retrieved successfully")
    })
    @GetMapping("/due-soon")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<List<TaskDto>>> getTasksDueSoon(
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "7") int days) {
        
        logger.info("GET /tasks/due-soon?days={}", days);
        
        List<TaskDto> dueSoonTasks = taskService.getTasksDueWithinDays(days);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(dueSoonTasks, 
                "Found " + dueSoonTasks.size() + " tasks due within " + days + " days"));
    }
    
    @Operation(summary = "Get task analytics and statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully")
    })
    @GetMapping("/analytics")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<Map<String, Object>>> getTaskAnalytics() {
        
        logger.info("GET /tasks/analytics");
        
        Map<String, Object> analytics = taskService.getTaskAnalytics();
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(analytics, 
                "Task analytics retrieved successfully"));
    }
}