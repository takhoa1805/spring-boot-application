package com.lreas.file_management.dtos;

import com.lreas.file_management.models.User;

public class UserInfoResponse {
    public String id;
    public String name;
    public String email;

    public UserInfoResponse() {}

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.name = user.getUsername();
        this.email = user.getEmail();
    }
}
