package com.lreas.generator.dtos;

import java.util.Date;

public class GenerateResourceDto {
    public String contentName;
    public String[] collaboratorsId;
    public String outputFolderId;
    public String contentType;
    public String isPublic;
    public String fileType;
    public Integer numberOfQuestions;
    public Double timeLimit; // in seconds
    public Boolean isGame;
    public Date startTime;
    public Date endTime;
    public Integer maxPlayers;
    public Boolean showCorrectAnswer;
    public Integer allowedAttempts;
    public String description;
    public Boolean shuffleAnswers;
    public transient String userId;
}
