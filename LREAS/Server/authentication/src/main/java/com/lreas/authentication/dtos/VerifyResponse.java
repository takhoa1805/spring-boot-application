package com.lreas.authentication.dtos;

import com.lreas.authentication.models.User;

public class VerifyResponse {
    private Boolean success;
    private String message;
    private String email;
    private User.ROLE role;
    private String institution;
    private String subdomain;
    private String username;

    public VerifyResponse(Boolean success,String message, String email, User.ROLE role, String institution, String subdomain, String username){
        this.success = success;
        this.message = message;
        this.email = email;
        this.role = role;
        this.institution = institution;
        this.subdomain = subdomain;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getMessage(){
        return message;
    }
    public String getEmail(){
        return email;
    }
    public User.ROLE getRole(){
        return role;
    }
    public String getInstitution(){
        return institution;
    }
    public String getSubdomain(){
        return subdomain;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setRole(User.ROLE role){
        this.role = role;
    }
    public void setInstitution(String institution){
        this.institution = institution;
    }
    public void setSubdomain(String subdomain){
        this.subdomain = subdomain;
    }

}
