package com.lreas.forum.services;

import com.lreas.forum.dtos.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ForumService {
    ThreadInfoDto createThread(CreateThreadRequest createThreadRequest);
    ThreadInfoDto getThread(String threadId, String userId);
    ThreadInfoDto updateThread(ThreadInfoDto newThreadInfoDto);
    ThreadInfoDto deleteThread(String threadId, String userId);

    CommentInfoDto createComment(CreateCommentRequest createCommentRequest);
    CommentInfoDto getComment(String commentId, String userId);
    CommentInfoDto updateComment(CommentInfoDto newCommentInfoDto);
    CommentInfoDto deleteComment(String commentId, String userId);

    TopicInfoDto createTopic(CreateTopicRequest createTopicRequest);
    TopicInfoDto getTopic(String topicId, String userId);
    TopicInfoDto updateTopic(TopicInfoDto newTopicInfoDto);
    TopicInfoDto deleteTopic(String topicId, String userId);

    List<TopicInfoDto> getAllTopicsInInstitution(String userId);
    List<TopicInfoDto> getAllTopicsInInstitution(String userId, Integer page, Integer pageSize);

    List<ThreadInfoDto> getAllThreadsInTopic(String topicId, String userId);
    List<ThreadInfoDto> getAllThreadsInTopic(String topicId, Integer page, Integer pageSize, String userId);

    List<CommentInfoDto> getAllCommentsInThread(String threadId, String userId);
    List<CommentInfoDto> getAllCommentsInThread(String threadId, Integer page, Integer pageSize, String userId);
}
