package com.lreas.forum.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "content", columnDefinition = "TEXT")
    @Setter
    private String content;

    @Column(name = "workflow_state")
    @Setter
    private STATE workflowState;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    @Setter
    private Topic topic;

    @Column(name = "date_modified")
    @Setter
    private Date dateModified;

    @Column(name = "subject")
    @Setter
    private String subject;

    @Column(name = "date_created")
    @Setter
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public enum STATE {
        AVAILABLE, DELETED
    }
}
