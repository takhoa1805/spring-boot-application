package com.lreas.generator.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    @Setter
    private String username;

    @Column(name = "password", nullable = false)
    @Setter
    private String password;

    @Column(name = "workflow_state", nullable = false)
    @Setter
    private STATE workflowState;

    @Column(name = "email", nullable = false, unique = true)
    @Setter
    private String email;

    @Column(name = "avt_path")
    @Setter
    private String avtPath;

    @Column(name = "birthday")
    @Setter
    private Date birthday;

    @Column(name = "gender")
    @Setter
    private GENDER gender;

    @Column(name = "other_gender")
    @Setter
    private String otherGender;

    @Column(name = "description")
    @Setter
    private String description;

    @Column(name = "phone_number")
    @Setter
    private String phoneNumber;

    @Column(name = "address")
    @Setter
    private String address;

    @Column(name = "role", nullable = false)
    @Setter
    private ROLE role; // ADMIN, STUDENT, TEACHER

    @Column(name="invitation_code", nullable = true)
    @Setter
    private String invitationCode;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    @Setter
    private Institution institution;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Resource> resources;

    @OneToMany(mappedBy = "resource.user", cascade = CascadeType.ALL)
    private List<ResourceAccessedBy> resourceAccessedBy;

    public enum STATE {
        ACTIVE, INACTIVE, PENDING
    }

    public enum ROLE {
        ADMIN, TEACHER, STUDENT
    }

    public enum GENDER {
        MALE, FEMALE, OTHER
    }
}
