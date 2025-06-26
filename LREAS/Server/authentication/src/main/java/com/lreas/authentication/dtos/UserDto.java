package com.lreas.authentication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    public String username;
    public String password;
    public String email;
    public String avtPath;
    public String role;
}
