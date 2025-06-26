package com.lreas.file_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lreas.file_management.dtos.*;
import com.lreas.file_management.services.FileManagementService;

import com.lreas.file_management.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class FileManagementController {
    private static final Logger logger = LoggerFactory.getLogger(FileManagementController.class);

    private final FileManagementService fileManagementService;
    private final JwtUtils jwtUtils;

    @Autowired
    public FileManagementController(
            FileManagementService fileManagementService,
            JwtUtils jwtUtils
    ) {
        this.fileManagementService = fileManagementService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        logger.info(jwtUtils.extractUserId(request));
        return "test";
    }

    @PostMapping("/resources/folder")
    public ResponseEntity<Object> createFolder(
            HttpServletRequest request,
            @RequestBody NewContentUploadDto newContentUploadDto
    ) {
        try {
            newContentUploadDto.userId = jwtUtils.extractUserId(request);
            ResourceResponse response = this.fileManagementService.createFolderOrUploadFile(newContentUploadDto, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/resources/file")
    public ResponseEntity<Object> uploadFile(
            HttpServletRequest request,
            @RequestParam("metadata") String metadata,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            NewContentUploadDto newContentUploadDto = mapper.readValue(metadata, NewContentUploadDto.class);
            newContentUploadDto.userId = jwtUtils.extractUserId(request);

            ResourceResponse resourceResponse = this.fileManagementService.createFolderOrUploadFile(newContentUploadDto, file);
            return new ResponseEntity<>(resourceResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/resources/{resourceId}")
    public ResponseEntity<Object> cloneResource(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            ResourceResponse resourceResponse = this.fileManagementService.cloneResource(
                    resourceId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(resourceResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/resources/scope")
    public ResponseEntity<Object> getAllResourcesByScope(
            HttpServletRequest request,
            @RequestParam("scope") String scope
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            List<ResourceResponse> resourceResponses = this.fileManagementService.getAllResourcesByScope(userId, scope);
            return new ResponseEntity<>(resourceResponses, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folder/resources/available")
    public ResponseEntity<Object> getAllResourcesAvailableInFolder(
            HttpServletRequest request,
            @RequestParam(required = false) String folderId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            List<ResourceResponse> resourceResponses = this.fileManagementService.getAllResourcesAvailableInFolder(folderId, userId);
            return new ResponseEntity<>(resourceResponses, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folder/files/available")
    public ResponseEntity<Object> getAllFilesAvailableInFolder(
            HttpServletRequest request,
            @RequestParam(required = false) String folderId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            List<ResourceResponse> resourceResponses = this.fileManagementService.getAllFilesAvailableInFolder(folderId, userId);
            return new ResponseEntity<>(resourceResponses, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folder/resources/generating")
    public ResponseEntity<Object> getAllResourcesGeneratingInFolder(
            HttpServletRequest request,
            @RequestParam(required = false) String folderId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            List<ResourceResponse> resourceResponses = this.fileManagementService.getAllResourcesGeneratingInFolder(folderId, userId);
            return new ResponseEntity<>(resourceResponses, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/folder/resources/generating/{resourceId}")
    public ResponseEntity<Object> deleteResourceGeneratingInFolder(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            Boolean response = this.fileManagementService.deleteResourceGeneratingInFolder(resourceId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/file/{resourceId}/download")
    public ResponseEntity<Object> downloadFile(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            DownloadFileDto response = this.fileManagementService.downloadFile(resourceId, userId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.fileName + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(response.stream);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/file/{resourceId}/size")
    public ResponseEntity<Object> getFileSize(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            Long response = this.fileManagementService.getFileSize(resourceId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/folders/{scope}/all")
    public ResponseEntity<Object> getAllResourcesByParentAndScope(
            HttpServletRequest request,
            @RequestParam(required = false) String folderId,
            @PathVariable String scope
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            List<ResourceResponse> resourceResponses = this.fileManagementService.getAllResourcesByParentAndScope(folderId, userId, scope);
            return new ResponseEntity<>(resourceResponses, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resources/{resourceId}/name")
    public ResponseEntity<Object> updateResourceName(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody ResourceRenameRequest resourceRenameRequest
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);

            ResourceRenameResponse response = this.fileManagementService.updateResourceName(resourceRenameRequest, resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(e.getMessage()));
        }
    }

    @PutMapping("/resources/{resourceId}/parent")
    public ResponseEntity<Object> moveResource(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody ResourceMoveRequest resourceMoveRequest
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);

            ResourceMoveResponse response = this.fileManagementService.updateResourceParent(resourceMoveRequest, resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/resources/{resourceId}/owner")
    public ResponseEntity<Object> changeOwner(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody ChangeResourceOwnerRequest changeResourceOwnerRequest
    ) {
        try {

//            ChangeResourceOwnerRequest changeResourceOwnerRequest = new ChangeResourceOwnerRequest("1fdc0eb7-55b0-46d0-9893-4d432d3a5340");

            String userId = jwtUtils.extractUserId(request);

            ChangeResourceOwnerResponse response = this.fileManagementService.updateResourceOwner(changeResourceOwnerRequest, resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resources/{resourceId}/contributor")
    public ResponseEntity<Object> addContributor(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody AddAccessRequest addAccessRequest
    ) {
        try {


            String userId = jwtUtils.extractUserId(request);

            AddAccessResponse response = this.fileManagementService.updateResourceContributor(addAccessRequest, resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resources/{resourceId}/viewer")
    public ResponseEntity<Object> addViewer(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody AddAccessRequest addAccessRequest
    ) {
        try {


            String userId = jwtUtils.extractUserId(request);

            AddAccessResponse response = this.fileManagementService.updateResourceViewer(addAccessRequest, resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/resources/{resourceId}/restore")
    public ResponseEntity<Object> restoreResource(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {


            String userId = jwtUtils.extractUserId(request);

            RestoreResourceResponse response = this.fileManagementService.restoreResource(resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //
    @DeleteMapping("/resources/{resourceId}/trash")
    public ResponseEntity<Object> moveToTrash(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);

            DeleteResourceResponse response = this.fileManagementService.moveToTrash(resourceId, userId);

            if (!response.getSuccess()
            ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/resources/{resourceId}/permanent")
    public ResponseEntity<Object> deletePermanent(
            HttpServletRequest request,
            @PathVariable String resourceId
    ) {
        try {


            String userId = jwtUtils.extractUserId(request);

            DeleteResourceResponse response = this.fileManagementService.deletePermanent(resourceId, userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/resources/{resourceId}/access")
    public ResponseEntity<Object> deleteAccess(
            HttpServletRequest request,
            @PathVariable String resourceId,
            @RequestBody DeleteAccessRequest deleteAccessRequest
    ) {
        try {


            String userId = jwtUtils.extractUserId(request);

            DeleteAccessResponse response = this.fileManagementService.deleteAccess(resourceId, deleteAccessRequest ,userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/resources/documents")
    public ResponseEntity<Object> createDocument(
            HttpServletRequest request,
            @RequestBody NewDocumentsRequest newDocumentsRequest
    ) {
        try {

            String userId = jwtUtils.extractUserId(request);
            String parentId = newDocumentsRequest.getParentId();
            String name = newDocumentsRequest.getName();
            String type = newDocumentsRequest.getType();


            NewDocumentsResponse response = this.fileManagementService.createDocument(parentId, name, type ,userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/resources/documents/{mongoId}/permissions")
    public ResponseEntity<Object> verifyPermission(
            HttpServletRequest request,
            @PathVariable String mongoId // Extracts <mongoId> from the URL
    ){
        try {

            String userId = jwtUtils.extractUserId(request);

            PermissionVerificationResponse response = this.fileManagementService.getPermissions(mongoId,userId);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of("Permission verification failed"));
            }

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
