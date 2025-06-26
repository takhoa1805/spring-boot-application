package com.lreas.quiz.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class DoQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "quiz_version_id", nullable = false)
    @Setter
    private String quizVersionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User user;

    @Column(name = "start_time")
    @Setter
    private Date startTime;

    @Column(name = "time_limit")
    @Setter
    private Double timeLimit; // in seconds

    @Column(name = "submit_time")
    @Setter
    private Date submitTime;

    @Column(name = "total_score")
    @Setter
    private Double totalScore;

    @Column(name = "max_score")
    @Setter
    private Double maxScore;
}
