package com.lreas.admin.dtos;

import java.util.List;

public class UserListResponse {
    private Boolean success;
    private String message;
    private List<com.lreas.admin.dtos.User> users;
    private String institutionName;

    public UserListResponse(Boolean success, String message, List<User> users, String institutionName) {
        this.success = success;
        this.message = message;
        this.users = users;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
