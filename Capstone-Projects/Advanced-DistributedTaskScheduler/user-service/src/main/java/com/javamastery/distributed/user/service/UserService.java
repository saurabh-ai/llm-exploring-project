package com.javamastery.distributed.user.service;

import com.javamastery.distributed.user.dto.UserRegistrationDto;
import com.javamastery.distributed.user.dto.JwtResponse;
import com.javamastery.distributed.user.dto.LoginRequest;
import com.javamastery.distributed.user.entity.User;
import com.javamastery.distributed.user.repository.UserRepository;
import com.javamastery.distributed.user.security.JwtService;
import com.javamastery.distributed.common.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserDto registerUser(UserRegistrationDto registrationDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(User.Role.valueOf(registrationDto.getRole().toUpperCase()));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOptional.get();
        
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        
        return new JwtResponse(token, user.getUsername(), user.getRole().name());
    }

    public UserDto getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return convertToDto(userOptional.get());
    }

    public UserDto getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return convertToDto(userOptional.get());
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtService.getUsernameFromToken(token);
            return userRepository.existsByUsername(username);
        } catch (Exception e) {
            return false;
        }
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isEnabled()
        );
    }
}