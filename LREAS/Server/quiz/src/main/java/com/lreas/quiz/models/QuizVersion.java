package com.lreas.quiz.models;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document
@Getter
public class QuizVersion {
    @Id
    private String id;

    @Field(name = "quiz_id")
    @Setter @Getter
    private String quizId;

    @Field(name = "date_updated")
    @Setter @Getter
    private Date dateUpdated;

    @Field(name = "is_game")
    @Setter @Getter
    private Boolean isGame;

    @Field(name = "date_started")
    @Setter @Getter
    private Date dateStarted;

    @Field(name = "date_ended")
    @Setter @Getter
    private Date dateEnded;

    @Field(name = "max_players")
    @Setter @Getter
    private Integer maxPlayers;

    @Field(name = "time_limit")
    @Setter @Getter
    private Double timeLimit;

    @Field(name = "title")
    @Setter @Getter
    private String title;

    @Field(name = "show_correct_answer")
    @Setter @Getter
    private Boolean showCorrectAnswer;

    @Field(name = "allowed_attempts")
    @Setter @Getter
    private Integer allowedAttempts;

    @Field(name = "description")
    @Setter @Getter
    private String description;

    @Field(name = "shuffle_answers")
    @Setter @Getter
    private Boolean shuffleAnswers;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'quiz_version': ?#{#self._id}}", lazy = true)
    @Setter @Getter
    private List<Question> questions;
}
