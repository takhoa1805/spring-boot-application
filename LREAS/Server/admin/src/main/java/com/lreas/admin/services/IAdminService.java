package com.lreas.admin.services;

import com.lreas.admin.dtos.*;
import com.lreas.admin.models.User;


public interface IAdminService {
    public UserListResponse getAllUsers(String institutionName) throws Exception;
    public UserUpdateResponse updateUserWorkflowState(String userId, User.STATE workflowState) throws Exception;
    public UserUpdateResponse updateUserRole(String userId, User.ROLE role) throws Exception;
}

