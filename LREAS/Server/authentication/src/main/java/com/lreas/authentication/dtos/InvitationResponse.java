package com.lreas.authentication.dtos;

import com.lreas.authentication.models.User;

public class InvitationResponse {
    private String email;
    private String username;
    private String message;
    private String institutionName;
    private User.ROLE role;
    private String subdomain;
    private Boolean success;
    private String invitationUrl;

    public InvitationResponse(
            boolean success,
            String message,
            String invitationUrl,
            String email,
            User.ROLE role,
            String institutionName,
            String usermame
    ) {
        this.success = success;
        this.message = message;
        this.email = email;
        this.invitationUrl = invitationUrl;
        this.role = role;
        this.institutionName = institutionName;
        this.username = usermame;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public User.ROLE getRole() {
        return role;
    }

    public void setRole(User.ROLE role) {
        this.role = role;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getInvitationUrl() {
        return invitationUrl;
    }

    public void setInvitationUrl(String invitationUrl) {
        this.invitationUrl = invitationUrl;
    }
}
