package com.lreas.file_management.dtos;

import com.lreas.file_management.models.Resource;

public class DeleteResourceResponse {
    private String message;
    private boolean success;
    private String resourceId;
    private Resource.STATE state;

    public DeleteResourceResponse(boolean success, String message, String resourceId, Resource.STATE state) {
        this.message = message;
        this.success = success;
        this.resourceId = resourceId;
        this.state = state;
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

    public Resource.STATE getState() {
        return state;
    }

    public void setState(Resource.STATE state) {
        this.state = state;
    }
}
