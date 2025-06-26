package com.lreas.quiz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lreas.quiz.dtos.*;
import com.lreas.quiz.services.QuizService;
import com.lreas.quiz.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("")
@CrossOrigin(value = {
        "http://localhost:3000",
        "http://lvh.me",
        "http://lvh.me:3000",
        "https://lreas.takhoa.site",
        "http://lreas.takhoa.site",
        "http://localhost:80"
})
public class QuizController {
    private final QuizService quizService;
    private final JwtUtils jwtUtils;

    @Autowired
    public QuizController(
            QuizService quizService,
            JwtUtils jwtUtils
    ) {
        this.quizService = quizService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createQuiz(
            HttpServletRequest request,
            @RequestBody CreateQuizDto createQuizDto
    ) {
        try {
            createQuizDto.userId = jwtUtils.extractUserId(request);
            QuizResourcesDto quizResourcesDto = this.quizService.createQuiz(createQuizDto);
            return new ResponseEntity<>(quizResourcesDto, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{resourceId}/update")
    public ResponseEntity<Object> updateQuiz(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody QuizResourcesDto quizResourcesDto
    ) {
        try {
            quizResourcesDto.userId = jwtUtils.extractUserId(request);
            quizResourcesDto.resourceId = resourceId;
            QuizResourcesDto quizResourcesResultDto = this.quizService.updateQuiz(quizResourcesDto);
            return new ResponseEntity<>(quizResourcesResultDto, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{resourceId}/edit")
    public ResponseEntity<Object> getQuizForEdit(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            QuizResourcesDto quizResourcesResultDto = this.quizService.getQuiz(
                    resourceId,
                    jwtUtils.extractUserId(request),
                    false,
                    true
            );
            return new ResponseEntity<>(quizResourcesResultDto, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{resourceId}/latest")
    public ResponseEntity<Object> getQuiz(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            QuizResourcesDto quizResourcesResultDto = this.quizService.getQuiz(
                    resourceId,
                    jwtUtils.extractUserId(request),
                    false,
                    false
            );
            return new ResponseEntity<>(quizResourcesResultDto, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{resourceId}/media/upload")
    public ResponseEntity<Object> uploadMedia(
            HttpServletRequest request,
            @RequestPart("files") List<MultipartFile> files,
            @PathVariable String resourceId,
            @RequestPart("metadata") String metadata,
            @RequestPart("quiz") String quiz
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ModifyMediaDto modifyMediaDto = objectMapper.readValue(metadata, ModifyMediaDto.class);
            modifyMediaDto.userId = jwtUtils.extractUserId(request);
            modifyMediaDto.files = files;
            modifyMediaDto.resourceId = resourceId;

            QuizResourcesDto quizResourcesDto = objectMapper.readValue(quiz, QuizResourcesDto.class);

            QuizResourcesDto response = this.quizService.uploadMediaForQuiz(modifyMediaDto, quizResourcesDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{resourceId}/info")
    public ResponseEntity<Object> getQuizInfo(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            QuizResourcesDto response = this.quizService.getQuiz(
                    resourceId,
                    jwtUtils.extractUserId(request),
                    true,
                    false
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{resourceId}/start")
    public ResponseEntity<Object> startTraditionalQuiz(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            AttemptQuizResponse attemptQuizResponse = this.quizService.startTraditionalQuiz(
                    resourceId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(attemptQuizResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<Object> getQuizByAttempt(
            @PathVariable String attemptId
    ) {
        try {
            AttemptQuizResponse response = this.quizService.getQuizByAttempt(attemptId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/submission/{attemptId}")
    public ResponseEntity<Object> submitTraditionalQuiz(
            @PathVariable String attemptId,
            @RequestBody SubmitQuizDto submitQuizDto
    ) {
        try {
            submitQuizDto.attemptId = attemptId;
            QuizSummaryResponse quizSummaryResponse = this.quizService.submitTraditionalQuiz(submitQuizDto);
            return new ResponseEntity<>(quizSummaryResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{resourceId}/submission/all")
    public ResponseEntity<Object> getAllQuizAttempts(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            QuizAllAttemptsResponse response = this.quizService.getAllQuizAttempts(
                    jwtUtils.extractUserId(request), resourceId
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/result/{attemptId}")
    public ResponseEntity<Object> getQuizResult(
            @PathVariable String attemptId
    ) {
        try {
            QuizResourcesDto response = this.quizService.getQuizReview(attemptId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/versions/{resourceId}/all")
    public ResponseEntity<Object> getAllQuizVersions(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            List<QuizVersionsResponse> response = this.quizService.getAllQuizVersions(
                    resourceId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/version/{versionId}")
    public ResponseEntity<Object> getQuizByVersion(
            HttpServletRequest request,
            @PathVariable String versionId
    ) {
        try {
            QuizResourcesDto response = this.quizService.getQuizByVersion(
                    jwtUtils.extractUserId(request), versionId
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/restore")
    public ResponseEntity<Object> restoreQuiz(
            HttpServletRequest request,
            @RequestBody RestoreQuizVersionDto restoreQuizVersionDto
    ) {
        try {
            restoreQuizVersionDto.userId = jwtUtils.extractUserId(request);

            QuizResourcesDto response = this.quizService.restoreQuiz(
                    restoreQuizVersionDto
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
