package com.lreas.quiz.dtos;

import java.util.List;

public class QuizAllAttemptsResponse {
    public String resourceId;
    public String quizId;
    public List<QuizSummaryResponse> quizResults;
}
