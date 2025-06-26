package com.lreas.file_management.dtos;

import com.lreas.file_management.models.ResourceAccessedBy;

public class AddAccessResponse {
    private boolean success;
    private String message;
    private String resourceId;
    private String userId;
    private ResourceAccessedBy.ROLE role;

    public AddAccessResponse(boolean success, String message, String resourceId, String userId, ResourceAccessedBy.ROLE role) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
        this.userId = userId;
        this.role = role;
    }



    public ResourceAccessedBy.ROLE getRole() {
        return role;
    }

    public void setRole(ResourceAccessedBy.ROLE role) {
        this.role = role;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
