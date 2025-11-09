package com.nanmax.hris.security;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
@Component
@RequestScope
public class AuthenticationHolder {
    private AuthenticationContext currentUser;
    public void setCurrentUser(AuthenticationContext user) {
        this.currentUser = user;
    }
    public AuthenticationContext getCurrentUser() {
        return currentUser;
    }
    public void clear() {
        this.currentUser = null;
    }
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    public boolean isAdmin() {
        return isAuthenticated() && currentUser.isAdmin();
    }
    public boolean isHRD() {
        return isAuthenticated() && currentUser.isHRD();
    }
}