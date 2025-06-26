package com.lreas.forum.repositories;

import com.lreas.forum.models.Comment;
import com.lreas.forum.models.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByThreadAndParentComment(Thread thread, Comment parentComment);

    Page<Comment> findByThreadAndParentCommentAndWorkflowState(Thread thread, Comment parentComment, Comment.STATE workflowState, Pageable pageable);
    Comment findByIdAndWorkflowState(String id, Comment.STATE state);
}
