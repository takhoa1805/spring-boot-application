package com.lreas.forum.services;

import com.lreas.forum.dtos.*;

import com.lreas.forum.models.Comment;
import com.lreas.forum.models.Thread;
import com.lreas.forum.models.Topic;
import com.lreas.forum.models.User;

import com.lreas.forum.repositories.CommentRepository;
import com.lreas.forum.repositories.ThreadRepository;
import com.lreas.forum.repositories.TopicRepository;
import com.lreas.forum.repositories.UserRepository;

import com.lreas.forum.utils.MinioClientUtils;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class ForumServiceImpl implements ForumService {
    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final MinioClientUtils minioClientUtils;

    public ForumServiceImpl(
            CommentRepository commentRepository,
            ThreadRepository threadRepository,
            UserRepository userRepository,
            TopicRepository topicRepository,
            MinioClientUtils minioClientUtils
    ) {
        this.commentRepository = commentRepository;
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.minioClientUtils = minioClientUtils;
    }

    public ThreadInfoDto createThread(
            CreateThreadRequest createThreadRequest
    ) {
        User user = this.userRepository.findById(createThreadRequest.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findById(createThreadRequest.topicId).orElseThrow(
                () -> new RuntimeException("Topic Not Found")
        );

        Date date = new Date();

        // create new thread
        Thread thread = new Thread();
        thread.setContent(createThreadRequest.content);
        thread.setWorkflowState(Thread.STATE.AVAILABLE);
        thread.setTopic(topic);
        thread.setDateModified(date);
        thread.setSubject(createThreadRequest.subject);
        thread.setDateCreated(date);
        thread.setUser(user);
        threadRepository.save(thread);

        return new ThreadInfoDto(thread, this.minioClientUtils, user);
    }

    public ThreadInfoDto getThread(
            String threadId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Thread thread = this.threadRepository.findByIdAndWorkflowState(threadId, Thread.STATE.AVAILABLE);
        if (thread == null) {
            throw new RuntimeException("Thread Not Found");
        }

        return new ThreadInfoDto(thread, this.minioClientUtils, user);
    }

    public ThreadInfoDto updateThread(
            ThreadInfoDto newThreadInfoDto
    ) {
        User user = this.userRepository.findById(newThreadInfoDto.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Thread thread = this.threadRepository.findByIdAndWorkflowState(newThreadInfoDto.threadId, Thread.STATE.AVAILABLE);
        if (thread == null) {
            throw new RuntimeException("Thread Not Found");
        }

        // check permission
        if (!thread.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
            throw new RuntimeException("Do Not Have Permission");
        }

        Topic topic = this.topicRepository.findById(newThreadInfoDto.topicId).orElseThrow(
                () -> new RuntimeException("Topic Not Found")
        );

        // update thread
        thread.setContent(newThreadInfoDto.content);
        thread.setTopic(topic);
        thread.setSubject(newThreadInfoDto.subject);
        thread.setDateModified(new Date());

        return new ThreadInfoDto(thread, this.minioClientUtils, user);
    }

    private ThreadInfoDto deleteThread(
            String threadId, String userId,
            Boolean forceDelete
    ) {
        User user = null;
        if (!forceDelete) {
            user = this.userRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("User Not Found")
            );
        }

        Thread thread = this.threadRepository.findById(threadId).orElse(null);

        if (thread != null) {
            // check permission
            if (user != null && !thread.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
                throw new RuntimeException("Do Not Have Permission");
            }

            // delete all thread comments
            List<Comment> comments = thread.getComments();
            for (Comment comment : comments) {
                this.deleteComment(comment.getId(), userId, true);
            }

            // delete thread
            thread.setWorkflowState(Thread.STATE.DELETED);
            threadRepository.save(thread);
        }

        return new ThreadInfoDto(thread, this.minioClientUtils, user);
    }

    public ThreadInfoDto deleteThread(
            String threadId, String userId
    ) {
        return this.deleteThread(threadId, userId, false);
    }

    public CommentInfoDto createComment(
            CreateCommentRequest createCommentRequest
    ) {
        Thread thread = this.threadRepository.findById(createCommentRequest.threadId).orElseThrow(
                () -> new RuntimeException("Thread Not Found")
        );

        User user = this.userRepository.findById(createCommentRequest.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Date date = new Date();

        Comment comment = new Comment();
        comment.setThread(thread);
        comment.setUser(user);
        comment.setDateModified(date);
        comment.setDateCreated(date);
        comment.setContent(createCommentRequest.content);
        comment.setWorkflowState(Comment.STATE.AVAILABLE);

        if (createCommentRequest.parentCommentId != null) {
            Comment parentComment = this.commentRepository.findById(createCommentRequest.parentCommentId).orElseThrow(
                    () -> new RuntimeException("Parent Comment Not Found")
            );
            comment.setParentComment(parentComment);
        }

        commentRepository.save(comment);

        return new CommentInfoDto(comment, this.minioClientUtils, user);
    }

    public CommentInfoDto getComment(
            String commentId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Comment comment = this.commentRepository.findByIdAndWorkflowState(commentId, Comment.STATE.AVAILABLE);
        if (comment == null) {
            throw new RuntimeException("Comment Not Found");
        }

        return new CommentInfoDto(comment, this.minioClientUtils, true, user);
    }

    public CommentInfoDto updateComment(
            CommentInfoDto newCommentInfoDto
    ) {
        User user = this.userRepository.findById(newCommentInfoDto.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Comment comment = this.commentRepository.findByIdAndWorkflowState(newCommentInfoDto.commentId, Comment.STATE.AVAILABLE);
        if (comment == null) {
            throw new RuntimeException("Comment Not Found");
        }

        // check permission
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
            throw new RuntimeException("Do Not Have Permission");
        }

        comment.setDateModified(new Date());
        comment.setContent(newCommentInfoDto.content);

        if (newCommentInfoDto.parentCommentId != null) {
            Comment parent = this.commentRepository.findByIdAndWorkflowState(newCommentInfoDto.parentCommentId, Comment.STATE.AVAILABLE);
            if (parent == null) {
                throw new RuntimeException("Parent Comment Not Found");
            }
            comment.setParentComment(parent);
        }

        commentRepository.save(comment);

        return new CommentInfoDto(comment, this.minioClientUtils, user);
    }

    private CommentInfoDto deleteComment(
            String commentId, String userId,
            Boolean forceDelete
    ) {
        User user = null;
        if (!forceDelete) {
            user = this.userRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("User Not Found")
            );
        }

        Comment comment = this.commentRepository.findByIdAndWorkflowState(commentId, Comment.STATE.AVAILABLE);

        if (comment != null) {
            // check permission
            if (user != null && !comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
                throw new RuntimeException("Do Not Have Permission");
            }

            // delete all replies
            List<Comment> children = comment.getChildComments();
            for (Comment child : children) {
                this.deleteComment(child.getId(), userId, true);
            }

            // delete comment
            comment.setWorkflowState(Comment.STATE.DELETED);
            commentRepository.save(comment);
        }

        return new CommentInfoDto(comment, this.minioClientUtils, user);
    }

    public CommentInfoDto deleteComment(
            String commentId, String userId
    ) {
        return this.deleteComment(commentId, userId, false);
    }

    public TopicInfoDto createTopic(
            CreateTopicRequest createTopicRequest
    ) {
        User user = this.userRepository.findById(createTopicRequest.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Date date = new Date();

        // create new topic
        Topic topic = new Topic();
        topic.setTitle(createTopicRequest.title);
        topic.setDateCreated(date);
        topic.setDateUpdated(date);
        topic.setWorkflowState(Topic.STATE.AVAILABLE);
        topic.setInstitution(user.getInstitution());
        topic.setUser(user);
        topicRepository.save(topic);

        return new TopicInfoDto(topic, user);
    }

    public TopicInfoDto getTopic(
            String topicId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findByIdAndWorkflowState(topicId, Topic.STATE.AVAILABLE);
        if (topic == null) {
            throw new RuntimeException("Topic Not Found");
        }

        return new TopicInfoDto(topic, user);
    }

    public TopicInfoDto updateTopic(
            TopicInfoDto newTopicInfoDto
    ) {
        User user = this.userRepository.findById(newTopicInfoDto.userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findByIdAndWorkflowState(newTopicInfoDto.topicId, Topic.STATE.AVAILABLE);
        if (topic == null) {
            throw new RuntimeException("Topic Not Found");
        }

        // check permission
        if (!topic.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
            throw new RuntimeException("Do Not Have Permission");
        }

        // update thread
        topic.setTitle(newTopicInfoDto.topicName);
        topic.setDateUpdated(new Date());
        topicRepository.save(topic);

        return new TopicInfoDto(topic, user);
    }

    public TopicInfoDto deleteTopic(
            String topicId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findByIdAndWorkflowState(topicId, Topic.STATE.AVAILABLE);

        if (topic != null) {
            // check permission
            if (!topic.getUser().getId().equals(user.getId()) && !user.getRole().equals(User.ROLE.ADMIN)) {
                throw new RuntimeException("Do Not Have Permission");
            }

            // delete all threads
            List<Thread> threads = topic.getThreads();
            for (Thread thread : threads) {
                this.deleteThread(thread.getId(), userId, true);
            }

            // delete topic
            topic.setWorkflowState(Topic.STATE.DELETED);
            topicRepository.save(topic);
        }

        return new TopicInfoDto(topic, user);
    }

    public List<TopicInfoDto> getAllTopicsInInstitution(
            String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        List<Topic> topics = this.topicRepository.findByInstitution(user.getInstitution());
        List<TopicInfoDto> topicInfoDtos = new LinkedList<>();
        for (Topic topic : topics) {
            if (topic.getWorkflowState().equals(Topic.STATE.AVAILABLE)) {
                topicInfoDtos.add(new TopicInfoDto(topic, user));
            }
        }

        return topicInfoDtos;
    }

    public List<TopicInfoDto> getAllTopicsInInstitution(
            String userId, Integer page, Integer pageSize
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        List<TopicInfoDto> topicInfoDtos = new LinkedList<>();

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("dateCreated").ascending());
        Page<Topic> topics = this.topicRepository.findByInstitutionAndWorkflowState(user.getInstitution(), Topic.STATE.AVAILABLE, pageable);

        for (Topic topic : topics) {
            topicInfoDtos.add(new TopicInfoDto(topic, topics, user));
        }

        return topicInfoDtos;
    }

    public List<ThreadInfoDto> getAllThreadsInTopic(
            String topicId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findById(topicId).orElseThrow(
                () -> new RuntimeException("Topic Not Found")
        );

        List<Thread> threads = this.threadRepository.findByTopic(topic);
        List<ThreadInfoDto> threadInfoDtos = new LinkedList<>();
        for (Thread thread : threads) {
            if (thread.getWorkflowState().equals(Thread.STATE.AVAILABLE)) {
                threadInfoDtos.add(new ThreadInfoDto(thread, this.minioClientUtils, user));
            }
        }

        return threadInfoDtos;
    }

    public List<ThreadInfoDto> getAllThreadsInTopic(
            String topicId, Integer page, Integer pageSize, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Topic topic = this.topicRepository.findById(topicId).orElseThrow(
                () -> new RuntimeException("Topic Not Found")
        );

        List<ThreadInfoDto> threadInfoDtos = new LinkedList<>();

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("dateCreated").ascending());
        Page<Thread> threads = this.threadRepository.findByTopicAndWorkflowState(topic, Thread.STATE.AVAILABLE, pageable);

        for (Thread thread : threads) {
            threadInfoDtos.add(new ThreadInfoDto(thread, this.minioClientUtils, threads, user));
        }

        return threadInfoDtos;
    }

    public List<CommentInfoDto> getAllCommentsInThread(
            String threadId, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Thread thread = this.threadRepository.findById(threadId).orElseThrow(
                () -> new RuntimeException("Thread Not Found")
        );

        List<Comment> comments = this.commentRepository.findByThreadAndParentComment(thread, null);
        List<CommentInfoDto> commentInfoDtos = new LinkedList<>();
        for (Comment comment : comments) {
            if (comment.getWorkflowState().equals(Comment.STATE.AVAILABLE)) {
                commentInfoDtos.add(new CommentInfoDto(comment, this.minioClientUtils, true, user));
            }
        }

        // sort the list by created time
        commentInfoDtos.sort((a, b) -> {
            if (a.dateCreated.before(b.dateCreated)) {
                return -1;
            }
            else if (a.dateCreated.after(b.dateCreated)) {
                return 1;
            }
            return 0;
        });

        return commentInfoDtos;
    }

    public List<CommentInfoDto> getAllCommentsInThread(
            String threadId, Integer page, Integer pageSize, String userId
    ) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        Thread thread = this.threadRepository.findById(threadId).orElseThrow(
                () -> new RuntimeException("Thread Not Found")
        );

        List<CommentInfoDto> commentInfoDtos = new LinkedList<>();

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("dateCreated").ascending());
        Page<Comment> comments = this.commentRepository.findByThreadAndParentCommentAndWorkflowState(thread, null, Comment.STATE.AVAILABLE, pageable);

        for (Comment comment : comments) {
            commentInfoDtos.add(new CommentInfoDto(comment, this.minioClientUtils, true, comments, user));
        }

        return commentInfoDtos;
    }
}
