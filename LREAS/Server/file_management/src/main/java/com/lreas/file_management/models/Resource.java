package com.lreas.file_management.models;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Entity
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "date_updated")
    @Setter @Getter
    private Date dateUpdated;

    @Column(name = "workflow_state")
    @Setter @Getter
    private STATE workflowState;

    @Column(name = "date_created")
    @Setter @Getter
    private Date dateCreated;

    @Column(name = "name")
    @Setter @Getter
    private String name;

    @Column(name = "is_folder")
    @Setter @Getter
    private Boolean isFolder;

    @Column(name = "is_quiz")
    @Setter
    private Boolean isQuiz;

    @Column(name = "mongo_id", unique = true)
    @Setter @Getter
    private String mongoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter @Getter
    private Resource parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter @Getter
    private User user;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    @Setter @Getter
    private Institution institution;

    @OneToMany(mappedBy = "resource.resource", cascade = CascadeType.ALL)
    private List<ResourceAccessedBy> resourceAccessedBy;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Resource> children;

    @OneToOne(mappedBy = "resource")
    private File file;

    @OneToOne(mappedBy = "resource")
    private Quiz quiz;

    public enum STATE {
        AVAILABLE, GENERATING, FAILED, TRASHED, DELETED
    }
}
