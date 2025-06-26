package com.lreas.file_management.models;

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
    @Setter
    private QuizVersion quizVersion;

    @Field(name = "time_limit")
    @Setter
    private Double timeLimit;

    @Field(name = "date_updated")
    @Setter
    private Date dateUpdated;

    @Field(name = "score")
    @Setter
    private Double score;

    @Field(name = "date_created")
    @Setter
    private Date dateCreated;

    @Field(name = "position")
    @Setter
    private Integer position;

    @Field(name = "title")
    @Setter
    private String title;

    @Field(name = "image")
    @Setter
    private String image; // image path

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'question': ?#{#self._id}}", lazy = true)
    @Setter
    private List<Answer> answers;
}
