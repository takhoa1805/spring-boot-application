package com.lreas.forum.dtos;

public class CreateCommentRequest {
    public String threadId;
    public String parentCommentId;
    public transient String userId;
    public String content;
}
