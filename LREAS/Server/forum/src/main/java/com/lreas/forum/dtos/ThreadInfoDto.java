package com.lreas.forum.dtos;

import com.lreas.forum.models.Comment;
import com.lreas.forum.models.Thread;
import com.lreas.forum.models.Topic;
import com.lreas.forum.models.User;
import com.lreas.forum.utils.MinioClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.*;

public class ThreadInfoDto {
    private final Logger logger = LoggerFactory.getLogger(ThreadInfoDto.class);

    public String threadId;
    public String content;
    public String topicId;
    public String subject;
    public String authorName;
    public String authorAvtPath;
    public Date createdTime;
    public Date updatedTime;
    public transient String userId;
    public Boolean canEdit;
    public PaginationInfoDto<Thread> paginationInfo;

    public ThreadInfoDto() {}

    public ThreadInfoDto(
            Thread thread,
            MinioClientUtils minioClientUtils,
            User user
    ) {
        this.init(thread, minioClientUtils, user);
    }

    public ThreadInfoDto(
            Thread thread,
            MinioClientUtils minioClientUtils,
            Page<Thread> page,
            User user
    ) {
        this.init(thread, minioClientUtils, user);
        this.paginationInfo = new PaginationInfoDto<>(page);
    }

    private void init(
            Thread thread,
            MinioClientUtils minioClientUtils,
            User user
    ) {
        if (thread == null || minioClientUtils == null || user == null) {
            return;
        }

        this.threadId = thread.getId();
        this.content = thread.getContent();
        this.topicId = thread.getTopic().getId();
        this.subject = thread.getSubject();
        this.authorName = thread.getUser().getUsername();

        try {
            if (thread.getUser().getAvtPath() != null) {
                this.authorAvtPath = minioClientUtils.getUrl(thread.getUser().getAvtPath());
            }
            else {
                this.authorAvtPath = "";
            }
        }
        catch (Exception e) {
            logger.error("{}: {}", "Thread Info Dto: ", e.getMessage());
            this.authorAvtPath = "";
        }

        this.createdTime = thread.getDateCreated();
        this.updatedTime = thread.getDateModified();

        this.canEdit = user.getRole().equals(User.ROLE.ADMIN) || thread.getUser().getId().equals(user.getId());
    }
}
