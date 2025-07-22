package com.javamastery.distributed.common.dto.user;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
    
    // Default constructor
    public UserDto() {}
    
    // Constructor
    public UserDto(Long id, String username, String email, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}