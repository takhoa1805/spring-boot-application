package com.lreas.authentication.dtos;

import com.lreas.authentication.models.User;

public class InvitationRequest {

    private String email;
    private User.ROLE role;
    private String username;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.ROLE getRole() {
        return role;
    }
    public void setRole(User.ROLE role) {
        this.role = role;
    }
}
