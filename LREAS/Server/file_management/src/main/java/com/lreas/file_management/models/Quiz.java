package com.lreas.file_management.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @OneToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    @Setter
    private Resource resource;

    @Column(name = "quiz_version_id", nullable = false)
    @Setter
    private String quizVersionId;
}
