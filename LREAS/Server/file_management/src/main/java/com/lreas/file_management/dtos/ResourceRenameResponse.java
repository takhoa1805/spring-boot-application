package com.lreas.file_management.dtos;

import com.lreas.file_management.models.Resource;

import java.util.Date;

public class ResourceRenameResponse {
    private Boolean success;
    private String message;
    private String name;
    private String resourceId;
    private Date dateUpdated;
    private Date dateCreated;
    private Boolean isFolder;
    private String parentResourceId;
    private String userId;
    private String username;
    private String institutionName;

    public ResourceRenameResponse(Boolean success, String message, String name, String resourceId, Date dateUpdated, Date dateCreated, Boolean isFolder, String parentResourceId, String userId, String username, String institutionName) {
        this.success = success;
        this.message = message;
        this.name = name;
        this.resourceId = resourceId;
        this.dateUpdated = dateUpdated;
        this.dateCreated = dateCreated;
        this.isFolder = isFolder;
        this.parentResourceId = parentResourceId;
        this.userId = userId;
        this.username = username;
        this.institutionName = institutionName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public String getParentResourceId() {
        return parentResourceId;
    }

    public void setParentResourceId(String parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
