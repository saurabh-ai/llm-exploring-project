package com.javamastery.ecommerce.user.service;

import com.javamastery.ecommerce.shared.exception.BadRequestException;
import com.javamastery.ecommerce.shared.exception.ResourceNotFoundException;
import com.javamastery.ecommerce.user.dto.*;
import com.javamastery.ecommerce.user.entity.Role;
import com.javamastery.ecommerce.user.entity.User;
import com.javamastery.ecommerce.user.repository.UserRepository;
import com.javamastery.ecommerce.user.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public JwtAuthenticationResponse authenticateUser(LoginDto loginDto) {
        User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), 
                                                         loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password"));
        
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username/email or password");
        }
        
        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }
        
        String jwt = tokenProvider.generateToken(user.getUsername());
        UserResponseDto userDto = convertToDto(user);
        
        return new JwtAuthenticationResponse(jwt, userDto);
    }
    
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setRoles(Set.of(Role.USER));
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }
    
    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return convertToDto(user);
    }
    
    public UserResponseDto updateUserProfile(String username, UserRegistrationDto updateDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Check if email is being changed and if it's already in use
        if (!user.getEmail().equals(updateDto.getEmail()) && 
            userRepository.existsByEmail(updateDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        
        user.setEmail(updateDto.getEmail());
        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setPhone(updateDto.getPhone());
        
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return convertToDto(user);
    }
    
    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}