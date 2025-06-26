package com.lreas.admin.services;

import com.lreas.admin.dtos.*;
import com.lreas.admin.models.Institution;
import com.lreas.admin.models.User;
import com.lreas.admin.repositories.UserRepository;
import com.lreas.admin.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService implements IAdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstitutionRepository institutionRepository;


    public UserListResponse getAllUsers(String institutionName) throws Exception{
        try {
            List<User> users = userRepository.findByInstitutionName(institutionName);

            List<com.lreas.admin.dtos.User> usersDto = new ArrayList<>();


            for (User user : users) {
                com.lreas.admin.dtos.User dto = new com.lreas.admin.dtos.User(
                        user.getEmail(),
                        user.getRole(),
                        user.getUsername(),
                        user.getId(),
                        user.getWorkflowState()
                );
                usersDto.add(dto);
            }

            UserListResponse userListResponse = new UserListResponse(
                    true,
                    "Users found",
                    usersDto,
                    institutionName
            );
            return userListResponse;
        }   catch(ResponseStatusException e){
            String message = e.getMessage();
            return new UserListResponse(
                    false,message,new ArrayList<com.lreas.admin.dtos.User>(),null
            );
        }
    }

    public UserUpdateResponse updateUserWorkflowState(String userId, User.STATE workflowState) throws Exception{
        try {
            User user = userRepository.findByUserId(userId);


            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            user.setWorkflowState(workflowState);
            userRepository.save(user);

            String message = (workflowState == User.STATE.INACTIVE) ? "User banned successfully" : (workflowState == User.STATE.DELETED ? "User deleted successfully" : "User unbanned successfully");

            return new UserUpdateResponse(
                    true,
                    message,
                    user.getEmail(),
                    user.getRole(),
                    user.getUsername(),
                    user.getId(),
                    workflowState
            );
        } catch (ResponseStatusException e) {
            String message = e.getMessage();
            return new UserUpdateResponse(
                    false,
                    message,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public UserUpdateResponse updateUserRole(String userId, User.ROLE role) throws Exception {
        try {
            User user = userRepository.findByUserId(userId);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }

            user.setRole(role);
            userRepository.save(user);

            return new UserUpdateResponse(
                    true,
                    "User role updated successfully",
                    user.getEmail(),
                    user.getRole(),
                    user.getUsername(),
                    user.getId(),
                    user.getWorkflowState()
            );
        } catch (ResponseStatusException e) {
            String message = e.getMessage();
            return new UserUpdateResponse(
                    false,
                    message,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}


