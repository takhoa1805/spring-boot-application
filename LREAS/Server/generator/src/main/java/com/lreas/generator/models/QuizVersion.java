package com.lreas.generator.models;

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
    @Setter
    private String quizId;

    @Field(name = "date_updated")
    @Setter
    private Date dateUpdated;

    @Field(name = "is_game")
    @Setter
    private Boolean isGame;

    @Field(name = "date_started")
    @Setter
    private Date dateStarted;

    @Field(name = "date_ended")
    @Setter
    private Date dateEnded;

    @Field(name = "max_players")
    @Setter
    private Integer maxPlayers;

    @Field(name = "time_limit")
    @Setter
    private Double timeLimit;

    @Field(name = "title")
    @Setter
    private String title;

    @Field(name = "show_correct_answer")
    @Setter
    private Boolean showCorrectAnswer;

    @Field(name = "allowed_attempts")
    @Setter
    private Integer allowedAttempts;

    @Field(name = "description")
    @Setter
    private String description;

    @Field(name = "shuffle_answers")
    @Setter
    private Boolean shuffleAnswers;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'quiz_version': ?#{#self._id}}", lazy = true)
    @Setter
    private List<Question> questions;
}
