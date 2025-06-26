package com.lreas.file_management.dtos;

import java.util.List;

public class NewContentUploadDto {
    public String contentName;
    public CollaboratorDto[] collaborators;
    public String parentResourceId;
    public String contentType;
    public Boolean isFolder;
    public Boolean isQuiz;
    public Boolean isPublic;
    public transient String userId;
    public String fileType;
}
