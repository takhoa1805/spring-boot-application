package com.lreas.file_management.utils;

import com.lreas.file_management.models.*;
import com.lreas.file_management.repositories.jpa.*;
import com.lreas.file_management.repositories.mongo.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Component
public class DocumentsUtils {
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAccessedByRepository resourceAccessedByRepository;
    private final FileRepository fileRepository;
    private final DocumentRepository documentRepository;


    @Autowired
    public DocumentsUtils(UserRepository userRepository,
                          ResourceRepository resourceRepository,
                          ResourceAccessedByRepository resourceAccessedByRepository,
                          FileRepository fileRepository,
                        DocumentRepository documentRepository
    ) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
        this.fileRepository = fileRepository;
        this.documentRepository = documentRepository;
    }

    public void checkCreateDocument(Resource parent, User user, String name) throws Exception{
        if (parent != null ){
            if (parent.getWorkflowState() != Resource.STATE.AVAILABLE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent resource not found");
            }
        }
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        if (name == null || name.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        if (parent != null ){
            ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(user.getId(), parent.getId());
            if (user.getRole() != User.ROLE.ADMIN && (permission == null || (permission.getRole() != ResourceAccessedBy.ROLE.OWNER && permission.getRole() != ResourceAccessedBy.ROLE.CONTRIBUTOR))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to update this folder");
            }
        }

    }

    public String createMongoDocument() {
        try {
            DocumentEntity newDocument = new DocumentEntity(
                    "<h2>Hello World from LREAS</h2><p>This is editable text. You can focus it and start typing.</p>"
            );

            DocumentEntity savedDocument = documentRepository.save(newDocument);
            return savedDocument.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create document in MongoDB.");
        }
    }

//    @Transactional(rollbackFor = Exception.class)
    public Resource createNewDocumentResource(String mongoId, Resource parent, String name, User user, String type) {
        try {

            Resource newResource = new Resource();
            newResource.setName(name);
            newResource.setIsFolder(false);
            newResource.setIsQuiz(false);
            newResource.setWorkflowState(Resource.STATE.AVAILABLE);
            newResource.setDateCreated(new Date());
            newResource.setDateUpdated(new Date());
            newResource.setParent(parent);
            newResource.setUser(user);
            newResource.setInstitution(user.getInstitution());
            newResource.setMongoId(mongoId);
            Resource savedResource = resourceRepository.save(newResource);


            ResourceAccessedBy permission = new ResourceAccessedBy();
            ResourceAccessedById resourceAccessedById = new ResourceAccessedById();
            resourceAccessedById.setResource(savedResource);
            resourceAccessedById.setUser(user);
            permission.setRole(ResourceAccessedBy.ROLE.OWNER);
            permission.setResource(resourceAccessedById);
            resourceAccessedByRepository.save(permission);

            File newFile = new File();
            newFile.setFile_path(mongoId);
            newFile.setResource(savedResource);
            newFile.setType(type);
            fileRepository.save(newFile);


            return savedResource;
        } catch (Exception e) {
            throw e;
        }
    }

}
