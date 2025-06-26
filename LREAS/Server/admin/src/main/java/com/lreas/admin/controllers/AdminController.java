package com.lreas.admin.controllers;

import com.lreas.admin.dtos.*;
import com.lreas.admin.models.User;
import com.lreas.admin.services.IAdminService;
import com.lreas.admin.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.logging.Logger;


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
public class AdminController {
    private final IAdminService adminService;
    private final JwtUtils jwtUtils;

    private static final Logger logger = Logger.getLogger(com.lreas.admin.controllers.AdminController.class.getName());

    @Autowired
    public AdminController(
            IAdminService adminService,
            JwtUtils jwtUtils
    ) {
        this.adminService = adminService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            return new ResponseEntity<>("Hello worlds", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(
            @RequestHeader ("Authorization") String authorizationHeader

    ) {
        String token = authorizationHeader.replace("Bearer ","");

        String institutionName = jwtUtils.extractInstitutionName(token);

        try {
            UserListResponse userListResponse = adminService.getAllUsers(institutionName);

            if (!userListResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userListResponse.getMessage()));
            }

            return ResponseEntity.ok(userListResponse);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during fetching users"));

        }

    }

    @PutMapping("/users/{userId}/state/ban")
    public ResponseEntity<Object> banUser(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserWorkflowState(userId, User.STATE.INACTIVE);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during banning user"));
        }
    }

    @PutMapping("/users/{userId}/state/unban")
    public ResponseEntity<Object> unbanUser(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserWorkflowState(userId, User.STATE.ACTIVE);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during unbanning user"));
        }
    }

    @DeleteMapping ("/users/{userId}/state")
    public ResponseEntity<Object> deleteUser(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserWorkflowState(userId, User.STATE.DELETED);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during deleting user"));
        }
    }


    @PutMapping("/users/{userId}/role/student")
    public ResponseEntity<Object> updateUserRoleToStudent(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserRole(userId, User.ROLE.STUDENT);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during update user role"));
        }
    }

    @PutMapping("/users/{userId}/role/teacher")
    public ResponseEntity<Object> updateUserRoleToTeacher(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserRole(userId, User.ROLE.TEACHER);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during update user role"));
        }
    }

    @PutMapping("/users/{userId}/role/admin")
    public ResponseEntity<Object> updateUserRoleToAdmin(
            @RequestHeader ("Authorization") String authorizationHeader,
            @PathVariable String userId
    ) {
        String token = authorizationHeader.replace("Bearer ","");


        try {
            com.lreas.admin.dtos.UserUpdateResponse userUpdateResponse = adminService.updateUserRole(userId, User.ROLE.ADMIN);
            if (!userUpdateResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(userUpdateResponse.getMessage()));
            }

            return ResponseEntity.ok(userUpdateResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during update user role"));
        }
    }
}
