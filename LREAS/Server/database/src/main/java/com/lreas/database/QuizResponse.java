package com.lreas.database;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Getter
public class QuizResponse {
    @Id
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "question")
    @Setter
    private Question question;

    @DocumentReference(lazy = true)
    @Field(name = "answer")
    @Setter
    private Answer answer;

    @Field(name = "do_quiz_id")
    @Setter
    private String doQuizId;

    @Field(name = "score")
    @Setter
    private Double score;
}
