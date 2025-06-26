package com.lreas.authentication.services;

import com.lreas.authentication.dtos.*;
import com.lreas.authentication.models.User;


public interface IAuthenticationService {
    public User getUserInfo(String username);
    public User newUser(UserDto userDto);
    public LoginResponse login(LoginRequest loginRequest) throws Exception;
    public SignupResponse signup(SignupRequest signupRequest) throws Exception;
    public InvitationDataResponse getInvitationData(String invitationCode) throws Exception;
    public VerifyResponse verifyUser(VerifyResquest verifyResquest, String invitationCode) throws Exception;
    public InvitationResponse invite(InvitationRequest invitationRequest, String institutionName) throws Exception;
    public ChangePasswordResponse updatePassword(ChangePasswordRequest changePasswordRequest);
}
