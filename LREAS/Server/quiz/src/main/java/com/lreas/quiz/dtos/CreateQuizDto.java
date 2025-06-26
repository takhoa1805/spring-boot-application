package com.lreas.quiz.dtos;

import com.lreas.quiz.models.ResourceAccessedBy;

import java.util.List;

public class CreateQuizDto {
    public String contentName;
    public List<Collaborator> collaborators;
    public String parentResourceId;
    public transient String userId;

    public static class Collaborator {
        public String id;
        public ResourceAccessedBy.ROLE role;
    }
}
