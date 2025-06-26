package com.lreas.quiz.dtos;

import com.lreas.quiz.models.Question;
import com.lreas.quiz.models.Quiz;
import com.lreas.quiz.models.QuizVersion;
import com.lreas.quiz.utils.QuizUtils;

import java.util.*;

public class QuizResourcesDto {
    public String resourceId;
    public String quizId;
    public String quizVersionId;
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

    public QuizResourcesDto(
            QuizVersion quizVersion,
            Quiz quiz,
            QuizUtils quizUtils
    ) {
        this.init(quizVersion, quiz, quizUtils, quizVersion.getShuffleAnswers());
    }

    public QuizResourcesDto(
            QuizVersion quizVersion,
            Quiz quiz,
            QuizUtils quizUtils,
            Boolean isShuffle
    ) {
        this.init(quizVersion, quiz, quizUtils, isShuffle);
    }

    private void init(
            QuizVersion quizVersion,
            Quiz quiz,
            QuizUtils quizUtils,
            Boolean isShuffle
    ) {
        if (quizVersion == null) {
            return;
        }

        if (isShuffle == null) {
            isShuffle = false;
        }

        this.resourceId = quiz.getResource().getId();
        this.quizId = quiz.getId();
        this.quizVersionId = quiz.getQuizVersionId();
        this.isGame = quizVersion.getIsGame();
        this.startTime = quizVersion.getDateStarted();
        this.endTime = quizVersion.getDateEnded();
        this.maxPlayers = quizVersion.getMaxPlayers();
        this.description = quizVersion.getDescription();
        this.userId = quiz.getResource().getUser().getId();
        if (quiz.getResource().getParent() != null) {
            this.parentResourceId = quiz.getResource().getParent().getId();
        }
        else {
            this.parentResourceId = null;
        }
        this.name = quiz.getResource().getName();
        this.showCorrectAnswer = quizVersion.getShowCorrectAnswer();
        this.allowedAttempts = quizVersion.getAllowedAttempts();
        this.shuffleAnswers = quizVersion.getShuffleAnswers();
        this.totalTime = 0.0;
        this.totalPoints = 0.0;
        this.createdTime = quiz.getResource().getDateCreated();
        this.updatedTime = quiz.getResource().getDateUpdated();

        if (quizVersion.getTimeLimit() != null) {
            this.totalTime = quizVersion.getTimeLimit();
        }

        List<Question> questionsLst = quizUtils.getQuestions(quizVersion);
        QuestionDto[] questionDtos = new QuestionDto[questionsLst.size()];
        for (Question q : questionsLst) {
            QuestionDto questionDto = new QuestionDto(q, quizUtils, isShuffle);
            questionDtos[q.getPosition()] = questionDto;

            if (quizVersion.getTimeLimit() == null) {
                this.totalTime += questionDto.time;
            }

            this.totalPoints += questionDto.points;
        }

        this.questions = Arrays.asList(questionDtos);

        // check if quiz need to be shuffled
        if (isShuffle) {
            Collections.shuffle(this.questions);
        }
    }

    private boolean compareDates(
            Date d1, Date d2
    ) {
        if (d1 == null && d2 == null) {
            return true;
        }
        else if (d1 == null || d2 == null) {
            return false;
        }
        return d1.getTime() == d2.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuizResourcesDto other = (QuizResourcesDto) o;

        // check question list
        boolean isQuestionsEqual = false;
        if (this.questions != null) {
            if (other.questions != null) {
                if (this.questions.size() == other.questions.size()) {
                    this.questions.sort(Comparator.comparingInt(q -> q.position));
                    other.questions.sort(Comparator.comparingInt(q -> q.position));
                    isQuestionsEqual = this.questions.equals(other.questions);
                }
            }
        }
        else {
            isQuestionsEqual = (other.questions == null);
        }

        return (
            Objects.equals(this.resourceId, other.resourceId) &&
            Objects.equals(this.quizId, other.quizId) &&
            Objects.equals(this.isGame, other.isGame) &&
            this.compareDates(this.startTime, other.startTime) &&
            this.compareDates(this.endTime, other.endTime) &&
            Objects.equals(this.maxPlayers, other.maxPlayers) &&
            Objects.equals(this.description, other.description) &&
            Objects.equals(this.userId, other.userId) &&
            Objects.equals(this.parentResourceId, other.parentResourceId) &&
            Objects.equals(this.name, other.name) &&
            Objects.equals(this.showCorrectAnswer, other.showCorrectAnswer) &&
            Objects.equals(this.allowedAttempts, other.allowedAttempts) &&
            Objects.equals(this.shuffleAnswers, other.shuffleAnswers) &&
            Objects.equals(this.totalTime, other.totalTime) &&
            Objects.equals(this.totalPoints, other.totalPoints) &&
            this.compareDates(this.createdTime, other.createdTime) &&
            this.compareDates(this.updatedTime, other.updatedTime) &&
            isQuestionsEqual
        );
    }
}
