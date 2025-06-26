package com.lreas.forum.controllers;

import com.lreas.forum.dtos.*;

import com.lreas.forum.services.ForumService;

import com.lreas.forum.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

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
public class ForumController {
    private final ForumService forumService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ForumController(
            ForumService forumService,
            JwtUtils jwtUtils
    ) {
        this.forumService = forumService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/thread/new")
    public ResponseEntity<Object> createThread(
            HttpServletRequest request,
            @RequestBody CreateThreadRequest createThreadRequest
    ) {
        try {
            createThreadRequest.userId = jwtUtils.extractUserId(request);

            ThreadInfoDto response = this.forumService.createThread(createThreadRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/thread/{threadId}")
    public ResponseEntity<Object> getThread(
            HttpServletRequest request,
            @PathVariable String threadId
    ) {
        try {
            ThreadInfoDto response = this.forumService.getThread(threadId, jwtUtils.extractUserId(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/thread/{threadId}")
    public ResponseEntity<Object> updateThread(
            HttpServletRequest request,
            @PathVariable String threadId,
            @RequestBody ThreadInfoDto newThreadInfoDto
    ) {
        try {
            newThreadInfoDto.threadId = threadId;
            newThreadInfoDto.userId = jwtUtils.extractUserId(request);
            ThreadInfoDto response = this.forumService.updateThread(newThreadInfoDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/thread/{threadId}")
    public ResponseEntity<Object> deleteThread(
            HttpServletRequest request,
            @PathVariable String threadId
    ) {
        try {
            ThreadInfoDto response = this.forumService.deleteThread(
                    threadId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/comment/new")
    public ResponseEntity<Object> createComment(
            HttpServletRequest request,
            @RequestBody CreateCommentRequest createCommentRequest
    ) {
        try {
            createCommentRequest.userId = jwtUtils.extractUserId(request);
            CommentInfoDto response = this.forumService.createComment(createCommentRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Object> getComment(
            HttpServletRequest request,
            @PathVariable String commentId
    ) {
        try {
            CommentInfoDto response = this.forumService.getComment(commentId, jwtUtils.extractUserId(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Object> updateComment(
            HttpServletRequest request,
            @PathVariable String commentId,
            @RequestBody CommentInfoDto newCommentInfoDto
    ) {
        try {
            newCommentInfoDto.commentId = commentId;
            newCommentInfoDto.userId = jwtUtils.extractUserId(request);
            CommentInfoDto response = this.forumService.updateComment(newCommentInfoDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(
            HttpServletRequest request,
            @PathVariable String commentId
    ) {
        try {
            CommentInfoDto response = this.forumService.deleteComment(
                    commentId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/topic/new")
    public ResponseEntity<Object> createTopic(
            HttpServletRequest request,
            @RequestBody CreateTopicRequest createTopicRequest
    ) {
        try {
            createTopicRequest.userId = jwtUtils.extractUserId(request);
            TopicInfoDto response = this.forumService.createTopic(createTopicRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Object> getTopic(
            HttpServletRequest request,
            @PathVariable String topicId
    ) {
        try {
            TopicInfoDto response = this.forumService.getTopic(topicId, jwtUtils.extractUserId(request));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/topic/{topicId}")
    public ResponseEntity<Object> updateTopic(
            HttpServletRequest request,
            @PathVariable String topicId,
            @RequestBody TopicInfoDto newTopicInfoDto
    ) {
        try {
            newTopicInfoDto.topicId = topicId;
            newTopicInfoDto.userId = jwtUtils.extractUserId(request);
            TopicInfoDto response = this.forumService.updateTopic(newTopicInfoDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/topic/{topicId}")
    public ResponseEntity<Object> deleteTopic(
            HttpServletRequest request,
            @PathVariable String topicId
    ) {
        try {
            TopicInfoDto response = this.forumService.deleteTopic(
                    topicId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topics/all")
    public ResponseEntity<Object> getAllTopicsInInstitution(
            HttpServletRequest request
    ) {
        try {
            List<TopicInfoDto> response = this.forumService.getAllTopicsInInstitution(
                    jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topics")
    public ResponseEntity<Object> getAllTopicsInInstitution(
            HttpServletRequest request,
            @RequestParam Integer page,
            @RequestParam Integer pageSize
    ) {
        try {
            List<TopicInfoDto> response = this.forumService.getAllTopicsInInstitution(
                    jwtUtils.extractUserId(request), page, pageSize
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic/{topicId}/threads/all")
    public ResponseEntity<Object> getAllThreadsInTopic(
            HttpServletRequest request,
            @PathVariable String topicId
    ) {
        try {
            List<ThreadInfoDto> response = this.forumService.getAllThreadsInTopic(
                    topicId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic/{topicId}/threads")
    public ResponseEntity<Object> getAllThreadsInTopic(
            HttpServletRequest request,
            @PathVariable String topicId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize
    ) {
        try {
            List<ThreadInfoDto> response = this.forumService.getAllThreadsInTopic(
                    topicId, page, pageSize, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/thread/{threadId}/comments/all")
    public ResponseEntity<Object> getAllCommentsInThread(
            HttpServletRequest request,
            @PathVariable String threadId
    ) {
        try {
            List<CommentInfoDto> response = this.forumService.getAllCommentsInThread(
                    threadId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/thread/{threadId}/comments")
    public ResponseEntity<Object> getAllCommentsInThread(
            HttpServletRequest request,
            @PathVariable String threadId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize
    ) {
        try {
            List<CommentInfoDto> response = this.forumService.getAllCommentsInThread(
                    threadId, page, pageSize, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
