package com.lreas.authentication.controllers;

import com.lreas.authentication.dtos.*;
import com.lreas.authentication.services.IAuthenticationService;
import com.lreas.authentication.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lreas.authentication.dtos.LoginRequest;
import com.lreas.authentication.dtos.SignupRequest;
import com.lreas.authentication.dtos.InvitationDataResponse;

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
public class AuthenticationController {
    private final IAuthenticationService authenticationService;
    private final JwtUtils jwtUtils;

    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());

    @Autowired
    public AuthenticationController(
            IAuthenticationService authenticationService,
            JwtUtils jwtUtils
    ) {
        this.authenticationService = authenticationService;
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

    @PostMapping("/login")
    public ResponseEntity<Object> loginController
            (@RequestBody LoginRequest loginRequest){
        try {
            LoginResponse response = authenticationService.login(loginRequest);

            if (!response.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(response.getMessage()));
            }


            return ResponseEntity.ok(response);
        }    catch (Exception e) {
            // For other exceptions, return internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signupController(
            @RequestBody SignupRequest signupRequest) {

        try {
            // Call the service method
            var signupResponse = authenticationService.signup(signupRequest);

            // Check if request is not success
            if (!signupResponse.getSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(signupResponse.getMessage()));
            }

            // Return 200 OK with the response if username is valid
            return ResponseEntity.ok(signupResponse);

        } catch (Exception e) {
            // Handle exceptions and return 500 status if any error occurs
            logger.log(Level.SEVERE, "Error during signup process.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during signup process"));
        }
    }


    @GetMapping("/users/verify/{invitationCode}")
    public ResponseEntity<Object> getInvitationData(
            @PathVariable("invitationCode") String invitationCode){
        try {
            InvitationDataResponse invitationDataResponse = authenticationService.getInvitationData(invitationCode);
            if (!invitationDataResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(invitationDataResponse.getMessage()));
            }

            return ResponseEntity.ok(invitationDataResponse);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during fetching invitation"));

        }
    }

    @PostMapping("/users/verify/{invitationCode}")
    public ResponseEntity<Object> verifyUser(
            @PathVariable("invitationCode") String invitationCode,
            @RequestBody VerifyResquest verifyResquest
    ){
        try {

            VerifyResponse verifyResponse = authenticationService.verifyUser(verifyResquest,invitationCode);

            if (!verifyResponse.getSuccess()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(verifyResponse.getMessage()));
            }

            return ResponseEntity.ok(verifyResponse);

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during verification"));

        }
    }
//
//
//    @GetMapping("/users/info/:{user_id}")
//    public ResponseEntity<User> getUserInfo(
//            @RequestParam("username") String username
//    ) {
//        try {
//            return new ResponseEntity<>(authenticationService.getUserInfo(username), HttpStatus.OK);
//        }
//        catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/users/invite")
    public ResponseEntity<Object> inviteUser(
            @RequestBody InvitationRequest invitationRequest,
            @RequestHeader ("Authorization") String authorizationHeader

    ) {
        try {
            String token = authorizationHeader.replace("Bearer ","");

            String institutionName = jwtUtils.extractInstitutionName(token);


            // Call the service method
            var invitationResponse = authenticationService.invite(invitationRequest, institutionName);

            // Check if request is not success
            if (!invitationResponse.getSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.of(invitationResponse.getMessage()));
            }

            // Return 200 OK with the response if username is valid
            return ResponseEntity.ok(invitationResponse);

        } catch (Exception e) {
            // Handle exceptions and return 500 status if any error occurs
            logger.log(Level.SEVERE, "Error during inviting process.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error during signup process"));
        }
    }

    @PutMapping("/users/password")
    public ResponseEntity<Object> updatePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        try {
            ChangePasswordResponse response = authenticationService.updatePassword(changePasswordRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of("Internal server error while updating password"));
        }
    }
}
