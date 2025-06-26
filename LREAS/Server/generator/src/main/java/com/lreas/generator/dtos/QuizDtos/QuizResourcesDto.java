package com.lreas.generator.dtos.QuizDtos;

import java.util.*;

public class QuizResourcesDto {
    public String resourceId;
    public String quizId;
    public Boolean isGame;
    public Date startTime;
    public Date endTime;
    public Integer maxPlayers;
    public String description;
    public String userId;
    public String parentResourceId;
    public String name;
    public Boolean showCorrectAnswer;
    public Integer allowedAttempts;
    public Boolean shuffleAnswers;
    public Double totalTime;
    public Double totalPoints;
    public Date createdTime;
    public Date updatedTime;
    public List<QuestionDto> questions;

    public QuizResourcesDto() {}
}
