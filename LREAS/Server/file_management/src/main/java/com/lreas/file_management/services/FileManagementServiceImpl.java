package com.lreas.file_management.services;

import com.lreas.file_management.dtos.*;

import com.lreas.file_management.models.*;

import com.lreas.file_management.repositories.jpa.*;
import com.lreas.file_management.repositories.mongo.AnswerRepository;
import com.lreas.file_management.repositories.mongo.QuestionRepository;
import com.lreas.file_management.repositories.mongo.QuizVersionRepository;
import com.lreas.file_management.utils.DocumentsUtils;
import com.lreas.file_management.utils.MinioClientUtils;
import com.lreas.file_management.utils.ResourceDeleteUtils;
import com.lreas.file_management.utils.ResourceUpdateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(rollbackOn = {Exception.class})
public class FileManagementServiceImpl implements FileManagementService {
    private final ResourceRepository resourceRepository;
    private final ResourceAccessedByRepository resourceAccessedByRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final FileRepository fileRepository;
    private final MinioClientUtils minioClientUtils;
    private final ResourceUpdateUtils resourceUpdateUtils;
    private final ResourceDeleteUtils resourceDeleteUtils;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizVersionRepository quizVersionRepository;
    private final DocumentsUtils documentsUtils;
    private final EntityManager entityManager;

    @Autowired
    public FileManagementServiceImpl(
            ResourceRepository resourceRepository,
            ResourceAccessedByRepository resourceAccessedByRepository,
            UserRepository userRepository,
            QuizRepository quizRepository,
            FileRepository fileRepository,
            MinioClientUtils minioClientUtils,
            ResourceUpdateUtils resourceUpdateUtils,
            ResourceDeleteUtils resourceDeleteUtils,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            QuizVersionRepository quizVersionRepository,
            DocumentsUtils documentsUtils,
            EntityManager entityManager
    ) {
        this.resourceRepository = resourceRepository;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.fileRepository = fileRepository;
        this.minioClientUtils = minioClientUtils;
        this.resourceUpdateUtils = resourceUpdateUtils;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.resourceDeleteUtils = resourceDeleteUtils;
        this.quizVersionRepository = quizVersionRepository;
        this.documentsUtils = documentsUtils;
        this.entityManager = entityManager;
    }

    private void setPermissionsForParentCollabs(
            Resource parent, Resource resource
    ) {
        if (parent == null || resource == null) {
            return;
        }

        // check whether entity is persisted or not
        if (!this.entityManager.contains(parent)) {
            return;
        }
        if (!this.entityManager.contains(resource)) {
            resourceRepository.save(resource);
        }

        List<ResourceAccessedBy> accessedByList = resourceAccessedByRepository.findByResource(parent.getId());
        if (accessedByList == null) {
            return;
        }

        for (ResourceAccessedBy accessedBy : accessedByList) {
            // check collab permission on resource
            ResourceAccessedBy resourceAccessedBy = resourceAccessedByRepository.findByUserAndResource(
                    accessedBy.getResource().getUser().getId(), resource.getId()
            );
            if (resourceAccessedBy != null) {
                continue;
            }

            ResourceAccessedBy parentAccess = new ResourceAccessedBy();

            ResourceAccessedById resourceAccessedByIdTemp = new ResourceAccessedById();
            resourceAccessedByIdTemp.setUser(accessedBy.getResource().getUser());
            resourceAccessedByIdTemp.setResource(resource);

            parentAccess.setResource(resourceAccessedByIdTemp);

            if (accessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.OWNER) == 0) {
                parentAccess.setRole(ResourceAccessedBy.ROLE.CONTRIBUTOR);
            }
            else {
                parentAccess.setRole(accessedBy.getRole());
            }

            resourceAccessedByRepository.save(parentAccess);
        }
    }

    public ResourceResponse createFolderOrUploadFile(
            NewContentUploadDto newContentUploadDto,
            MultipartFile file
    ) throws Exception {
        Date date = new Date();
        Resource resource = new Resource();

        if (newContentUploadDto.parentResourceId != null) {
            ResourceAccessedBy resourceAccessedBy = this.resourceAccessedByRepository.findByUserAndResource(
                    newContentUploadDto.userId, newContentUploadDto.parentResourceId
            );
            if (resourceAccessedBy == null || resourceAccessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.VIEWER) == 0) {
                return null;
            }

            // get parent resource
            Resource parentResource = resourceAccessedBy.getResource().getResource();

            // get current user
            User user = resourceAccessedBy.getResource().getUser();

            resource.setParent(parentResource);
            resource.setUser(user);
            resource.setInstitution(user.getInstitution());

            // set permission for parent and all contributors in this resource
            this.setPermissionsForParentCollabs(parentResource, resource);

            // update modify time for parent
            parentResource.setDateUpdated(date);
            resourceRepository.save(parentResource);
        } else {
            User user = userRepository.findById(newContentUploadDto.userId).orElseThrow(
                    () -> new RuntimeException("User not found")
            );

            resource.setParent(null);
            resource.setUser(user);
            resource.setInstitution(user.getInstitution());
        }

        // save resource
        resource.setDateUpdated(date);
        resource.setWorkflowState(Resource.STATE.AVAILABLE);
        resource.setDateCreated(date);
        resource.setName(newContentUploadDto.contentName);
        resource.setIsFolder(newContentUploadDto.isFolder);
        resource.setIsQuiz(newContentUploadDto.isQuiz);
        resourceRepository.save(resource);

        // add to resource_accessed_by table
        ResourceAccessedById resourceAccessedById = new ResourceAccessedById();
        resourceAccessedById.setResource(resource);
        resourceAccessedById.setUser(resource.getUser());

        ResourceAccessedBy resourceAccessedByTemp = new ResourceAccessedBy();
        resourceAccessedByTemp.setResource(resourceAccessedById);
        resourceAccessedByTemp.setRole(ResourceAccessedBy.ROLE.OWNER);
        resourceAccessedByRepository.save(resourceAccessedByTemp);

        // add to resource_accessed_by table for collaborators
        List<CollaboratorDto> collaboratorDtos = new LinkedList<>();
        for (CollaboratorDto collab : newContentUploadDto.collaborators) {
            ResourceAccessedById tempId = new ResourceAccessedById();
            tempId.setResource(resource);

            Optional<User> currUserOp = userRepository.findById(collab.id);
            if (currUserOp.isEmpty()) {
                continue;
            }
            tempId.setUser(currUserOp.get());

            // add to collaborators
            collaboratorDtos.add(
                new CollaboratorDto(
                    currUserOp.get(), collab.role
                )
            );

            ResourceAccessedBy tempAccessedBy = new ResourceAccessedBy();
            tempAccessedBy.setResource(tempId);
            tempAccessedBy.setRole(collab.role);
            resourceAccessedByRepository.save(tempAccessedBy);
        }

        // if upload file
        if (file != null) {
            File fileEntity = new File();
            fileEntity.setType(newContentUploadDto.fileType);
            fileEntity.setFile_path("");
            fileEntity.setResource(resource);
            fileRepository.save(fileEntity);

            minioClientUtils.uploadFile(
                    fileEntity.getId(),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

            // verify upload
            minioClientUtils.assertExistence(fileEntity.getId());

            fileEntity.setFile_path(
                    fileEntity.getId()
            );
            fileRepository.save(fileEntity);
        }

        // set up return dto
        return new ResourceResponse(
                resource, ResourceAccessedBy.ROLE.OWNER,
                collaboratorDtos
        );
    }

    public ResourceResponse cloneResource(
            String resourceId, String userId
    ) throws Exception {
        Date date = new Date();

        // get user
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        // get original resource
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new RuntimeException("Resource not found")
        );

        // check parent role
        Resource parent = resource.getParent();
        if (parent != null) {
            ResourceAccessedBy parentRole = resourceAccessedByRepository.findByUserAndResource(
                    userId, parent.getId()
            );
            if (parentRole == null || parentRole.getRole().compareTo(ResourceAccessedBy.ROLE.VIEWER) == 0) {
                throw new RuntimeException("Do Not Have Permission!");
            }

            // update parent
            parent.setDateUpdated(date);
            resourceRepository.save(resource);

            // set permission for parent and all contributors in this resource
            this.setPermissionsForParentCollabs(parent, resource);
        }

        // create new clone resource
        Resource resourceClone = new Resource();
        resourceClone.setDateUpdated(date);
        resourceClone.setWorkflowState(Resource.STATE.AVAILABLE);
        resourceClone.setDateCreated(date);
        resourceClone.setName(resource.getName());
        resourceClone.setIsFolder(resource.getIsFolder());
        resourceClone.setIsQuiz(resource.getIsQuiz());
        resourceClone.setParent(parent);
        resourceClone.setUser(user);
        resourceClone.setInstitution(user.getInstitution());
        resourceRepository.save(resourceClone);

        // set role for the cloned resource
        ResourceAccessedBy resourceAccessedBy = new ResourceAccessedBy();

        ResourceAccessedById resourceAccessedById = new ResourceAccessedById();
        resourceAccessedById.setResource(resourceClone);
        resourceAccessedById.setUser(user);

        resourceAccessedBy.setResource(resourceAccessedById);
        resourceAccessedBy.setRole(ResourceAccessedBy.ROLE.OWNER);
        resourceAccessedByRepository.save(resourceAccessedBy);

        // clone if resource is a file
        if (!resource.getIsFolder()) {
            if (resource.getIsQuiz()) {
                // get original quiz
                Quiz quizOrigin = quizRepository.findByResource(resource);
                if (quizOrigin == null) {
                    throw new RuntimeException("Quiz Not Found");
                }

                // get latest version of original quiz
                QuizVersion quizVersionOriginal = quizVersionRepository.findById(
                        quizOrigin.getQuizVersionId()
                ).orElseThrow(
                        () -> new RuntimeException("Quiz Version Not Found")
                );

                // create a clone quiz
                Quiz quiz = new Quiz();
                quiz.setResource(resourceClone);
                quizRepository.save(quiz);

                // create a clone quiz_version
                QuizVersion quizVersion = new QuizVersion();
                quizVersion.setQuizId(quizVersionOriginal.getId());
                quizVersion.setDateUpdated(date);
                quizVersion.setIsGame(quizVersionOriginal.getIsGame());
                quizVersion.setDateStarted(quizVersionOriginal.getDateStarted());
                quizVersion.setDateEnded(quizVersionOriginal.getDateEnded());
                quizVersion.setMaxPlayers(quizVersionOriginal.getMaxPlayers());
                quizVersion.setTimeLimit(quizVersionOriginal.getTimeLimit());
                quizVersion.setTitle(quizVersionOriginal.getTitle());
                quizVersion.setShowCorrectAnswer(quizVersionOriginal.getShowCorrectAnswer());
                quizVersion.setAllowedAttempts(quizVersionOriginal.getAllowedAttempts());
                quizVersion.setDescription(quizVersionOriginal.getDescription());
                quizVersion.setShuffleAnswers(quizVersionOriginal.getShuffleAnswers());
                quizVersionRepository.save(quizVersion);

                // clone all questions in this quiz
                List<Question> questionClones = quizVersionOriginal.getQuestions().stream().map(question -> {
                    Question questionClone = new Question();
                    questionClone.setQuizVersion(quizVersion);
                    questionClone.setTimeLimit(question.getTimeLimit());
                    questionClone.setDateUpdated(date);
                    questionClone.setScore(question.getScore());
                    questionClone.setDateCreated(date);
                    questionClone.setPosition(question.getPosition());
                    questionClone.setTitle(question.getTitle());
                    questionClone.setImage(question.getImage());
                    questionRepository.save(questionClone);

                    // clone all answers for this question
                    List<Answer> answerClones = questionClone.getAnswers().stream().map(answer -> {
                        Answer answerClone = new Answer();
                        answerClone.setQuestion(questionClone);
                        answerClone.setText(answer.getText());
                        answerClone.setIsCorrect(answer.getIsCorrect());
                        answerRepository.save(answerClone);
                        return answerClone;
                    }).toList();

                    // update answers for this question
                    questionClone.setAnswers(answerClones);
                    questionRepository.save(questionClone);

                    return questionClone;
                }).toList();
                quizVersion.setQuestions(questionClones);
                quizVersionRepository.save(quizVersion);
            }
            else {
                // get original file
                File fileOrigin = fileRepository.findByResource(resource);
                if (fileOrigin == null) {
                    throw new RuntimeException("File Not Found");
                }

                // create a clone file
                File fileEntity = new File();
                fileEntity.setType(fileOrigin.getType());
                fileEntity.setFile_path("");
                fileEntity.setResource(resourceClone);
                fileRepository.save(fileEntity);

                // create a clone object in the storage
                this.minioClientUtils.cloneObject(
                        fileOrigin.getFile_path(), fileEntity.getId()
                );

                // set file path
                fileEntity.setFile_path(fileEntity.getId());
                fileRepository.save(fileEntity);
            }
        }

        // set the return value
        return new ResourceResponse(
                resourceClone, ResourceAccessedBy.ROLE.OWNER,
                Collections.emptyList()
        );
    }

    public List<ResourceResponse> getAllResourcesByScope(
            String userId, String scope
    ) {
        List<ResourceResponse> resourceResponses = new LinkedList<>();
        List<Resource> resources = null;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        switch (scope.toLowerCase()) {
            case "owned":
                resources = user.getResources();
                break;
            case "shared":
                resources = new LinkedList<>();
                List<ResourceAccessedBy> lstContributor = resourceAccessedByRepository.findByUserAndRole(
                        userId, ResourceAccessedBy.ROLE.CONTRIBUTOR
                );
                List<ResourceAccessedBy> lstViewer = resourceAccessedByRepository.findByUserAndRole(
                        userId, ResourceAccessedBy.ROLE.VIEWER
                );

                List<Resource> finalResources = resources;
                Stream.concat(lstContributor.stream(), lstViewer.stream()).forEach(r -> {
                    if (r.getRole().compareTo(ResourceAccessedBy.ROLE.OWNER) > 0) {
                        finalResources.add(r.getResource().getResource());
                    }
                });

                break;
            case "trashed":
                resources = user.getResources().stream().filter(
                        resource -> resource.getWorkflowState().compareTo(Resource.STATE.TRASHED) == 0
                ).collect(Collectors.toList());
                break;
        }

        Optional.ofNullable(resources).ifPresent(resourceList -> resourceList.forEach(resource -> {
            ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(userId, resource.getId());
            ResourceResponse resourceResponse = new ResourceResponse(
                resource, accessedBy.getRole(),
                resource.getResourceAccessedBy().stream().map(
                    r -> {
                        if (
                                r.getRole().compareTo(ResourceAccessedBy.ROLE.OWNER) != 0
                                && r.getRole().compareTo(ResourceAccessedBy.ROLE.NO_ACCESS) != 0
                        ) {
                            return new CollaboratorDto(
                                    r.getResource().getUser(),
                                    r.getRole()
                            );
                        }
                        return null;
                    }
                ).filter(Objects::nonNull).collect(Collectors.toList())
            );
            resourceResponses.add(resourceResponse);
        }));

        return resourceResponses;
    }

    private List<ResourceResponse> getAllResourcesInFolder(
            String parentId, String userId,
            Resource.STATE... states
    ) {
        List<Resource> resources;
        if (parentId != null && !parentId.isEmpty()) {
            Resource parentResource = resourceRepository.findById(parentId).orElseThrow(
                    () -> new RuntimeException("Parent Resource Not Found")
            );

            if (parentResource.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) != 0) {
                throw new RuntimeException("Parent Resource Not Found");
            }

            resources = parentResource.getChildren();
        }
        else {
            resources = resourceRepository.findByParent(null);
        }

        // declare a map for states
        Set<Resource.STATE> stateSet = new HashSet<>(Arrays.asList(states));

        List<ResourceResponse> result = new LinkedList<>();
        for (Resource c : resources) {
            if (!stateSet.contains(c.getWorkflowState())) {
                continue;
            }

            List<CollaboratorDto> collaboratorDtos = new LinkedList<>();
            for (ResourceAccessedBy ra : c.getResourceAccessedBy()) {
                if (
                        ra.getRole().compareTo(ResourceAccessedBy.ROLE.OWNER) == 0 ||
                                ra.getRole().compareTo(ResourceAccessedBy.ROLE.NO_ACCESS) == 0
                ) {
                    continue;
                }
                collaboratorDtos.add(new CollaboratorDto(
                        ra.getResource().getUser(),
                        ra.getRole()
                ));
            }

            ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(userId, c.getId());
            if (accessedBy == null) {
                continue;
            }

            result.add(new ResourceResponse(
                    c, accessedBy.getRole(),
                    collaboratorDtos
            ));
        }

        return result;
    }

    public List<ResourceResponse> getAllResourcesAvailableInFolder(String parentId, String userId) {
        return this.getAllResourcesInFolder(parentId, userId, Resource.STATE.AVAILABLE);
    }

    public List<ResourceResponse> getAllFilesAvailableInFolder(String parentId, String userId) {
        return this.getAllResourcesAvailableInFolder(parentId, userId).stream().filter(
            r -> !r.isFolder && !r.isQuiz
        ).toList();
    }

    public List<ResourceResponse> getAllResourcesByParentAndScope(
            String parentId, String userId, String scope
    ) {
        return this.getAllResourcesByScope(userId, scope).stream().filter(
            r -> {
                if (scope.equals("trashed")) {
                    return true;
                }

                if (parentId == null || parentId.isEmpty()) {
                    if (r.parentId == null) {
                        Resource temp = resourceRepository.findByResourceId(r.id);
                        return temp != null && temp.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) == 0;
                    }
                    else {
                        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                                userId, r.parentId
                        );

                        if (accessedBy == null) {
                            return true;
                        }
                        else {
                            return accessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.NO_ACCESS) == 0;
                        }
                    }
                }
                else {
                    if (parentId.equals(r.parentId)) {
                        Resource temp = resourceRepository.findByResourceId(r.id);
                        return temp != null && temp.getWorkflowState().compareTo(Resource.STATE.AVAILABLE) == 0;
                    }
                }
                return false;
            }
        ).toList();
    }

    public List<ResourceResponse> getAllResourcesGeneratingInFolder(String parentId, String userId) {
        return this.getAllResourcesInFolder(parentId, userId, Resource.STATE.GENERATING, Resource.STATE.FAILED);
    }

    public Boolean deleteResourceGeneratingInFolder(String resourceId, String userId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new RuntimeException("Resource Not Found")
        );
        if (!resource.getWorkflowState().equals(Resource.STATE.GENERATING) && !resource.getWorkflowState().equals(Resource.STATE.FAILED)) {
            throw new RuntimeException("Resource Is Not Generating");
        }

        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(userId, resourceId);
        if (accessedBy == null) {
            return false;
        }
        if (accessedBy.getRole().compareTo(ResourceAccessedBy.ROLE.CONTRIBUTOR) > 0) {
            throw new RuntimeException("User Is Not Allowed");
        }

        resource.setWorkflowState(Resource.STATE.DELETED);
        resource.setDateUpdated(new Date());
        resourceRepository.save(resource);
        return true;
    }

    public DownloadFileDto downloadFile(
            String resourceId, String userId
    ) throws Exception {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new RuntimeException("Resource Not Found")
        );
        if (resource.getIsFolder() || resource.getIsQuiz()) {
            throw new RuntimeException("Resource Is Not File");
        }

        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                userId, resourceId
        );
        if (accessedBy == null) {
            throw new RuntimeException("User Is Not Allowed");
        }

        File file = fileRepository.findByResource(resource);
        if (file == null) {
            throw new RuntimeException("File Not Found");
        }

        // get file
        InputStream inputStream = this.minioClientUtils.getFile(file.getFile_path());

        DownloadFileDto result = new DownloadFileDto();
        result.fileName = resource.getName() + "." + file.getType();
        result.stream = new InputStreamResource(inputStream);

        return result;
    }

    public Long getFileSize(
            String resourceId, String userId
    ) throws Exception {
        ResourceAccessedBy accessedBy = resourceAccessedByRepository.findByUserAndResource(
                userId, resourceId
        );
        if (accessedBy == null) {
            throw new RuntimeException("User Is Not Allowed");
        }

        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new RuntimeException("Resource Not Found")
        );
        if (resource.getIsFolder() || resource.getIsQuiz()) {
            throw new RuntimeException("Resource Is Not File");
        }

        File file = fileRepository.findByResource(resource);
        if (file == null) {
            throw new RuntimeException("File Not Found");
        }

        InputStream inputStream = this.minioClientUtils.getFile(file.getFile_path());
        return new InputStreamResource(inputStream).contentLength();
    }

    public ResourceRenameResponse updateResourceName(
            ResourceRenameRequest request,
            String resourceId,
            String userId
    ) throws Exception {
        try {
            Resource resource = resourceUpdateUtils.checkRename(request, resourceId, userId);

            resource.setName(request.getName());
            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);

            ResourceRenameResponse response = new ResourceRenameResponse(
                    true,
                    "",
                    resource.getName(),
                    resource.getId(),
                    new Date(),
                    resource.getDateCreated(),
                    resource.getIsFolder(),
                    resource.getParent() == null ? null : resource.getParent().getId(),
                    resource.getUser().getId(),
                    resource.getUser().getUsername(),
                    resource.getUser().getInstitution().getName()
            );

            return response;

        } catch (EntityNotFoundException e) {
            return new ResourceRenameResponse(
                    false,
                    e.getMessage(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public ResourceMoveResponse updateResourceParent(
            ResourceMoveRequest request,
            String resourceId,
            String userId
    ) throws Exception {
        try {
            Resource parentResource = null;
            if (request.getParentId() != null && !(request.getParentId().isEmpty())){
                parentResource = resourceUpdateUtils.checkMove(request, resourceId, userId);
            }
            Resource resource = resourceRepository.findByResourceId(resourceId);

            // set permission for parent and all contributors in this resource
            this.setPermissionsForParentCollabs(parentResource, resource);

            resource.setParent(parentResource);
            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);

            if (request.getParentId() != null && !(request.getParentId().isEmpty())) {
                parentResource.setDateUpdated(new Date());
                resourceRepository.save(parentResource);
            }


            ResourceMoveResponse response = new ResourceMoveResponse(
                    true,
                    "Resource moved successfully",
                    parentResource == null ? null : parentResource.getId(),
                    resourceId
            );

            return response;

        } catch (EntityNotFoundException e) {
            return new ResourceMoveResponse(
                    false,
                    e.getMessage(),
                    null,
                    null
            );
        }
    }

    public ChangeResourceOwnerResponse updateResourceOwner(ChangeResourceOwnerRequest request,
                                                           String resourceId,
                                                           String userId) throws Exception {
        try {

            Resource resource = resourceUpdateUtils.checkChangeOwner(request, resourceId, userId);
            User newOwner = userRepository.findByUserId(request.getNewOwner());

            resourceUpdateUtils.changeOwnerRecursive(resource,newOwner );

            return new ChangeResourceOwnerResponse(
                true,
                "Resource owner changed successfully",
                resourceId,
                new Date(),
                resource.getIsFolder(),
                    newOwner.getId(),
                newOwner.getUsername(),
                newOwner.getInstitution().getName()
            );
        } catch (Exception e) {
            return new ChangeResourceOwnerResponse(
                false,
                e.getMessage(),
                resourceId,
                null,
                null,
                userId,
                null,
                null
            );
        }
    }

    public AddAccessResponse updateResourceContributor(AddAccessRequest request, String resourceId, String userId) throws Exception{

        try {
            User contributor = userRepository.findByUserId(request.getUserId());
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);

            resourceUpdateUtils.checkAddContributorOrViewer(contributor, resource,user, ResourceAccessedBy.ROLE.CONTRIBUTOR);

            resourceUpdateUtils.addContributorOrViewerRecursive(resource,contributor, ResourceAccessedBy.ROLE.CONTRIBUTOR);

            return new AddAccessResponse(
                    true,
                    "Contributor added successfully",
                    resourceId,
                    request.getUserId(),
                    ResourceAccessedBy.ROLE.CONTRIBUTOR
            );

        } catch (Exception e) {
            return new AddAccessResponse(
                    false,
                    e.getMessage(),
                    resourceId,
                    request.getUserId(),
                    ResourceAccessedBy.ROLE.CONTRIBUTOR
            );
        }
    }

    public AddAccessResponse updateResourceViewer(AddAccessRequest request, String resourceId, String userId) throws Exception{

        try {
            User viewer = userRepository.findByUserId(request.getUserId());
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);

            resourceUpdateUtils.checkAddContributorOrViewer(viewer, resource,user,ResourceAccessedBy.ROLE.VIEWER);

            resourceUpdateUtils.addContributorOrViewerRecursive(resource,viewer,ResourceAccessedBy.ROLE.VIEWER);

            return new AddAccessResponse(
                    true,
                    "Viewer added successfully",
                    resourceId,
                    request.getUserId(),
                    ResourceAccessedBy.ROLE.VIEWER
            );

        } catch (Exception e) {
            return new AddAccessResponse(
                    false,
                    e.getMessage(),
                    resourceId,
                    request.getUserId(),
                    ResourceAccessedBy.ROLE.VIEWER
            );
        }
    }

    public RestoreResourceResponse restoreResource(String resourceId, String userId) throws Exception {
        try {
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);

            resourceUpdateUtils.checkRestoreResource(resource, user);

            resourceUpdateUtils.restoreResourceRecursive(resource);

            return new RestoreResourceResponse(
                true,
                "Resource restored successfully",
                resourceId
            );


        } catch (Exception e) {
            return new RestoreResourceResponse(
                false,
                e.getMessage(),
                resourceId
            );
        }
    }

    public DeleteResourceResponse moveToTrash(String resourceId, String userId) throws Exception {
        try {
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);

            resourceDeleteUtils.checkMoveToTrash(resource, user);

            resourceDeleteUtils.deleteRecursive(resource, Resource.STATE.TRASHED);

            return new DeleteResourceResponse(
                true,
                "Resource moved to trash successfully",
                resourceId,
                Resource.STATE.TRASHED
            );

        } catch (Exception e) {
            return new DeleteResourceResponse(
                false,
                e.getMessage(),
                resourceId,
                null
            );
        }
    }

    public DeleteResourceResponse deletePermanent(String resourceId, String userId) throws Exception {
        try {
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);

            resourceDeleteUtils.checkDeletePermanent(resource, user);

            resourceDeleteUtils.deleteRecursive(resource, Resource.STATE.DELETED);

            return new DeleteResourceResponse(
                true,
                "Resource deleted permanently",
                resourceId,
                Resource.STATE.DELETED
            );

        } catch (Exception e) {
            return new DeleteResourceResponse(
                false,
                e.getMessage(),
                resourceId,
                null
            );
        }
    }

    public DeleteAccessResponse deleteAccess(String resourceId, DeleteAccessRequest request, String userId) throws Exception{
        try {
            Resource resource = resourceRepository.findByResourceId(resourceId);
            User user = userRepository.findByUserId(userId);
            User deleteUser = userRepository.findByUserId(request.getUserId());

            resourceDeleteUtils.checkDeleteAccess(resource,deleteUser, user);

            resourceDeleteUtils.deleteAccessRecursive(resource, deleteUser, user);

            return new DeleteAccessResponse(
                    true,
                    "Access removed successfully",
                    resourceId,
                    request.getUserId()
            );

        } catch (Exception e) {
            return new DeleteAccessResponse(
                    false,
                    e.getMessage(),
                    resourceId,
                    request.getUserId()
            );
        }
    }

    public NewDocumentsResponse createDocument(String parentId, String name, String type, String userId) throws Exception{
        try {

            Resource parent ;

            if (parentId == null ){
                parent = null;
            }   else {
                parent = resourceRepository.findByResourceId(parentId);
            }

            User user = userRepository.findByUserId(userId);
            Resource resource;

            documentsUtils.checkCreateDocument(parent,user,name);

            String mongoId = documentsUtils.createMongoDocument();

            if (mongoId == null ){
                return new NewDocumentsResponse(
                        false,
                        "Failed to create document",
                        null,
                        parent.getId(),
                        name,
                        type,
                        user.getId(),
                        mongoId
                );
            }


            if (type.toLowerCase().contains("slide")){
                resource = documentsUtils.createNewDocumentResource(mongoId, parent, name, user, "SLIDE");
            }   else {
                resource = documentsUtils.createNewDocumentResource(mongoId, parent, name, user, "DOCUMENT");
            }

            if (resource == null ){
                return new NewDocumentsResponse(
                        false,
                        "Failed to create document",
                        null,
                        parent.getId(),
                        name,
                        type,
                        user.getId(),
                        null
                );
            }

            return new NewDocumentsResponse(
                    true,
                    "New document created successfully",
                    resource.getId(),
                    parentId,
                    name,
                    type,
                    userId,
                    mongoId
            );

        } catch (Exception e) {
            return new NewDocumentsResponse(
                    false,
                    e.getMessage(),
                    null,
                    null,
                    name,
                    type,
                    null,
                    null
            );
        }
    }

    public PermissionVerificationResponse getPermissions (String mongoId, String userId) throws Exception{
        try {
            User user = userRepository.findByUserId(userId);

            if (user == null){
                return new PermissionVerificationResponse(
                        false,
                        "User not found",
                        null,
                        null,
                        null,
                        null,
                        null
                );
            }

            if (mongoId == null){
                return new PermissionVerificationResponse(
                        false,
                        "Document not found",
                        null,
                        null,
                        null,
                        null,
                        null
                );
            }

            ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndMongoId(user.getId(), mongoId);
            Boolean readPermission = false;
            Boolean writePermission = false;

            if (permission == null || permission.getRole().equals(ResourceAccessedBy.ROLE.NO_ACCESS)){
                return new PermissionVerificationResponse(
                        true,
                        "Permission not found",
                        mongoId,
                        user.getId(),
                        mongoId,
                        false,
                        false
                );
            } else{
                // Full read & write if current user is owner, collaborator or admin
                if (permission.getRole().equals(ResourceAccessedBy.ROLE.OWNER) ||
                    permission.getRole().equals(ResourceAccessedBy.ROLE.CONTRIBUTOR) ||
                    user.getRole().equals(User.ROLE.ADMIN)){
                    readPermission = true;
                    writePermission = true;
                }   else if (permission.getRole().equals(ResourceAccessedBy.ROLE.VIEWER)){
                    readPermission = true;
                    writePermission = false;
                }   else {
                    readPermission = false;
                    writePermission = false;
                }
            }

            return new PermissionVerificationResponse(
                    true,
                    "Permission found",
                    mongoId,
                    user.getId(),
                    mongoId,
                    readPermission,
                    writePermission
            );

        } catch (Exception e) {
            return new PermissionVerificationResponse(
                    false,
                    e.getMessage(),
                    null,
                    null,
                    null,
                    false,
                    false
            );
        }
    }

}
