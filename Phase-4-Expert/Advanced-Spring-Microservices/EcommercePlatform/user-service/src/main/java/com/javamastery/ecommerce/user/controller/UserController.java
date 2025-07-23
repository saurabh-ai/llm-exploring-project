package com.javamastery.ecommerce.user.controller;

import com.javamastery.ecommerce.shared.dto.BaseResponse;
import com.javamastery.ecommerce.user.dto.*;
import com.javamastery.ecommerce.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User registration, authentication and profile management")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<BaseResponse<UserResponseDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        
        UserResponseDto user = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("User registered successfully", user));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<BaseResponse<JwtAuthenticationResponse>> authenticateUser(
            @Valid @RequestBody LoginDto loginDto) {
        
        JwtAuthenticationResponse response = userService.authenticateUser(loginDto);
        return ResponseEntity.ok(BaseResponse.success("Login successful", response));
    }
    
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserResponseDto>> getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDto user = userService.getUserProfile(username);
        return ResponseEntity.ok(BaseResponse.success(user));
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserResponseDto>> updateCurrentUserProfile(
            @Valid @RequestBody UserRegistrationDto updateDto) {
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDto user = userService.updateUserProfile(username, updateDto);
        return ResponseEntity.ok(BaseResponse.success("Profile updated successfully", user));
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(BaseResponse.success(user));
    }
}