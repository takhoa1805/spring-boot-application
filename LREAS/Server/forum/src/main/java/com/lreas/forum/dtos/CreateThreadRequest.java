package com.lreas.forum.dtos;

public class CreateThreadRequest {
    public String content;
    public String topicId;
    public String subject;
    public transient String userId;
}
