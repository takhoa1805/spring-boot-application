package com.lreas.forum.dtos;

import com.lreas.forum.models.Comment;
import com.lreas.forum.models.User;
import com.lreas.forum.utils.MinioClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.*;

public class CommentInfoDto {
    private final Logger logger = LoggerFactory.getLogger(CommentInfoDto.class);

    public String commentId;
    public String threadId;
    public String parentCommentId;
    public String authorName;
    public String authorAvtPath;
    public Date dateCreated;
    public Date dateModified;
    public String content;
    public transient String userId;
    public Boolean canEdit;
    public PaginationInfoDto<Comment> paginationInfo;
    public List<CommentInfoDto> childrenComments;

    public CommentInfoDto() {}

    public CommentInfoDto(
            Comment comment,
            MinioClientUtils minioClientUtils,
            User user
    ) {
        this.init(comment, minioClientUtils, false, user);
    }

    public CommentInfoDto(
            Comment comment,
            MinioClientUtils minioClientUtils,
            Boolean loadChildren,
            User user
    ) {
        this.init(comment, minioClientUtils, loadChildren, user);
    }

    public CommentInfoDto(
            Comment comment,
            MinioClientUtils minioClientUtils,
            Boolean loadChildren,
            Page<Comment> page,
            User user
    ) {
        this.init(comment, minioClientUtils, loadChildren, user);
        this.paginationInfo = new PaginationInfoDto<>(page);
    }

    private void init(
            Comment comment,
            MinioClientUtils minioClientUtils,
            Boolean loadChildren,
            User user
    ) {
        if (comment == null || minioClientUtils == null || user == null) {
            return;
        }

        this.commentId = comment.getId();
        this.threadId = comment.getThread().getId();

        if (comment.getParentComment() != null) {
            this.parentCommentId = comment.getParentComment().getId();
        }
        else {
            this.parentCommentId = null;
        }

        this.authorName = comment.getUser().getUsername();

        try {
            if (comment.getUser().getAvtPath() != null) {
                this.authorAvtPath = minioClientUtils.getUrl(comment.getUser().getAvtPath());
            }
            else {
                this.authorAvtPath = "";
            }
        }
        catch (Exception e) {
            logger.error("{}: {}", "Comment Info Dto: ", e.getMessage());
            this.authorAvtPath = "";
        }

        this.dateCreated = comment.getDateCreated();
        this.dateModified = comment.getDateModified();
        this.content = comment.getContent();

        if (loadChildren) {
            List<Comment> comments = comment.getChildComments();
            this.childrenComments = new LinkedList<>();
            for (Comment child : comments) {
                if (child.getWorkflowState().equals(Comment.STATE.AVAILABLE)) {
                    this.childrenComments.add(new CommentInfoDto(child, minioClientUtils, true, user));
                }
            }

            // sort the list by created time
            this.childrenComments.sort((a, b) -> {
                if (a.dateCreated.before(b.dateCreated)) {
                    return -1;
                }
                else if (a.dateCreated.after(b.dateCreated)) {
                    return 1;
                }
                return 0;
            });
        }
        else {
            this.childrenComments = Collections.emptyList();
        }

        this.canEdit = user.getRole().equals(User.ROLE.ADMIN) || comment.getUser().getId().equals(user.getId());
    }
}
