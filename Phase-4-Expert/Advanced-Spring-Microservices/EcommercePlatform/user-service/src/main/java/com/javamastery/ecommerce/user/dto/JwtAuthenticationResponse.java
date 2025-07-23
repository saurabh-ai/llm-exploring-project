package com.javamastery.ecommerce.user.dto;

public class JwtAuthenticationResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private UserResponseDto user;
    
    // Constructors
    public JwtAuthenticationResponse() {}
    
    public JwtAuthenticationResponse(String accessToken, UserResponseDto user) {
        this.accessToken = accessToken;
        this.user = user;
    }
    
    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public UserResponseDto getUser() {
        return user;
    }
    
    public void setUser(UserResponseDto user) {
        this.user = user;
    }
}