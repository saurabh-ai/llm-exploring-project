package com.javamastery.distributed.user.service;

import com.javamastery.distributed.user.dto.UserRegistrationDto;
import com.javamastery.distributed.user.dto.LoginRequest;
import com.javamastery.distributed.user.dto.JwtResponse;
import com.javamastery.distributed.user.entity.User;
import com.javamastery.distributed.user.repository.UserRepository;
import com.javamastery.distributed.user.security.JwtService;
import com.javamastery.distributed.common.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setRole("USER");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        user.setRole(User.Role.USER);
        user.setEnabled(true);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto result = userService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
        assertTrue(result.isEnabled());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.registerUser(registrationDto)
        );
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        // Act
        JwtResponse result = userService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "hashedpassword");
        verify(jwtService).generateToken("testuser", "USER");
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.authenticateUser(loginRequest)
        );
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.authenticateUser(loginRequest)
        );
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testGetUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.getUserByUsername("nonexistent")
        );
        assertEquals("User not found", exception.getMessage());
    }
}