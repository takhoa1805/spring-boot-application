package com.lreas.generator.models;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
@Getter
public class Answer {
    @Id
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "question")
    @Setter
    private Question question;

    @Field(name = "text")
    @Setter
    private String text;

    @Field(name = "is_correct")
    @Setter
    private Boolean isCorrect;

    @Field(name = "date_created")
    @Setter
    private Date dateCreated;

    @Field(name = "date_updated")
    @Setter
    private Date dateUpdated;
}
