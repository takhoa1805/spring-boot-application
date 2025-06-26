package com.lreas.file_management.models;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

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
}
