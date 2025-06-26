package com.lreas.file_management.utils;

import com.lreas.file_management.models.ResourceAccessedBy;
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
public class ResourceDeleteUtils {
    private static final Logger log = LogManager.getLogger(ResourceUpdateUtils.class);
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAccessedByRepository resourceAccessedByRepository;

    @Autowired
    public ResourceDeleteUtils(UserRepository userRepository,
                               ResourceRepository resourceRepository,
                               ResourceAccessedByRepository resourceAccessedByRepository
    ) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.resourceAccessedByRepository = resourceAccessedByRepository;
    }
    
    public void checkMoveToTrash(Resource resource, User user)throws Exception{
        if (resource == null ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (resource.getWorkflowState().equals(Resource.STATE.TRASHED) || resource.getWorkflowState().equals(Resource.STATE.DELETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource is already in trash");
        }
        
        
        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(user.getId(), resource.getId());
        
        // Check if request user is not admin, and not owner of the resource
        if (permission == null || (!(permission.getRole().equals(ResourceAccessedBy.ROLE.OWNER)) && !(user.getRole().equals(User.ROLE.ADMIN)))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to move this resource to trash");
        }
        
    
    }
    
    public void checkDeletePermanent(Resource resource, User user) throws Exception{
        if (resource == null ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!(resource.getWorkflowState().equals(Resource.STATE.TRASHED)) ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource is not in trash");
        }


        ResourceAccessedBy permission = resourceAccessedByRepository.findByUserAndResource(user.getId(), resource.getId());

        // Check if request user is not admin, and not owner of the resource
        if (permission == null || (!(permission.getRole().equals(ResourceAccessedBy.ROLE.OWNER)) && !(user.getRole().equals(User.ROLE.ADMIN))) ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this resource permanently");
        }

    }


    public void delete(Resource resource, Resource.STATE state) throws Exception{

        if (resource == null ){
            return;
        }

        resource.setDateUpdated(new Date());
        resource.setWorkflowState(state);

        resourceRepository.save(resource);

    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRecursive(Resource resource, Resource.STATE state) throws Exception{

        try {
            if (resource == null ){
                return;
            }

            this.delete(resource, state);


            if (resource.getIsFolder()){
                List<Resource> children = resourceRepository.findByParent(resource);

                for (Resource child : children){
                    this.deleteRecursive(child,state);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void checkDeleteAccess(Resource resource, User deleteUser,User requestUser) throws Exception{
        if (resource == null ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        if (deleteUser == null || requestUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!(resource.getWorkflowState().equals(Resource.STATE.AVAILABLE)) ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }


        ResourceAccessedBy requestUserPermission = resourceAccessedByRepository.findByUserAndResource(requestUser.getId(), resource.getId());

        // Check if request user is not admin, and not owner of the resource
        if (requestUserPermission == null || (!(requestUserPermission.getRole().equals(ResourceAccessedBy.ROLE.OWNER)) && !(requestUser.getRole().equals(User.ROLE.ADMIN)))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to remove resource's accessibility");
        }

    }



    public void deleteAccess(Resource resource, User deleteUser, User requestUser) throws Exception {

        if (resource == null || deleteUser == null  || requestUser == null){
            return;
        }

        ResourceAccessedBy deleteUserPermission = resourceAccessedByRepository.findByUserAndResource(deleteUser.getId(), resource.getId());

        // Don't remove access to owner's resources
        if (deleteUserPermission != null && deleteUserPermission.getRole().equals(ResourceAccessedBy.ROLE.OWNER)){
            return;
        } else if (deleteUserPermission == null){
            return;
        }

        deleteUserPermission.setRole(ResourceAccessedBy.ROLE.NO_ACCESS);
        resource.setDateUpdated(new Date());

        resourceAccessedByRepository.save(deleteUserPermission);
        resourceRepository.save(resource);


    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAccessRecursive(Resource resource, User deleteUser, User requestUser) throws Exception{
        try {
            if (resource == null ){
                return;
            }
            this.deleteAccess(resource, deleteUser, requestUser);
            if (resource.getIsFolder()){
                List<Resource> children = resourceRepository.findByParent(resource);

                for (Resource child : children){
                    this.deleteAccessRecursive(child,deleteUser, requestUser);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
