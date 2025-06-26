package com.lreas.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    @Setter
    private String title;

    @Column(name = "date_created")
    @Setter
    private Date dateCreated;

    @Column(name = "date_updated")
    @Setter
    private Date dateUpdated;

    @Column(name = "workflow_state")
    @Setter
    private STATE workflowState;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    @Setter
    private Institution institution;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Thread> threads;

    public enum STATE {
        AVAILABLE, DELETED
    }
}
