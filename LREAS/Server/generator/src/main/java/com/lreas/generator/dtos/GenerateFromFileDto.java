package com.lreas.generator.dtos;

import com.lreas.generator.models.ResourceAccessedBy;

import java.util.List;

public class GenerateFromFileDto {
    public String resourceId;
    public String newResourceName;
    public TYPE type;
    public List<Collaborator> collaborators;
    public transient String userId;
    public String outputFolderId;
    public Object[] args;

    public enum TYPE {
        QUIZ, SLIDES, NOTEBOOK
    }

    public static class Collaborator {
        public String id;
        public ResourceAccessedBy.ROLE role;
    }
}
