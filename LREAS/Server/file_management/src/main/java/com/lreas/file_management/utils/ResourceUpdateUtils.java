package com.lreas.file_management.utils;

import com.lreas.file_management.dtos.ChangeResourceOwnerRequest;
import com.lreas.file_management.dtos.ResourceMoveRequest;
import com.lreas.file_management.dtos.ResourceRenameRequest;
import com.lreas.file_management.models.ResourceAccessedBy;
import com.lreas.file_management.models.ResourceAccessedById;
import com.lreas.file_management.models.User;
import com.lreas.file_management.repositories.jpa.UserRepository;
import com.lreas.file_management.repositories.jpa.ResourceRepository;
import com.lreas.file_management.repositories.jpa.ResourceAccessedByRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lreas.file_management.models.Resource;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import java.util.List;


@Component
public class ResourceUpdateUtils {
    private static final Logger log = LogManager.getLogger(ResourceUpdateUtils.class);
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAccessedByRepository resourceAccessedByRepository;

    @Autowired
    public ResourceUpdateUtils(UserRepository userRepository,
                                ResourceRepository resourceRepository,
                                ResourceAccessedByRepository resourceAccessedByRepository
    ) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
    }


    public Resource checkRename(ResourceRenameRequest request, String resourceId, String userId) throws Exception {

        try {
            if (resourceId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
            }

            if (request.getName() == null || request.getName().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
            }

            Resource resource = resourceRepository.findByResourceId((resourceId));

            if (resource == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
            }
            if (resource.getWorkflowState() != Resource.STATE.AVAILABLE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource is not found");
            }

            ResourceAccessedBy resourceAccessedBy = resourceAccessedByRepository.findByUserAndResource(userId, resourceId);
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
            }

            if((resourceAccessedBy == null || (resourceAccessedBy.getRole() == ResourceAccessedBy.ROLE.VIEWER)) && !(user.getRole()==User.ROLE.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to rename resource");
            }

            return resource;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to rename resource");

        }

    }

    public Resource checkMove(ResourceMoveRequest request, String resourceId, String userId) throws Exception {

        if (resourceId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }

        if (request.getParentId().equals(resourceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot move resource to itself");
        }

        Resource resource = resourceRepository.findByResourceId((resourceId));
        Resource parentResource = request.getParentId() == null ? null : resourceRepository.findByResourceId(request.getParentId());


        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }
        if (resource.getWorkflowState() != Resource.STATE.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource is not found");
        }

        ResourceAccessedBy resourceAccessedBy = resourceAccessedByRepository.findByUserAndResource(userId, resourceId);
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        if((resourceAccessedBy == null || (resourceAccessedBy.getRole() == ResourceAccessedBy.ROLE.VIEWER)) && !(user.getRole()==User.ROLE.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to move resource");
        }



        if (parentResource != null && parentResource.getWorkflowState() != Resource.STATE.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent resource is not found");
        }

        if (parentResource != null && (parentResource.getIsQuiz() || !parentResource.getIsFolder())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent resource is not a folder");
        }

        if (parentResource != null){
            ResourceAccessedBy parentResourceAccessedBy = resourceAccessedByRepository.findByUserAndResource(userId, request.getParentId());

            if((resourceAccessedBy == null || (resourceAccessedBy.getRole() == ResourceAccessedBy.ROLE.VIEWER)) && !(user.getRole()==User.ROLE.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to modify parent folder");
            }
        }

        return parentResource;

    }

    public Resource checkChangeOwner(ChangeResourceOwnerRequest request, String resourceId, String userId) throws Exception {


        if (resourceId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }

        if (request.getNewOwner() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New owner not found");
        }

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(userId, resourceId);
        if (user.getRole() != User.ROLE.ADMIN && (permission == null || permission.getRole() != ResourceAccessedBy.ROLE.OWNER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to change owner");
        }


        Resource resource = resourceRepository.findByResourceId((resourceId));

        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }
        if (resource.getWorkflowState() != Resource.STATE.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource is not found");
        }

        return resource;

    }

    public ResourceAccessedBy changeOwner(Resource resource, User newOwner ){

        if (resource == null  || newOwner == null){
            return null;
        }


        // Change current owner to collaborator
        // Create new access for new owner

        List<ResourceAccessedBy> permission = resourceAccessedByRepository.findByResourceAndRole(resource.getId(), ResourceAccessedBy.ROLE.OWNER);


        // If current owner is also new owner
        if (permission != null && !permission.isEmpty() && permission.get(0).getResource().getUser().getId().equals(newOwner.getId())){
            return permission.get(0);
        }


        ResourceAccessedById access = new ResourceAccessedById();
        access.setResource(resource);
        access.setUser(newOwner);

        if (permission == null || permission.isEmpty()){
            ResourceAccessedBy newPermission = new ResourceAccessedBy();
            newPermission.setResource(access);
            newPermission.setRole(ResourceAccessedBy.ROLE.OWNER);
            resourceAccessedByRepository.save(newPermission);

            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);
            return newPermission;
        }   else {
            permission.get(0).setRole(ResourceAccessedBy.ROLE.CONTRIBUTOR);
            resourceAccessedByRepository.save(permission.get(0));

            ResourceAccessedBy newPermission = new ResourceAccessedBy();
            newPermission.setResource(access);
            newPermission.setRole(ResourceAccessedBy.ROLE.OWNER);
            resourceAccessedByRepository.save(newPermission);

            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);

        }

        return permission.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeOwnerRecursive(Resource resource, User newOwner ) throws Exception{

        try {
            if (resource == null  || newOwner == null){
                return;
            }

            this.changeOwner(resource, newOwner);


            if (resource.getIsFolder()){
                List<Resource> children = resourceRepository.findByParent(resource);

                for (Resource child : children){
                    this.changeOwnerRecursive(child, newOwner);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void checkAddContributorOrViewer(User contributorOrViewer, Resource resource, User user, ResourceAccessedBy.ROLE role) throws Exception {

        if (resource == null ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }
        if (resource.getWorkflowState() != Resource.STATE.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }

        if (contributorOrViewer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contributor not found");
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }


        // Viewer can only view non-folder resources
        if (resource.getIsFolder() && role == ResourceAccessedBy.ROLE.VIEWER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Viewer cannot be added to folder");
        }

        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(user.getId(), resource.getId());

        // If request user is not ADMIN, not OWNER, Not CONTRIBUTOR => no permission to add new contributor
        if (user.getRole() != User.ROLE.ADMIN &&
                (permission == null ||
                    (permission.getRole() != ResourceAccessedBy.ROLE.OWNER &&
                    permission.getRole() != ResourceAccessedBy.ROLE.CONTRIBUTOR))
                )
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission add contributor");
        }

    }

    public void addContributorOrViewer(Resource resource, User contributorOrViewer, ResourceAccessedBy.ROLE role) throws Exception{
        //Check if there's already a contributor permission for this user - resource - access role
        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(contributorOrViewer.getId(), resource.getId());
        int rolePrio = 0;
        int currentRolePrio = 0;

        if(role == ResourceAccessedBy.ROLE.OWNER){
            rolePrio = 3;
        } else if(role == ResourceAccessedBy.ROLE.CONTRIBUTOR){
            rolePrio = 2;
        } else if(role == ResourceAccessedBy.ROLE.VIEWER){
            rolePrio = 1;
        }



        if (permission == null){
            ResourceAccessedById access = new ResourceAccessedById();
            access.setResource(resource);
            access.setUser(contributorOrViewer);
            ResourceAccessedBy newPermission = new ResourceAccessedBy();
            newPermission.setResource(access);
            newPermission.setRole(role);
            resourceAccessedByRepository.save(newPermission);

            resource.setDateUpdated(new Date());
            resourceRepository.save(resource);
            return;

        }  else {
            if(permission.getRole() == ResourceAccessedBy.ROLE.OWNER){
                currentRolePrio = 3;
            } else if(permission.getRole() == ResourceAccessedBy.ROLE.CONTRIBUTOR){
                currentRolePrio = 2;
            } else if(permission.getRole() == ResourceAccessedBy.ROLE.VIEWER){
                currentRolePrio = 1;
            }
            if(rolePrio > currentRolePrio){
                permission.setRole(role);
                resourceAccessedByRepository.save(permission);

                resource.setDateUpdated(new Date());
                resourceRepository.save(resource);
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void addContributorOrViewerRecursive(Resource resource, User contributorOrViewer, ResourceAccessedBy.ROLE role) throws Exception{
        try {
            if (resource == null || contributorOrViewer == null){
                return;
            }

            this.addContributorOrViewer(resource, contributorOrViewer, role);

            if (resource.getIsFolder()){
                List<Resource> children = resourceRepository.findByParent(resource);
                for (Resource child : children){
                    this.addContributorOrViewerRecursive(child, contributorOrViewer,role);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void checkRestoreResource(Resource resource, User user) throws Exception {


        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(user.getId(), resource.getId());
        if (user.getRole() != User.ROLE.ADMIN && (permission == null || permission.getRole() != ResourceAccessedBy.ROLE.OWNER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not have permission to change owner");
        }


    }

    public void restoreResource(Resource resource) throws Exception {
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");
        }
        resource.setWorkflowState(Resource.STATE.AVAILABLE);
        resource.setDateUpdated(new Date());
        resourceRepository.save(resource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void restoreResourceRecursive(Resource resource) throws Exception {
        try {
            if (resource == null) {
                return;
            }
            this.restoreResource(resource);
            if (resource.getIsFolder()) {
                List<Resource> children = resourceRepository.findByParent(resource);
                for (Resource child : children) {
                    this.restoreResourceRecursive(child);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
