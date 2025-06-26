package com.lreas.quiz.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ModifyMediaDto {
    public String resourceId;
    public transient List<MultipartFile> files;
    public List<String> questionsId;
    public List<Double> widths;
    public List<Double> heights;
    public transient String userId;
}
