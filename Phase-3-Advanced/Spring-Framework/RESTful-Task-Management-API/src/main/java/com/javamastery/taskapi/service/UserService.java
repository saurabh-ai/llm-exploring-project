package com.javamastery.taskapi.service;

import com.javamastery.taskapi.dto.CreateUserRequest;
import com.javamastery.taskapi.dto.TaskDto;
import com.javamastery.taskapi.dto.UserDto;
import com.javamastery.taskapi.exception.UserNotFoundException;
import com.javamastery.taskapi.exception.ValidationException;
import com.javamastery.taskapi.model.User;
import com.javamastery.taskapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for User operations
 */
@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final TaskService taskService;
    
    @Autowired
    public UserService(UserRepository userRepository, TaskService taskService) {
        this.userRepository = userRepository;
        this.taskService = taskService;
    }
    
    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination: {}", pageable);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToDto);
    }
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        UserDto userDto = convertToDto(user);
        userDto.setTaskCount(user.getTasks().size());
        return userDto;
    }
    
    /**
     * Create a new user
     */
    public UserDto createUser(CreateUserRequest request) {
        logger.debug("Creating new user: {}", request);
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists: " + request.getUsername());
        }
        
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists: " + request.getEmail());
        }
        
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );
        
        User savedUser = userRepository.save(user);
        logger.info("User created with ID: {}", savedUser.getId());
        
        return convertToDto(savedUser);
    }
    
    /**
     * Update user information
     */
    public UserDto updateUser(Long id, CreateUserRequest request) {
        logger.debug("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        // Validate username uniqueness (excluding current user)
        if (!existingUser.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists: " + request.getUsername());
        }
        
        // Validate email uniqueness (excluding current user)
        if (!existingUser.getEmail().equals(request.getEmail()) &&
            userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists: " + request.getEmail());
        }
        
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        
        User updatedUser = userRepository.save(existingUser);
        logger.info("User updated with ID: {}", updatedUser.getId());
        
        return convertToDto(updatedUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        // Check if user has tasks - in a real application, you might want to handle this differently
        if (!user.getTasks().isEmpty()) {
            throw new ValidationException("Cannot delete user with existing tasks. User has " + 
                    user.getTasks().size() + " tasks.");
        }
        
        userRepository.delete(user);
        logger.info("User deleted with ID: {}", id);
    }
    
    /**
     * Get user's tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getUserTasks(Long userId) {
        logger.debug("Fetching tasks for user ID: {}", userId);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        
        return taskService.getTasksByUserId(userId);
    }
    
    /**
     * Check if user exists
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Convert User entity to UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setTaskCount(user.getTasks().size());
        return dto;
    }
}