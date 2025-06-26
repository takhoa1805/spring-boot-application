package com.lreas.authentication.utils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.lreas.authentication.dtos.InvitationRequest;
import com.lreas.authentication.models.Institution;
import com.lreas.authentication.models.User;
import com.lreas.authentication.repositories.UserRepository;
import com.lreas.authentication.repositories.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


@Component
public class InvitationUtils {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // No confusing characters (0, O, I, 1)
    private static final int CODE_LENGTH = 8; // Adjust as needed
    private static final Random RANDOM = new SecureRandom();
    private UserRepository userRepository;
    private InstitutionRepository institutionRepository;

    @Autowired
    public InvitationUtils(UserRepository userRepository, InstitutionRepository institutionRepository) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
    }

    public static String generateReadableCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public Institution check (InvitationRequest invitationRequest, String institutionName) throws Exception {
        // Check email
        List<User> emails = userRepository.findByEmail(invitationRequest.getEmail());
        if (invitationRequest.getEmail() == null || invitationRequest.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email is required");
        }
        if (invitationRequest.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Role is required");
        }
        if (invitationRequest.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name is required");
        }
        if (!emails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Check institution name
        List<Institution> institutions = institutionRepository.findByName(institutionName);
        if (institutions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Institution does not exists");
        }
        return institutions.get(0);
    }




}
