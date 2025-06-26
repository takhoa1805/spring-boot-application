package com.lreas.file_management.dtos;

import java.util.Date;

public class ChangeResourceOwnerResponse {
    private Boolean success;
    private String message;
    private String resourceId;
    private Date dateUpdated;
    private Boolean isFolder;
    private String ownerId;
    private String ownerUsername;
    private String ownerInstitutionName;

    public ChangeResourceOwnerResponse(Boolean success, String message, String resourceId, Date dateUpdated, Boolean isFolder, String ownerId, String ownerUsername, String ownerInstitutionName) {
        this.success = success;
        this.message = message;
        this.resourceId = resourceId;
        this.dateUpdated = dateUpdated;
        this.isFolder = isFolder;
        this.ownerId = ownerId;
        this.ownerUsername = ownerUsername;
        this.ownerInstitutionName = ownerInstitutionName;
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

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerInstitutionName() {
        return ownerInstitutionName;
    }

    public void setOwnerInstitutionName(String ownerInstitutionName) {
        this.ownerInstitutionName = ownerInstitutionName;
    }
}
