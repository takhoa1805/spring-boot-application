package com.lreas.admin.dtos;

public class User {
    private String email;
    private com.lreas.admin.models.User.ROLE role;
    private String name;
    private String userId;
    private com.lreas.admin.models.User.STATE workflowState;

    public User(String email, com.lreas.admin.models.User.ROLE role, String name, String userId, com.lreas.admin.models.User.STATE workflowState) {
        this.email = email;
        this.role = role;
        this.name = name;
        this.userId = userId;
        this.workflowState = workflowState;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public com.lreas.admin.models.User.ROLE getRole() {
        return role;
    }

    public void setRole(com.lreas.admin.models.User.ROLE role) {
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

    public com.lreas.admin.models.User.STATE getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(com.lreas.admin.models.User.STATE workflowState) {
        this.workflowState = workflowState;
    }
}
