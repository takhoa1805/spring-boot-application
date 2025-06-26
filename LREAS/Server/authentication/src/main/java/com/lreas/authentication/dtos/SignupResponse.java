package com.lreas.authentication.dtos;

public class SignupResponse {
    private String username;
    private String message;
    private String institutionName;
    private String role;
    private String subdomain;
    private Boolean success;

    public SignupResponse(Boolean success,String username, String message, String institutionName, String role, String subdomain){
        this.success = success;
        this.username = username;
        this.message = message;
        this.institutionName = institutionName;
        this.role = role;
        this.subdomain = subdomain;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getUsername(){
        return username;
    }
    public String getMessage(){
        return message;
    }
    public String getInstitutionName(){
        return institutionName;
    }
    public String getRole(){
        return role;
    }
    public String getSubdomain(){
        return subdomain;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setInstitutionName(String institutionName){
        this.institutionName = institutionName;
    }
    public void setRole(String role){
        this.role = role;
    }
    public void setSubdomain(String subdomain){
        this.subdomain = subdomain;
    }


}
