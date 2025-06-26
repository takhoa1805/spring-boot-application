package com.lreas.profile.dtos;

import com.lreas.profile.models.User;

import java.util.Date;

public class UserInfoUpdate {
    public transient String userId;
    public String username;
    public Date birthday;
    public User.GENDER gender;
    public String otherGender;
    public String description;
    public String email;
    public String phone;
    public String address;
}
