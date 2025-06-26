package com.lreas.authentication.utils;

import com.lreas.authentication.dtos.LoginRequest;

import com.lreas.authentication.models.User;
import com.lreas.authentication.repositories.UserRepository;
import com.lreas.authentication.repositories.InstitutionRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginUtils {
    private UserRepository userRepository;
    private InstitutionRepository institutionRepository;
    private RestTemplate restTemplate;
    @Value("${secret.admin}")
    private String adminSecret;
    @Value("${secret.teacher}")
    private String teacherSecret;
    @Value("${secret.student}")
    private String studentSecret;
    @Value("${key.admin}")
    private String adminKey;
    @Value("${key.teacher}")
    private String teacherKey;
    @Value("${key.student}")
    private String studentKey;



    @Autowired
    public LoginUtils(UserRepository userRepository, InstitutionRepository institutionRepository){
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.restTemplate = new RestTemplate();

        this.restTemplate = new RestTemplate();

    }

    public User check(LoginRequest loginRequest){
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        // Check  email
        List<User> users = userRepository.findByEmail(loginRequest.getEmail());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(8);
        if (users.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong credentials");

        }
        if (users.get(0).getWorkflowState() != User.STATE.ACTIVE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not active");
        }
        boolean matches = bCryptPasswordEncoder.matches(loginRequest.getPassword(), users.get(0).getPassword());
        if (!matches){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong credentials");

        }
        return users.get(0);


    }

    public String getSecret(User.ROLE role){
        switch (role){
            case ADMIN:
                return adminSecret;
            case TEACHER:
                return teacherSecret;
            case STUDENT:
                return studentSecret;
            default:
                return null;
        }
    }

    public String getKey(User.ROLE role){
        switch (role){
            case ADMIN:
                return adminKey;
            case TEACHER:
                return teacherKey;
            case STUDENT:
                return studentKey;
            default:
                return null;
        }
    }

    public String generateToken(String secret, String key, User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("institutionName", user.getInstitution().getName());
        claims.put("subdomain", user.getInstitution().getSubdomain());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(key)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }


}
