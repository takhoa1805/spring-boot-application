package com.lreas.file_management.dtos;

public class NewDocumentsResponse {
    private Boolean success;
    private String message;
    private String resourceId;
    private String parentId;
    private String name;
    private String type;
    private String ownerId;
    private String mongoId;

    public NewDocumentsResponse(Boolean success, String message, String resourceId, String parentId, String name, String type, String ownerId, String mongoId) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
        this.mongoId=mongoId;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
