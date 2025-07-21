package com.javamastery.taskapi.controller;

import com.javamastery.taskapi.dto.*;
import com.javamastery.taskapi.service.UserService;
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

/**
 * REST Controller for User management operations
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Operation(summary = "Get all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    @GetMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<Page<UserDto>>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("GET /users - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserDto> users = userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(users, 
                "Successfully retrieved " + users.getTotalElements() + " users"));
    }
    
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<UserDto>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        logger.info("GET /users/{}", id);
        
        UserDto user = userService.getUserById(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(user, 
                "User retrieved successfully"));
    }
    
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
    })
    @PostMapping
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<UserDto>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        logger.info("POST /users - {}", request);
        
        UserDto createdUser = userService.createUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.javamastery.taskapi.dto.ApiResponse.success(createdUser, 
                        "User registered successfully"));
    }
    
    @Operation(summary = "Update user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<UserDto>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        
        logger.info("PUT /users/{} - {}", id, request);
        
        UserDto updatedUser = userService.updateUser(id, request);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(updatedUser, 
                "User updated successfully"));
    }
    
    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete user with existing tasks")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        logger.info("DELETE /users/{}", id);
        
        userService.deleteUser(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(
                "User deleted successfully"));
    }
    
    @Operation(summary = "Get user's tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's tasks retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/tasks")
    public ResponseEntity<com.javamastery.taskapi.dto.ApiResponse<List<TaskDto>>> getUserTasks(
            @Parameter(description = "User ID") @PathVariable Long id) {
        
        logger.info("GET /users/{}/tasks", id);
        
        List<TaskDto> userTasks = userService.getUserTasks(id);
        
        return ResponseEntity.ok(com.javamastery.taskapi.dto.ApiResponse.success(userTasks, 
                "Retrieved " + userTasks.size() + " tasks for user"));
    }
}