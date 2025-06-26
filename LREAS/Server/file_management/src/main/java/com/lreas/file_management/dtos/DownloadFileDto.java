package com.lreas.file_management.dtos;

import org.springframework.core.io.InputStreamResource;

public class DownloadFileDto {
    public String fileName;
    public InputStreamResource stream;
}
