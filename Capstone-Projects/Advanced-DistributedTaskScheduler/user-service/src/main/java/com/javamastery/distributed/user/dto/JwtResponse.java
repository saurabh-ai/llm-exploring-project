package com.javamastery.distributed.user.dto;

public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    
    // Default constructor
    public JwtResponse() {}
    
    // Constructor
    public JwtResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}