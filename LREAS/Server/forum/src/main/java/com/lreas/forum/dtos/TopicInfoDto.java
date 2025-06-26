package com.lreas.forum.dtos;

import com.lreas.forum.models.Topic;
import com.lreas.forum.models.User;
import org.springframework.data.domain.Page;

import java.util.Date;

public class TopicInfoDto {
    public String topicId;
    public String topicName;
    public String authorName;
    public Date createdTime;
    public Date updatedTime;
    public transient String userId;
    public Boolean canEdit;
    public PaginationInfoDto<Topic> paginationInfo;

    public TopicInfoDto() {}

    public TopicInfoDto(
            Topic topic, User user
    ) {
        this.init(topic, user);
    }

    public TopicInfoDto(
            Topic topic,
            Page<Topic> page,
            User user
    ) {
        this.init(topic, user);
        this.paginationInfo = new PaginationInfoDto<>(page);
    }

    private void init(
            Topic topic, User user
    ) {
        if (topic == null || user == null) {
            return;
        }

        this.topicId = topic.getId();
        this.topicName = topic.getTitle();
        this.authorName = topic.getUser().getUsername();
        this.createdTime = topic.getDateCreated();
        this.updatedTime = topic.getDateUpdated();

        this.canEdit = user.getRole().equals(User.ROLE.ADMIN) || topic.getUser().getId().equals(user.getId());
    }
}
