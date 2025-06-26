package com.lreas.generator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lreas.generator.configs.GeminiConfig;
import com.lreas.generator.dtos.GenerateFromFileDto;
import com.lreas.generator.dtos.NotebookGenerateDto;
import com.lreas.generator.dtos.QuizDtos.QuizResourcesDto;
import com.lreas.generator.models.File;
import com.lreas.generator.models.Quiz;
import com.lreas.generator.models.Resource;
import com.lreas.generator.repositories.jpa.FileRepository;
import com.lreas.generator.repositories.jpa.QuizRepository;
import com.lreas.generator.repositories.jpa.ResourceRepository;
import com.lreas.generator.services.GeneratorServiceImpl;
import com.lreas.generator.services.GrpcQuizServiceGrpcClient;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GeminiUtils {
    private static final Logger logger = LoggerFactory.getLogger(GeminiUtils.class);

    private final GeminiConfig geminiConfig;
    private final String baseUrl;
    private final String cacheUrl;

    private final int CHUNK_SIZE = 5;

    private final ResourceRepository resourceRepository;
    private final QuizRepository quizRepository;
    private final FileRepository fileRepository;
    private final DocumentsUtils documentsUtils;
    private final GrpcQuizServiceGrpcClient grpcQuizServiceGrpcClient;
    private final Set<String> stoppingResources = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public GeminiUtils(
            GeminiConfig geminiConfig,
            ResourceRepository resourceRepository,
            QuizRepository quizRepository,
            FileRepository fileRepository,
            DocumentsUtils documentsUtils,
            GrpcQuizServiceGrpcClient grpcQuizServiceGrpcClient
    ) {
        this.geminiConfig = geminiConfig;
        this.baseUrl = this.geminiConfig.getBaseUrl().formatted(
                this.geminiConfig.getModelCode(),
                this.geminiConfig.getApiKey()
        );
        this.cacheUrl = this.geminiConfig.getCacheUrl().formatted(
                this.geminiConfig.getApiKey()
        );

        this.resourceRepository = resourceRepository;
        this.quizRepository = quizRepository;
        this.fileRepository = fileRepository;
        this.documentsUtils = documentsUtils;
        this.grpcQuizServiceGrpcClient = grpcQuizServiceGrpcClient;
    }

    private String mapMimeType(String mimeType) {
        return switch (mimeType) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc" -> "application/msword";
            case "mp4" -> "video/mp4";
            case "webm" -> "video/webm";
            case "mpeg" -> "video/mpeg";
            default -> throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
        };
    }

    private String generateContentWithCache(
            String prompt, String cacheName,
            GenerateFromFileDto.TYPE contentType
    ) {
        String result = "";
        String contents = "";

        // read template
        try {
            InputStream inputStream = null;

            if (contentType.equals(GenerateFromFileDto.TYPE.QUIZ)) {
                inputStream = GeminiUtils.class.getResourceAsStream("/GeminiPrompts/quiz/generate_quiz.json");
            }
            else if (contentType.equals(GenerateFromFileDto.TYPE.NOTEBOOK)) {
                inputStream = GeminiUtils.class.getResourceAsStream("/GeminiPrompts/notebook/generate_notebook.json");
            }

            if (inputStream == null) {
                return contents;
            }

            contents = IOUtils.toString(inputStream, StandardCharsets.UTF_8).formatted(
                    prompt,
                    cacheName
            );

            // close stream
            inputStream.close();
        }
        catch (Exception e) {
            logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
            return contents;
        }

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(contents, headers);

        // send request and parse result
        ResponseEntity<String> response = new RestTemplate().exchange(
                this.baseUrl, HttpMethod.POST, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(Objects.requireNonNull(response.getBody()));

            result = json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        }

        return result;
    }

    public String generateCacheContent(
            String base64FileContent, String fileType
    ) {
        String ttl = "1800s";
        String result = "";
        String contents = "";

        // read template
        try (InputStream inputStream = GeminiUtils.class.getResourceAsStream("/GeminiPrompts/cache_content.json")) {
            if (inputStream == null) {
                return contents;
            }

            contents = IOUtils.toString(inputStream, StandardCharsets.UTF_8).formatted(
                    this.geminiConfig.getModelCode(),
                    this.mapMimeType(fileType),
                    base64FileContent,
                    ttl
            );
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return contents;
        }

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(contents, headers);

        // send request and parse result
        ResponseEntity<String> response = new RestTemplate().exchange(
                this.cacheUrl, HttpMethod.POST, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(Objects.requireNonNull(response.getBody()));

            result = json.getString("name");
        }
        return result;
    }

    public String generateContent(
            String prompt, String cacheName,
            GenerateFromFileDto.TYPE contentType
    ) {
        while (true) {
            try {
                return this.generateContentWithCache(prompt, cacheName, contentType);
            }
            catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    logger.info("{}: {}", Thread.currentThread().getName(), "Too Many Requests");
                    logger.info("{}: {}", Thread.currentThread().getName(), "Waiting Before Trying Again...");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    }
                    catch (InterruptedException ie) {
                        logger.info("{}: {}", Thread.currentThread().getName(), "Stopped");
                        return "";
                    }

                    logger.info("{}: {}", Thread.currentThread().getName(), "Done Waiting");
                }
                else {
                    throw e;
                }
            }
        }
    }

    @Async
    @Transactional(rollbackOn = {Exception.class}, value = Transactional.TxType.REQUIRES_NEW)
    public void createResource(
            InputStream fileInputStream,
            File file,
            GenerateFromFileDto generateFromFileDto,
            Object... args
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource createdResource = (Resource) args[0];

        final Resource resource = resourceRepository.findById(createdResource.getId()).orElse(null);
        if (resource == null) {
            return;
        }

        List<String> prompts = new LinkedList<>();
        boolean success = false;
        AtomicBoolean isStopped = new AtomicBoolean(false);

        try {
            // building prompt thread
            Thread promptThread = new Thread(() -> {
                // logging
                logger.debug("{}: {}", Thread.currentThread().getName(), "Started");

                // build prompt for different content type
                if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.QUIZ)) {
                    // read template
                    try (InputStream inputStream = GeneratorServiceImpl.class.getResourceAsStream("/GeminiPrompts/quiz/generate_quiz.txt")) {
                        if (inputStream == null) {
                            return;
                        }

                        // read prompt template as string
                        String promptTemplate = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                        // get args
                        int numOfQuestions = Integer.parseInt(generateFromFileDto.args[0].toString());
                        double timeLimit = Double.parseDouble(generateFromFileDto.args[1].toString());

                        // chunk generation
                        int numChunks = (numOfQuestions - 1) / this.CHUNK_SIZE + 1;
                        double questionTimeLimit = timeLimit / numOfQuestions;
                        for (int i = 1; i <= numChunks; i++) {
                            int currNumOfQuestions = this.CHUNK_SIZE;

                            if (i == numChunks) {
                                currNumOfQuestions = numOfQuestions - (i - 1) * this.CHUNK_SIZE;
                            }

                            double currTimeLimit = questionTimeLimit * currNumOfQuestions;

                            prompts.add(promptTemplate.formatted(
                                    currNumOfQuestions,
                                    currTimeLimit
                            ));
                        }
                    }
                    catch (NumberFormatException e) {
                        logger.error("{}: {}", Thread.currentThread().getName(), "Invalid Args");
                    }
                    catch (Exception e) {
                        logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                    }
                }
                else if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.NOTEBOOK)) {
                    // read template
                    try (InputStream inputStream = GeneratorServiceImpl.class.getResourceAsStream("/GeminiPrompts/notebook/generate_notebook.txt")) {
                        if (inputStream == null) {
                            return;
                        }

                        // read prompt template as string
                        String promptTemplate = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

                        prompts.add(promptTemplate);
                    }
                    catch (Exception e) {
                        logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
                    }
                }

                logger.debug("{}: {}", Thread.currentThread().getName(), "Completed");
            });
            promptThread.setName("Building Prompt Thread");
            promptThread.start();

            // generate resource based on prompt
            String base64FileContent = Base64.getEncoder().encodeToString(fileInputStream.readAllBytes());
            fileInputStream.close();

            // logging
            logger.debug("{}: {}", Thread.currentThread().getName(), "Creating Cache");

            // create cache
            String cacheName = this.generateCacheContent(base64FileContent, file.getType());

            // logging
            logger.debug("{}: {}", Thread.currentThread().getName(), "Created Cache Successfully With Name: " + cacheName);

            // wait for building prompt thread
            promptThread.join();
            if (prompts.isEmpty()) {
                return;
            }

            // logging
            logger.debug("{}: {}", Thread.currentThread().getName(), "Generating Json");

            // constructing threads for generating prompts
            String[] jsonResponses = new String[prompts.size()];
            final Thread[] jsonThreads = new Thread[prompts.size()];
            for (int i = 0; i < jsonResponses.length; i++) {
                final int finalI = i;

                jsonThreads[i] = new Thread(() -> jsonResponses[finalI] = this.generateContent(
                        prompts.get(finalI),
                        cacheName,
                        generateFromFileDto.type
                ));
                jsonThreads[i].setName(String.format("Generating Json Thread %d", finalI));
                jsonThreads[i].start();

                // check stopping status
                if (this.stoppingResources.contains(resource.getId())) {
                    isStopped.set(true);
                    return;
                }
            }

            // add thread for checking stopping signal
            Thread checkStopThread = new Thread(() -> {
                while (true) {
                    try {
                        // check stopping status
                        if (this.stoppingResources.contains(resource.getId())) {
                            // send interrupt signal to Generating Json Thread
                            for (Thread jsonThread : jsonThreads) {
                                jsonThread.interrupt();
                            }
                            isStopped.set(true);
                            return;
                        }

                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.debug("{}: {}", Thread.currentThread().getName(), "Stopped");
                        return;
                    }
                }
            });
            checkStopThread.setName("Check Stop Thread");
            checkStopThread.start();

            // wait for threads to be completed
            for (Thread jsonThread : jsonThreads) {
                jsonThread.join();
            }

            // stop check stop thread
            checkStopThread.interrupt();

            // logging
            logger.debug("{}: {}. Size: {}", Thread.currentThread().getName(), "Successfully Generated Json", jsonResponses.length);

            if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.QUIZ)) {
                // logging
                logger.debug("{}: {}", Thread.currentThread().getName(), "Generating Quiz From Json");

                QuizResourcesDto createdQuiz = (QuizResourcesDto) args[1];

                // convert json to object
                QuizResourcesDto quizResourcesDto = objectMapper.readValue(jsonResponses[0], QuizResourcesDto.class);
                for (int i = 1; i < jsonResponses.length; i++) {
                    // check stopping status
                    if (this.stoppingResources.contains(resource.getId())) {
                        isStopped.set(true);
                        return;
                    }

                    QuizResourcesDto quizResourcesDtoNext = objectMapper.readValue(jsonResponses[i], QuizResourcesDto.class);

                    quizResourcesDto.totalTime += quizResourcesDtoNext.totalTime;
                    quizResourcesDto.totalPoints += quizResourcesDtoNext.totalPoints;
                    quizResourcesDto.questions.addAll(quizResourcesDtoNext.questions);
                }

                // update position for each question
                for (int i = 0; i < quizResourcesDto.questions.size(); i++) {
                    quizResourcesDto.questions.get(i).position = i;
                }

                // update quiz from the gemini response
                quizResourcesDto.resourceId = createdQuiz.resourceId;
                quizResourcesDto.quizId = createdQuiz.quizId;
                quizResourcesDto.startTime = createdQuiz.startTime;
                quizResourcesDto.endTime = createdQuiz.endTime;
                quizResourcesDto.allowedAttempts = createdQuiz.allowedAttempts;
                quizResourcesDto.createdTime = createdQuiz.createdTime;
                quizResourcesDto.updatedTime = new Date();
                quizResourcesDto.userId = createdQuiz.userId;
                quizResourcesDto.name = generateFromFileDto.newResourceName;

                // check stopping status
                if (this.stoppingResources.contains(resource.getId())) {
                    isStopped.set(true);
                    return;
                }

                // send updated quiz to quiz service
                this.grpcQuizServiceGrpcClient.updateQuiz(quizResourcesDto);

                // logging
                logger.debug("{}: {}", Thread.currentThread().getName(), "Successfully Generated Quiz");
            }
            else if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.NOTEBOOK)) {
                // logging
                logger.debug("{}: {}", Thread.currentThread().getName(), "Generating Notebook From Json");

                StringBuilder stringBuilder = new StringBuilder("<div>");
                for (String jsonResponse : jsonResponses) {
                    // check stopping status
                    if (this.stoppingResources.contains(resource.getId())) {
                        isStopped.set(true);
                        return;
                    }

                    NotebookGenerateDto notebookGenerateDtoNext = objectMapper.readValue(jsonResponse, NotebookGenerateDto.class);
                    stringBuilder.append(notebookGenerateDtoNext.content);
                }
                stringBuilder.append("</div>");

                NotebookGenerateDto notebookGenerateDto = new NotebookGenerateDto();
                notebookGenerateDto.content = stringBuilder.toString();

                // logging
                logger.debug("{}: {}", Thread.currentThread().getName(), "Saving Notebook");
                boolean result = this.documentsUtils.saveDocuments(
                        resource.getId(),
                        objectMapper.writeValueAsString(notebookGenerateDto)
                );

                // logging
                if (result) {
                    logger.debug("{}: {}", Thread.currentThread().getName(), "Successfully Generated Notebook");
                }
                else {
                    logger.debug("{}: {}", Thread.currentThread().getName(), "Failed To Save Notebook");
                }
            }

            // update for resource
            resource.setWorkflowState(Resource.STATE.AVAILABLE);
            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);

            // set success
            success = true;
        }
        catch (Exception e) {
            logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
        }
        finally {
            if (!success && !isStopped.get()) {
                Quiz quizPending = quizRepository.findByResource(resource);
                if (quizPending != null) {
                    quizRepository.delete(quizPending);
                }

                File filePending = fileRepository.findByResource(resource);
                if (filePending != null) {
                    fileRepository.delete(filePending);
                }

                // set new state for resource
                resource.setWorkflowState(Resource.STATE.FAILED);
                resource.setDateUpdated(new Date());
                resourceRepository.save(resource);
            }

            if (isStopped.get()) {
                logger.debug("{}: {}", Thread.currentThread().getName(), "Stopped");
            }

            // remove this resource from stoppingResource
            this.stoppingResources.remove(resource.getId());
        }
    }
}
