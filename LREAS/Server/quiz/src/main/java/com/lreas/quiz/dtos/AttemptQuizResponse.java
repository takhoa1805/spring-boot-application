package com.lreas.quiz.dtos;

import java.util.Date;

public class AttemptQuizResponse {
    public boolean success;
    public String attemptId;
    public Date startTime;
    public Double timeLimit;
    public Boolean[] isMultipleChoices;
    public QuizResourcesDto quiz;
}
