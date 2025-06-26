package com.lreas.file_management.dtos;

import com.lreas.file_management.models.Resource;

public class RestoreResourceResponse {
    private boolean success;
    private String message;
    private String resourceId;

    public RestoreResourceResponse(boolean success, String message, String resourceId) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;

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
}
