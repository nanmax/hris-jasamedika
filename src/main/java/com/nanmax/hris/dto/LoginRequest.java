package com.nanmax.hris.dto;
public class LoginRequest {
    private String email;
    private String password;
    private String profile;
    public LoginRequest() {}
    public LoginRequest(String email, String password, String profile) {
        this.email = email;
        this.password = password;
        this.profile = profile;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
}