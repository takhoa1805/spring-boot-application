package com.lreas.authentication.utils;

import com.lreas.authentication.dtos.SignupRequest;

import com.lreas.authentication.models.User;
import com.lreas.authentication.models.Institution;
import com.lreas.authentication.repositories.UserRepository;
import com.lreas.authentication.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class SignupUtils {
    private UserRepository userRepository;
    private InstitutionRepository institutionRepository;

    @Autowired
    public SignupUtils(UserRepository userRepository, InstitutionRepository institutionRepository){
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
    }

    public void check(SignupRequest signupRequest) {
        // Check request
        if (signupRequest.getUsername() == null || signupRequest.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (signupRequest.getInstitutionName() == null || signupRequest.getInstitutionName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution name is required");
        }
        if (signupRequest.getSubdomain() == null || signupRequest.getSubdomain().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain is required");
        }

        // Check email
        List<User> emails = userRepository.findByEmail(signupRequest.getEmail());
        if (!emails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Check institution name
        List<Institution> institutionNames = institutionRepository.findByName(signupRequest.getInstitutionName());
        if (!institutionNames.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution name already exists");
        }

        // Check institution subdomain
        List<Institution> institutionSubdomains = institutionRepository.findBySubdomain(signupRequest.getSubdomain());
        if (!institutionSubdomains.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution subdomain already exists");
        }
    }



}
