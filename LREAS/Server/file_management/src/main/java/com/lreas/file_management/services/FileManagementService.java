package com.lreas.file_management.services;

import com.lreas.file_management.dtos.*;

import com.lreas.file_management.dtos.ResourceRenameRequest;
import com.lreas.file_management.dtos.ResourceRenameResponse;
import com.lreas.file_management.dtos.ResourceResponse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileManagementService {
    ResourceResponse createFolderOrUploadFile(NewContentUploadDto newContentUploadDto, MultipartFile file) throws Exception;
    ResourceResponse cloneResource(String resourceId, String userId) throws Exception;
    List<ResourceResponse> getAllResourcesByScope(String userId, String scope);
    List<ResourceResponse> getAllResourcesAvailableInFolder(String parentId, String userId);
    List<ResourceResponse> getAllFilesAvailableInFolder(String parentId, String userId);
    List<ResourceResponse> getAllResourcesByParentAndScope(String parentId, String userId, String scope);
    List<ResourceResponse> getAllResourcesGeneratingInFolder(String parentId, String userId);
    Boolean deleteResourceGeneratingInFolder(String resourceId, String userId);
    DownloadFileDto downloadFile(String resourceId, String userId) throws Exception;
    Long getFileSize(String resourceId, String userId) throws Exception;
    ResourceRenameResponse updateResourceName(ResourceRenameRequest request, String resourceId, String userId) throws Exception;
    ResourceMoveResponse updateResourceParent(ResourceMoveRequest request, String resourceId, String userId) throws Exception;
    ChangeResourceOwnerResponse updateResourceOwner(ChangeResourceOwnerRequest request, String resourceId, String userId) throws Exception;
    AddAccessResponse updateResourceContributor(AddAccessRequest request, String resourceId, String userId) throws Exception;
    AddAccessResponse updateResourceViewer(AddAccessRequest request, String resourceId, String userId) throws Exception;
    RestoreResourceResponse restoreResource(String resourceId, String userId) throws Exception;
    DeleteResourceResponse moveToTrash(String resourceId, String userId) throws Exception;
    DeleteResourceResponse deletePermanent(String resourceId, String userId) throws Exception;
    DeleteAccessResponse deleteAccess(String resourceId, DeleteAccessRequest request, String userId) throws Exception;
    NewDocumentsResponse createDocument(String parentId, String name, String type, String userId) throws Exception;
    PermissionVerificationResponse getPermissions(String mongoId, String userId) throws Exception;
}
