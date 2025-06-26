package com.lreas.quiz.utils;

import com.lreas.quiz.dtos.AbstractChoiceDto;
import com.lreas.quiz.dtos.QuestionDto;
import com.lreas.quiz.models.*;

import com.lreas.quiz.repositories.jpa.DoQuizRepository;
import com.lreas.quiz.repositories.jpa.QuizRepository;
import com.lreas.quiz.repositories.jpa.ResourceAccessedByRepository;
import com.lreas.quiz.repositories.jpa.ResourceRepository;
import com.lreas.quiz.repositories.mongo.AnswerRepository;
import com.lreas.quiz.repositories.mongo.QuestionRepository;
import com.lreas.quiz.repositories.mongo.QuizVersionRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class QuizUtils {
    private final ResourceAccessedByRepository resourceAccessedByRepository;
    private final ResourceRepository resourceRepository;
    private final QuizVersionRepository quizVersionRepository;
    private final QuizRepository quizRepository;
    private final DoQuizRepository doQuizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Getter
    private final MinioClientUtils minioClientUtils;

    private final Queue<String> doingQuizzes;
    private final HashSet<String> doQuizIds;

    private ScheduledExecutorService executor;

    @Autowired
    public QuizUtils(
            ResourceAccessedByRepository resourceAccessedByRepository,
            ResourceRepository resourceRepository,
            QuizVersionRepository quizVersionRepository,
            QuizRepository quizRepository,
            DoQuizRepository doQuizRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            MinioClientUtils minioClientUtils
    ) {
        this.resourceAccessedByRepository = resourceAccessedByRepository;
        this.resourceRepository = resourceRepository;
        this.quizVersionRepository = quizVersionRepository;
        this.quizRepository = quizRepository;
        this.doQuizRepository = doQuizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.minioClientUtils = minioClientUtils;

        this.doingQuizzes = new LinkedList<>();
        this.doQuizIds = new HashSet<>();
        this.executor = null;
    }

    public Object[] checkQuiz(
            String resourceId, String userId,
            ResourceAccessedBy.ROLE role
    ) {
        // check permission
        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                userId, resourceId
        );
        if (accessedBy == null || accessedBy.getRole().compareTo(role) > 0) {
            throw new RuntimeException("Do Not Have Permission");
        }

        // get resource
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new RuntimeException("Resource Not Found")
        );
        if (
                resource.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) != 0 &&
                        resource.getWorkflowState().compareTo(Resource.STATE.GENERATING) != 0
        ) {
            throw new RuntimeException("Resource Not Found");
        }
        if (resource.getIsFolder() || !resource.getIsQuiz()) {
            throw new RuntimeException("Resource Is Not Quiz");
        }

        // get related quiz
        Quiz quiz = quizRepository.findByResource(resource);
        if (quiz == null) {
            throw new RuntimeException("Quiz Not Found");
        }

        // get latest quiz version
        QuizVersion quizVersion = quizVersionRepository.findById(quiz.getQuizVersionId()).orElse(null);
        if (quizVersion == null) {
            throw new RuntimeException("Quiz Version Not Found");
        }

        return new Object[] {resource, quiz, quizVersion, accessedBy.getResource().getUser()};
    }

    public int getRemainingAttempts(
            QuizVersion quizVersion, User user
    ) {
        if (quizVersion.getAllowedAttempts() != null) {
            List<DoQuiz> doQuizListAll = doQuizRepository.findByQuizVersionIdAndUser(
                    quizVersion.getId(), user
            );
            return quizVersion.getAllowedAttempts() - doQuizListAll.size();
        }

        return 0;
    }

    public void startDoQuiz(String doQuizId) {
        if (this.doQuizIds.contains(doQuizId)) {
            return;
        }

        this.doingQuizzes.offer(doQuizId);
        this.doQuizIds.add(doQuizId);
        this.startCheckingSubmitThread();
    }

    @Transactional(rollbackOn = Exception.class)
    protected void autoSubmitQuiz() {
        if (doingQuizzes.isEmpty()) {
            this.executor.shutdown();
            return;
        }

        Date now = new Date();
        String doQuizId = this.doingQuizzes.poll();

        if (doQuizId == null) {
            return;
        }

        DoQuiz doQuiz = this.doQuizRepository.findById(doQuizId).orElse(null);
        if (doQuiz == null) {
            return;
        }

        long timeLimit = Double.valueOf(doQuiz.getTimeLimit() * 1000d).longValue();
        Date expectSubmitTime = new Date(doQuiz.getStartTime().getTime() + timeLimit);

        if (now.after(expectSubmitTime)) {
            // get quiz version related to this attempt
            QuizVersion quizVersion = quizVersionRepository.findById(doQuiz.getQuizVersionId()).orElse(null);

            double maxScore = 0d;
            List<Question> questions = questionRepository.findByQuizVersion(quizVersion);
            for (Question question : questions) {
                maxScore += question.getScore();
            }

            doQuiz.setTotalScore(0d);
            doQuiz.setMaxScore(maxScore);
            doQuiz.setSubmitTime(expectSubmitTime);
            this.doQuizRepository.save(doQuiz);
        }
        else {
            this.doingQuizzes.offer(doQuizId);
        }
    }

    private void startCheckingSubmitThread() {
        if (this.executor == null || this.executor.isShutdown()) {
            this.executor = Executors.newSingleThreadScheduledExecutor();

            this.executor.scheduleAtFixedRate(
                    this::autoSubmitQuiz,
                    1,
                    1,
                    TimeUnit.SECONDS
            );
        }
    }

    public List<Question> getQuestions(QuizVersion quizVersion) {
        List<Question> questions = questionRepository.findByQuizVersion(quizVersion);
        if (questions == null) {
            return Collections.emptyList();
        }
        return questions;
    }

    public List<Answer> getAnswers(Question question) {
        List<Answer> answers = answerRepository.findByQuestion(question);
        if (answers == null) {
            return Collections.emptyList();
        }
        return answers;
    }
}
