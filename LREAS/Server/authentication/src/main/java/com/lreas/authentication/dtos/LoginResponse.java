package com.lreas.authentication.dtos;

public class LoginResponse {
    private String token;
    private Boolean success;
    private String message;

    public LoginResponse (Boolean success,String token, String message){
        this.token = token;
        this.message = message;
        this.success = success;
    }

    public String getToken(){
        return token;
    }
    public Boolean getSuccess() {
        return success;
    }
    public String getMessage(){
        return message;
    }

}
