package com.lreas.generator.services;

import com.lreas.generator.dtos.GenerateFromFileDto;
import com.lreas.generator.dtos.GenerateResourceResponse;
import com.lreas.generator.dtos.NewDocumentsResponse;
import com.lreas.generator.dtos.QuizDtos.QuizResourcesDto;
import com.lreas.generator.models.*;
import com.lreas.generator.repositories.jpa.*;
import com.lreas.generator.utils.DocumentsUtils;
import com.lreas.generator.utils.GeminiUtils;
import com.lreas.generator.utils.MinioClientUtils;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional(rollbackOn = {Exception.class})
public class GeneratorServiceImpl implements GeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(GeneratorServiceImpl.class);
    private final int CHUNK_SIZE = 5;

    private final GeminiUtils geminiUtils;
    private final DocumentsUtils documentsUtils;
    private final ResourceAccessedByRepository resourceAccessedByRepository;
    private final ResourceRepository resourceRepository;
    private final MinioClientUtils minioClientUtils;
    private final FileRepository fileRepository;
    private final GrpcQuizServiceGrpcClient grpcQuizServiceGrpcClient;

    private final Set<String> stoppingResources = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public GeneratorServiceImpl(
            GeminiUtils geminiUtils,
            DocumentsUtils documentsUtils,
            ResourceAccessedByRepository resourceAccessedByRepository,
            ResourceRepository resourceRepository,
            MinioClientUtils minioClientUtils,
            FileRepository fileRepository,
            GrpcQuizServiceGrpcClient grpcQuizServiceGrpcClient
    ) {
        this.geminiUtils = geminiUtils;
        this.documentsUtils = documentsUtils;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
        this.resourceRepository = resourceRepository;
        this.minioClientUtils = minioClientUtils;
        this.fileRepository = fileRepository;
        this.grpcQuizServiceGrpcClient = grpcQuizServiceGrpcClient;
    }

    public GenerateResourceResponse generateFromResource(
            GenerateFromFileDto generateFromFileDto
    ) throws Exception {
        // get resource and check its availability
        Resource resource = resourceRepository.findById(generateFromFileDto.resourceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found")
        );
        if (resource.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) != 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
        }
        if (resource.getIsFolder() || resource.getIsQuiz()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource Is Not File");
        }

        // check permission
        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                generateFromFileDto.userId, resource.getId()
        );
        if (accessedBy == null || accessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.VIEWER) > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Do Not Have Permission");
        }

        // get output folder
        if (generateFromFileDto.outputFolderId != null) {
            Resource folderResource = resourceRepository.findById(generateFromFileDto.outputFolderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder Not Found")
            );
            if (folderResource.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) != 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder Not Found");
            }
            if (!folderResource.getIsFolder()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource Is Not Folder");
            }
        }

        InputStream fileInputStream;

        // get file
        File file = fileRepository.findByResource(resource);
        if (file != null) {
            // load file from object storage
            fileInputStream = this.minioClientUtils.getFile(file.getFile_path());
            if (fileInputStream == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File Not Found");
            }
        }
        else {
            // check if resource is document type
            if (resource.getMongoId() != null) {
                String content = this.documentsUtils.getDocuments(resource.getMongoId());
                fileInputStream = new ByteArrayInputStream(content.getBytes(
                        StandardCharsets.UTF_8
                ));
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File Not Found");
            }
        }

        // setup return data
        GenerateResourceResponse generateResourceResponse = new GenerateResourceResponse();

        // handle different generation types
        if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.QUIZ)) {
            // create new quiz
            QuizResourcesDto createdQuiz = this.grpcQuizServiceGrpcClient.createQuiz(generateFromFileDto);

            generateResourceResponse.resourceId = createdQuiz.resourceId;

            // set the resource state to pending
            Resource createdQuizResource = resourceRepository.findById(createdQuiz.resourceId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot Create Quiz")
            );
            createdQuizResource.setWorkflowState(Resource.STATE.GENERATING);
            resourceRepository.save(createdQuizResource);

            this.geminiUtils.createResource(
                fileInputStream,
                file,
                generateFromFileDto,
                createdQuizResource, createdQuiz
            );
        }
        else if (generateFromFileDto.type.equals(GenerateFromFileDto.TYPE.SLIDES)) {
            this.geminiUtils.createResource(
                fileInputStream,
                file,
                generateFromFileDto
            );
        }
        else {
            NewDocumentsResponse documentResponse = this.documentsUtils.createDocument(
                    generateFromFileDto.outputFolderId, generateFromFileDto.newResourceName,
                    generateFromFileDto.userId
            );

            if (documentResponse.resourceId != null) {
                // get new resource
                Resource createdResource = resourceRepository.findById(documentResponse.resourceId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot Create Resource")
                );
                createdResource.setWorkflowState(Resource.STATE.GENERATING);
                resourceRepository.save(createdResource);

                this.geminiUtils.createResource(
                    fileInputStream,
                    file,
                    generateFromFileDto,
                    createdResource
                );
            }
        }

        return generateResourceResponse;
    }

    public Boolean stopGenerating(
            String resourceId, String userId
    ) {
        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                userId, resourceId
        );
        if (accessedBy == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Do Not Have Permission");
        }
        if (accessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.VIEWER) >= 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Do Not Have Permission");
        }

        // add to stopping queue
        this.stoppingResources.add(resourceId);

        return true;
    }
}
