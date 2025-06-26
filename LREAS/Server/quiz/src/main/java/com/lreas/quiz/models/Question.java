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
public class Question {
    @Id
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "quiz_version")
    @Setter @Getter
    private QuizVersion quizVersion;

    @Field(name = "time_limit")
    @Setter @Getter
    private Double timeLimit;

    @Field(name = "date_updated")
    @Setter @Getter
    private Date dateUpdated;

    @Field(name = "score")
    @Setter @Getter
    private Double score;

    @Field(name = "date_created")
    @Setter @Getter
    private Date dateCreated;

    @Field(name = "position")
    @Setter @Getter
    private Integer position;

    @Field(name = "title")
    @Setter @Getter
    private String title;

    @Field(name = "image")
    @Setter @Getter
    private String image; // image path

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'question': ?#{#self._id}}", lazy = true)
    @Setter @Getter
    private List<Answer> answers;
}
