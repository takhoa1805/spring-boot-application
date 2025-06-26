package com.lreas.quiz.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

@Getter
@Entity(name = "institutions")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    @Setter
    private String name;

    @Column(name = "email")
    @Setter
    private String email;

    @Column(name = "subdomain")
    @Setter
    private String subdomain;

    @Column(name = "workflow_state")
    @Setter
    private STATE workflowState;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    @Setter
    private List<User> users;

    public enum STATE {
        ACTIVE, INACTIVE
    }
}
