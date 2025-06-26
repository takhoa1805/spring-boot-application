package com.lreas.file_management.dtos;

public class ResourceMoveRequest {
    private String parentId;

    // No-argument constructor
    public ResourceMoveRequest() {
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ResourceMoveRequest(String parentId) {
        this.parentId = parentId;
    }
}