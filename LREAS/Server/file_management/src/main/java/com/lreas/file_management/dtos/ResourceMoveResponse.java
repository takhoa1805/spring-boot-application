package com.lreas.file_management.dtos;

public class ResourceMoveResponse {
    private Boolean success;
    private String message;
    private String parentId;
    private String resourceId;

    public ResourceMoveResponse(Boolean success, String message, String parentId, String resourceId) {
        this.success = success;
        this.message = message;
        this.parentId = parentId;
        this.resourceId = resourceId;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

}
