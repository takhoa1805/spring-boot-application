package com.lreas.generator.controllers;

import com.lreas.generator.dtos.GenerateFromFileDto;
import com.lreas.generator.dtos.GenerateResourceDto;
import com.lreas.generator.dtos.GenerateResourceResponse;
import com.lreas.generator.dtos.QuizDtos.QuizResourcesDto;
import com.lreas.generator.services.GeneratorService;
import com.lreas.generator.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("")
@CrossOrigin(value = {
        "http://localhost:3000",
        "http://lvh.me",
        "http://lvh.me:3000",
        "https://lreas.takhoa.site",
        "http://lreas.takhoa.site",
        "http://localhost:80"
})
public class GeneratorController {
    private static Logger logger = LoggerFactory.getLogger(GeneratorController.class);

    private final GeneratorService generatorService;
    private final JwtUtils jwtUtils;

    @Autowired
    public GeneratorController(
            GeneratorService generatorService,
            JwtUtils jwtUtils
    ) {
        this.generatorService = generatorService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/resources/generate")
    public ResponseEntity<Object> generateFromResource(
            HttpServletRequest request,
            @RequestBody GenerateFromFileDto generateFromFileDto
    ) {
        try {
            generateFromFileDto.userId = jwtUtils.extractUserId(request);
            GenerateResourceResponse response = this.generatorService.generateFromResource(
                    generateFromFileDto
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resources/generate/stop/{resourceId}")
    public ResponseEntity<Object> generateFromResource(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            Boolean response = this.generatorService.stopGenerating(
                    resourceId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
