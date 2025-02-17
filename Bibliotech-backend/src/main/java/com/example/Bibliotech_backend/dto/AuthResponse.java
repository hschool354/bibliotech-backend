package com.example.Bibliotech_backend.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private boolean isFirstLogin;

    public AuthResponse(String token, String username, String email, boolean isFirstLogin) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.isFirstLogin = isFirstLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }
}