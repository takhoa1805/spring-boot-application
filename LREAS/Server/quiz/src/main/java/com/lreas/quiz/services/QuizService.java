package com.lreas.quiz.services;

import com.lreas.quiz.dtos.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuizService {
    QuizResourcesDto createQuiz(CreateQuizDto createQuizDto);
    QuizResourcesDto updateQuiz(QuizResourcesDto quizResourcesDtoUpdate);
    QuizResourcesDto getQuiz(String resourceId, String userId, Boolean infoOnly, Boolean isForEdit);
    QuizResourcesDto uploadMediaForQuiz(ModifyMediaDto modifyMediaDto, QuizResourcesDto quizResourcesDto) throws Exception;
    AttemptQuizResponse startTraditionalQuiz(String resourceId, String userId);
    AttemptQuizResponse getQuizByAttempt(String attemptId);
    QuizSummaryResponse submitTraditionalQuiz(SubmitQuizDto submitQuizDto);
    QuizAllAttemptsResponse getAllQuizAttempts(String userId, String resourceId);
    QuizResourcesDto getQuizReview(String attemptId);
    List<QuizVersionsResponse> getAllQuizVersions(String resourceId, String userId);
    QuizResourcesDto getQuizByVersion(String userId, String versionId);
    QuizResourcesDto restoreQuiz(RestoreQuizVersionDto restoreQuizVersionDto);
}
