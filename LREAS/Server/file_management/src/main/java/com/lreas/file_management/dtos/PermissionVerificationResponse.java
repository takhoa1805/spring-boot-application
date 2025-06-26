package com.lreas.file_management.dtos;

public class PermissionVerificationResponse {
    Boolean success;
    String message;
    String resourceId;
    String ownerId;
    String mongoId;
    Boolean readPermission;
    Boolean writePermission;

    public PermissionVerificationResponse(Boolean success, String message, String resourceId, String ownerId, String mongoId, Boolean readPermission, Boolean writePermission) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
        this.ownerId = ownerId;
        this.mongoId = mongoId;
        this.readPermission = readPermission;
        this.writePermission = writePermission;
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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public Boolean getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(Boolean readPermission) {
        this.readPermission = readPermission;
    }

    public Boolean getWritePermission() {
        return writePermission;
    }

    public void setWritePermission(Boolean writePermission) {
        this.writePermission = writePermission;
    }
}
