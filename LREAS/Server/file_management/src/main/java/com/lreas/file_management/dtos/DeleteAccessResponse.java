package com.lreas.file_management.dtos;

public class DeleteAccessResponse {
    private String message;
    private boolean success;
    private String resourceId;
    private String userId;

    public DeleteAccessResponse( boolean success, String message, String resourceId, String userId) {
        this.message = message;
        this.success = success;
        this.resourceId = resourceId;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
