package com.lreas.quiz.dtos;

import java.util.Date;

public class QuizSummaryResponse {
    public String attemptId;
    public STATUS status;
    public Double totalScore;
    public Double maxScore;
    public Date startTime;
    public Date submitTime;
    public Long duration; // in seconds
    public String username;

    public enum STATUS {
        FINISHED, IN_PROGRESS
    }
}
