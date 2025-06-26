package com.lreas.generator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lreas.generator.configs.DocumentsConfig;
import com.lreas.generator.dtos.NewDocumentsRequest;
import com.lreas.generator.dtos.NewDocumentsResponse;
import com.lreas.generator.models.Resource;
import com.lreas.generator.repositories.jpa.ResourceRepository;
import com.lreas.generator.services.GrpcFileManagementServiceGrpcClient;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DocumentsUtils {
    private final Logger logger = LoggerFactory.getLogger(DocumentsUtils.class);

    private final DocumentsConfig documentsConfig;

    private final ResourceRepository resourceRepository;

    private final GrpcFileManagementServiceGrpcClient grpcFileManagementServiceGrpcClient;

    @Autowired
    public DocumentsUtils(
            DocumentsConfig documentsConfig,
            ResourceRepository resourceRepository,
            GrpcFileManagementServiceGrpcClient grpcFileManagementServiceGrpcClient
    ) {
        this.documentsConfig = documentsConfig;
        this.resourceRepository = resourceRepository;
        this.grpcFileManagementServiceGrpcClient = grpcFileManagementServiceGrpcClient;
    }

    public boolean saveDocuments(
            String resourceId, String content
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(content, headers);

        // get mongo id of resource
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found")
        );

        if (resource.getMongoId() == null) {
            return false;
        }

        String url = documentsConfig.getBaseUrl() + "/" + resource.getMongoId();

        ResponseEntity<String> response = new RestTemplate().exchange(
                url, HttpMethod.POST, entity, String.class
        );

        return response.getStatusCode() == HttpStatus.OK;
    }

    public String getDocuments(String resourceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        // get mongo id of resource
        Resource resource = resourceRepository.findByMongoId(resourceId);
        if (resource == null) {
            return null;
        }

        String url = documentsConfig.getBaseUrl() + "/" + resource.getMongoId();

        ResponseEntity<String> response = new RestTemplate().exchange(
                url, HttpMethod.GET, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(Objects.requireNonNull(response.getBody()));

            return json.getString("content");
        }
        return null;
    }

    public NewDocumentsResponse createDocument(
            String parentId, String name,
            String userId
    ) {
        try {
            NewDocumentsRequest newDocumentsRequest = new NewDocumentsRequest();
            newDocumentsRequest.parentId = parentId;
            newDocumentsRequest.name = name;
            newDocumentsRequest.type = "DOCUMENT";
            newDocumentsRequest.userId = userId;

            return this.grpcFileManagementServiceGrpcClient.createDocument(
                    newDocumentsRequest
            );
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
