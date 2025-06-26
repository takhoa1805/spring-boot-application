package com.lreas.authentication.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ResourceAccessedBy {
    @EmbeddedId
    private ResourceAccessedById resource;

    @Column(name = "role")
    private ROLE role;

    public enum ROLE {
        OWNER, CONTRIBUTOR, VIEWER
    }
}
