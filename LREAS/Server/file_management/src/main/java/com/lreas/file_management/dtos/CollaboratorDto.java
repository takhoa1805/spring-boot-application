package com.lreas.file_management.dtos;

import com.lreas.file_management.models.ResourceAccessedBy;
import com.lreas.file_management.models.User;

public class CollaboratorDto {
    public String id;
    public String name;
    public String email;
    public ResourceAccessedBy.ROLE role;
    public transient User.ROLE userRole;

    public CollaboratorDto() {}

    public CollaboratorDto(User user, ResourceAccessedBy.ROLE role) {
        this.id = user.getId();
        this.name = user.getUsername();
        this.email = user.getEmail();
        this.role = role;
        this.userRole = user.getRole();
    }
}
