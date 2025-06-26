package com.lreas.profile.dtos;

import com.lreas.profile.models.User;

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
