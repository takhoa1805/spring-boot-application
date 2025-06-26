package com.lreas.admin.dtos;

import com.lreas.admin.models.User;

public class UserUpdateResponse {
    private Boolean success;
    private String message;
    private String email;
    private com.lreas.admin.models.User.ROLE role;
    private String name;
    private String userId;
    private com.lreas.admin.models.User.STATE workflowState;

    public UserUpdateResponse(Boolean success, String message, String email, User.ROLE role, String name, String userId, User.STATE workflowState) {
        this.success = success;
        this.message = message;
        this.email = email;
        this.role = role;
        this.name = name;
        this.userId = userId;
        this.workflowState = workflowState;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User.STATE getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(User.STATE workflowState) {
        this.workflowState = workflowState;
    }
}
