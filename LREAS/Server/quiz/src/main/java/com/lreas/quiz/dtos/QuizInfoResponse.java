package com.lreas.quiz.dtos;

import com.lreas.quiz.models.ResourceAccessedBy;

import java.util.Date;

public class QuizInfoResponse extends QuizResourcesDto {
    public String quizName;
    public String quizDescription;
    public Double timeLimit;
    public Integer attemptsAllowed;
    public String ownerName;
    public Date lastModifiedTime;
    public Boolean isInProgress;
    public Integer remainingAttempts;
    public String quizVersionId;
    public String avtPath;
    public Integer numberOfQuestions;
    public Date startDoQuizTime;
    public ResourceAccessedBy.ROLE role;
}
