package com.lreas.profile.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lreas.profile.dtos.NotiInfoDto;
import com.lreas.profile.dtos.UserInfoDto;
import com.lreas.profile.dtos.UserInfoResponse;
import com.lreas.profile.dtos.UserInfoUpdate;
import com.lreas.profile.services.ProfileService;
import com.lreas.profile.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class ProfileController {
    private final ProfileService profileService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ProfileController(
            ProfileService profileService,
            JwtUtils jwtUtils
    ) {
        this.profileService = profileService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("")
    public ResponseEntity<Object> getUserProfile(
            HttpServletRequest request
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            UserInfoDto response = this.profileService.getUserInfo(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserProfileById(
            HttpServletRequest request,
            @PathVariable String userId

    ) {
        try {
            String role = jwtUtils.extractRole(request);
            if (!role.equals("ADMIN")) {
                return new ResponseEntity<>("Permission Denied", HttpStatus.FORBIDDEN);
            }
            UserInfoDto response = this.profileService.getUserInfo(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-email")
    public ResponseEntity<Object> getUserByEmail(
            HttpServletRequest request,
            @RequestParam String email
    ) {
        try {
            String userId = jwtUtils.extractUserId(request);
            UserInfoResponse response = this.profileService.getUserInfoFromEmail(email, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("")
    public ResponseEntity<Object> updateUserProfile(
            HttpServletRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile file,
            @RequestPart("update_info") String updateInfo
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserInfoUpdate userInfoUpdate = mapper.readValue(updateInfo, UserInfoUpdate.class);
            userInfoUpdate.userId = jwtUtils.extractUserId(request);

            UserInfoDto response = this.profileService.updateUserInfo(file, userInfoUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUserProfileById(
            HttpServletRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile file,
            @RequestPart("update_info") String updateInfo,
            @PathVariable String userId
    ) {
        try {

            String role = jwtUtils.extractRole(request);
            if (!role.equals("ADMIN")) {
                return new ResponseEntity<>("Permission Denied", HttpStatus.FORBIDDEN);
            }

            ObjectMapper mapper = new ObjectMapper();
            UserInfoUpdate userInfoUpdate = mapper.readValue(updateInfo, UserInfoUpdate.class);
            userInfoUpdate.userId = userId;

            UserInfoDto response = this.profileService.updateUserInfo(file, userInfoUpdate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<Object> getNotiInfo(
            HttpServletRequest request
    ) {
        try {
            List<NotiInfoDto> response = this.profileService.getNotification(
                    jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/notification/{notificationId}/read")
    public ResponseEntity<Object> markRead(
            HttpServletRequest request,
            @PathVariable String notificationId
    ) {
        try {
            NotiInfoDto response = this.profileService.markRead(
                    notificationId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/notification/{notificationId}/unread")
    public ResponseEntity<Object> markUnread(
            HttpServletRequest request,
            @PathVariable String notificationId
    ) {
        try {
            NotiInfoDto response = this.profileService.markUnread(
                    notificationId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/notification/{notificationId}")
    public ResponseEntity<Object> deleteNotification(
            HttpServletRequest request,
            @PathVariable String notificationId
    ) {
        try {
            NotiInfoDto response = this.profileService.deleteNotification(
                    notificationId, jwtUtils.extractUserId(request)
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
