package com.lreas.generator.dtos.QuizDtos;

import java.util.*;

public class QuestionDto {
    public String questionId;
    public String question;
    public Double time;
    public Double points;
    public Integer position;
    public String imageObjectName;
    public String imageUrl;
    public Date createdTime;
    public Date updatedTime;
    public Map<String, String> imageMetadata;
    public List<ChoiceDto> choices;

    public QuestionDto() {}
}
