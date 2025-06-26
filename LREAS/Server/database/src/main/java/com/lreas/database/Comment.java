package com.lreas.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    @Setter
    private Thread thread;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @Column(name = "date_modified")
    @Setter
    private Date dateModified;

    @Column(name = "date_created")
    @Setter
    private Date dateCreated;

    @Column(name = "content", columnDefinition = "TEXT")
    @Setter
    private String content;

    @Column(name = "workflow_state")
    @Setter
    private STATE workflowState;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    @Setter
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> childComments;

    public enum STATE {
        AVAILABLE, DELETED
    }
}
