package com.lreas.authentication.services;

import com.lreas.authentication.dtos.*;
import com.lreas.authentication.models.Institution;
import com.lreas.authentication.models.User;
import com.lreas.authentication.repositories.UserRepository;
import com.lreas.authentication.repositories.InstitutionRepository;
import com.lreas.authentication.utils.InvitationUtils;
import com.lreas.authentication.utils.LoginUtils;
import com.lreas.authentication.utils.SignupUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
public class AuthenticationService implements IAuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private SignupUtils signupUtils;
    @Autowired
    private LoginUtils loginUtils;
    @Autowired
    private InvitationUtils invitationUtils;


    public User getUserInfo(String username) {

        List<User> users = userRepository.findByUsername(username);

        if (username == null){
            return null;
        } else if (users.isEmpty()){
            return null;
        }

        return users.get(0);

    }

    public LoginResponse login(LoginRequest loginRequest) throws Exception {

        try {
            User user = loginUtils.check(loginRequest);
            if (user == null) {
                return new LoginResponse(false, null,"User not found");
            }

            String secret = loginUtils.getSecret(user.getRole());
            String key = loginUtils.getKey(user.getRole());

            if (secret == null || key == null) {
                return new LoginResponse(false, null, "User not found");
            }


            String token = loginUtils.generateToken(secret, key, user);

            if (token != null) {
                return new LoginResponse(true,token,null);

            }

            return new LoginResponse(false, null, "Invalid request");

        }   catch(ResponseStatusException e){
            String message = e.getMessage();
            return new LoginResponse(false, null,message);
        }


    }

    public SignupResponse signup(SignupRequest signupRequest) throws Exception {
        try {
            // Check existence: of institution name, institution subdomain. User email, username
            signupUtils.check(signupRequest);

//        if (message != null){
//            return new SignupResponse(null, message, null, null, null);
//        }


            Institution institution = new Institution();
            institution.setName(signupRequest.getInstitutionName());
            institution.setSubdomain(signupRequest.getSubdomain());
            institution.setWorkflowState(Institution.STATE.ACTIVE);
            institution.setEmail(signupRequest.getEmail());

            Institution savedInstitution = institutionRepository.save(institution);

            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setRole(User.ROLE.ADMIN);
            user.setWorkflowState(User.STATE.ACTIVE);
            user.setInstitution(savedInstitution);

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
            String encodedPassword = bCryptPasswordEncoder.encode(signupRequest.getPassword());
            user.setPassword(encodedPassword);

            userRepository.save(user);


            SignupResponse signupResponse = new SignupResponse(
                    true,
                    signupRequest.getUsername(),
                    "New account and institution created",
                    signupRequest.getInstitutionName(),
                    "admin",
                    signupRequest.getSubdomain()
            );
            return signupResponse;
        }   catch (ResponseStatusException e) {
            String message = e.getMessage();
            return new SignupResponse(false,null, message, null, null, null);

        }
    }

    public InvitationResponse invite(InvitationRequest invitationRequest, String institutionName) throws Exception {
        try {
            // Check existence: of institution name, institution subdomain. User email, username
            Institution institution = invitationUtils.check(invitationRequest, institutionName);


            if (institution == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution not found");
            }

            String invitationCode = invitationUtils.generateReadableCode();
            User user = new User();
            user.setEmail(invitationRequest.getEmail());

            user.setRole(invitationRequest.getRole());
            user.setWorkflowState(User.STATE.PENDING);
            user.setInstitution(institution);
            user.setInvitationCode(invitationCode);
            user.setUsername(invitationRequest.getUsername());


            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
            String encodedPassword = bCryptPasswordEncoder.encode(invitationCode);
            user.setPassword(encodedPassword);

            userRepository.save(user);


            InvitationResponse invitationResponse = new InvitationResponse(
                    true,
                    "New user has been created",
                    "lvh.me/verify/"+invitationCode,
                    invitationRequest.getEmail(),
                    invitationRequest.getRole(),
                    institutionName,
                    invitationRequest.getUsername()
            );
            return invitationResponse;
        }   catch (ResponseStatusException e) {
            String message = e.getMessage();
            return new InvitationResponse(false, message, null, null, null,null, invitationRequest.getUsername());

        }
    }

    public InvitationDataResponse getInvitationData(String invitationCode) throws Exception{
        try
        {
            //Get user by invitation
            List<User> users = userRepository.findByInvitationCode(invitationCode);
            if (users.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invitation code");
            }


            InvitationDataResponse invitationDataResponse = new InvitationDataResponse(
                    true,
                    "",
                    users.get(0).getEmail(),
                    users.get(0).getRole(),
                    users.get(0).getInstitution().getName(),
                    users.get(0).getInstitution().getSubdomain(),
                    users.get(0).getUsername()
            );
            return invitationDataResponse;

        }   catch(ResponseStatusException e){
            String message = e.getMessage();
            return new InvitationDataResponse(
                    false,message,"",null,null,null,null
            );
        }
    }

    public VerifyResponse verifyUser(VerifyResquest verifyResquest, String invitationCode) throws Exception{
        try {
            //Check for null request fields
            if (verifyResquest.getPassword() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
            }
            List<User> users = userRepository.findByInvitationCode(invitationCode);
            if (users.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invitation code");
            }

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
            String encodedPassword = bCryptPasswordEncoder.encode(verifyResquest.getPassword());
            users.get(0).setPassword(encodedPassword);
            users.get(0).setWorkflowState(User.STATE.ACTIVE);
            users.get(0).setInvitationCode(null);

            userRepository.save(users.get(0));

            VerifyResponse verifyResponse = new VerifyResponse(
                    true,
                    "User verified successfully",
                    users.get(0).getEmail(),
                    users.get(0).getRole(),
                    users.get(0).getInstitution().getName(),
                    users.get(0).getInstitution().getSubdomain(),
                    users.get(0).getUsername()
            );
            return verifyResponse;
        }   catch(ResponseStatusException e){
            String message = e.getMessage();
            return new VerifyResponse(
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

    public User newUser(UserDto userDto) {
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(userDto.getPassword());
        newUser.setEmail(userDto.getEmail());
        newUser.setAvtPath(userDto.getAvtPath());
        newUser.setRole(User.ROLE.valueOf(userDto.getRole().toUpperCase()));

        return userRepository.save(newUser);
    }

    public ChangePasswordResponse updatePassword(
            ChangePasswordRequest changePasswordRequest
    ) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(changePasswordRequest.email);
        loginRequest.setPassword(changePasswordRequest.currentPassword);

        ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse();

        try {
            User user = loginUtils.check(loginRequest);
            if (user != null) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
                String encodedPassword = bCryptPasswordEncoder.encode(changePasswordRequest.newPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);

                changePasswordResponse.success = true;
                changePasswordResponse.message = "Password changed successfully";
            }
        }
        catch (Exception e) {
            changePasswordResponse.success = false;
            changePasswordResponse.message = "Wrong password";
        }

        return changePasswordResponse;
    }
}


