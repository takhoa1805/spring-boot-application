package com.lreas.file_management.dtos;

import com.lreas.file_management.models.ResourceAccessedBy;
import com.lreas.file_management.models.Resource;
import com.lreas.file_management.models.User;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ResourceResponse {
    public String contentName;
    public String author;
    public Boolean isFolder;
    public Boolean isQuiz;
    public String id;
    public String parentId;
    public ResourceAccessedBy.ROLE role;
    public List<CollaboratorDto> collaborators;
    public String mongoId;
    public Resource.STATE state;
    public Date updatedTime;
    public String fileType;

    public ResourceResponse() {}

    public ResourceResponse(
            Resource resource, ResourceAccessedBy.ROLE role,
            List<CollaboratorDto> collaborators
    ) {
        this.contentName = resource.getName();
        this.author = resource.getUser().getUsername();
        this.isFolder = resource.getIsFolder();
        this.isQuiz = resource.getIsQuiz();
        this.id = resource.getId();
        this.parentId = resource.getParent() != null ? resource.getParent().getId() : null;
        this.role = role;
        this.collaborators = new LinkedList<>(collaborators);

        // get owner of this resource
        User owner = resource.getUser();
        CollaboratorDto ownerCollab = new CollaboratorDto(
                owner, ResourceAccessedBy.ROLE.OWNER
        );
        this.collaborators.add(0, ownerCollab);

        // filter out admin user
        this.collaborators.removeIf(
                collaborator -> collaborator.userRole == User.ROLE.ADMIN
                        && collaborator.role != ResourceAccessedBy.ROLE.OWNER
        );

        this.mongoId = resource.getMongoId();
        this.state = resource.getWorkflowState();
        this.updatedTime = resource.getDateUpdated();

        if (!resource.getIsFolder() && !resource.getIsQuiz()) {
            if (resource.getFile() != null) {
                this.fileType = resource.getFile().getType();
            }
        }
    }
}
