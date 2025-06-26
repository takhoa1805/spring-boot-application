package com.lreas.quiz.services;

import com.lreas.quiz.dtos.QuizResourcesDto;
import com.lreas.quiz.models.Quiz;
import com.lreas.quiz.repositories.jpa.*;
import com.lreas.quiz.repositories.mongo.AnswerRepository;
import com.lreas.quiz.repositories.mongo.QuestionRepository;
import com.lreas.quiz.repositories.mongo.QuizResponseRepository;
import com.lreas.quiz.repositories.mongo.QuizVersionRepository;
import com.lreas.quiz.utils.MinioClientUtils;
import com.lreas.quiz.utils.QuizUtils;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import com.lreas.quiz.models.*;
import com.lreas.quiz.dtos.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@Qualifier(value = "quizServiceImpl")
public class QuizServiceImpl implements QuizService {
    private final Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final ResourceAccessedByRepository resourceAccessedByRepository;
    private final MinioClientUtils minioClientUtils;
    private final QuizVersionRepository quizVersionRepository;
    private final DoQuizRepository doQuizRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final QuizUtils quizUtils;

    @Autowired
    public QuizServiceImpl(
            QuizRepository quizRepository,
            UserRepository userRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            ResourceRepository resourceRepository,
            ResourceAccessedByRepository resourceAccessedByRepository,
            MinioClientUtils minioClientUtils,
            QuizVersionRepository quizVersionRepository,
            DoQuizRepository doQuizRepository,
            QuizResponseRepository quizResponseRepository,
            QuizUtils quizUtils
    ) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.resourceRepository = resourceRepository;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
        this.minioClientUtils = minioClientUtils;
        this.quizVersionRepository = quizVersionRepository;
        this.doQuizRepository = doQuizRepository;
        this.quizResponseRepository = quizResponseRepository;
        this.quizUtils = quizUtils;
    }

    public QuizResourcesDto createQuiz(
            CreateQuizDto createQuizDto
    ) {
        Resource resource = new Resource();
        ResourceAccessedBy resourceAccessedBy = new ResourceAccessedBy();

        Date date = new Date();

        // get tomorrow date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        Date tomorrow = calendar.getTime();

        if (createQuizDto.userId != null) {
            User user = userRepository.findById(createQuizDto.userId).orElseThrow(
                    () -> new RuntimeException("User not found")
            );
            resource.setUser(user);
            resource.setInstitution(user.getInstitution());

            ResourceAccessedById resourceAccessedById = new ResourceAccessedById();
            resourceAccessedById.setResource(resource);
            resourceAccessedById.setUser(user);

            resourceAccessedBy.setResource(resourceAccessedById);
            resourceAccessedBy.setRole(ResourceAccessedBy.ROLE.OWNER);
        } else {
            throw new RuntimeException("User id must not be null");
        }

        if (createQuizDto.parentResourceId != null) {
            Resource parent = resourceRepository.findById(createQuizDto.parentResourceId).orElseThrow(
                    () -> new RuntimeException("Parent Resource not found")
            );

            ResourceAccessedBy temp = this.resourceAccessedByRepository.findByUserAndResource(
                    createQuizDto.userId, createQuizDto.parentResourceId
            );
            if (temp == null || temp.getRole().compareTo(ResourceAccessedBy.ROLE.VIEWER) == 0) {
                throw new RuntimeException("Do Not Have Permission!");
            }

            resource.setParent(parent);
        } else {
            resource.setParent(null);
        }

        resource.setDateCreated(date);
        resource.setDateUpdated(date);
        resource.setWorkflowState(Resource.STATE.AVAILABLE);
        resource.setName(createQuizDto.contentName);
        resource.setIsFolder(false);
        resource.setIsQuiz(true);
        resourceRepository.save(resource);

        resourceAccessedByRepository.save(resourceAccessedBy);

        // add collaborators
        for (CreateQuizDto.Collaborator collab : createQuizDto.collaborators) {
            User collaborator = userRepository.findById(collab.id).orElse(null);
            if (collaborator != null) {
                ResourceAccessedBy accessBy = new ResourceAccessedBy();

                ResourceAccessedById accessById = new ResourceAccessedById();
                accessById.setResource(resource);
                accessById.setUser(collaborator);

                accessBy.setResource(accessById);
                accessBy.setRole(collab.role);

                resourceAccessedByRepository.save(accessBy);
            }
        }

        // create new version of quiz
        QuizVersion quizVersion = new QuizVersion();
        quizVersion.setDateUpdated(date);
        quizVersion.setTitle(String.valueOf(date.getTime()));
        quizVersion.setDateStarted(date);
        quizVersion.setDateEnded(tomorrow);
        quizVersion.setAllowedAttempts(1); // default value
        quizVersion.setTimeLimit(300.0); // default value
        quizVersionRepository.save(quizVersion);

        // create new quiz
        Quiz quiz = new Quiz();
        quiz.setResource(resource);
        quiz.setQuizVersionId(quizVersion.getId());
        quizRepository.save(quiz);

        // update quiz version
        quizVersion.setQuizId(quiz.getId());
        quizVersion.setQuestions(Collections.emptyList());
        quizVersionRepository.save(quizVersion);

        return new QuizResourcesDto(quizVersion, quiz, this.quizUtils);
    }

    public QuizResourcesDto updateQuiz(
            QuizResourcesDto quizResourcesDtoUpdate
    ) {
        Date date = new Date();

        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                quizResourcesDtoUpdate.resourceId,
                quizResourcesDtoUpdate.userId,
                ResourceAccessedBy.ROLE.CONTRIBUTOR
        );
        Resource resource = (Resource) getQuizInfo[0];
        Quiz quiz = (Quiz) getQuizInfo[1];
        QuizVersion quizOldVersion = (QuizVersion) getQuizInfo[2];

        // check with updated quiz
        if ((new QuizResourcesDto(quizOldVersion, quiz, this.quizUtils, false)).equals(quizResourcesDtoUpdate)) {
            return quizResourcesDtoUpdate;
        }

        // update resource information
        resource.setDateUpdated(date);
        resource.setName(quizResourcesDtoUpdate.name);
        resourceRepository.save(resource);

        // get old version of this quiz, and process media data in a separate thread
        Thread thread = new Thread(() -> {
            Map<String, String> map = new HashMap<>(quizResourcesDtoUpdate.questions.size());
            for (QuestionDto question : quizResourcesDtoUpdate.questions) {
                map.put(question.questionId, question.imageObjectName);
            }

            if (quizOldVersion != null) {
                List<Question> oldQuestions = questionRepository.findByQuizVersion(quizOldVersion);
                for (Question oldQuestion : oldQuestions) {
                    if (map.containsKey(oldQuestion.getId())) {
                        if (oldQuestion.getImage() != null && !oldQuestion.getImage().equals(map.get(oldQuestion.getId()))) {
                            try {
                                Map<String, String> tags = minioClientUtils.getTags(
                                        oldQuestion.getImage()
                                );
                                tags.put("status", MinioClientUtils.FILE_STATUS.DELETED.toString());
                                minioClientUtils.setTags(tags, oldQuestion.getImage());
                            } catch (Exception e) {
                                logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                            }
                        }
                    }
                }
            }
        });
        thread.setName("Quiz Update");
        thread.start();

        // create new quiz version
        QuizVersion quizNewVersion = new QuizVersion();

        quizNewVersion.setQuizId(quiz.getId());
        quizNewVersion.setDateUpdated(date);
        quizNewVersion.setIsGame(quizResourcesDtoUpdate.isGame);
        quizNewVersion.setDateStarted(quizResourcesDtoUpdate.startTime);
        quizNewVersion.setDateEnded(quizResourcesDtoUpdate.endTime);
        quizNewVersion.setMaxPlayers(quizResourcesDtoUpdate.maxPlayers);
        quizNewVersion.setTimeLimit(quizResourcesDtoUpdate.totalTime);
        quizNewVersion.setTitle(String.valueOf(date.getTime()));
        quizNewVersion.setShowCorrectAnswer(quizResourcesDtoUpdate.showCorrectAnswer);
        quizNewVersion.setAllowedAttempts(quizResourcesDtoUpdate.allowedAttempts);
        quizNewVersion.setDescription(quizResourcesDtoUpdate.description);
        quizNewVersion.setShuffleAnswers(quizResourcesDtoUpdate.shuffleAnswers);
        quizVersionRepository.save(quizNewVersion);

        // update quiz
        quiz.setQuizVersionId(quizNewVersion.getId());
        quizRepository.save(quiz);

        // update questions
        List<Question> questions = new LinkedList<>();
        for (QuestionDto questionUpdated : quizResourcesDtoUpdate.questions) {
            // create new question
            Question question = new Question();

            question.setQuizVersion(quizNewVersion);
            question.setTimeLimit(questionUpdated.time);
            question.setDateUpdated(date);
            question.setScore(questionUpdated.points);
            question.setDateCreated(date);
            question.setPosition(questionUpdated.position);
            question.setTitle(questionUpdated.question);
            question.setImage(questionUpdated.imageObjectName);
            questionRepository.save(question);

            // update answers
            List<Answer> answers = new LinkedList<>();
            for (AbstractChoiceDto choiceUpdated : questionUpdated.choices) {
                // create new answer
                Answer answer = new Answer();

                answer.setQuestion(question);
                answer.setText(choiceUpdated.answer);
                answer.setIsCorrect(choiceUpdated.correct);
                answer.setDateCreated(date);
                answer.setDateUpdated(date);
                answerRepository.save(answer);

                answers.add(answer);
            }

            question.setAnswers(answers);
            questionRepository.save(question);

            questions.add(question);
        }

        // update questions for quiz version
        quizNewVersion.setQuestions(questions);
        quizVersionRepository.save(quizNewVersion);

        return new QuizResourcesDto(quizNewVersion, quiz, this.quizUtils);
    }

    public QuizResourcesDto getQuiz(
            String resourceId, String userId,
            Boolean infoOnly,
            Boolean isForEdit
    ) {
        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                resourceId,
                userId,
                ResourceAccessedBy.ROLE.VIEWER
        );
        Resource resource = (Resource) getQuizInfo[0];
        Quiz quiz = (Quiz) getQuizInfo[1];
        QuizVersion quizVersion = (QuizVersion) getQuizInfo[2];
        User user = (User) getQuizInfo[3];

        if (infoOnly) {
            User owner = resource.getUser();
            QuizInfoResponse quizInfoResponse = new QuizInfoResponse();

            // get remaining attempts
            quizInfoResponse.remainingAttempts = this.quizUtils.getRemainingAttempts(
                    quizVersion, user
            );

            // get role
            ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                    userId, resource.getId()
            );

            // check if quiz is in progress
            List<DoQuiz> doQuizNotSubmitted = doQuizRepository.findByQuizVersionIdAndUserAndSubmitTime(
                    quizVersion.getId(), user, null
            );

            if (doQuizNotSubmitted != null && !doQuizNotSubmitted.isEmpty()) {
                // sort by start time
                doQuizNotSubmitted.sort((a, b) -> b.getStartTime().compareTo(a.getStartTime()));

                quizInfoResponse.isInProgress = true;

                Date latestStartTime = doQuizNotSubmitted.get(0).getStartTime();
                for (DoQuiz doQuiz : doQuizNotSubmitted) {
                    if (doQuiz.getStartTime().after(latestStartTime)) {
                        latestStartTime = doQuiz.getStartTime();
                    }
                }
                quizInfoResponse.startDoQuizTime = latestStartTime;
            }
            else {
                quizInfoResponse.isInProgress = false;
                quizInfoResponse.startDoQuizTime = null;
            }

            quizInfoResponse.quizName = resource.getName();
            quizInfoResponse.startTime = quizVersion.getDateStarted();
            quizInfoResponse.endTime = quizVersion.getDateEnded();
            quizInfoResponse.quizDescription = quizVersion.getDescription();
            quizInfoResponse.timeLimit = quizVersion.getTimeLimit();
            quizInfoResponse.attemptsAllowed = quizVersion.getAllowedAttempts();
            quizInfoResponse.ownerName = owner.getUsername();
            quizInfoResponse.lastModifiedTime = resource.getDateUpdated();
            quizInfoResponse.quizVersionId = quizVersion.getId();
            quizInfoResponse.isGame = quizVersion.getIsGame();
            quizInfoResponse.role = accessedBy.getRole();

            // get number of questions
            quizInfoResponse.numberOfQuestions = questionRepository.countByQuizVersion(quizVersion);

            // get avatar
            if (owner.getAvtPath() != null && !owner.getAvtPath().isEmpty()) {
                try {
                    quizInfoResponse.avtPath = this.minioClientUtils.getUrl(owner.getAvtPath());
                } catch (Exception e) {
                    logger.error("Error While Getting Avatar Url: {}", e.getMessage());
                }
            }

            return quizInfoResponse;
        }

        return new QuizResourcesDto(quizVersion, quiz, this.quizUtils, !isForEdit);
    }

    public QuizResourcesDto uploadMediaForQuiz(
            ModifyMediaDto modifyMediaDto,
            QuizResourcesDto quizResourcesDto
    ) throws Exception {
        if (modifyMediaDto.questionsId.size() != modifyMediaDto.files.size()) {
            throw new RuntimeException("Question Id and File Length Not Match");
        }

        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                modifyMediaDto.resourceId,
                modifyMediaDto.userId,
                ResourceAccessedBy.ROLE.CONTRIBUTOR
        );
        Resource resource = (Resource) getQuizInfo[0];
        QuizVersion quizVersion = (QuizVersion) getQuizInfo[2];

        // store current questions in a map
        Map<String, QuestionDto> currQuestionsMap = new HashMap<>(quizResourcesDto.questions.size());
        for (QuestionDto questionDto : quizResourcesDto.questions) {
            currQuestionsMap.put(questionDto.questionId, questionDto);
        }

        // get question related to this quiz (sequentially)
        Thread[] threads = new Thread[modifyMediaDto.questionsId.size()];
        for (int i = 0; i < modifyMediaDto.questionsId.size(); i++) {
            final MultipartFile file = modifyMediaDto.files.get(i);
            final String questionId = modifyMediaDto.questionsId.get(i);
            final String width = modifyMediaDto.widths.get(i).toString();
            final String height = modifyMediaDto.heights.get(i).toString();

            threads[i] = new Thread(() -> {
                Question question = questionRepository.findByIdAndQuizVersion(
                        questionId, quizVersion
                );
                if (question == null) {
                    throw new RuntimeException("Question Not Found");
                }

                // upload media
                String objectName = question.getId();
                // setup metadata
                Map<String, String> metadata = new HashMap<>();
                metadata.put("name", file.getName());
                metadata.put("size", Long.toString(file.getSize()));
                metadata.put("type", file.getContentType());
                metadata.put("filename", file.getOriginalFilename());
                metadata.put("contentType", file.getContentType());
                metadata.put("last_modified", String.valueOf((resource.getDateUpdated().getTime() / 1000))); // unix timestamp
                metadata.put("width", width);
                metadata.put("height", height);

                // setup tags
                Map<String, String> tags = new HashMap<>();
                metadata.put("status", MinioClientUtils.FILE_STATUS.AVAILABLE.toString());

                try {
                    // upload file to minio
                    minioClientUtils.uploadFile(
                            objectName,
                            file.getInputStream(),
                            file.getSize(),
                            file.getContentType(),
                            metadata
                    );

                    // set tags
                    minioClientUtils.setTags(tags, objectName);
                }
                catch (Exception e) {
                    logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                    return;
                }

                // update image object name for each question
                if (currQuestionsMap.containsKey(question.getId())) {
                    currQuestionsMap.get(question.getId()).imageObjectName = question.getId();
                }
            });
            threads[i].setName("Upload Media Thread " + i);
            threads[i].start();
        }

        // joining threads
        for (Thread thread : threads) {
            thread.join();
        }

        return this.updateQuiz(quizResourcesDto);
    }

    public AttemptQuizResponse startTraditionalQuiz(
            String resourceId, String userId
    ) {
        Date date = new Date();

        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                resourceId,
                userId,
                ResourceAccessedBy.ROLE.VIEWER
        );
        QuizVersion quizVersion = (QuizVersion) getQuizInfo[2]; // latest version
        User user = (User) getQuizInfo[3];

        // init return data
        AttemptQuizResponse attemptQuizResponse = new AttemptQuizResponse();
        if (date.before(quizVersion.getDateStarted()) || date.after(quizVersion.getDateEnded())) {
            attemptQuizResponse.success = false;
            return attemptQuizResponse;
        }

        attemptQuizResponse.success = true;

        // set time limit
        attemptQuizResponse.timeLimit = quizVersion.getTimeLimit();

        // check previous attempt
        List<DoQuiz> prevDoQuiz = doQuizRepository.findByQuizVersionIdAndSubmitTime(
                quizVersion.getId(),
                null
        );
        if (prevDoQuiz != null && !prevDoQuiz.isEmpty()) {
            Iterator<DoQuiz> iterator = prevDoQuiz.iterator();
            DoQuiz latestDoQuiz = iterator.next();

            while (iterator.hasNext()) {
                DoQuiz doQuiz = iterator.next();
                if (doQuiz.getStartTime().after(latestDoQuiz.getStartTime())) {
                    latestDoQuiz = doQuiz;
                }
            }

            // setup return data
            attemptQuizResponse.attemptId = latestDoQuiz.getId();
            attemptQuizResponse.startTime = latestDoQuiz.getStartTime();

            return attemptQuizResponse;
        }

        // check remaining attempts
        if (this.quizUtils.getRemainingAttempts(quizVersion, user) == 0) {
            attemptQuizResponse.success = false;
            return attemptQuizResponse;
        }

        // create a new attempt
        DoQuiz doQuiz = new DoQuiz();
        doQuiz.setQuizVersionId(quizVersion.getId());
        doQuiz.setUser(user);
        doQuiz.setStartTime(date);
        doQuiz.setTimeLimit(quizVersion.getTimeLimit());
        doQuiz.setSubmitTime(null);
        doQuizRepository.save(doQuiz);

        // start a checking submit thread
        this.quizUtils.startDoQuiz(doQuiz.getId());

        // setup return data
        attemptQuizResponse.attemptId = doQuiz.getId();
        attemptQuizResponse.startTime = date;

        return attemptQuizResponse;
    }

    public AttemptQuizResponse getQuizByAttempt(
            String attemptId
    ) {
        DoQuiz doQuiz = doQuizRepository.findById(attemptId).orElseThrow(
                () -> new RuntimeException("Attempt Not Found")
        );
        if (doQuiz.getSubmitTime() != null) {
            throw new RuntimeException("Attempt Already Submitted");
        }

        QuizVersion quizVersion = quizVersionRepository.findById(doQuiz.getQuizVersionId()).orElseThrow(
                () -> new RuntimeException("Quiz Version Not Found")
        );

        Quiz quiz = quizRepository.findById(quizVersion.getQuizId()).orElseThrow(
                () -> new RuntimeException("Quiz Not Found")
        );

        AttemptQuizResponse attemptQuizResponse = new AttemptQuizResponse();
        attemptQuizResponse.attemptId = doQuiz.getId();
        attemptQuizResponse.startTime = doQuiz.getStartTime();
        attemptQuizResponse.timeLimit = doQuiz.getTimeLimit();
        attemptQuizResponse.quiz = new QuizResourcesDto(quizVersion, quiz, this.quizUtils);
        attemptQuizResponse.isMultipleChoices = new Boolean[attemptQuizResponse.quiz.questions.size()];

        // set correct of all answers to false
        for (int i = 0; i < attemptQuizResponse.quiz.questions.size(); i++) {
            QuestionDto questionDto = attemptQuizResponse.quiz.questions.get(i);
            int countCorrect = 0;

            for (AbstractChoiceDto abstractChoiceDto : questionDto.choices) {
                if (abstractChoiceDto.correct) {
                    countCorrect++;
                }
                abstractChoiceDto.correct = false;
            }

            attemptQuizResponse.isMultipleChoices[i] = countCorrect > 1;
        }

        return attemptQuizResponse;
    }

    public QuizSummaryResponse submitTraditionalQuiz(
            SubmitQuizDto submitQuizDto
    ) {
        Date date = new Date();

        // setup return data
        QuizSummaryResponse quizSummaryResponse = new QuizSummaryResponse();

        // get attempt
        DoQuiz doQuiz = doQuizRepository.findById(submitQuizDto.attemptId).orElseThrow(
                () -> new RuntimeException("Attempt Not Found")
        );

        // check previous submit
        if (doQuiz.getSubmitTime() != null) {
            quizSummaryResponse.attemptId = doQuiz.getId();
            quizSummaryResponse.status = QuizSummaryResponse.STATUS.FINISHED;
            quizSummaryResponse.totalScore = doQuiz.getTotalScore();
            quizSummaryResponse.maxScore = doQuiz.getMaxScore();
            quizSummaryResponse.startTime = doQuiz.getStartTime();
            quizSummaryResponse.submitTime = date;
            quizSummaryResponse.duration = Math.abs(
                    doQuiz.getSubmitTime().getTime() - doQuiz.getStartTime().getTime()
            ) / 1000;
            return quizSummaryResponse;
        }

        // get quiz version related to this attempt
        QuizVersion quizVersion = quizVersionRepository.findById(doQuiz.getQuizVersionId()).orElseThrow(
                () -> new RuntimeException("Quiz Version Not Found")
        );

        double maxScore = 0d;
        List<Question> questions = questionRepository.findByQuizVersion(quizVersion);
        for (Question question : questions) {
            maxScore += question.getScore();
        }

        double totalScore = 0d;

        // save submitted answers
        for (QuestionDto submittedQuestionDto : submitQuizDto.submittedQuestions) {
            Question question = questionRepository.findByIdAndQuizVersion(
                    submittedQuestionDto.questionId, quizVersion
            );
            if (question == null) {
                continue;
            }

            // get all answers of the question
            int numOfCorrectAnswers = 0;
            List<Answer> answers = answerRepository.findByQuestion(question);
            for (Answer answer : answers) {
                if (answer.getIsCorrect()) {
                    numOfCorrectAnswers++;
                }
            }

            // loop through each submitted answer
            double questionSubmittedScore = 0d;

            for (AbstractChoiceDto abstractChoiceDto : submittedQuestionDto.choices) {
                // check only selected answer (correct: true)
                if (!abstractChoiceDto.correct) {
                    continue;
                }

                // get related answer
                Optional<Answer> trueAnswerOp = answerRepository.findById(abstractChoiceDto.choiceId);
                if (trueAnswerOp.isPresent()) {
                    Answer trueAnswer = trueAnswerOp.get();

                    QuizResponse quizResponse = new QuizResponse();
                    quizResponse.setQuestion(question);
                    quizResponse.setAnswer(trueAnswer);
                    quizResponse.setDoQuizId(doQuiz.getId());

                    double answerScore = 0d;
                    if (trueAnswer.getIsCorrect()) { // if true answer is matched with submitted answer, add score
                        answerScore = (1d / numOfCorrectAnswers) * question.getScore();
                    }

                    questionSubmittedScore += answerScore;
                    quizResponse.setScore(answerScore);

                    quizResponseRepository.save(quizResponse);
                }
            }

            totalScore += questionSubmittedScore;
        }

        // set submit time
        doQuiz.setSubmitTime(date);
        doQuiz.setTotalScore(totalScore);
        doQuiz.setMaxScore(maxScore);
        doQuizRepository.save(doQuiz);

        // setup return data
        quizSummaryResponse.attemptId = doQuiz.getId();
        quizSummaryResponse.status = QuizSummaryResponse.STATUS.FINISHED;
        quizSummaryResponse.totalScore = totalScore;
        quizSummaryResponse.maxScore = maxScore;
        quizSummaryResponse.startTime = doQuiz.getStartTime();
        quizSummaryResponse.submitTime = date;
        quizSummaryResponse.duration = Math.abs(date.getTime() - doQuiz.getStartTime().getTime()) / 1000;

        return quizSummaryResponse;
    }

    public QuizAllAttemptsResponse getAllQuizAttempts(
            String userId, String resourceId
    ) {
        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                resourceId,
                userId,
                ResourceAccessedBy.ROLE.VIEWER
        );
        Resource resource = (Resource) getQuizInfo[0];
        Quiz quiz = (Quiz) getQuizInfo[1];
        User user = (User) getQuizInfo[3];

        // check if user is owner
        boolean isOwner = resource.getUser().getId().equals(userId);

        // setup return value
        QuizAllAttemptsResponse quizAllAttemptsResponse = new QuizAllAttemptsResponse();
        quizAllAttemptsResponse.resourceId = resourceId;
        quizAllAttemptsResponse.quizId = quiz.getId();
        quizAllAttemptsResponse.quizResults = new LinkedList<>();

        List<QuizVersion> quizVersions = quizVersionRepository.findByQuizId(quiz.getId());
        if (quizVersions != null) {
            for (QuizVersion quizVersion : quizVersions) {
                // get all attempts related to this quiz version
                List<DoQuiz> doQuizzes;
                if (isOwner) {
                    // if user is owner, get all attempts
                    doQuizzes = doQuizRepository.findByQuizVersionId(
                            quizVersion.getId()
                    );
                }
                else {
                    // if not, only get related user
                    doQuizzes = doQuizRepository.findByQuizVersionIdAndUser(
                            quizVersion.getId(), user
                    );
                }

                for (DoQuiz doQuiz : doQuizzes) {
                    // setup return value
                    QuizSummaryResponse quizSummaryResponse = new QuizSummaryResponse();
                    quizSummaryResponse.attemptId = doQuiz.getId();

                    if (doQuiz.getSubmitTime() != null) {
                        quizSummaryResponse.status = QuizSummaryResponse.STATUS.FINISHED;
                        quizSummaryResponse.duration = Math.abs(doQuiz.getSubmitTime().getTime() - doQuiz.getStartTime().getTime()) / 1000;
                    }
                    else {
                        quizSummaryResponse.status = QuizSummaryResponse.STATUS.IN_PROGRESS;
                        quizSummaryResponse.duration = null;
                    }

                    quizSummaryResponse.totalScore = doQuiz.getTotalScore();
                    quizSummaryResponse.maxScore = doQuiz.getMaxScore();
                    quizSummaryResponse.startTime = doQuiz.getStartTime();
                    quizSummaryResponse.submitTime = doQuiz.getSubmitTime();
                    quizSummaryResponse.username = doQuiz.getUser().getUsername();

                    // add to return data
                    quizAllAttemptsResponse.quizResults.add(quizSummaryResponse);
                }
            }
        }

        return quizAllAttemptsResponse;
    }

    public QuizResourcesDto getQuizReview(
            String attemptId
    ) {
        // get attempt
        DoQuiz doQuiz = doQuizRepository.findById(attemptId).orElseThrow(
                () -> new RuntimeException("Attempt not found")
        );

        // get quiz version related to the attempt
        QuizVersion quizVersion = quizVersionRepository.findById(doQuiz.getQuizVersionId()).orElseThrow(
                () -> new RuntimeException("Quiz version not found")
        );

        // get quiz of this quiz version
        Quiz quiz = quizRepository.findById(quizVersion.getQuizId()).orElseThrow(
                () -> new RuntimeException("Quiz not found")
        );

        // construct quiz dto
        QuizResourcesDto quizResourcesDto = new QuizResourcesDto(
                quizVersion, quiz, this.quizUtils, false
        );

        // loop through all questions
        for (QuestionDto questionDto : quizResourcesDto.questions) {
            List<AbstractChoiceDto> choiceSubmittedDtos = new LinkedList<>();
            List<QuizResponse> quizResponses = quizResponseRepository.getByQuestionIdAndDoQuizId(
                    questionDto.questionId, doQuiz.getId()
            );

            // construct a map for quiz responses
            Map<String, Boolean> mapQuizAnswerResponses = new HashMap<>();
            for (QuizResponse quizResponse : quizResponses) {
                mapQuizAnswerResponses.put(quizResponse.getAnswer().getId(), true);
            }

            // loop through all answers
            for (AbstractChoiceDto abstractChoiceDto : questionDto.choices) {
                ChoiceSubmittedDto choiceSubmittedDto = new ChoiceSubmittedDto();
                choiceSubmittedDto.copyFrom(abstractChoiceDto);
                choiceSubmittedDto.submittedCorrect = mapQuizAnswerResponses.containsKey(abstractChoiceDto.choiceId);
                choiceSubmittedDtos.add(choiceSubmittedDto);
            }

            questionDto.choices = choiceSubmittedDtos;
        }

        return quizResourcesDto;
    }

    public List<QuizVersionsResponse> getAllQuizVersions(
            String resourceId, String userId
    ) {
        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                resourceId,
                userId,
                ResourceAccessedBy.ROLE.VIEWER
        );
        Quiz quiz = (Quiz) getQuizInfo[1];

        List<QuizVersion> quizVersions = quizVersionRepository.findByQuizId(quiz.getId());
        List<QuizVersionsResponse> responses = new LinkedList<>();
        for (QuizVersion quizVersion : quizVersions) {
            QuizVersionsResponse quizVersionsResponse = new QuizVersionsResponse();
            quizVersionsResponse.resourceId = resourceId;
            quizVersionsResponse.quizId = quiz.getId();
            quizVersionsResponse.versionId = quizVersion.getId();
            quizVersionsResponse.updatedTime = quizVersion.getDateUpdated();
            quizVersionsResponse.versionName = quizVersion.getTitle();
            responses.add(quizVersionsResponse);
        }

        // sort by updated time
        responses.sort(
            (o1, o2) -> {
                if (o1.updatedTime.after(o2.updatedTime)) {
                    return -1;
                }
                if (o1.updatedTime.before(o2.updatedTime)) {
                    return 1;
                }
                return 0;
            }
        );

        return responses;
    }

    public QuizResourcesDto getQuizByVersion(
            String userId, String versionId
    ) {
        QuizVersion quizVersion = quizVersionRepository.findById(versionId).orElseThrow(
                () -> new RuntimeException("Quiz Version Not Found")
        );

        Quiz quiz = quizRepository.findById(quizVersion.getQuizId()).orElseThrow(
                () -> new RuntimeException("Quiz Not Found")
        );

        // check quiz info
        this.quizUtils.checkQuiz(
                quiz.getResource().getId(),
                userId,
                ResourceAccessedBy.ROLE.VIEWER
        );

        return new QuizResourcesDto(quizVersion, quiz, this.quizUtils, false);
    }

    public QuizResourcesDto restoreQuiz(
            RestoreQuizVersionDto restoreQuizVersionDto
    ) {
        // get and check quiz info
        Object[] getQuizInfo = this.quizUtils.checkQuiz(
                restoreQuizVersionDto.resourceId,
                restoreQuizVersionDto.userId,
                ResourceAccessedBy.ROLE.VIEWER
        );
        Quiz quiz = (Quiz) getQuizInfo[1];

        // check quiz version that need to be restored
        QuizVersion quizVersion = quizVersionRepository.findByQuizIdAndId(
                quiz.getId(),
                restoreQuizVersionDto.versionId
        );
        if (quizVersion == null) {
            throw new RuntimeException("Quiz Version Not Found");
        }

        // set update time for quiz version
        quizVersion.setDateUpdated(new Date());
        quizVersionRepository.save(quizVersion);

        // restore quiz version
        quiz.setQuizVersionId(quizVersion.getId());
        quizRepository.save(quiz);

        return new QuizResourcesDto(quizVersion, quiz, this.quizUtils, false);
    }
}
